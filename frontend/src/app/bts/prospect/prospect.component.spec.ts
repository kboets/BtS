import {ProspectComponent} from "./prospect.component";
import {ComponentFixture, TestBed} from "@angular/core/testing";
import {League} from "../domain/league";
import {Result} from "../domain/result";
import {Round} from "../domain/round";
import {Teams} from "../domain/teams";
import {ProspectObjectGenerator} from "./prospectObjectGenerator";
import {FormBuilder, FormGroup, FormsModule, NG_VALUE_ACCESSOR, ReactiveFormsModule} from "@angular/forms";

import {HttpClientTestingModule} from "@angular/common/http/testing";
import {ResultService} from "../results/result.service";
import {LeagueResults} from "./leagueResults";
import {of} from "rxjs";
import {CUSTOM_ELEMENTS_SCHEMA, forwardRef} from "@angular/core";
import {Panel} from "primeng/panel";
import {AppMainComponent} from "../../app.main.component";
import {InputSwitchModule} from "primeng/inputswitch";
import {RadioButtonModule} from "primeng/radiobutton";
import {DropdownModule} from "primeng/dropdown";
import {CriteriaResults} from "./criteriaResults";


describe('Prospect component', () => {
    let component: ProspectComponent, mockResultService;
    let fixture: ComponentFixture<ProspectComponent>;
    let league: League, results: Result[], rounds: Round[], teams: Teams[], leagueResults: LeagueResults[];

    beforeEach(()=> {
        teams = ProspectObjectGenerator.teams;
        rounds = ProspectObjectGenerator.rounds;
        league = ProspectObjectGenerator.league;
        league.teamDtos = teams;
        results = ProspectObjectGenerator.results;
        leagueResults = [{
            league:  league,
            results: results
        }]
        mockResultService = jasmine.createSpyObj(['getAllResultForLeagues']);

    TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, InputSwitchModule, RadioButtonModule, DropdownModule, HttpClientTestingModule],
        declarations: [ProspectComponent]
    }).compileComponents();

    });

    beforeEach(() => {
        fixture = TestBed.createComponent(ProspectComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('it should be created', () => {
        expect(component).toBeTruthy();
    });

    describe('mapLeagueResults -> test filterResultsOfLeagueResults', function () {
        it('Given one play game, should limit the results size to 9 results', () => {
            const updatedLeagueResult = component.filterResultsOfLeagueResults(leagueResults, 1);
            expect(updatedLeagueResult[0].results.length).toBe(9);
        });
        it('Given 2 game plays, should limit the results size to 18 results', () => {
            const updatedLeagueResult = component.filterResultsOfLeagueResults(leagueResults, 2);
            expect(updatedLeagueResult[0].results.length).toBe(18);
        });
    });

    describe('mapLeagueResults -> test retrieveAllWinning and retrieveNotLosingTeamsWithResults', function () {
        it('Given 9 results, retrieve all winning teams', () => {
            const updatedLeagueResult = component.filterResultsOfLeagueResults(leagueResults, 1);
            expect(updatedLeagueResult[0].results.length).toBe(9);
            const criteriaResults = component.retrieveAllWinningTeamsWithResult(updatedLeagueResult);
            expect(criteriaResults.length).toEqual(1);
            const results4Team = criteriaResults[0].results4Team;
            results4Team.forEach(results4Team => {
                const winningTeam = results4Team.team;
                const results = results4Team.results;
                results.forEach(result => {
                    if(result.goalsHomeTeam > result.goalsAwayTeam) {
                        expect(result.homeTeam.teamId === winningTeam.teamId);
                    } else if(result.goalsAwayTeam > result.goalsHomeTeam) {
                        expect(result.awayTeam.teamId === result.homeTeam.teamId)
                    }
                })
            });
        });
        it('Given 9 results, retrieve all non losing teams', () => {
            const updatedLeagueResult = component.filterResultsOfLeagueResults(leagueResults, 1);
            expect(updatedLeagueResult[0].results.length).toBe(9);
            const criteriaResults = component.retrieveAllNotLosingTeamsWithResults(updatedLeagueResult);
            expect(criteriaResults.length).toEqual(1);
            const results4Team = criteriaResults[0].results4Team;
            results4Team.forEach(results4Team => {
                const notLosingTeam = results4Team.team;
                const results = results4Team.results;
                results.forEach(result => {
                    if(result.goalsHomeTeam >= result.goalsAwayTeam) {
                        expect(result.homeTeam.teamId === notLosingTeam.teamId);
                    } else if(result.goalsAwayTeam >= result.goalsHomeTeam) {
                        expect(result.awayTeam.teamId === result.homeTeam.teamId)
                    }
                })
            });
        });

        it('Given 18 results, retrieve all winning teams', () => {
            const updatedLeagueResult = component.filterResultsOfLeagueResults(leagueResults, 2);
            expect(updatedLeagueResult[0].results.length).toBe(18);
            const criteriaResults = component.retrieveAllWinningTeamsWithResult(updatedLeagueResult);
            expect(criteriaResults.length).toEqual(1);
            const results4Team = criteriaResults[0].results4Team;
            results4Team.forEach(results4Team => {
                const winningTeam = results4Team.team;
                const results = results4Team.results;
                results.forEach(result => {
                    if(result.goalsHomeTeam > result.goalsAwayTeam) {
                        expect(result.homeTeam.teamId === winningTeam.teamId);
                    } else if(result.goalsAwayTeam > result.goalsHomeTeam) {
                        expect(result.awayTeam.teamId === result.homeTeam.teamId)
                    }
                })
            });
        });
    });

    describe('mapLeagueResults -> test retrieveAllWinning and retrieveNotLosingTeamsWithResults', function () {

    });



});



