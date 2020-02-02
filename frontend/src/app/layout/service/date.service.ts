import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {Observable, throwError} from "rxjs";
import {GeneralError} from "../../general/generalError";
import {catchError} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class DateService {

  constructor(private http: HttpClient) { }

  getCurrentTime() : Observable<Date | GeneralError> {
      return this.http.get<Date>("/bts/api/current")
          .pipe(
              catchError(err => this.handleHttpError(err))
          );
  }

  private handleHttpError(error: HttpErrorResponse) : Observable<GeneralError>{
        let dataError = new GeneralError();
        dataError.errorNumber = error.status;
        dataError.errorMessage = error.message;
        dataError.userFriendlyMessage = "Er liep iets fout bij het ophalen van de data";
        return throwError(dataError);
    }

}
