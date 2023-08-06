import {Component, OnInit} from "@angular/core";
import {LeagueService} from "./league.service";
import {Subject} from "rxjs";
import {GeneralError} from "../domain/generalError";
import {FormBuilder, FormGroup} from "@angular/forms";
import * as _ from 'underscore';
import {League} from "../domain/league";
import {CountryService} from "./country.service";

@Component({
    selector: 'bts-league',
    templateUrl: './league.component.html'
})
export class LeagueComponent implements OnInit {

    private errorMessageSubject = new Subject<GeneralError>();
    errorMessage$ = this.errorMessageSubject.asObservable();

    error: GeneralError;
    leagueForm : FormGroup;

    selectedLeagues: string[];
    currentLeagues: League[];
    availableLeagues: string[];

    constructor(private leagueService : LeagueService, private countryService : CountryService,
                private fb: FormBuilder) {
    }

    ngOnInit(): void {
        this.leagueForm = this.fb.group({
            toSelectedLeagues: [''],
            toAvailableLeagues: ['']
        });
        this.selectedLeagues = [];
        this.availableLeagues = [];

        this.leagueService.leagueRefreshNeeded$
                .subscribe(() => {
                this.getLeaguesCurrentSeason();
            })
        this.getLeaguesCurrentSeason();
    }



    private getLeaguesCurrentSeason() {
        this.leagueService.selectedLeaguesWithCountries$
            .subscribe(
                (result: League[]) => this.currentLeagues = result
            );
    }


    handleToSelectedLeagues() {
        const leagueId = this.leagueForm.get('toSelectedLeagues').value;
        this.selectedLeagues = [];
        this.selectedLeagues = _.values(leagueId);
    }

    handleToAvailableLeagues() {
        const leagueId = this.leagueForm.get('toAvailableLeagues').value;
        this.availableLeagues = [];
        this.availableLeagues = _.values(leagueId);
    }

    updateToSelectedLeagues() {
        this.leagueService.updateToSelectedLeague(this.selectedLeagues)
            .subscribe(
            (result ) => {
                this.leagueService.selectedLeaguesUpdated(result);
            }
        );

    }

    updateToAvailableLeagues() {
        this.leagueService.updateToAvailableLeague(this.availableLeagues)
            .subscribe(
            (result: League[]) => {
                this.leagueService.availableLeaguesUpdated(result);
            }
        )
    }

    selectButtonDisabled(): boolean {
        return this.selectedLeagues.length === 0;
    }

    removeButtonDisabled(): boolean {
        return this.availableLeagues.length === 0;
    }



}
