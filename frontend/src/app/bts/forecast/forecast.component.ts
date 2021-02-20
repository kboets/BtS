import {Component, OnInit} from "@angular/core";
import {FormBuilder, FormGroup} from "@angular/forms";
import {SelectItem} from "primeng/api";
import {ResultsComponent} from "../results/results.component";
import {ResultService} from "../results/result.service";
import {GeneralError} from "../domain/generalError";
import {Result} from "../domain/result";
import {catchError, filter, first, map, tap} from "rxjs/operators";
import {BehaviorSubject, combineLatest, EMPTY, Observable, Subject} from "rxjs";
import * as _ from 'underscore';
import {League} from "../domain/league";
import {LeagueResults} from "./leagueResults";
import {Teams} from "../domain/teams";
import {Results4Team} from "./results4Team";
import {CriteriaResults} from "./criteriaResults";
import {Result4Team} from "./result4Team";
import {Criteria4TeamResult} from "./criteria4TeamResult";

@Component({
    selector: 'bts-forecast',
    templateUrl: './forecast.component.html'
})
export class ForecastComponent implements OnInit {

    forecastForm: FormGroup;
    totalGames: SelectItem[];
    selectionCriteria : Map<string, any>;


    private errorMessageSubject = new Subject<GeneralError>();
    errorMessage$ = this.errorMessageSubject.asObservable();

    private selectionChangedSubject = new Subject<Map<string, any>>();
    selectionChanged$ = this.selectionChangedSubject.asObservable();

    resultSelectedLeagues$ : Observable<LeagueResults[]>;
    resultNonSelectedLeagues$ : Observable<LeagueResults[]>;

    selectedLeagues$: Observable<Map<League, Result[]>>;
    nonSelectedLeagues$: Observable<Map<League,Result[]>>;

    constructor(private fb: FormBuilder, private resultService: ResultService) {
        this.totalGames = [];
        for (let i = 1; i < 11; i++) {
            this.totalGames.push({label: ''+i, value: i});
        }
        this.selectionCriteria = new Map<string, any>();
        this.forecastForm = this.fb.group({
            allLeagues: {value: false, disabled: false},
            resultGames: ['notLost', []],
            selectedGames: [1]
        });
        this.getChanges(this.forecastForm.value);
    }

    ngOnInit(): void {
        this.forecastForm.valueChanges.subscribe(
            value => this.getChanges(value)
        );

        //retrieve all the results from the selected leagues
        this.resultSelectedLeagues$ = this.resultService.getAllResultForLeagues(true)
            .pipe(
                catchError(err => {
                    this.errorMessageSubject.next(err);
                    return EMPTY;
                })
            );
        //retrieve all the results from the non selected leagues
        this.resultNonSelectedLeagues$ = this.resultService.getAllResultForLeagues(false)
            .pipe(
                catchError(err => {
                    this.errorMessageSubject.next(err);
                    return EMPTY;
                })
            );

        // filter the results based on the selection criteria
        combineLatest([this.resultSelectedLeagues$, this.selectionChanged$])
            .pipe(
               tap(()=> console.log("entered the combineLatest")),
                map(([leagueResults, criteriaMap]) => {
                    return this.mapLeagueResults(leagueResults, criteriaMap);
                }),
                //tap(data => console.log(JSON.stringify(data))),
                catchError(err => {
                    this.errorMessageSubject.next(err);
                    return EMPTY;
                })
            )
            .subscribe((data) => {
                console.log(data);
            });
    }

    getChanges(value: string) {
        let keys = Object.keys(value);
        this.selectionCriteria.clear();
        for(let key of keys) {
            this.selectionCriteria.set(key, value[key]);
        }
        //console.log('selection criteria', this.selectionCriteria);
        this.selectionChangedSubject.next(this.selectionCriteria);
    }

