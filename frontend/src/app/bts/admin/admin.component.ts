import {Component, OnInit} from "@angular/core";
import {AdminService} from "./admin.service";
import {EMPTY, Observable, Subject} from "rxjs";
import {Admin} from "../domain/admin";
import {GeneralError} from "../domain/generalError";
import {catchError, map} from "rxjs/operators";
import {LeagueService} from "../league/league.service";
import {League} from "../domain/league";
import {ConfirmationService, MessageService} from "primeng/api";
import {AdminKeys} from "../domain/adminKeys";

@Component({
    selector: 'bts-admin',
    providers: [ConfirmationService,MessageService],
    templateUrl: './admin.component.html'
})
export class AdminComponent implements OnInit {

    selectedLeague: League;
    selectedSeason: Admin;
    private errorMessageSubject = new Subject<GeneralError>();
    errorMessage$ = this.errorMessageSubject.asObservable();
    adminDataList$: Observable<Admin[]>;
    allLeagues$: Observable<League[]>;
    allSeasons: Admin[] = [];
    currentSeason: number;

    resultDeleted$: Observable<boolean>;
    seasonUpdated$: Observable<Admin>;


    columns: any[];

    constructor(private adminService: AdminService, private leagueService: LeagueService, private confirmationService: ConfirmationService) {}


    ngOnInit(): void {
        this.columns = [
            { field: 'adminKey', header: 'Type' },
            { field: 'value', header: 'value' },
            { field: 'date', header: 'Datum', data: true , format: `dd/MM/yyyy HH:mm` }
        ];

        this.adminService.adminDatas$
            .pipe(
                map(admins => admins.filter(admin => admin.adminKey === AdminKeys.SEASON))
            )
            .subscribe((data) => {
                this.allSeasons = data;
                let admin = {
                    "adminKey": AdminKeys.SEASON,
                    "value": "2019",
                    "date": new Date()
                }
                this.allSeasons.push(admin);
            });

        this.adminDataList$ = this.adminService.adminDatas$
            .pipe(
                catchError(err => {
                    this.errorMessageSubject.next(err);
                    return EMPTY;
                })
            );
        this.allLeagues$ = this.leagueService.leagues$
            .pipe(
                catchError(err => {
                    this.errorMessageSubject.next(err);
                    return EMPTY;
                })
            );
    }

    deleteResults() {
        this.confirmationService.confirm({
            message: 'Verwijder al de resulaten? Dit kan niet ongedaan gemaakt worden',
            header: 'Bevestiging',
            icon: 'pi pi-info-circle',
            accept: () => {
                this.resultDeleted$ = this.adminService.deleteResults4League(this.selectedLeague.league_id);
            }

        });
    }

    updateSeason() {
        console.log('selected season ', this.selectedSeason.value);
        this.confirmationService.confirm({
            message: 'U staat op het punt om het seizoen te veranderen, bent u zeker ?',
            header: 'Bevestiging',
            icon: 'pi pi-info-circle',
            accept: () => {
                this.seasonUpdated$ = this.adminService.updateAdmin(this.selectedSeason);
            }

        });
    }

}
