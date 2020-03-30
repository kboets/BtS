import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {Country} from "./country";
import {catchError, tap} from "rxjs/operators";
import {GeneralError} from "../../general/generalError";
import {throwError} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class CountryService {

    constructor(private http: HttpClient) { }

    //all selectable countries
    selectableCountries$ = this.http.get<Country[]> (`/bts/api/getAllSelectableCountries`)
        .pipe(
            tap(data => console.log('selectable countries', JSON.stringify(data))),
            catchError(this.handleHttpError)
        );

    availableCountries$ = this.http.get<Country[]> (`/bts/api/getAllAvailableCountries`)
        .pipe(
            tap(data => console.log('available countries', JSON.stringify(data))),
            catchError(this.handleHttpError)
        );

    private handleHttpError(error: HttpErrorResponse) {
        let dataError = new GeneralError();
        dataError.errorNumber = error.status;
        dataError.errorMessage = error.message;
        dataError.userFriendlyMessage = "Er liep iets fout bij het ophalen van de selectable countries";
        return throwError(dataError);
    }
}