import {Component} from '@angular/core';
import {catchError, tap} from 'rxjs/operators';
import {EMPTY, Subject} from 'rxjs';
import {LeagueService} from '../league/league.service';
import {TeamsService} from './teams.service';
import {GeneralError} from '../domain/generalError';

@Component({
    // tslint:disable-next-line:component-selector
    selector: 'bts-league',
    templateUrl: './teams.component.html'
})
export class TeamsComponent {

    error: GeneralError;
    showStanding = false;

    constructor(private leagueService: LeagueService, private teamsService: TeamsService) {
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
        this.teamsService.selectedLeagueChanged(+league_id);
    }
}
