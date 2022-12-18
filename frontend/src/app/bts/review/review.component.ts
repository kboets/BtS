import {Component, OnInit} from "@angular/core";
import {combineLatest, EMPTY, Observable, pipe, Subject} from "rxjs";
import {GeneralError} from "../domain/generalError";
import {Forecast} from "../domain/forecast";
import {ForecastService} from "../forecast/forecast.service";
import {AdminService} from "../admin/admin.service";
import {catchError, map, switchMap, tap} from "rxjs/operators";
import {League} from "../domain/league";
import * as _ from 'underscore';
import {ForecastUtility} from "../common/forecastUtility";

@Component({
    selector: 'bts-review',
    templateUrl: './review.component.html',
    styleUrls: ['../forecast/forecast.css']
})
export class ReviewComponent implements OnInit {

    forecastUtility: ForecastUtility;
    private errorMessageSubject = new Subject<GeneralError>();
    errorMessage$ = this.errorMessageSubject.asObservable();

    public leagues$: Observable<League[]>;
    public selectedLeague: League;
    private selectedLeagueSubject = new Subject<League>();
    selectedLeagueAction = this.selectedLeagueSubject.asObservable();

    public rounds$: Observable<Forecast[]>;
    public forecastForRoundAndLeague: Forecast;
    private selectedForecastSubject = new Subject<Forecast>();
    selectedRoundAction = this.selectedForecastSubject.asObservable();

    private forecastData$: Observable<Forecast[]>;

    columns: any[];

    constructor(private forecastService: ForecastService, private adminService: AdminService) {
        this.forecastUtility = ForecastUtility.getInstance();
    }

    ngOnInit(): void {
        //get all forecast data
        this.forecastData$ = this.forecastService.getAllForecasts()
            .pipe(catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            }));


        //get all unique leagues from forecast data
        this.leagues$ = this.forecastData$.pipe(
            map(forecasts => {
                return _.map(forecasts, function (forecast) {
                    return forecast.league;
                });
            }),
            map(leagues => {
                return _.uniq(leagues, league => league.name);
            }),
            tap(leagues => {
                this.selectedLeagueSubject.next(_.first(leagues));
            })
        ).pipe(catchError(err => {
            this.errorMessageSubject.next(err);
            return EMPTY;
        }));

        //get forecast for selected league and round
        this.rounds$ = combineLatest(
            [this.forecastData$, this.selectedLeagueAction]
        ).pipe(
            map(([forecasts, league]) => {
                return _.filter(forecasts, function (forecast) {
                    if (forecast.league.name === league.name) {
                        return forecast;
                    }
                })
            }),
            tap(forecasts => {
                this.forecastForRoundAndLeague =_.first(forecasts)
                this.forecastForRoundAndLeague.forecastDetails.sort((n1,n2) => n2.finalScore - n1.finalScore)
                //console.log('selectedForecast tap:', this.forecastForRoundAndLeague);
                this.selectedForecastSubject.next(_.first(forecasts));
            }),
            catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            }));


    }


    public onLeagueChange() {
        this.selectedLeagueSubject.next(this.selectedLeague);
    }

    public onRoundChange() {
        //console.log('selectedForecast onRoundChange: ', this.forecastForRoundAndLeague);
        this.forecastForRoundAndLeague.forecastDetails.sort((n1,n2) => n2.finalScore - n1.finalScore)
        this.selectedForecastSubject.next(this.forecastForRoundAndLeague);
    }

}
