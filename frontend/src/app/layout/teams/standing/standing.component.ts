import {Component, Input} from "@angular/core";
import {routerTransition} from "../../../router.animations";
import {League} from "../../../general/league";
import {TeamsService} from "../teams.service";
import {catchError, map, tap} from "rxjs/operators";
import {combineLatest, EMPTY, Subject} from "rxjs";
import {GeneralError} from "../../../general/generalError";
import {LeagueService} from "../../league/league.service";

@Component({
    selector: 'bts-standing',
    templateUrl: './standing.component.html'
})
export class StandingComponent {

    @Input()
    showStanding : boolean;

    private errorMessageSubject = new Subject<string>();
    errorMessage$ = this.errorMessageSubject.asObservable();


    constructor(private teamsService: TeamsService, private leagueService: LeagueService) { }

    //standings
    standings$ = this.teamsService.standings$
        .pipe(
            tap(data => console.log('standings ', JSON.stringify(data))),
            catchError(err => {
                console.log('error occured');
                this.errorMessageSubject.next(err);
                return EMPTY;
            })
        );

    //league
    leagueWithId$ = this.teamsService.leagueWithId$
        .pipe(
            catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            })
        );

    //selected league with the standing of each team
    selectedLeagueWithTeamStanding$ = combineLatest([
        this.standings$,
        this.leagueWithId$]
    ).pipe(
        tap(data =>  console.log('data retrieved in combineLatest ', JSON.stringify(data))),
        map(([standings, league]) =>
            league.teamDtos.map(teamDto =>({
                ...teamDto,
                standing: standings.find(s => s.team_id === teamDto.teamId)
            }))
        )
    );

}
