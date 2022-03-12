import {Injectable} from "@angular/core";
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import {GeneralError} from "../domain/generalError";
import {Observable, throwError} from "rxjs";
import {Algorithm} from "../domain/algorithm";
import {catchError} from "rxjs/operators";

@Injectable({
    providedIn: 'root'
})
export class AlgorithmService {

    constructor(private http: HttpClient) {
    }


    getAlgorithms(): Observable<Algorithm[]> {
        return this.http.get<Algorithm[]>(`/btsapi/api/algorithm/all`, {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        }).pipe(catchError(AlgorithmService.handleHttpError))
    }

    saveAlgorithm(algorithm: Algorithm): Observable<Algorithm> {
        console.log('entered the save', algorithm);
        return this.http.post<Algorithm>(`/btsapi/api/algorithm/save`, algorithm, {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        }).pipe(catchError(AlgorithmService.handleHttpError));
    }

    private static handleHttpError(error: HttpErrorResponse) {
        console.log("entering the handle HttpError of algorithm service " + error.message);
        let dataError = new GeneralError();
        dataError.errorNumber = error.status;
        dataError.errorMessage = error.message;
        dataError.userFriendlyMessage = "Er liep iets fout bij algoritme";
        return throwError(dataError);
    }
}
