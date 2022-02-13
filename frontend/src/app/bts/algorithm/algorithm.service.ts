import {Injectable} from "@angular/core";
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import {GeneralError} from "../domain/generalError";
import {Observable, throwError} from "rxjs";
import {Forecast} from "../domain/forecast";
import {Algorithm} from "../domain/algorithm";

@Injectable({
    providedIn: 'root'
})
export class AlgorithmService {

    constructor(private http: HttpClient) {  }


    getAlgorithms() : Observable<Algorithm[]> {
        return this.http.get<Algorithm[]>(`/btsapi/api/algorithm/all`, {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        })
    }

    private handleHttpError(error: HttpErrorResponse) {
        console.log("entering the handle HttpError of algorithm service "+error.message);
        let dataError = new GeneralError();
        dataError.errorNumber = error.status;
        dataError.errorMessage = error.message;
        dataError.userFriendlyMessage = "Er liep iets fout bij het ophalen van de forecast";
        return throwError(dataError);
    }
}
