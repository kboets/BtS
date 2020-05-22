import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {Country} from "../../general/country";
import {catchError, tap} from "rxjs/operators";
import {GeneralError} from "../../general/generalError";
import {throwError} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class CountryService {

    constructor(private http: HttpClient) { }

    //all selectable countries
    countries$ = this.http.get<Country[]> (`/bts/api/country/get`)
        .pipe(
            //tap(data => console.log('selectable countries ', JSON.stringify(data))),
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
