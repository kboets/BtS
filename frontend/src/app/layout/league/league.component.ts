import {Component} from "@angular/core";
import {routerTransition} from "../../router.animations";
import {LeagueService} from "./league.service";
import {catchError} from "rxjs/operators";
import {GeneralError} from "../../general/generalError";
import {EMPTY} from "rxjs";

@Component({
    selector: 'app-league',
    templateUrl: './league.component.html',
    animations: [routerTransition()]
})
export class LeagueComponent {

    errorMessage: GeneralError;
    constructor(private leagueService : LeagueService) {
    }

    leagues$ = this.leagueService.leagues$
        .pipe(
            catchError(err => {
                this.errorMessage = err;
                return EMPTY;
            })
        );


}
