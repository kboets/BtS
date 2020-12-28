import {Component, OnInit} from "@angular/core";
import {LeagueService} from "../league/league.service";
import {catchError, map, tap} from "rxjs/operators";
import {combineLatest, EMPTY, forkJoin, merge, Observable, Subject} from "rxjs";
import {GeneralError} from "../domain/generalError";
import {Round} from "../domain/round";
import {ResultService} from "./result.service";
import {Result} from "../domain/result";
import {RoundService} from "../round/round.service";

@Component({
    selector: 'bts-results',
    templateUrl: './results.components.html',
    styleUrls:['./results.css']
})
export class ResultsComponent implements OnInit {

    private errorMessageSubject = new Subject<GeneralError>();
    errorMessage$ = this.errorMessageSubject.asObservable();

    private selectedRoundSubject = new Subject<Round>()
    selectedRoundAction = this.selectedRoundSubject.asObservable();

    private result4RoundSubject = new Subject<Round>();
    results4RoundAction = this.result4RoundSubject.asObservable();
    selectedRound$: Observable<Round>;
    currentRound$ : Observable<Round>;
    results$ : Observable<Result[]>;
    allRounds$ : Observable<Round[]>;
    result4Round$ : Observable<Result[]>;


    selectedRound: Round;
    currentRound: Round;


    constructor(private resultService: ResultService, private leagueService: LeagueService, private roundService: RoundService) {
    }

    selectedLeaguesWithCountries$ = this.leagueService.selectedLeaguesWithCountries$
        .pipe(
            catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            })
        );

    ngOnInit(): void {

    }

    toggleResult(league_id: string) {
        //get all results for this league
        this.results$ = this.resultService.getAllResultForLeague(+league_id)
            .pipe(
                catchError(err => {
                    this.errorMessageSubject.next(err);
                    return EMPTY;
                })
            );
        //get the current round for this league
        this.currentRound$ = this.roundService.getCurrentRoundForLeague(+league_id)
            .pipe(
                catchError(err => {
                    this.errorMessageSubject.next(err);
                    return EMPTY;
                })
           );

        //get all rounds for this league
        this.allRounds$ =  this.roundService.getAllRoundsForLeague(+league_id)
            .pipe(
                catchError(err => {
                    this.errorMessageSubject.next(err);
                    return EMPTY;
                })
            );


        this.selectedRound$ = merge(
            this.currentRound$,
            this.selectedRoundAction
        );

        this.currentRound$
            .subscribe((result) =>
                this.currentRound = result
            );

        //
        this.result4Round$ = combineLatest([
            this.results$, this.selectedRound$
        ]).pipe(
            tap(() => console.log('mapped results')),
            map(([results, currentRound]) =>
                results.filter(result => result.round === currentRound.round)
            ),
            catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            })
        );
    }



    onRoundSelected(round: Round) {
        this.selectedRoundSubject.next(round);
    }


}
