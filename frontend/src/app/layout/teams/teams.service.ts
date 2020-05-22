import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {LeagueService} from "../league/league.service";
import {combineLatest, from, Subject} from "rxjs";
import {filter, map, mergeMap, switchMap, toArray} from "rxjs/operators";
import {Standing} from "../../general/standing";

@Injectable({
    providedIn: 'root'
})
export class TeamsService {

    constructor(private http: HttpClient, private leagueService: LeagueService) {
    }

    private leagueSelectedSubject = new Subject<number>();
    leagueSelectedAction$ = this.leagueSelectedSubject.asObservable();

    //league (with country) when clicked on overview
    selectedLeague$ = combineLatest([
        this.leagueService.selectedLeaguesWithCountries$,
        this.leagueSelectedAction$
    ]).pipe(
        map(([leagues, selectedLeagueId]) =>
            leagues.find(league => +league.league_id === selectedLeagueId)
        )
    );

    standingsForSelectedLeague$ = this.selectedLeague$
        .pipe(
            switchMap(selectedLeague =>
                from(selectedLeague.league_id)
                    .pipe(mergeMap(leagueId => this.http.get<Standing>(`/bts/api/standing/league/${leagueId}`)),
                    toArray()
                )
        )
    );

    selectedLeagueWithTeamStanding$ = combineLatest([
        this.selectedLeague$,
        this.standingsForSelectedLeague$]
    ).pipe(
        map(([leagues, standing]) =>
            leagues.teamDtos.map(teamDto =>({
                ...teamDto,
                standing: standing.find(s => s.team_id === teamDto.teamId)
            }))
        )
    );

    selectedLeagueChanged(selectedLeagueId : number) {
        this.leagueSelectedSubject.next(selectedLeagueId)
    }
}
