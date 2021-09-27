import {Injectable} from "@angular/core";
import {Observable, throwError} from "rxjs";
import {Forecast} from "../domain/forecast";
import {catchError, shareReplay} from "rxjs/operators";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {GeneralError} from "../domain/generalError";

@Injectable({
    providedIn: 'root'
})
export class ForecastService {

    constructor(private http: HttpClient) {  }

    getForecasts(): Observable<Forecast[]> {
        return this.http.get<Forecast[]>(`/btsapi/api/forecast/all`)
            .pipe(
                shareReplay(1),
                catchError(this.handleHttpError)
            );
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
