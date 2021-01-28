import {Injectable} from "@angular/core";
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import {catchError, map, tap} from "rxjs/operators";
import {BehaviorSubject, combineLatest, Observable, of, Subject, throwError} from "rxjs";
import {League} from "../domain/league";
import {CountryService} from "./country.service";
import {GeneralError} from "../domain/generalError";

@Injectable({
    providedIn: 'root'
})
export class LeagueService {

    private _leagueRefreshNeeded$ = new Subject<League[]>();
    leagueRefreshAction$ = this._leagueRefreshNeeded$.asObservable();

    constructor(private http: HttpClient, private countryService: CountryService) {
    }

    get leagueRefreshNeeded$(): Subject<League[]> {
        return this._leagueRefreshNeeded$;
    }


    leagues$ = this.http.get<League[]>(`/btsapi/api/league/leaguesCurrentSeason`)
        .pipe(
            //tap(data => console.log('selectable leagues ', JSON.stringify(data))),
            catchError(this.handleHttpError)
        );


    leaguesCurrentSeason$ = this._leagueRefreshNeeded$
        .pipe(() =>
                this.getLeaguesCurrentSeason(),
            catchError(this.handleHttpError)
        );


    selectedLeaguesWithCountries$ = combineLatest([this.leaguesCurrentSeason$, this.countryService.countries$])
        .pipe(
            map(([leagues, countries]) =>
                leagues.map(league => ({
                    ...league,
                    country: countries.find(c => c.countryCode === league.countryCode).country
                }) as League)
            )
        );

    getLeaguesCurrentSeason(): Observable<League[]> {
        return this.http.get<League[]>(`/btsapi/api/league/leaguesCurrentSeason`)
            .pipe(
                //tap(data => console.log('getAllSelectedLeagues ->leagues ', JSON.stringify(data))),
                catchError(this.handleHttpError)
            );
    }

    updateToSelectedLeague(ids: string[]): Observable<League[]> {
        return this.http.put<League[]>(`/btsapi/api/league/toSelected`, ids, {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        }).pipe(
            tap(() => {
                this._leagueRefreshNeeded$.next()
            })
        );
    }

    updateToAvailableLeague(ids: string[]): Observable<League[]> {
        return this.http.put<League[]>(`/btsapi/api/league/toAvailable`, ids, {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        }).pipe(
            tap(() => {
                this._leagueRefreshNeeded$.next()
            })
        );
    }

    getLeagueById(id: number) {
        return this.http.get<League>(`/btsapi/api/league/get/${id}`)
    }

    selectedLeaguesUpdated(result: League[]) {
        //console.log("league service : selectedLeaguesUpdated " +result);
        this._leagueRefreshNeeded$.next(result);
    }

    availableLeaguesUpdated(result: League[]) {
        //console.log("league service : availableLeaguesUpdated " +result);
        this._leagueRefreshNeeded$.next(result);
    }

    private handleHttpError(error: HttpErrorResponse) {
        //console.log("entering the handleHttpError of league service "+error.message);
        let dataError = new GeneralError();
        dataError.errorNumber = error.status;
        dataError.errorMessage = error.message;
        dataError.userFriendlyMessage = "Er liep iets fout bij het ophalen van de leagues";
        return throwError(dataError);
    }
}
