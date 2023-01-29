import {Injectable} from "@angular/core";
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import {BehaviorSubject, Observable, Subject, throwError} from "rxjs";
import {Round} from "../domain/round";
import {catchError, shareReplay, switchMap, tap} from "rxjs/operators";
import {GeneralError} from "../domain/generalError";

@Injectable({
    providedIn: 'root'
})
export class RoundService {

    constructor(private http: HttpClient) {  }

    private leagueSelectedSubject = new BehaviorSubject<number>(0);
    leagueSelectedAction$ = this.leagueSelectedSubject.asObservable();

    //retrieve current round
    currentRound$ = this.leagueSelectedAction$
        .pipe(
            switchMap(leagueId => this.getCurrentRoundForLeague(leagueId))
        )
        .pipe(
            //tap(data => console.log('standing ', JSON.stringify(data))),
            catchError(this.handleHttpError)
        );


    getAllRoundsForLeague(leagueId: number) : Observable<Round[]> {
        return this.http.get<Round[]>(`/btsapi/api/round/all/${leagueId}`)
            .pipe(
                //tap(data => console.log('getCurrent round for league  '+id, JSON.stringify(data))),
                shareReplay(1),
                catchError(this.handleHttpError)
            );
    }

    getCurrentRoundForLeague(id: number): Observable<Round> {
        return this.http.get<Round>(`/btsapi/api/round/current/${id}`)
            .pipe(
                shareReplay(1),
                catchError(this.handleHttpError)
            );
    }

    selectedLeagueChanged(selectedLeagueId : number) {
        this.leagueSelectedSubject.next(selectedLeagueId);
    }

    updateCurrentRound(roundId: number, leagueId: number): Observable<Round> {
        return this.http.get<Round>(`/btsapi/api/round/update/${leagueId}/${roundId}`)
            .pipe(
                shareReplay(1),
                catchError(this.handleHttpError)
            );
    }

    private handleHttpError(error: HttpErrorResponse) {
        let dataError = new GeneralError();
        dataError.errorNumber = error.status;
        dataError.errorMessage = error.message;
        dataError.userFriendlyMessage = "Er liep iets fout bij het ophalen van de rounds";
        return throwError(dataError);
    }
}
