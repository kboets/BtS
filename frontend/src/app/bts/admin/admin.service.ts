import {Injectable} from "@angular/core";
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams} from "@angular/common/http";
import {GeneralError} from "../domain/generalError";
import {BehaviorSubject, combineLatest, Observable, Subject, throwError} from "rxjs";
import {catchError, map, tap} from "rxjs/operators";
import {Admin} from "../domain/admin";
import {Environment} from "../domain/environment";

@Injectable({
    providedIn: 'root'
})
export class AdminService {

    private _historicDataNeedsUpdate$: Subject<number> = new Subject<number>();
    historicDataAction$ = this._historicDataNeedsUpdate$.asObservable();

    constructor(private http: HttpClient) {

    }

    get historicDataNeedsUpdate$(): Subject<number> {
        return this._historicDataNeedsUpdate$;
    }



    adminDatas$ = this.http.get<Admin[]>(`/btsapi/api/admin/all`)
        .pipe(
            //tap(data => console.log('all admin data ', JSON.stringify(data))),
            catchError(this.handleHttpError)
        );

    currentSeason$ = this.http.get<number>(`/btsapi/api/admin/currentSeason`)
        .pipe(
            //tap(data => console.log('all admin data ', JSON.stringify(data))),
            catchError(this.handleHttpError)
        );

    environment$ = this.http.get<Environment>(`/btsapi/api/admin/currentVersion`)
        .pipe(
            //tap(data => console.log('all admin data ', JSON.stringify(data))),
            catchError(this.handleHttpError)
        );

   isHistoricData$ = combineLatest([this.currentSeason$, this.historicDataAction$])
        .pipe(
            //tap(data => console.log('historic data ', JSON.stringify(data))),
            map(([currentSeason, selectedSeason]) =>
                currentSeason !== selectedSeason
            )
        );

    deleteResults4League(id: string): Observable<boolean> {
        return this.http.post<boolean>(`/btsapi/api/admin/deleteResults`, id, {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        });
    }

    deleteStandings4League(id: string): Observable<boolean> {
        return this.http.post<boolean>(`/btsapi/api/admin/deleteStanding`, id, {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        });
    }

    deleteLeague(id: string): Observable<boolean> {
        return this.http.post<boolean>(`/btsapi/api/admin/deleteLeague`, id, {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        });
    }

    deleteForecast(forecast_id: number) {
        return this.http.post<boolean>(`/btsapi/api/admin/deleteForecasts`,  forecast_id, {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        });
    }

    updateAdmin(admin: Admin): Observable<Admin> {
        return this.http.put<Admin>(`/btsapi/api/admin/update`, admin, {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        });
    }

    private handleHttpError(error: HttpErrorResponse) {
        let dataError = new GeneralError();
        dataError.errorNumber = error.status;
        dataError.errorMessage = error.message;
        dataError.userFriendlyMessage = "Er liep iets fout bij het ophalen van de admin data";
        return throwError(dataError);
    }

}
