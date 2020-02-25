import {Injectable} from "@angular/core";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {League} from "./league";
import {catchError, map, tap} from "rxjs/operators";
import {GeneralError} from "../../general/generalError";
import {of, throwError} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class LeagueService {

    constructor(private http: HttpClient) {
    }

    countryCode:string = 'BE';

    //all leagues
    leagues$ =  this.http.get<League[]>(`/bts/api/currentLeagueForCountry/${this.countryCode}`)
        .pipe(
            tap(data => console.log('leagues', JSON.stringify(data))),
            catchError(this.handleHttpError)
        );


    private handleHttpError(error: HttpErrorResponse) {
        let dataError = new GeneralError();
        dataError.errorNumber = error.status;
        dataError.errorMessage = error.message;
        dataError.userFriendlyMessage = "Er liep iets fout bij het ophalen van de leagues";
        return throwError(dataError);
    }
}
