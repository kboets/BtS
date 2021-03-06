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
import * as _ from 'underscore';
import {Round} from "../domain/round";
import {RoundService} from "../round/round.service";

@Component({
    selector: 'bts-admin',
    providers: [ConfirmationService,MessageService],
    templateUrl: './admin.component.html'
})
export class AdminComponent implements OnInit {

    selectedLeague: League;
    selectedLeagueRound: League;
    selectedSeasonAdmin: Admin;
    selectedRound: Round;
    private errorMessageSubject = new Subject<GeneralError>();
    errorMessage$ = this.errorMessageSubject.asObservable();
    adminDataList$: Observable<Admin[]>;
    allLeagues$: Observable<League[]>;
    allSeasons: Admin[] = [];
    currentSeason: number;
    selectedSeason: number;

    resultDeleted$: Observable<boolean>;
    seasonUpdated$: Observable<Admin>;
    selectedRoundUpdated$: Observable<Round>;

    columns: any[];

    constructor(private adminService: AdminService, private leagueService: LeagueService,
                private confirmationService: ConfirmationService, private roundService: RoundService) {}


    ngOnInit(): void {
        this.columns = [
            { field: 'adminKey', header: 'Type' },
            { field: 'value', header: 'value' },
            { field: 'date', header: 'Datum', data: true , format: `dd/MM/yyyy HH:mm` }
        ];


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
                this.selectedSeason = +data[0].value;
                this.setCurrentAndPreviousSeason(data[0]);
                this.adminService.historicDataNeedsUpdate$.next(this.selectedSeason);
            });

        //all admin data
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

    private setCurrentAndPreviousSeason(adminDBSeason: Admin) {
        this.allSeasons = [];
        if (+adminDBSeason.value  === this.currentSeason) {
            const previousSeason = this.currentSeason - 1;
            let adminPreviousSeason = {
                "adminKey": AdminKeys.SEASON,
                "value": previousSeason.toString(),
                "date": new Date()
            }
            this.allSeasons.push(adminPreviousSeason);
        } else {
            let adminNextSeason = {
                "adminKey": AdminKeys.SEASON,
                "value": this.currentSeason.toString(),
                "date": new Date()
            }
            this.allSeasons.push(adminNextSeason);
        }
        this.allSeasons.push(adminDBSeason);
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
        this.confirmationService.confirm({
            message: 'U staat op het punt om het seizoen te veranderen, bent u zeker ?',
            header: 'Bevestiging',
            icon: 'pi pi-info-circle',
            accept: () => {
                this.seasonUpdated$ = this.adminService.updateAdmin(this.selectedSeasonAdmin);
                this.adminService.historicDataNeedsUpdate$.next(+this.selectedSeasonAdmin.value);
            }

        });
    }

    updateRound() {
        this.selectedRoundUpdated$ = this.roundService.updateCurrentRound(+this.selectedRound.roundId, +this.selectedLeagueRound.league_id);
    }

    getCurrentRound(): Round {
        let rounds = this.selectedLeagueRound.roundDtos;
        let currentRound= _.findWhere(rounds , {"current": true});
        return currentRound;

    }

}
