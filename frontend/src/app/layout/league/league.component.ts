import {Component} from "@angular/core";
import {routerTransition} from "../../router.animations";
import {LeagueService} from "./league.service";
import {catchError, tap} from "rxjs/operators";
import {GeneralError} from "../../general/generalError";
import {EMPTY} from "rxjs";

@Component({
    selector: 'bts-league',
    templateUrl: './league.component.html',
    styleUrls: ['./league.component.scss'],
    animations: [routerTransition()]
})
export class LeagueComponent {

    error: GeneralError;
    selectedLeagues: string[] = [];

    constructor(private leagueService : LeagueService) {
    }

    availableLeaguesWithCountries$ = this.leagueService.availableLeaguesWithCountries$
        .pipe(
            catchError(err => {
                this.error = err;
                return EMPTY;
            })
        );

    selectedLeaguesWithCountries$ = this.leagueService.selectedLeaguesWithCountries$
        .pipe(
            catchError(err => {
                this.error = err;
                return EMPTY;
            })
        );

    handleSelectedLeagues() {
        console.log('entered the handle selected leagues ' +this.selectedLeagues.length);
    }
}
