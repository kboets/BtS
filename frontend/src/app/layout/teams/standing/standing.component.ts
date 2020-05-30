import {Component, Input} from "@angular/core";
import {routerTransition} from "../../../router.animations";
import {League} from "../../../general/league";
import {TeamsService} from "../teams.service";
import {catchError, map} from "rxjs/operators";
import {EMPTY, Subject} from "rxjs";
import {GeneralError} from "../../../general/generalError";

@Component({
    selector: 'bts-standing',
    templateUrl: './standing.component.html'
})
export class StandingComponent {

    @Input()
    showStanding : boolean;

    private errorMessageSubject = new Subject<string>();
    errorMessage$ = this.errorMessageSubject.asObservable();


    constructor(private teamsService : TeamsService) { }

    standingsForSelectedLeague$ = this.teamsService.standingsForSelectedLeague$
        .pipe(
            catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            })
        );

    selectedLeague$ = this.teamsService.selectedLeague$
        .pipe(
            catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            })
        );

    selectedLeagueWithTeamStanding$ = this.teamsService.selectedLeagueWithTeamStanding$
        .pipe(
            catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            })
        );



}
