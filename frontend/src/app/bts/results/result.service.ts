import {Injectable} from "@angular/core";
import {Observable, Subject, throwError} from "rxjs";
import {League} from "../domain/league";
import {catchError, map, shareReplay, tap} from "rxjs/operators";
import {Round} from "../domain/round";
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams} from "@angular/common/http";
import {GeneralError} from "../domain/generalError";
import {Result} from "../domain/result";
import * as _ from 'underscore';
import {LeagueResults} from "../prospect/leagueResults";

@Injectable({
    providedIn: 'root'
})
export class ResultService {

    private _prospectSelectedLeaguesRefreshNeeded = new Subject<Map<string, any>>();
    prospectRefreshNeededAction$ = this._prospectSelectedLeaguesRefreshNeeded.asObservable();

    private _prospectNonSelectedLeaguesRefreshNeeded = new Subject<Map<string, any>>();
    prospectRefreshNonSelectedNeededAction$ = this._prospectNonSelectedLeaguesRefreshNeeded.asObservable();

    get prospectSelectedLeaguesRefreshNeeded(): Subject<Map<string, any>> {
        return this._prospectSelectedLeaguesRefreshNeeded;
    }
    get prospectNonSelectedLeaguesRefreshNeeded(): Subject<Map<string, any>> {
        return this._prospectNonSelectedLeaguesRefreshNeeded;
    }

    constructor(private http: HttpClient) {  }

    getAllResultForLeague(id: number): Observable<Result[]> {
        return this.http.get<Result[]>(`/btsapi/api/result/all/${id}`)
            .pipe(
                //tap(data => console.log('get all result for league  '+id, JSON.stringify(data))),
                shareReplay(1),
                catchError(this.handleHttpError)
            );
    }

    getAllResultForLeagues(selected: boolean): Observable<LeagueResults[]>  {
        if(selected) {
            return this.http.get<LeagueResults[]>(`/btsapi/api/result/allSelected`)
                .pipe(
                    //tap(data => console.log('get allSelected ', JSON.stringify(data[0].results))),
                    shareReplay(1),
                    catchError(this.handleHttpError)
                );
        } else {
            return this.http.get<LeagueResults[]>(`/btsapi/api/result/allNonSelected`)
                .pipe(
                    //tap(data => console.log('get all result for league  '+id, JSON.stringify(data))),
                    shareReplay(1),
                    catchError(this.handleHttpError)
                );
        }
    }

    sortByResults(a, b) {
        if(a.rank > b.rank) {
            return 1;
        } else {
            return -1;
        }
    }

    private handleHttpError(error: HttpErrorResponse) {
        let dataError = new GeneralError();
        dataError.errorNumber = error.status;
        dataError.errorMessage = error.message;
        dataError.userFriendlyMessage = "Er liep iets fout bij het ophalen van de result";
        return throwError(dataError);
    }
}
