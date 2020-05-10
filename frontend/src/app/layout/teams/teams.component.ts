import {Component} from "@angular/core";
import {routerTransition} from "../../router.animations";
import {catchError, tap} from "rxjs/operators";
import {GeneralError} from "../../general/generalError";
import {EMPTY, Subject} from "rxjs";
import {LeagueService} from "../league/league.service";

@Component({
    selector: 'bts-league',
    templateUrl: './teams.component.html',
    animations: [routerTransition()]
})
export class TeamsComponent {

    error: GeneralError;
    private leagueSelectedSubject;
    showStanding: boolean = false;
    leagueSelectedAction$;

    constructor(private leagueService : LeagueService) {
        this.leagueSelectedSubject = new Subject();
        this.leagueSelectedSubject = this.leagueSelectedSubject.asObservable();
    }


    selectedLeaguesWithCountries$ = this.leagueService.selectedLeaguesWithCountries$
        .pipe(
            catchError(err => {
                this.error = err;
                return EMPTY;
            })
        );

    toggleChild(){
        this.showStanding = !this.showStanding;
    }
}
