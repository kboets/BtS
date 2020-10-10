import {Component, OnInit} from "@angular/core";
import {LeagueService} from "./league.service";
import {catchError, map, tap} from "rxjs/operators";
import {combineLatest, EMPTY} from "rxjs";
import {GeneralError} from "../domain/generalError";
import {FormBuilder, FormGroup} from "@angular/forms";
import * as _ from 'underscore';
import {element} from "protractor";
import {League} from "../domain/league";
import {CountryService} from "./country.service";

@Component({
    selector: 'bts-league',
    templateUrl: './league.component.html',
    styleUrls: ['./league.component.css'],
})
export class LeagueComponent implements OnInit {

    error: GeneralError;
    leagueForm : FormGroup;

    selectedLeagues: string[];
    currentLeagues: League[];
    availableLeagues: string[];

    constructor(private leagueService : LeagueService, private countryService : CountryService, private fb: FormBuilder) {
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
        this.leagueService.getLeaguesCurrentSeason()
            .subscribe(
                (result: League[]) => this.currentLeagues = result
            );
    }

    // leagues$ = this.leagueService.leagues$
    //     .pipe(
    //         catchError(err => {
    //             this.error = err;
    //             return EMPTY;
    //         })
    //     );

    // selectedLeaguesWithCountries$ = this.leagueService.selectedLeaguesWithCountries$
    //     .pipe(
    //         catchError(err => {
    //             this.error = err;
    //             return EMPTY;
    //         })
    //     );

    handleToSelectedLeagues() {
        const leagueId = this.leagueForm.get('toSelectedLeagues').value;
        //console.log('to selectable league ' +leagueId);
        this.selectedLeagues = [];
        this.selectedLeagues = _.values(leagueId);
    }

    handleToAvailableLeagues() {
        const leagueId = this.leagueForm.get('toAvailableLeagues').value;
        //console.log('to availalable league ' +leagueId);
        this.availableLeagues = [];
        this.availableLeagues = _.values(leagueId);
        //console.log(this.availableLeagues);
    }

    updateToSelectedLeagues() {
        //console.log('update to selected leagues..'+this.selectedLeagues);
        this.leagueService.updateToSelectedLeague(this.selectedLeagues)
            .subscribe(
            (result ) => {
                this.leagueService.selectedLeaguesUpdated(result);
            }
        );

    }

    updateToAvailableLeagues() {
        //console.log('update to available leagues..'+this.availableLeagues);
        this.leagueService.updateToAvailableLeague(this.availableLeagues)
            .subscribe(
            (result: League[]) => {
                this.leagueService.availableLeaguesUpdated(result);
            }
        )
    }

    private selectButtonDisabled(): boolean {
        return this.selectedLeagues.length === 0;
    }

    private removeButtonDisabled(): boolean {
        return this.availableLeagues.length === 0;
    }



}
