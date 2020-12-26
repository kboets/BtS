import {Injectable} from "@angular/core";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {Observable, Subject, throwError} from "rxjs";
import {Rounds} from "../domain/rounds";
import {catchError, shareReplay, tap} from "rxjs/operators";
import {GeneralError} from "../domain/generalError";
import {ClientRound} from "../domain/clientRound";
import {League} from "../domain/league";

@Injectable({
    providedIn: 'root'
})
export class RoundService {

    private _selectedRoundNeedUpdate$ = new Subject<Rounds>();

    constructor(private http: HttpClient) {  }

    get selectedRoundNeedUpdate$(): Subject<Rounds> {
        return this._selectedRoundNeedUpdate$;
    }

    getAllRoundsForLeague(leagueId: number) : Observable<Rounds[]> {
        return this.http.get<Rounds[]>(`/bts/api/round/all/${leagueId}`)
            .pipe(
                //tap(data => console.log('getCurrent round for league  '+id, JSON.stringify(data))),
                shareReplay(2),
                catchError(this.handleHttpError)
            );
    }

    getCurrentRoundForLeague(id: number): Observable<Rounds> {
        return this.http.get<Rounds>(`/bts/api/round/current/${id}`)
            .pipe(
                // tap(() => {
                //     this._selectedRoundNeedUpdate$.next()
                // }),
                shareReplay(2),
                catchError(this.handleHttpError)
            );
    }

    private handleHttpError(error: HttpErrorResponse) {
        let dataError = new GeneralError();
        dataError.errorNumber = error.status;
        dataError.errorMessage = error.message;
        dataError.userFriendlyMessage = "Er liep iets fout bij het ophalen van de rounds";
        //this.errorMessageSubject.next(dataError);
        return throwError(dataError);
    }
}
