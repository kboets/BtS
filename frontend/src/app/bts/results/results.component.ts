import {Component, OnInit} from "@angular/core";
import {LeagueService} from "../league/league.service";
import {catchError, map, tap} from "rxjs/operators";
import {BehaviorSubject, combineLatest, EMPTY, forkJoin, merge, Observable, Subject} from "rxjs";
import {GeneralError} from "../domain/generalError";
import {Round} from "../domain/round";
import {ResultService} from "./result.service";
import {Result} from "../domain/result";
import {RoundService} from "../round/round.service";
import * as _ from 'underscore';
import {StandingService} from "../standing/standing.service";
import {Standing} from "../domain/standing";
import {Teams} from "../domain/teams";

@Component({
    selector: 'bts-results',
    templateUrl: './results.components.html',
    styleUrls:['./results.css']
})
export class ResultsComponent implements OnInit {


    private errorMessageSubject = new Subject<GeneralError>();
    errorMessage$ = this.errorMessageSubject.asObservable();

    // selected round
    private selectedRoundSubject = new Subject<Round>();
    selectedRoundAction = this.selectedRoundSubject.asObservable();

    //selected results for the team
    private selectedResult4TeamSubject = new BehaviorSubject<String>("5");
    selectedResult4TeamAction = this.selectedResult4TeamSubject.asObservable();

    selectedRound$: Observable<Round>;
    currentRound$ : Observable<Round>;
    nextRound$ : Observable<Round>;
    allRounds$ : Observable<Round[]>;
    results$ : Observable<Result[]>;
    //results for specific round
    result4Round$ : Observable<Result[]>;
    //results for next round
    result4NextRound$ : Observable<Result[]>;
    //all results until next round
    resultAllUntilNextRound$: Observable<Result[]>

    standing$: Observable<Standing[]>;
    //last 5 or 8 results for specific team
    resultsLatest4Teams$: Observable<Result[]>;
    resultsAll4Teams$: Observable<Result[]>;

    selectedRound: Round;
    columns: any[];
    showLeagues: boolean
    showStanding: boolean;
    selectedTeam: Teams;


    constructor(private resultService: ResultService, private leagueService: LeagueService,
                private roundService: RoundService, private standingService: StandingService) {
    }

    ngOnInit(): void {
        this.columns = [
            { field: 'standing.rank', header: 'Plaats' },
            { field: 'name', header: 'Club' },
            { field: 'team.standing.allSubStanding.matchPlayed', header: 'Wedstrijden' },
            { field: 'team.standing.points', header: 'Punten' },
            { field: 'team.standing.allSubStanding.win', header: 'Winst' },
            { field: 'team.standing.allSubStanding.draw', header: 'Gelijk' },
            { field: 'team.standing.allSubStanding.lose', header: 'Verlies' },
        ];
        this.showLeagues = true;
        this.showStanding = true;
    }

    selectedLeaguesWithCountries$ = this.leagueService.selectedLeaguesWithCountries$
        .pipe(
            catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            })
        );

    toggleLeagueResult(league_id: string) {
        this.showLeagues = false;

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

        //get the next round
        this.nextRound$ = combineLatest(
            [this.allRounds$, this.currentRound$]
        ).pipe(
            map(([rounds, currentRound]) => {
                return _.chain(rounds).filter(function (round) { return round.playRound === currentRound.playRound+1}).first().value();
            }),
            catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            })
        );

        //merge the current and the selected of the dropdown to selected round
        this.selectedRound$ = merge(
            this.currentRound$,
            this.selectedRoundAction
        );

        //get all results for this league
        this.results$ = this.resultService.getAllResultForLeague(+league_id)
            .pipe(
                catchError(err => {
                    console.log('entered in the catch Error');
                    this.errorMessageSubject.next(err);
                    return EMPTY;
                })
            );

        //results for each selected round
        this.result4Round$ = combineLatest([
            this.results$, this.selectedRound$
        ]).pipe(
            map(([results, currentRound]) =>
                results.filter(result => result.round === currentRound.round)
            ),
            catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            })
        );

        //results for the next round
        this.result4NextRound$ = combineLatest([
            this.results$, this.nextRound$
        ]).pipe(
            map(([results, currentRound]) =>
                results.filter(result => result.round === currentRound.round)
            ),
            catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            })
        );

        //get all results until next round
        this.resultAllUntilNextRound$ = combineLatest(
            [this.results$, this.selectedRound$]
        ).pipe(
            //tap(()=> console.log('arrived in the results all until next round')),
            map(([results, currentRound]) => {
                return _.filter(results, function (result) {
                    return result.round <= currentRound.round;
                })
            }),
            //tap(data => console.log('all results until the next round', JSON.stringify(data))),
            catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            })
        );

        //standing for the league
        this.standing$ = this.standingService.getStandingForLeague(+league_id)
            .pipe(
                //tap(()=> console.log('arrived in the standing ')),
                map(items => items.sort(ResultsComponent.sortByStandingRank),
                catchError(err => {
                    this.errorMessageSubject.next(err);
                    return EMPTY;
                })
            ));



    }

    onClickTeam(teamId: string) {
        //console.log('Arrived in the onClickTeam '+teamId);
        this.showStanding = false;
        this.selectedResult4TeamSubject.next(teamId);


        // latest 5 results for a specific team
        this.resultsLatest4Teams$ = combineLatest(
            [this.resultAllUntilNextRound$, this.selectedResult4TeamAction]
        ).pipe(
            //tap(()=> console.log('arrived in the results latest for team')),
            map(([results, teamId]) => {
                return _.chain(results.reverse())
                    .filter(function (result) {
                        return result.awayTeam.teamId === teamId || result.homeTeam.teamId === teamId;
                    })
                    .first(5).value();
            }),
            //tap(data => console.log('latest 5 results for team team ', JSON.stringify(data))),
            catchError(err => {
                console.log('exception ',err);
                this.errorMessageSubject.next(err);
                return EMPTY;
            })
        );

        // all results for a specific team
        this.resultsAll4Teams$ = combineLatest(
            [this.resultAllUntilNextRound$, this.selectedResult4TeamAction]
        ).pipe(
            map(([results, teamId]) => {
                return _.filter(results, function (result) {
                    //console.log(" result team id " +result.homeTeam.teamId);
                    return result.awayTeam.teamId === teamId || result.homeTeam.teamId === teamId;
                })
            }),
            //tap(data => console.log('all results 4 team ', JSON.stringify(data))),
            catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            })
        );

        //get team
        combineLatest(
            [this.resultAllUntilNextRound$, this.selectedResult4TeamAction]
        ).pipe(
            map(([results, teamId]) => {
                return _.chain(results).filter(function (result) { return result.homeTeam.teamId === teamId}).first().value();
            }),
            //tap(data => console.log('all results 4 team ', JSON.stringify(data))),
            catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            })
        ).subscribe(data => this.selectedTeam = data.homeTeam);

    }

    public static sortByStandingRank(a, b) {
        if(a.rank > b.rank) {
            return 1;
        } else {
            return -1;
        }
    }

    onRoundSelected(round: Round) {
        this.selectedRoundSubject.next(round);
    }

    togglePanel() {
        this.showLeagues = !this.showLeagues;
        this.showStanding = true;
    }


}
