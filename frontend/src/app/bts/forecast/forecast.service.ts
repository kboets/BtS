import {Injectable} from "@angular/core";
import {Observable, Subject, throwError} from "rxjs";
import {Forecast} from "../domain/forecast";
import {catchError, shareReplay} from "rxjs/operators";
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import {GeneralError} from "../domain/generalError";
import {Standing} from "../domain/standing";
import {Round} from "../domain/round";

@Injectable({
    providedIn: 'root'
})
export class ForecastService {

    private _forecastRefreshNeeded$ = new Subject<Forecast []>();

    constructor(private http: HttpClient) {  }

    get forecastRefreshNeeded$(): Subject<Forecast []> {
        return this._forecastRefreshNeeded$;
    }

    getAllReviewRounds(leagueId: number): Observable<Round[]> {
        return this.http.get<Round[]>(`/btsapi/api/forecast/reviewRounds/${leagueId}`)
            .pipe(
                shareReplay(1),
                catchError(ForecastService.handleHttpError)
            );
    }

    getReviewForecastsForAlgorithmRoundAndLeague(algorithmId: number, leagueId: number, round: number): Observable<Forecast> {
        return this.http.get<Forecast>(`/btsapi/api/forecast/review/${algorithmId}/${leagueId}/${round}`)
            .pipe(
                shareReplay(1),
                catchError(ForecastService.handleHttpError)
            );

    }

    getAllForecasts(): Observable<Forecast[]> {
        return this.http.get<Forecast[]>(`/btsapi/api/forecast/all`)
            .pipe(
                shareReplay(1),
                catchError(ForecastService.handleHttpError)
            );
    }

    getFilteredForecasts(scores: number[]) : Observable<Forecast[]> {
        return this.http.put<Forecast[]>(`/btsapi/api/forecast/requested`, scores, {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        })
    }

    runForecasts(): Observable<boolean> {
        return this.http.get<boolean>(`/btsapi/api/forecast/recalculate`)
            .pipe(
                catchError(ForecastService.handleHttpError)
            );
    }


    private static handleHttpError(error: HttpErrorResponse) {
        console.log("entering the handle HttpError of result service "+error.message);
        let dataError = new GeneralError();
        dataError.errorNumber = error.status;
        dataError.errorMessage = error.message;
        dataError.userFriendlyMessage = "Er liep iets fout bij het ophalen van de forecast";
        return throwError(dataError);
    }


}
