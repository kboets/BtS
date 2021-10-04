import {Injectable} from "@angular/core";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {Standing} from "../domain/standing";
import {Result} from "../domain/result";
import {catchError, shareReplay, tap} from "rxjs/operators";
import {Observable, throwError} from "rxjs";
import {GeneralError} from "../domain/generalError";

@Injectable({
    providedIn: 'root'
})
export class StandingService {

    constructor(private http: HttpClient) {}

    getStandingForLeague(leagueId: number) : Observable<Standing[]> {
        return this.http.get<Standing[]>(`/btsapi/api/standing/league/${leagueId}`)
            .pipe(
                //tap(data => console.log('get all standing for league  '+leagueId, JSON.stringify(data))),
                //shareReplay(1),
                catchError(this.handleHttpError)
            );
    }


    private handleHttpError(error: HttpErrorResponse) {
        console.log("entering the handleHttpError of standing service "+error.message);
        let dataError = new GeneralError();
        dataError.errorNumber = error.status;
        dataError.errorMessage = error.message;
        dataError.userFriendlyMessage = "Er liep iets fout bij het ophalen van de standing";
        //this.errorMessageSubject.next(dataError);
        return throwError(dataError);
    }
}
