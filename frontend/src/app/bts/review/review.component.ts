import {Component, OnInit} from "@angular/core";
import {combineLatest, EMPTY, Observable, Subject} from "rxjs";
import {GeneralError} from "../domain/generalError";
import {Forecast} from "../domain/forecast";
import {ForecastService} from "../forecast/forecast.service";
import {catchError, filter, map, switchMap, tap} from "rxjs/operators";
import {League} from "../domain/league";
import * as _ from 'underscore';
import {ForecastUtility} from "../common/forecastUtility";
import {AlgorithmService} from "../algorithm/algorithm.service";
import {Algorithm} from "../domain/algorithm";
import {ForecastDetail} from "../domain/forecastDetail";
import {Teams} from "../domain/teams";
import {LeagueService} from "../league/league.service";

@Component({
    selector: 'bts-review',
    templateUrl: './review.component.html',
    styleUrls: ['../forecast/forecast.css']
})
export class ReviewComponent implements OnInit {

    private errorMessageSubject = new Subject<GeneralError>();
    errorMessage$ = this.errorMessageSubject.asObservable();

    // forecast data
    forecastUtility: ForecastUtility;
    private forecastData$: Observable<Forecast[]>;
    private forecastsForAlgorithm$: Observable<Forecast[]>;
    // leagues
    public leagues$: Observable<League[]>;
    public selectedLeague: League;
    private selectedLeagueSubject = new Subject<League>();
    selectedLeagueAction = this.selectedLeagueSubject.asObservable();
    // rounds
    public rounds$: Observable<Forecast[]>;
    public forecastForRoundAndLeague: Forecast;
    private selectedForecastSubject = new Subject<Forecast>();
    // algorithm
    public algorithms$: Observable<Algorithm[]>;
    public currentAlgorithm$: Observable<Algorithm>;
    private selectedAlgorithmSubject = new Subject<Algorithm>();
    selectedAlgorithmAction = this.selectedAlgorithmSubject.asObservable();
    public selectedAlgorithm: Algorithm;
    public currentAlgorithm: Algorithm;
    public displayWarningMessage: boolean;
    public warningForecastDetail: ForecastDetail;

    columns: any[];

    constructor(private forecastService: ForecastService, private algorithmService: AlgorithmService, private leagueService: LeagueService) {
        this.forecastUtility = ForecastUtility.getInstance();
        this.displayWarningMessage = false;
    }

    ngOnInit(): void {
        // 1. get all algorithms
        // 2. select the current
        // 3. retrieve the forecast for this algorithm for each league and round

        this.algorithms$ = this.algorithmService.getAlgorithms()
            .pipe(
                tap(algorithms => {
                    for(const algorithm of algorithms) {
                        if (algorithm.current) {
                            this.selectedAlgorithm = algorithm;
                            this.selectedAlgorithmSubject.next(algorithm);
                        }
                    }
                }),
                catchError(err => {
                    this.errorMessageSubject.next(err);
                    return EMPTY;
                }));

        this.leagues$ = this.leagueService.leagues$.pipe(
            tap(leagues => {
                    this.selectedLeagueSubject.next(_.first(leagues));
            }));


        this.forecastsForAlgorithm$ = this.selectedAlgorithmAction
            .pipe(
                switchMap(() => this.forecastService.getReviewForecastsForAlgorithm(this.selectedAlgorithm.algorithm_id)),
                catchError(err => {
                    this.errorMessageSubject.next(err);
                    return EMPTY;
                }));


        // //get all unique leagues from forecast data
        // this.leagues$ = this.forecastsForAlgorithm$.pipe(
        //     map(forecasts => {
        //         return _.map(forecasts, function (forecast) {
        //             return forecast.league;
        //         });
        //     }),
        //     map(leagues => {
        //         return _.uniq(leagues, league => league.name);
        //     }),
        //     tap(leagues => {
        //         this.selectedLeagueSubject.next(_.first(leagues));
        //     })
        // ).pipe(catchError(err => {
        //     this.errorMessageSubject.next(err);
        //     return EMPTY;
        // }));

        //get forecast for selected league and round
        this.rounds$ = combineLatest(
            [this.forecastsForAlgorithm$, this.selectedLeagueAction]
        ).pipe(
            map(([forecasts, league]) => {
                return _.filter(forecasts, function (forecast) {
                    if (forecast.league.name === league.name) {
                        return forecast;
                    }
                })
            }),
            tap(forecasts => {
                this.forecastForRoundAndLeague = _.last(forecasts)
                this.handleChange();
                this.selectedForecastSubject.next(_.last(forecasts));
            }),
            catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            }));
    }

    public determineHomeTeamColor(forecastDetail: ForecastDetail): string {
        if (this.needColorValue(forecastDetail)) {
            if (forecastDetail.nextGame.goalsHomeTeam > forecastDetail.nextGame.goalsAwayTeam) {
                return 'background-color:#20d077';
            } else {
                return 'background-color:#ef6262';
            }
        }

        return 'background-color:transparent'
    }

    public determineAwayTeamColor(forecastDetail: ForecastDetail): string {
        if (this.needColorValue(forecastDetail)) {
            if (forecastDetail.nextGame.goalsAwayTeam > forecastDetail.nextGame.goalsHomeTeam) {
                return 'background-color:#20d077';
            } else {
                return 'background-color:#ef6262';
            }
        }

        return 'background-color:transparent'
    }

    private needColorValue(forecastDetail: ForecastDetail): boolean {
        if (forecastDetail.finalScore >= this.selectedAlgorithm.threshold) {
            return true;
        }
        return false;
    }


    public onLeagueChange() {
        this.selectedLeagueSubject.next(this.selectedLeague);
    }

    public onRoundChange() {
        this.handleChange();
        this.selectedForecastSubject.next(this.forecastForRoundAndLeague);
    }

    public onAlgorithmChange() {
        this.selectedAlgorithmSubject.next(this.selectedAlgorithm);
    }

    public sortRound(forecasts: Forecast[]) {
        return forecasts.sort((n1, n2) => n2.round - n1.round);

    }
    private handleChange() {
        this.forecastForRoundAndLeague.forecastDetails.sort((n1, n2) => n2.finalScore - n1.finalScore)
    }



    showWarningMessage(forecastDetail: ForecastDetail) {
        this.displayWarningMessage = true;
        this.warningForecastDetail = forecastDetail;
    }


}
