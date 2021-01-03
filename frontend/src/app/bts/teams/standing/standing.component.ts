import {Component, Input, OnInit} from "@angular/core";
import {TeamsService} from "../teams.service";
import {catchError, map, shareReplay, tap} from "rxjs/operators";
import {combineLatest, EMPTY, Subject} from "rxjs";
import {LeagueService} from "../../league/league.service";
import * as _ from 'underscore';


@Component({
    selector: 'bts-standing',
    templateUrl: './standing.component.html'
})
export class StandingComponent implements OnInit {

    @Input()
    showStanding : boolean;

    cols: any[];

    private errorMessageSubject = new Subject<string>();
    errorMessage$ = this.errorMessageSubject.asObservable();


    constructor(private teamsService: TeamsService, private leagueService: LeagueService) { }

    //standings
    standings$ = this.teamsService.standings$
        .pipe(
            //tap(data => console.log('standings ', JSON.stringify(data))),
            shareReplay(1),
            catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            })
        );

    //league
    leagueWithId$ = this.teamsService.leagueWithId$
        .pipe(
            //tap(data => console.log('league ', JSON.stringify(data))),
            shareReplay(1),
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
                ...teamDto
            }))
        ),
        shareReplay(1),
        //tap(data => console.log(JSON.stringify(data)))
    );

    //selected league with sorted standing of each team
    selectedLeagueWithTeamStandingSorted$ = this.selectedLeagueWithTeamStanding$
        .pipe(
            map(items => items.sort(this.sortByStandingRank),
        ));

    private sortByStandingRank(a,b) {
        if(a.standing.rank > b.standing.rank) {
            return 1;
        } else {
            return -1;
        }
    }

    ngOnInit(): void {
        this.cols = [
            { field: 'standing.rank', header: 'Plaats' },
            { field: 'name', header: 'Club' },
            { field: 'team.standing.points', header: 'Punten' },
            { field: 'team.standing.allSubStanding.matchPlayed', header: 'Wedstrijden' },
            { field: 'team.standing.allSubStanding.win', header: 'Winst' },
            { field: 'team.standing.allSubStanding.draw', header: 'Gelijk' },
            { field: 'team.standing.allSubStanding.lose', header: 'Verlies' },
        ];
    }
}
