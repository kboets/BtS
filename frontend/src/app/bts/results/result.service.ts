import {Injectable} from "@angular/core";
import {Observable, Subject, throwError} from "rxjs";
import {League} from "../domain/league";
import {catchError, shareReplay, tap} from "rxjs/operators";
import {Round} from "../domain/round";
import {HttpClient, HttpErrorResponse, HttpParams} from "@angular/common/http";
import {GeneralError} from "../domain/generalError";
import {Result} from "../domain/result";
import * as _ from 'underscore';

@Injectable({
    providedIn: 'root'
})
export class ResultService {


    constructor(private http: HttpClient) {  }

    getAllResultForLeague(id: number): Observable<Result[]> {
        return this.http.get<Result[]>(`/bts/api/result/all/${id}`)
            .pipe(
                //tap(data => console.log('get all result for league  '+id, JSON.stringify(data))),
                shareReplay(2),
                catchError(this.handleHttpError)
            );
    }


    private handleHttpError(error: HttpErrorResponse) {
        console.log("entering the handleHttpError of result service "+error.message);
        let dataError = new GeneralError();
        dataError.errorNumber = error.status;
        dataError.errorMessage = error.message;
        dataError.userFriendlyMessage = "Er liep iets fout bij het ophalen van de round";
        //this.errorMessageSubject.next(dataError);
        return throwError(dataError);
    }
}
