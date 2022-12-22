import {Component, OnInit} from "@angular/core";
import {combineLatest, EMPTY, Observable, Subject} from "rxjs";
import {GeneralError} from "../domain/generalError";
import {Forecast} from "../domain/forecast";
import {ForecastService} from "../forecast/forecast.service";
import {catchError, filter, map, tap} from "rxjs/operators";
import {League} from "../domain/league";
import * as _ from 'underscore';
import {ForecastUtility} from "../common/forecastUtility";
import {AlgorithmService} from "../algorithm/algorithm.service";
import {Algorithm} from "../domain/algorithm";

@Component({
    selector: 'bts-review',
    templateUrl: './review.component.html',
    styleUrls: ['../forecast/forecast.css']
})
export class ReviewComponent implements OnInit {

    forecastUtility: ForecastUtility;
    private errorMessageSubject = new Subject<GeneralError>();
    errorMessage$ = this.errorMessageSubject.asObservable();

    // forecast data
    private forecastData$: Observable<Forecast[]>;
    private forecastsForAlgorithm$: Observable<Forecast[]>;

    public leagues$: Observable<League[]>;
    public selectedLeague: League;
    private selectedLeagueSubject = new Subject<League>();

    selectedLeagueAction = this.selectedLeagueSubject.asObservable();
    public rounds$: Observable<Forecast[]>;
    public forecastForRoundAndLeague: Forecast;
    private selectedForecastSubject = new Subject<Forecast>();

    selectedRoundAction = this.selectedForecastSubject.asObservable();
    public algorithms$: Observable<Algorithm[]>;
    public currentAlgorithm$: Observable<Algorithm>;
    private selectedAlgorithmSubject = new Subject<Algorithm>();
    selectedAlgorithmAction = this.selectedAlgorithmSubject.asObservable();

    public selectedAlgorithm: Algorithm;

    columns: any[];

    constructor(private forecastService: ForecastService, private algorithmService: AlgorithmService) {
        this.forecastUtility = ForecastUtility.getInstance();
    }

    ngOnInit(): void {
        //get all forecast data
        this.forecastData$ = this.forecastService.getAllForecasts()
            .pipe(catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            }));

        // get all algorithms
        this.algorithms$ = this.algorithmService.getAlgorithms()
            .pipe(
                tap(algorithms => console.log('algorithms', algorithms)),
                catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            }));

        // get current algorithms
        this.currentAlgorithm$ = this.algorithmService.getCurrentAlgorithm()
            .pipe(
                tap(algorithm => {
                    console.log('current algorithm ', algorithm);
                    this.selectedAlgorithmSubject.next(algorithm);
                    this.selectedAlgorithm = algorithm;
                })
            )
            .pipe(catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            }));


        //get all forecast data for selected algorithm
        this.forecastsForAlgorithm$ = combineLatest(
            [this.forecastData$, this.selectedAlgorithmAction]
        ).pipe(map(([forecasts, algorithm]) => {
                return _.filter(forecasts, function (forecast) {
                    if (forecast.algorithmDto.name === algorithm.name) {
                        return forecast;
                    }
                })
            }), tap(forecasts => {
                console.log('forecast for algorithm ', forecasts);
            }),
            catchError(err => {
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
                this.forecastForRoundAndLeague = _.first(forecasts)
                this.handleChange();
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
        this.handleChange();
        this.selectedForecastSubject.next(this.forecastForRoundAndLeague);
    }

    public onAlgorithmChange() {

    }

    private handleChange() {
        this.forecastForRoundAndLeague.forecastDetails.sort((n1, n2) => n2.finalScore - n1.finalScore)
        this.selectedAlgorithm = this.forecastForRoundAndLeague.algorithmDto;
    }


}
