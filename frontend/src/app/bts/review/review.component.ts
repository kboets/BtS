import {Component, OnInit} from "@angular/core";
import {EMPTY, Observable, Subject} from "rxjs";
import {GeneralError} from "../domain/generalError";
import {Forecast} from "../domain/forecast";
import {ForecastService} from "../forecast/forecast.service";
import {AdminService} from "../admin/admin.service";
import {catchError, map} from "rxjs/operators";
import {League} from "../domain/league";
import * as _ from 'underscore';

@Component({
    selector: 'bts-review',
    templateUrl: './review.component.html',
    styleUrls: ['../forecast/forecast.css']
})
export class ReviewComponent implements OnInit {

    private errorMessageSubject = new Subject<GeneralError>();
    errorMessage$ = this.errorMessageSubject.asObservable();

    public forecastData: Forecast[];
    public leagues$: Observable<string[]>;
    public selectedLeague: string;
    public leagues: League[];
    private forecastData$: Observable<Forecast[]>;

    constructor(private forecastService: ForecastService, private adminService: AdminService) {
        this.forecastData = [];
        this.leagues = [];
    }

    ngOnInit(): void {
        this.forecastData$ = this.forecastService.getAllForecasts()
            .pipe(catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            }));


        this.leagues$ = this.forecastData$.pipe(
            map(forecasts => {
                return _.map(forecasts, function (forecast) {
                    return forecast.league.name;
                });
            }),
            map(leagues => {
                return _.uniq(leagues);
            })
        ).pipe(catchError(err => {
            this.errorMessageSubject.next(err);
            return EMPTY;
        }));
    }

}
