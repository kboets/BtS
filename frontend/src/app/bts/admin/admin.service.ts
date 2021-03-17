import {Injectable} from "@angular/core";
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import {GeneralError} from "../domain/generalError";
import {Observable, throwError} from "rxjs";
import {catchError, tap} from "rxjs/operators";
import {Admin} from "../domain/admin";

@Injectable({
    providedIn: 'root'
})
export class AdminService {

    constructor(private http: HttpClient) {}

    adminDatas$ = this.http.get<Admin[]>(`/btsapi/api/admin/all`)
        .pipe(
            //tap(data => console.log('all admin data ', JSON.stringify(data))),
            catchError(this.handleHttpError)
        );

    private handleHttpError(error: HttpErrorResponse) {
        let dataError = new GeneralError();
        dataError.errorNumber = error.status;
        dataError.errorMessage = error.message;
        dataError.userFriendlyMessage = "Er liep iets fout bij het ophalen van de admin data";
        return throwError(dataError);
    }

    deleteResults4League(id: string): Observable<boolean> {
        return this.http.post<boolean>(`/btsapi/api/admin/deleteResults`, id, {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        });
    }
}
