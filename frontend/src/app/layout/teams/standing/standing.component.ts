import {Component, Input} from "@angular/core";
import {routerTransition} from "../../../router.animations";
import {League} from "../../../general/league";
import {TeamsService} from "../teams.service";
import {catchError, map} from "rxjs/operators";
import {EMPTY} from "rxjs";
import {GeneralError} from "../../../general/generalError";

@Component({
    selector: 'bts-standing',
    templateUrl: './standing.component.html'
})
export class StandingComponent {

    @Input()
    showStanding : boolean;

    error: GeneralError;

    constructor(private teamsService : TeamsService) { }

    selectedLeagueWithTeamStanding$ = this.teamsService.selectedLeagueWithTeamStanding$
        .pipe(
            catchError(err => {
                this.error = err;
                return EMPTY;
            })
        );



}
