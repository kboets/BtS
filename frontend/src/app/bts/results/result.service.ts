import {Injectable} from "@angular/core";
import {Observable, throwError} from "rxjs";
import {League} from "../domain/league";
import {catchError, shareReplay, tap} from "rxjs/operators";
import {Rounds} from "../domain/rounds";
import {HttpClient, HttpErrorResponse, HttpParams} from "@angular/common/http";
import {GeneralError} from "../domain/generalError";
import {Result} from "../domain/result";

@Injectable({
    providedIn: 'root'
})
export class ResultService {

    constructor(private http: HttpClient) {

    }

    getCurrentRoundForLeague(id: number): Observable<Rounds> {
        return this.http.get<Rounds>(`/bts/api/round/current/${id}`)
            .pipe(
                //tap(data => console.log('getCurrent round for league  '+id, JSON.stringify(data))),
                catchError(this.handleHttpError)
            );
    }

    getAllResultForLeague(id: number): Observable<Result[]> {
        return this.http.get<Result[]>(`/bts/api/result/all/${id}`)
            .pipe(
                tap(data => console.log('get all result for league  '+id, JSON.stringify(data))),
                catchError(this.handleHttpError)
            );
    }


    private handleHttpError(error: HttpErrorResponse) {
        //console.log("entering the handleHttpError of league service "+error.message);
        let dataError = new GeneralError();
        dataError.errorNumber = error.status;
        dataError.errorMessage = error.message;
        dataError.userFriendlyMessage = "Er liep iets fout bij het ophalen van de round";
        return throwError(dataError);
    }
}
