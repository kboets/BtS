import {Injectable} from "@angular/core";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {Observable, Subject, throwError} from "rxjs";
import {Round} from "../domain/round";
import {catchError, shareReplay, tap} from "rxjs/operators";
import {GeneralError} from "../domain/generalError";

@Injectable({
    providedIn: 'root'
})
export class RoundService {



    constructor(private http: HttpClient) {  }


    getAllRoundsForLeague(leagueId: number) : Observable<Round[]> {
        return this.http.get<Round[]>(`/bts/api/round/all/${leagueId}`)
            .pipe(
                //tap(data => console.log('getCurrent round for league  '+id, JSON.stringify(data))),
                shareReplay(2),
                catchError(this.handleHttpError)
            );
    }

    getCurrentRoundForLeague(id: number): Observable<Round> {
        return this.http.get<Round>(`/bts/api/round/current/${id}`)
            .pipe(
                // tap(() => {
                //     this._selectedRoundNeedUpdate$.next()
                // }),
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
