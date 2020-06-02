import {Component, Input} from "@angular/core";
import {TeamsService} from "../teams.service";
import {catchError, map, tap} from "rxjs/operators";
import {combineLatest, EMPTY, Subject} from "rxjs";
import {LeagueService} from "../../league/league.service";
import * as _ from 'underscore';
import {Standing} from "../../../general/standing";

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
            //tap(data => console.log('standings ', JSON.stringify(data))),
            catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            })
        );

    //league
    leagueWithId$ = this.teamsService.leagueWithId$
        .pipe(
            //tap(data => console.log('league ', JSON.stringify(data))),
            catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            })
        );

    //selected league with the standing of each team
    selectedLeagueWithTeamStanding$ = combineLatest([
        this.leagueWithId$,
        this.standings$]
    ).pipe(
        map(([league, standings, ]) =>
            league.teamDtos.map(
                teamDto =>({
                ...teamDto,
                //standing: _.sortBy(_.findWhere(standings, {team_id: teamDto.teamId}), "rank")
                standing: _.findWhere(standings, {team_id: teamDto.teamId})
            }))
        )
    );

    //selected league with sorted standing of each team
    selectedLeagueWithTeamStandingSorted$ = this.selectedLeagueWithTeamStanding$
        .pipe(
            map(items => items.sort(this.sortByStandingRank)
        ));

    private sortByStandingRank(a,b) {
        if(a.standing.rank > b.standing.rank) {
            return 1;
        } else {
            return -1;
        }
    }
}
