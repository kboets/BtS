import {Injectable} from "@angular/core";
import {Observable, throwError} from "rxjs";
import {League} from "../domain/league";
import {catchError, shareReplay, tap} from "rxjs/operators";
import {Rounds} from "../domain/rounds";
import {HttpClient, HttpErrorResponse, HttpParams} from "@angular/common/http";
import {GeneralError} from "../domain/generalError";
import {Result} from "../domain/result";
import * as _ from 'underscore';

@Injectable({
    providedIn: 'root'
})
export class ResultService {

    private roundMap = new Map([
        ["Regular_Season_-_1", "Ronde 1"],
        ["Regular_Season_-_2", "Ronde 2"],
        ["Regular_Season_-_3", "Ronde 3"],
        ["Regular_Season_-_4", "Ronde 4"],
        ["Regular_Season_-_5", "Ronde 5"],
        ["Regular_Season_-_6", "Ronde 6"],
        ["Regular_Season_-_7", "Ronde 7"],
        ["Regular_Season_-_8", "Ronde 8"],
        ["Regular_Season_-_9", "Ronde 9"],
        ["Regular_Season_-_10", "Ronde 10"],
        ["Regular_Season_-_11", "Ronde 11"],
        ["Regular_Season_-_12", "Ronde 12"],
        ["Regular_Season_-_13", "Ronde 13"],
        ["Regular_Season_-_14", "Ronde 14"],
        ["Regular_Season_-_15", "Ronde 15"],
        ["Regular_Season_-_16", "Ronde 16"],
        ["Regular_Season_-_17", "Ronde 17"],
        ["Regular_Season_-_18", "Ronde 18"],
        ["Regular_Season_-_19", "Ronde 19"],
        ["Regular_Season_-_20", "Ronde 20"],
        ["Regular_Season_-_21", "Ronde 21"],
        ["Regular_Season_-_22", "Ronde 22"],
        ["Regular_Season_-_23", "Ronde 23"],
        ["Regular_Season_-_24", "Ronde 24"],
        ["Regular_Season_-_25", "Ronde 25"],
        ["Regular_Season_-_26", "Ronde 26"],
        ["Regular_Season_-_27", "Ronde 27"],
        ["Regular_Season_-_28", "Ronde 28"],
        ["Regular_Season_-_29", "Ronde 29"],
        ["Regular_Season_-_30", "Ronde 30"],
        ["Regular_Season_-_31", "Ronde 31"],
        ["Regular_Season_-_32", "Ronde 32"],
        ["Regular_Season_-_33", "Ronde 33"],
        ["Regular_Season_-_34", "Ronde 34"],
    ]);

    constructor(private http: HttpClient) {  }


    getCurrentRoundForLeague(id: number): Observable<Rounds> {
        return this.http.get<Rounds>(`/bts/api/round/current/${id}`)
            .pipe(
                //tap(data => console.log('getCurrent round for league  '+id, JSON.stringify(data))),
                catchError(this.handleHttpError)
            );
    }

    getAllResultForLeague(id: number): Observable<Result[]> {
        return this.http.get<Result[]>(`/bts/api/result/all/${id}`)
            .pipe(
                //tap(data => console.log('get all result for league  '+id, JSON.stringify(data))),
                catchError(this.handleHttpError)
            );
    }

    getCurrentRound(round : string): string {
        console.log(this.roundMap.get(round));
        return this.roundMap.get(round);
    }


    private handleHttpError(error: HttpErrorResponse) {
        //console.log("entering the handleHttpError of league service "+error.message);
        let dataError = new GeneralError();
        dataError.errorNumber = error.status;
        dataError.errorMessage = error.message;
        dataError.userFriendlyMessage = "Er liep iets fout bij het ophalen van de round";
        return throwError(dataError);
    }
}
