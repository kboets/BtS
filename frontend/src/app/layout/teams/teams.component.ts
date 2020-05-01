import {Component} from "@angular/core";
import {routerTransition} from "../../router.animations";
import {catchError, tap} from "rxjs/operators";
import {GeneralError} from "../../general/generalError";
import {EMPTY} from "rxjs";
import {LeagueService} from "../league/league.service";

@Component({
    selector: 'bts-league',
    templateUrl: './teams.component.html',
    animations: [routerTransition()]
})
export class TeamsComponent {

    error: GeneralError;

    constructor(private leagueService : LeagueService) {
    }

    selectedLeaguesWithCountries$ = this.leagueService.selectedLeaguesWithCountries$
        .pipe(
            catchError(err => {
                this.error = err;
                return EMPTY;
            })
        );

}
