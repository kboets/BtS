import {Injectable} from "@angular/core";
import {Observable, Subject, throwError} from "rxjs";
import {Forecast} from "../domain/forecast";
import {catchError, shareReplay} from "rxjs/operators";
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import {GeneralError} from "../domain/generalError";

@Injectable({
    providedIn: 'root'
})
export class ForecastService {

    private _forecastRefreshNeeded$ = new Subject<Forecast []>();

    constructor(private http: HttpClient) {  }

    get forecastRefreshNeeded$(): Subject<Forecast []> {
        return this._forecastRefreshNeeded$;
    }

    getAllForecasts(): Observable<Forecast[]> {
        return this.http.get<Forecast[]>(`/btsapi/api/forecast/all`)
            .pipe(
                catchError(this.handleHttpError)
            );
    }

    getFilteredForecasts(scores: number[]) : Observable<Forecast[]> {
        return this.http.put<Forecast[]>(`/btsapi/api/forecast/requested`, scores, {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        })
    }



    private handleHttpError(error: HttpErrorResponse) {
        console.log("entering the handle HttpError of result service "+error.message);
        let dataError = new GeneralError();
        dataError.errorNumber = error.status;
        dataError.errorMessage = error.message;
        dataError.userFriendlyMessage = "Er liep iets fout bij het ophalen van de forecast";
        return throwError(dataError);
    }


}