    private mapLeagueResults(leagueResults: LeagueResults[], criteriaMap: Map<string, any>): CriteriaResults[] {
        const selectedGames = criteriaMap.get('selectedGames');
        const resultGame = criteriaMap.get('resultGames');

        //1. reduce the number of results based on the selection criteria
        const filteredLeagueResults = _.filter(leagueResults, function (leagueResult) {
            const requestedResults = leagueResult.league.teamDtos.length/2 * selectedGames;
            let results = leagueResult.results.slice(0, requestedResults);
            leagueResult.results = [];
            leagueResult.results = results;
            return leagueResult;
        });

        //2. retrieve all teams who have not lost or won with their results
        const criteria4TeamResults = _.map(filteredLeagueResults, function (filteredLeagueResult: LeagueResults) {
            let criteria4TeamResult: Criteria4TeamResult = new Criteria4TeamResult();
            criteria4TeamResult.league = filteredLeagueResult.league;
            if(resultGame === 'won') {
                criteria4TeamResult.teamResults = ForecastComponent.getWinningTeams(filteredLeagueResult.results);
            } else {
                criteria4TeamResult.teamResults = ForecastComponent.getNotLostTeams(filteredLeagueResult.results);
            }
            return criteria4TeamResult;
        });

        let criteriaResults: CriteriaResults[] = [];

        //3.1 if only 1 game is needed, return it
        if(selectedGames === 1) {
            criteria4TeamResults.forEach(criteria4TeamResult => {
                let criteriaResult = new CriteriaResults();
                criteriaResult.league = criteria4TeamResult.league;
                criteriaResult.teamResults = [];
                criteria4TeamResult.teamResults.forEach(result => {
                    criteriaResult.teamResults.push(result);
                })
                criteriaResults.push(criteriaResult);
            });
            return criteriaResults;
        }
        //3.2 limit the results based on the requested games
        criteria4TeamResults.forEach(criteria4TeamResult => {
            let criteriaResult = new CriteriaResults();
            criteriaResult.league = criteria4TeamResult.league;
            criteriaResult.teamResults = ForecastComponent.getRequestedNumberOfTeams(criteria4TeamResult.teamResults, criteriaMap);
            criteriaResults.push(criteriaResult);
        });

        return criteriaResults;

    }

    public static getRequestedNumberOfTeams(result4Teams: Result4Team[], criteriaMap: Map<string, any>): Results4Team[] {
        const teamIds = _.unique(result4Teams.map(result4Team => result4Team.team.teamId));
        let selectedTeamIds: string[] = [];
        const selectedGames = criteriaMap.get('selectedGames');
        // verify the teams that fulfill the criteria
        teamIds.forEach(teamId => {
            let counter = 0;
            result4Teams.forEach(result4Team => {
                if(result4Team.team.teamId === teamId) {
                    counter++;
                }
                if(counter === selectedGames) {
                   selectedTeamIds.push(teamId);
                }
            });
        });

        let limitedResults4Teams : Results4Team[] = [];
        let uniqueSelectedTeamIds = _.unique(selectedTeamIds);
        uniqueSelectedTeamIds.forEach(teamId => {
            let selectedResult4Team = new Results4Team();
            selectedResult4Team.results = [];
            result4Teams.forEach(resultTeam => {
            if (resultTeam.team.teamId === teamId) {
                     selectedResult4Team.team = resultTeam.team;
                     selectedResult4Team.results.push(resultTeam.result);
                 }
            });
            limitedResults4Teams.push(selectedResult4Team);
        });
        return limitedResults4Teams;
    }

    public static getNotLostTeams(results: Result[]) : Result4Team[] {
        //console.log('getNotLostTeams ', results.length);
        let results4Teams : Result4Team[] = [];
        results.forEach(result => {
            if(result.goalsAwayTeam == result.goalsHomeTeam) {
                let result4Team = new Result4Team();
                result4Team.result = result;
                result4Team.team = result.homeTeam;
                results4Teams.push(result4Team);
                result4Team = new Result4Team();
                result4Team.result = result;
                result4Team.team = result.awayTeam;
                results4Teams.push(result4Team);
            } else {
                results4Teams.push(this.getWinningTeam(result));
            }
        })
        return results4Teams;
    }

    public static getWinningTeams(results: Result[]): Result4Team[] {
        let results4Teams : Result4Team[] = [];
        results.forEach(result => {
            results4Teams.push(this.getWinningTeam(result));
        });
        return results4Teams;
    }

    public static getWinningTeam(result: Result) : Result4Team {
        let result4Team = new Result4Team();
        if(result.goalsAwayTeam > result.goalsHomeTeam) {
            result4Team.team = result.awayTeam;
        } else if(result.goalsHomeTeam > result.goalsAwayTeam) {
            result4Team.team = result.homeTeam;
        }
        result4Team.result = result;
        return result4Team;
    }




}
