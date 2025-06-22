import {Component, OnInit} from "@angular/core";
import {AdminService} from "./admin.service";
import {combineLatest, EMPTY, Observable, Subject} from "rxjs";
import {Admin} from "../domain/admin";
import {GeneralError} from "../domain/generalError";
import {catchError, map, tap} from "rxjs/operators";
import {LeagueService} from "../league/league.service";
import {League} from "../domain/league";
import {ConfirmationService, MessageService} from "primeng/api";
import {AdminKeys} from "../domain/adminKeys";
import * as _ from 'underscore';
import {Round} from "../domain/round";
import {RoundService} from "../round/round.service";
import {Forecast} from "../domain/forecast";
import {ForecastService} from "../forecast/forecast.service";

@Component({
    selector: 'bts-admin',
    providers: [ConfirmationService,MessageService],
    templateUrl: './admin.component.html'
})
export class AdminComponent implements OnInit {

    private errorMessageSubject = new Subject<GeneralError>();
    errorMessageAction$ = this.errorMessageSubject.asObservable();
    private forecastLeagueSubject = new Subject<League>();
    selectedForecastLeagueAction = this.forecastLeagueSubject.asObservable();
    public forecastForRoundAndLeague: Forecast;
    private selectedForecastSubject = new Subject<Forecast>();

    selectedLeague: League;
    selectedLeagueRound: League;
    selectedLeagueStanding: League;
    selectedLeagueDelete: League;
    selectedSeasonAdmin: Admin;
    selectedRound: Round;
    adminDataList$: Observable<Admin[]>;
    allLeagues$: Observable<League[]>;
    allSelectedLeague$: Observable<League[]>;
    allForecasts$: Observable<Forecast[]>;
    forecastLeagues$: Observable<League[]>;
    selectedForecastLeague: League;
    forecastLeagueAndRounds$: Observable<Forecast[]>;
    selectedForecastRoundLeague: Forecast;


    allSeasons: Admin[] = [];
    currentSeason: number;
    selectedSeason: number;


    showResultMessage: boolean;
    showStandingMessage: boolean;
    showLeagueMessage:boolean;
    showForecastMessage:boolean;
    seasonUpdated$: Observable<Admin>;
    selectedRoundUpdated$: Observable<Round>;

    columns: any[];

    constructor(private adminService: AdminService, private leagueService: LeagueService,
                private confirmationService: ConfirmationService, private roundService: RoundService,
                private forecastService: ForecastService) {
        this.showResultMessage = false;
        this.showStandingMessage = false;
        this.showLeagueMessage = false;
        this.showForecastMessage = false;
    }


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

        // all admin data
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

        this.allSelectedLeague$ = this.leagueService.leaguesSelected$
            .pipe(
                catchError(err => {
                    this.errorMessageSubject.next(err);
                    return EMPTY;
                })
            );

        // get all the forecasts
       this.initForecastData();
    }

    public onForecastLeagueChange() {
        this.selectedForecastRoundLeague = null;
        this.forecastLeagueSubject.next(this.selectedForecastLeague);
    }

    private initForecastData() {
        this.allForecasts$ = this.forecastService.getAllForecastsCurrentSeason()
            .pipe(catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            }));

        // get all unique leagues from forecast data
        this.forecastLeagues$ = this.allForecasts$.pipe(
            map(forecasts => {
                return _.map(forecasts, function (forecast) {
                    return forecast.league;
                });
            }),
            map(leagues => {
                return _.uniq(leagues, league => league.name);
            })
        ).pipe(catchError(err => {
            this.errorMessageSubject.next(err);
            return EMPTY;
        }));

        //get forecast for selected league and round
        this.forecastLeagueAndRounds$ = combineLatest(
            [this.allForecasts$, this.selectedForecastLeagueAction]
        ).pipe(
            map(([forecasts, league]) => {
                return _.filter(forecasts, function (forecast) {
                    if (forecast.league.name === league.name) {
                        return forecast;
                    }
                })
            }),
            catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            }));
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
                this.adminService.deleteResults4League(this.selectedLeague.league_id)
                    .subscribe((data) => {
                        this.showResultMessage = true;
                        this.removeAcknowledgeMessage();
                    });

            }

        });
    }

    deleteStanding() {
        this.confirmationService.confirm({
            message: 'Verwijder al de standen ? Dit kan niet ongedaan gemaakt worden',
            header: 'Bevestiging',
            icon: 'pi pi-info-circle',
            accept: () => {
                this.adminService.deleteStandings4League(this.selectedLeagueStanding.league_id)
                    .subscribe((data) => {
                        this.showStandingMessage = true;
                        this.removeAcknowledgeMessage();
                    })
            }

        });
    }

    deleteLeague() {
        this.confirmationService.confirm({
            message: 'Verwijder deze competitie ? Dit kan niet ongedaan gemaakt worden',
            header: 'Bevestiging',
            icon: 'pi pi-info-circle',
            accept: () => {
                this.adminService.deleteLeague(this.selectedLeagueDelete.league_id)
                    .subscribe((data) => {
                        this.showLeagueMessage = true;
                        this.removeAcknowledgeMessage();
                    })
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

    deleteForecast() {
        this.confirmationService.confirm({
            message: 'Verwijder deze forecast ? Dit kan niet ongedaan gemaakt worden',
            header: 'Bevestiging',
            icon: 'pi pi-info-circle',
            accept: () => {
                this.adminService.deleteForecast(this.selectedForecastRoundLeague.id)
                    .subscribe((data) => {
                        this.showForecastMessage = true;
                        this.initForecastData();
                        this.removeAcknowledgeMessage();
                    })
            }

        });
    }

    sortRounds() {
        this.selectedLeagueRound.roundDtos.sort((a,b) => +a.playRound - +b.playRound);
    }

    getCurrentRound(): Round {
        this.sortRounds();
        let rounds = this.selectedLeagueRound.roundDtos;
        return  _.findWhere(rounds , {"current": true});
    }

    private removeAcknowledgeMessage() {
        setTimeout(() => {
            this.showStandingMessage = false;
            this.showResultMessage = false;
            this.showForecastMessage = false;
            this.selectedLeagueStanding = null;
            this.selectedLeague = null;
            this.selectedForecastLeague = null;
            this.selectedForecastRoundLeague = null;
        }, 5000);
    }



}
