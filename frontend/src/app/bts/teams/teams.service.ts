import {Injectable} from "@angular/core";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {LeagueService} from "../league/league.service";
import {BehaviorSubject, combineLatest, from, Subject, throwError} from "rxjs";
import {catchError, filter, map, mergeMap, switchMap, tap, toArray} from "rxjs/operators";
import {Standing} from "../domain/standing";
import {GeneralError} from "../domain/generalError";


@Injectable({
    providedIn: 'root'
})
export class TeamsService {

    constructor(private http: HttpClient, private leagueService: LeagueService) {
    }

    private leagueSelectedSubject = new BehaviorSubject<number>(0);
    leagueSelectedAction$ = this.leagueSelectedSubject.asObservable();

    //retrieve the standing
    standings$ = this.leagueSelectedAction$
        .pipe(
            switchMap(leagueId =>
                this.http.get<Standing[]>(`/btsapi/api/standing/league/${leagueId}`))
        )
        .pipe(
            //tap(data => console.log('standing ', JSON.stringify(data))),
            catchError(this.handleHttpError)
        );

    //retrieve the league
    leagueWithId$ = this.leagueSelectedAction$
        .pipe(
            switchMap(leagueId => this.leagueService.getLeagueById(leagueId))
        )
        .pipe(
            //tap(data => console.log('league ', JSON.stringify(data))),
            catchError(this.handleHttpError)
        );


    selectedLeagueChanged(selectedLeagueId : number) {
        console.log("team service : selectedLeagueChanged " +selectedLeagueId);
        this.leagueSelectedSubject.next(selectedLeagueId);
    }

    private handleHttpError(error: HttpErrorResponse) {
        let dataError = new GeneralError();
        dataError.errorNumber = error.status;
        dataError.errorMessage = error.message;
        dataError.userFriendlyMessage = "Er liep iets fout bij het ophalen van de teams";
        return throwError(dataError);
    }

}
