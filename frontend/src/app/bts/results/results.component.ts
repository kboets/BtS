import {Component, OnInit} from "@angular/core";
import {LeagueService} from "../league/league.service";
import {catchError, tap} from "rxjs/operators";
import {EMPTY, Subject} from "rxjs";
import {GeneralError} from "../domain/generalError";
import {Rounds} from "../domain/rounds";
import {ResultService} from "./result.service";
import {Result} from "../domain/result";
import * as _ from 'underscore';
import {RoundService} from "../round/round.service";
import {ClientRound} from "../domain/clientRound";
import {League} from "../domain/league";

@Component({
    selector: 'bts-results',
    templateUrl: './results.components.html',
    styleUrls:['./results.css']
})
export class ResultsComponent implements OnInit {

    error: GeneralError;
    currentRound : Rounds;
    results : Result[];
    results4Round : Result[];
    clientRounds : ClientRound[];
    selectedRound: ClientRound;
    firstSelectedRound: ClientRound;


    constructor(private resultService: ResultService, private leagueService: LeagueService, private roundService: RoundService) {
        this.clientRounds = [];
    }

    selectedLeaguesWithCountries$ = this.leagueService.selectedLeaguesWithCountries$
        .pipe(
            catchError(err => {
                this.error = err;
                return EMPTY;
            })
        );

    errorMessage$ = this.resultService.errorMessage$
        .pipe(
            catchError(err => {
                this.error = err;
                return EMPTY;
            })
        );


    ngOnInit(): void {
        this.roundService.selectedRoundNeedUpdate$.subscribe((result) =>
            this.setSelectedRound(result)
        );
    }

    toggleResult(league_id: string) {
        this.resultService.getAllResultForLeague(+league_id)
            .subscribe((data: Result[]) => {
                this.results = data;
            });

        this.roundService.getCurrentRoundForLeague(+league_id)
            .subscribe(
                (result: Rounds) =>  {
                    this.currentRound = result;
                    this.setResultsForRound(this.currentRound.round);
                    this.roundService.selectedRoundNeedUpdate$.next(result);
                });

        this.roundService.getAllRoundsForLeague(+league_id)
            .subscribe((result: Rounds[]) => this.setClientRounds(result));
    }

    setResultsForRound(round : string)  {
        this.results4Round = _.where(this.results, {round: round});
    }

    setClientRounds(rounds : Rounds[]) {
        let index = 1;
        for(let i = 0; i<rounds.length;i++) {
            this.clientRounds.push(this.createClientRound(index, rounds[i]));
            index++
        }
    }

    setSelectedRound(round: Rounds) {
        //console.log('current round '+this.currentRound.round);
        let g = _.chain(this.clientRounds).filter(function (x) { return x.value === round.round}).first().value();
        console.log('selected round '+ g.label);
        //this.selectedRound = ;
    }


    createClientRound(index: number, round: Rounds) {
        let roundString = "Ronde ";
        roundString+=index
        return {
            "index": index,
            "value": round.round,
            "label": roundString
        };

    }
}
