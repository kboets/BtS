import {Injectable} from "@angular/core";
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import {GeneralError} from "../domain/generalError";
import {Observable, Subject, throwError} from "rxjs";
import {Algorithm} from "../domain/algorithm";
import {catchError, tap} from "rxjs/operators";

@Injectable({
    providedIn: 'root'
})
export class AlgorithmService {

    private readonly _algorithmRefreshNeeded$;
    algorithmRefreshAction$;


    get algorithmRefreshNeeded$(): Subject<Algorithm> {
        return this._algorithmRefreshNeeded$;
    }

    constructor(private http: HttpClient) {
        this._algorithmRefreshNeeded$ = new Subject<Algorithm>();
        this.algorithmRefreshAction$ = this._algorithmRefreshNeeded$.asObservable();

    }

    getAlgorithms(): Observable<Algorithm[]> {
        return this.http.get<Algorithm[]>(`/btsapi/api/algorithm/all`, {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        }).pipe(
            catchError(AlgorithmService.handleHttpError)
        )
    }

    getAllButCurrentAlgorithms(): Observable<Algorithm[]> {
        return this.http.get<Algorithm[]>(`/btsapi/api/algorithm/allNotCurrent`, {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        }).pipe(
            catchError(AlgorithmService.handleHttpError)
        )
    }

    getCurrentAlgorithm(): Observable<Algorithm> {
        return this.http.get<Algorithm>(`/btsapi/api/algorithm/current`, {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        }).pipe(
            catchError(AlgorithmService.handleHttpError)
        )
    }

    saveAlgorithm(algorithm: Algorithm): Observable<Algorithm> {
        return this.http.post<Algorithm>(`/btsapi/api/algorithm/save`, algorithm, {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        }).pipe(
            tap(() => {
                this._algorithmRefreshNeeded$.next()
            }),
            catchError(AlgorithmService.handleHttpError)
        );
    }

    updateAlgorithm(algorithm: Algorithm): Observable<Algorithm> {
        return this.http.post<Algorithm>(`/btsapi/api/algorithm/update`, algorithm, {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        }).pipe(
            tap(() => {
                this._algorithmRefreshNeeded$.next()
            }),
            catchError(AlgorithmService.handleHttpError)
        );
    }

    deleteAlgorithm(algorithm: Algorithm): Observable<boolean> {
        return this.http.post<boolean>(`/btsapi/api/algorithm/delete`, algorithm, {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        }).pipe(
            tap((result) => {
                this._algorithmRefreshNeeded$.next()
            }),
            catchError(AlgorithmService.handleHttpError)
        );
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
