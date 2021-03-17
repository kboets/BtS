import {Component, OnInit} from "@angular/core";
import {AdminService} from "./admin.service";
import {EMPTY, Observable, Subject} from "rxjs";
import {Admin} from "../domain/admin";
import {GeneralError} from "../domain/generalError";
import {catchError} from "rxjs/operators";
import {LeagueService} from "../league/league.service";
import {League} from "../domain/league";
import {ConfirmationService, MessageService} from "primeng/api";

@Component({
    selector: 'bts-admin',
    providers: [ConfirmationService,MessageService],
    templateUrl: './admin.component.html'
})
export class AdminComponent implements OnInit {

    public selectedLeague: League;
    private errorMessageSubject = new Subject<GeneralError>();
    errorMessage$ = this.errorMessageSubject.asObservable();
    adminDataList$: Observable<Admin[]>;
    allLeagues$: Observable<League[]>;
    resultDeleted$: Observable<boolean>;


    columns: any[];

    constructor(private adminService: AdminService, private leagueService: LeagueService, private confirmationService: ConfirmationService, private messageService: MessageService) {}


    ngOnInit(): void {
        this.columns = [
            { field: 'adminKey', header: 'Type' },
            { field: 'value', header: 'value' },
            { field: 'date', header: 'Datum', data: true , format: `dd/MM/yyyy HH:mm` }
        ];
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
        console.log('selected league ', this.selectedLeague.name);
        this.confirmationService.confirm({
            message: 'Verwijder al de resulaten? Dit kan niet ongedaan gemaakt worden',
            header: 'Bevestiging',
            icon: 'pi pi-info-circle',
            accept: () => {
                this.resultDeleted$ = this.adminService.deleteResults4League(this.selectedLeague.league_id);
            }

        });
    }

}
