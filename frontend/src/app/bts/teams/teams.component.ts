import {Component} from '@angular/core';
import {catchError, tap} from 'rxjs/operators';
import {EMPTY, Subject} from 'rxjs';
import {LeagueService} from '../league/league.service';
import {TeamsService} from './teams.service';
import {GeneralError} from '../domain/generalError';
import {RoundService} from "../round/round.service";

@Component({
    selector: 'bts-league',
    templateUrl: './teams.component.html'
})
export class TeamsComponent {

    error: GeneralError;
    showStanding = false;
    _leagueId: number;

    constructor(private leagueService: LeagueService, private teamsService: TeamsService, private roundService: RoundService) {
    }

    get leagueId(): number {
        return this._leagueId;
    }

    set leagueId(value: number) {
        this._leagueId = value;
    }

    selectedLeaguesWithCountries$ = this.leagueService.selectedLeaguesWithCountries$
        .pipe(
            catchError(err => {
                this.error = err;
                return EMPTY;
            })
        );


    toggleStanding(league_id: string) {
        this.showStanding = !this.showStanding;
        this._leagueId = +league_id;
        this.teamsService.selectedLeagueChanged(+league_id);
        this.roundService.selectedLeagueChanged(+league_id);
    }
}
