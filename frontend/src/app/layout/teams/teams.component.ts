import {Component} from "@angular/core";
import {routerTransition} from "../../router.animations";
import {catchError, tap} from "rxjs/operators";
import {GeneralError} from "../../general/generalError";
import {EMPTY, Subject} from "rxjs";
import {LeagueService} from "../league/league.service";
import {TeamsService} from "./teams.service";

@Component({
    selector: 'bts-league',
    templateUrl: './teams.component.html',
    animations: [routerTransition()]
})
export class TeamsComponent {

    error: GeneralError;
    showStanding: boolean = false;
    leagueSelectedSubject = new Subject();
    leagueSelectedAction$ = this.leagueSelectedSubject.asObservable();

    constructor(private leagueService : LeagueService, private teamsService : TeamsService) {
    }

    selectedLeaguesWithCountries$ = this.leagueService.selectedLeaguesWithCountries$
        .pipe(
            catchError(err => {
                this.error = err;
                return EMPTY;
            })
        );



    toggleStanding(league_id: string){
        this.showStanding = !this.showStanding;
        this.teamsService.selectedLeagueChanged(+league_id);
    }
}
