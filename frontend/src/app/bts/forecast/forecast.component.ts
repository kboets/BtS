import {Component, OnInit} from "@angular/core";
import {BehaviorSubject, combineLatest, EMPTY, Observable, Subject} from "rxjs";
import {GeneralError} from "../domain/generalError";
import {Forecast} from "../domain/forecast";
import {ForecastService} from "./forecast.service";
import {catchError, map} from "rxjs/operators";
import {League} from "../domain/league";
import {ForecastDetail} from "../domain/forecastDetail";
import * as _ from 'underscore';
import {AdminService} from "../admin/admin.service";
import {AdminKeys} from "../domain/adminKeys";

@Component({
    selector: 'bts-forecast',
    templateUrl: './forecast.component.html'
})
export class ForecastComponent implements OnInit {

    private errorMessageSubject = new Subject<GeneralError>();
    errorMessage$ = this.errorMessageSubject.asObservable();

    public forecastData: Forecast[];
    public forecastDetails: ForecastDetail[];
    public isHistoricData: boolean;
    // sorting and expanding on table
    public isExpanded: boolean;
    public expandedRows = {};
    public temDataLength: number;

    currentSeason: number;
    displayScoreInfo: boolean;
    selectedForecastLeague: League;
    selectedForecastDetail: ForecastDetail;
    rowGroupMetadata: any;

    constructor(private forecastService: ForecastService, private adminService: AdminService) {
        this.forecastDetails = [];
        this.forecastData = [];
        this.displayScoreInfo = false;
        this.isExpanded = false;
        this.temDataLength = 0;
    }

    ngOnInit(): void {
        this.getDefaultForecast();

        //retrieve the current season as number.
        this.adminService.currentSeason$.subscribe((data) => {
            this.currentSeason = data;
        });

        // retrieve selected admin season from DB
        this.adminService.adminDatas$
            .pipe(
                map(admins => admins.filter(admin => admin.adminKey === AdminKeys.SEASON))
            )
            .subscribe((data) => {
                this.adminService.historicDataNeedsUpdate$.next(+data[0].value);
            });

        this.adminService.isHistoricData$
            .subscribe((data) => {
                this.isHistoricData = data;
            })
    }

    onSort() {
        this.updateRowGroupMetaData();
    }

    updateRowGroupMetaData() {
        this.rowGroupMetadata = {};
        if (this.forecastData) {
            for (let i = 0; i < this.forecastData.length; i++) {
                let rowData = this.forecastData[i];
                let leagueName = rowData.league.name;
                if (i == 0) {
                    this.rowGroupMetadata[leagueName] = { index: 0, size: 1 };
                } else {
                    let previousRowData = this.forecastData[i - 1];
                    let previousRowGroup = previousRowData.league.name;
                    if (leagueName === previousRowGroup)
                        this.rowGroupMetadata[leagueName].size++;
                    else
                        this.rowGroupMetadata[leagueName] = { index: i, size: 1 };
                }
            }
        }
    }

    expandAll() {
        if(!this.isExpanded){
            this.forecastData.forEach(data =>{
                this.expandedRows[data.league.name] = true;
            })
        } else {
            this.expandedRows={};
        }
        this.isExpanded = !this.isExpanded;
    }

    onRowExpand() {
        //console.log("row expanded", Object.keys(this.expandedRows).length);
        if(Object.keys(this.expandedRows).length === this.temDataLength){
            this.isExpanded = true;
        }
        //console.log(this.expandedRows);
    }
    onRowCollapse() {
        //console.log("row collapsed",Object.keys(this.expandedRows).length);
        if(Object.keys(this.expandedRows).length === 0){
            this.isExpanded = false;
        }
    }

    showScoreInfo(forecastDetail: ForecastDetail) {
        this.displayScoreInfo = true;
        this.selectedForecastDetail = forecastDetail;
    }

    private getDefaultForecast(): Forecast[] {
        this.forecastService.getForecasts()
            .pipe(
                catchError(err => {
                    this.errorMessageSubject.next(err);
                    return EMPTY;
                })
            ).subscribe(data => {
            this.forecastData = data;
        });
        return this.forecastData;
    }

}
