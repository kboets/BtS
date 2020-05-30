import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {LeagueService} from "../league/league.service";
import {BehaviorSubject, combineLatest, from, Subject} from "rxjs";
import {filter, map, mergeMap, switchMap, tap, toArray} from "rxjs/operators";
import {Standing} from "../../general/standing";

@Injectable({
    providedIn: 'root'
})
export class TeamsService {

    constructor(private http: HttpClient, private leagueService: LeagueService) {
    }

    private leagueSelectedSubject = new BehaviorSubject<number>(0);
    leagueSelectedAction$ = this.leagueSelectedSubject.asObservable();

    //1. league (with country) when clicked on overview
    selectedLeague$ = combineLatest([
        this.leagueService.selectedLeaguesWithCountries$,
        this.leagueSelectedAction$
    ]).pipe(
        map(([leagues, selectedLeagueId]) =>
            leagues.find(league => +league.league_id === selectedLeagueId)
        ),
        tap(league => console.log('selected league 1 ' +league.league_id))
    );

    //2. retrieve standings for league
    standingsForSelectedLeague$ = this.selectedLeague$
        .pipe(
            switchMap(selectedLeague =>
                 this.http.get<Standing>(`/bts/api/standing/league/${selectedLeague.league_id}`)),
                        toArray(),
            tap(data => console.log(JSON.stringify(data)))
         );

    //3. selected league with the standing of each team
    selectedLeagueWithTeamStanding$ = combineLatest([
        this.selectedLeague$,
        this.standingsForSelectedLeague$]
    ).pipe(
        tap(data => console.log('before selectedLeagueWithTeamStanding$')),
        map(([leagues, standing]) =>
            leagues.teamDtos.map(teamDto =>({
                ...teamDto,
                standing: standing.find(s => s.team_id === teamDto.teamId)
            })),
            tap(league => console.log('league with standing ', JSON.stringify(league))
        )
    ));

    selectedLeagueChanged(selectedLeagueId : number) {
        //console.log("team service : selectedLeagueChanged " +selectedLeagueId);
        this.leagueSelectedSubject.next(selectedLeagueId)
    }
}
