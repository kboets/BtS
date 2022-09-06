import {Component, OnInit} from "@angular/core";
import {EMPTY, Observable, Subject} from "rxjs";
import {GeneralError} from "../domain/generalError";
import {Forecast} from "../domain/forecast";
import {ForecastService} from "./forecast.service";
import {catchError, map} from "rxjs/operators";
import {League} from "../domain/league";
import {ForecastDetail} from "../domain/forecastDetail";
import * as _ from 'underscore';
import {AdminService} from "../admin/admin.service";
import {AdminKeys} from "../domain/adminKeys";
import {Table} from "primeng/table";
import {Teams} from "../domain/teams";

@Component({
    selector: 'bts-forecast',
    templateUrl: './forecast.component.html',
    styleUrls:['./forecast.css']
})
export class ForecastComponent implements OnInit {

    private errorMessageSubject = new Subject<GeneralError>();
    errorMessage$ = this.errorMessageSubject.asObservable();

    public forecastData: Forecast[];
    private forecastData$ : Observable<Forecast[]>;
    public forecastDetails: ForecastDetail[];
    public isHistoricData: boolean;
    // sorting and expanding on table
    public isExpanded: boolean;
    public expandedRows = {};
    public temDataLength: number;
    // filtering the score
    public scores: any[];
    public selectedScores: any[];
    private previousScores: any[];

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
        this.selectedScores = [];
        this.previousScores = [];
        this.scores =[
            {value: 50, label: '< 50'},
            {value: 100, label: '50 > 100'},
            {value: 150, label: '100 > 150'},
            {value: 500, label: '> 150'}
        ]
    }


    ngOnInit(): void {
        this.forecastService.forecastRefreshNeeded$.subscribe(() => {
            this.getForecastData();
        })

        this.getForecastData();

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

    isSameHomeTeam(forecastDetail: ForecastDetail): boolean {
        return this.isSameTeam(forecastDetail.team, forecastDetail.nextResult.homeTeam);
    }

    isSameAwayTeam(forecastDetail: ForecastDetail) : boolean {
        return this.isSameTeam(forecastDetail.team, forecastDetail.nextResult.awayTeam);
    }

    private isSameTeam(detailTeam: Teams, otherTeam: Teams): boolean {
        //console.log('detail team ', detailTeam.name, ' other team ', otherTeam.name);
        return detailTeam.name === otherTeam.name;

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
        if(Object.keys(this.expandedRows).length === this.temDataLength){
            this.isExpanded = true;
        }
    }
    onRowCollapse() {
        if(Object.keys(this.expandedRows).length === 0){
            this.isExpanded = false;
        }
    }

    showScoreInfo(forecastDetail: ForecastDetail) {
        this.displayScoreInfo = true;
        this.selectedForecastDetail = forecastDetail;
    }

    clear(table: Table) {
        table.clear();
        this.selectedScores = [];
        this.forecastService.forecastRefreshNeeded$.next();
    }

    getRequestedScoreValue() {
        if(_.isEqual(this.selectedScores, this.previousScores) === false) {
            this.forecastService.forecastRefreshNeeded$.next();
            this.previousScores = this.selectedScores.slice();
        }
    }

    private getForecastData() {
        this.forecastService.getFilteredForecasts(this.selectedScores)
            .pipe(
                catchError(err => {
                    this.errorMessageSubject.next(err);
                    return EMPTY;
                })
            ).subscribe((result: Forecast[]) => this.forecastData = result);
    }
}
