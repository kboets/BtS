import {Component, Input, OnInit} from "@angular/core";
import {TeamsService} from "../teams.service";
import {catchError, map, shareReplay, tap} from "rxjs/operators";
import {combineLatest, EMPTY, Subject} from "rxjs";
import {LeagueService} from "../../league/league.service";
import * as _ from 'underscore';
import {ResultsComponent} from "../../results/results.component";


@Component({
    selector: 'bts-standing',
    templateUrl: './standing.component.html'
})
export class StandingComponent implements OnInit {

    @Input()
    showStanding : boolean;
    @Input()
    leagueId: number;

    cols: any[];

    private errorMessageSubject = new Subject<string>();
    errorMessage$ = this.errorMessageSubject.asObservable();

    toggleStanding() {
        this.showStanding = !this.showStanding;
        this.teamsService.selectedLeagueChanged(this.leagueId);
    }

    constructor(private teamsService: TeamsService, private leagueService: LeagueService) { }

    //standings
    standings$ = this.teamsService.standings$
        .pipe(
            //tap(data => console.log('standings ', JSON.stringify(data))),
            map(items => items.sort(ResultsComponent.sortByStandingRank)),
            shareReplay(1),
            catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            })
        );


    ngOnInit(): void {
        this.cols = [
            { field: 'rank', header: 'Plaats' },
            { field: 'team.name', header: 'Club' },
            { field: 'allSubStanding.matchPlayed', header: 'Wedstrijden' },
            { field: 'allSubStanding.win', header: 'Winst' },
            { field: 'allSubStanding.draw', header: 'Gelijk' },
            { field: 'allSubStanding.lose', header: 'Verlies' },
            { field: 'points', header: 'Punten' }
        ];
    }
}
