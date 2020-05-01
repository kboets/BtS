import {Injectable} from "@angular/core";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {League} from "../../general/league";
import {catchError, map, tap} from "rxjs/operators";
import {GeneralError} from "../../general/generalError";
import {combineLatest, of, throwError} from "rxjs";
import {CountryService} from "./country.service";

@Injectable({
    providedIn: 'root'
})
export class LeagueService {

    constructor(private http: HttpClient, private countryService: CountryService) {
    }

    availableLeagues$ = this.http.get<League[]>(`/bts/api/league/availableCurrentSeason`)
        .pipe(
            //tap(data => console.log('selectable leagues ', JSON.stringify(data))),
            catchError(this.handleHttpError)
        );

    selectedLeagues$ = this.http.get<League[]>(`/bts/api/league/selectedCurrentSeason`)
        .pipe(
            //tap(data => console.log('selectable leagues ', JSON.stringify(data))),
            catchError(this.handleHttpError)
        );

    availableLeaguesWithCountries$ = combineLatest([this.availableLeagues$, this.countryService.countries$])
        .pipe(
            map(([leagues, countries]) =>
                leagues.map(league => ({
                    ...league,
                    country : countries.find(c => c.countryCode === league.countryCode).country
                }) as League)
            )
        );

    selectedLeaguesWithCountries$ = combineLatest([this.selectedLeagues$, this.countryService.countries$])
        .pipe(
            map(([leagues, countries]) =>
                leagues.map(league => ({
                    ...league,
                    country : countries.find(c => c.countryCode === league.countryCode).country
                }) as League)
            )
        );

    private handleHttpError(error: HttpErrorResponse) {
        //console.log("entering the handleHttpError of league service "+error.message);
        let dataError = new GeneralError();
        dataError.errorNumber = error.status;
        dataError.errorMessage = error.message;
        dataError.userFriendlyMessage = "Er liep iets fout bij het ophalen van de leagues";
        return throwError(dataError);
    }
}
