import {Component, OnInit} from "@angular/core";
import {FormBuilder, FormGroup} from "@angular/forms";
import {SelectItem, TreeNode} from "primeng/api";
import {ResultService} from "../results/result.service";
import {GeneralError} from "../domain/generalError";
import {Result} from "../domain/result";
import {catchError} from "rxjs/operators";
import {EMPTY, Subject} from "rxjs";
import * as _ from 'underscore';
import {LeagueResults} from "./leagueResults";
import {Results4Team} from "./results4Team";
import {CriteriaResults} from "./criteriaResults";

@Component({
    selector: 'bts-prospect',
    templateUrl: './prospect.component.html'
})
export class ProspectComponent implements OnInit {

    prospectForm: FormGroup;
    totalGames: SelectItem[];
    selectionCriteria: Map<string, any>;
    criteriaResultsSelectedLeagues: CriteriaResults[];
    criteriaResultsNonSelectedLeagues: CriteriaResults[];
    leaguesSelectedTree: TreeNode[];
    leaguesNonSelectedTree: TreeNode[];

    private errorMessageSubject = new Subject<GeneralError>();
    errorMessage$ = this.errorMessageSubject.asObservable();

    resultsSelectedLeagues: LeagueResults[];
    resultsNonSelectedLeagues: LeagueResults[];



    constructor(private fb: FormBuilder, private resultService: ResultService) {
        this.totalGames = [];
        for (let i = 1; i < 11; i++) {
            this.totalGames.push({label: '' + i, value: i});
        }
        this.selectionCriteria = new Map<string, any>();

    }

    ngOnInit(): void {
        this.prospectForm = this.fb.group({
            allLeagues: {value: false, disabled: false},
            resultGames: ['notLost', []],
            selectedGames: [1]
        });
        this.getChanges(this.prospectForm.value);

        this.prospectForm.valueChanges.subscribe(
            value => this.getChanges(value)
        );

        this.getResultsForSelectedLeague();
        this.getResultsForNonSelectedLeague();

        this.resultService.prospectRefreshNeededAction$
            .subscribe((data: Map<string, any>) => {
                this.criteriaResultsSelectedLeagues = this.mapLeagueResults(this.resultsSelectedLeagues, data);
                this.leaguesSelectedTree = this.createNodeTree(this.criteriaResultsSelectedLeagues);
            });

        this.resultService.prospectRefreshNonSelectedNeededAction$
            .subscribe((data: Map<string, any>) => {
                this.criteriaResultsNonSelectedLeagues = this.mapLeagueResults(this.resultsNonSelectedLeagues, data);
                this.leaguesNonSelectedTree = this.createNodeTree(this.criteriaResultsNonSelectedLeagues);
            });

    }

    expandAllSelected(){
        this.leaguesSelectedTree.forEach( node => {
            this.expandRecursive(node, true);
        } );
    }

    expandAllNonSelected(){
        this.leaguesNonSelectedTree.forEach( node => {
            this.expandRecursive(node, true);
        } );
    }

    collapseAllSelected(){
        this.leaguesSelectedTree.forEach( node => {
            this.expandRecursive(node, false);
        } );
    }

    collapseAllNonSelected(){
        this.leaguesNonSelectedTree.forEach( node => {
            this.expandRecursive(node, false);
        } );
    }

    private expandRecursive(node:TreeNode, isExpand:boolean){
        node.expanded = isExpand;
        if (node.children){
            node.children.forEach( childNode => {
                this.expandRecursive(childNode, isExpand);
            } );
        }
    }

    private getResultsForSelectedLeague() {
        this.resultService.getAllResultForLeagues(true)
            .pipe(
                catchError(err => {
                    this.errorMessageSubject.next(err);
                    return EMPTY;
                })
            ).subscribe((data) => {
            this.resultsSelectedLeagues = data;
        });
    }

    private getResultsForNonSelectedLeague() {
        this.resultService.getAllResultForLeagues(false)
            .pipe(
                catchError(err => {
                    this.errorMessageSubject.next(err);
                    return EMPTY;
                })
            ).subscribe((data) => {
            this.resultsNonSelectedLeagues = data;
        });
    }

    getChanges(value: string) {
        let keys = Object.keys(value);
        this.selectionCriteria.clear();
        for (let key of keys) {
            this.selectionCriteria.set(key, value[key]);
        }
        if(this.selectionCriteria.get('allLeagues')) {
            this.leaguesSelectedTree = [];
            this.resultService.prospectNonSelectedLeaguesRefreshNeeded.next(this.selectionCriteria);
        } else {
            this.leaguesNonSelectedTree = [];
            this.resultService.prospectSelectedLeaguesRefreshNeeded.next(this.selectionCriteria);
        }
    }

    public filterResultsOfLeagueResults(leagueResults: LeagueResults[], selectedGames: number): LeagueResults[] {
        return _.filter(leagueResults, function (leagueResult) {
            const requestedResults = leagueResult.league.teamDtos.length / 2 * selectedGames;
            let results = leagueResult.results.slice(0, requestedResults);
            leagueResult.results = [];
            leagueResult.results = results;
            return leagueResult;
        });
    }

    public retrieveAllWinningTeamsWithResult(leagueResults: LeagueResults[]): CriteriaResults[] {
        let criteriaResults: CriteriaResults[] = []
        const criteriaResult = _.map(leagueResults, function (leagueResult: LeagueResults) {
            let criteriaResult: CriteriaResults = new CriteriaResults();
            criteriaResult.league = leagueResult.league;
            criteriaResult.results4Team = ProspectComponent.getWinningTeams(leagueResult.results);
            criteriaResults.push(criteriaResult);
        });
        if (criteriaResult instanceof CriteriaResults) {
            criteriaResults.push(criteriaResult);
        }
        return criteriaResults;
    }

    public retrieveAllNotLosingTeamsWithResults(leagueResults: LeagueResults[]): CriteriaResults[] {
        let criteriaResults: CriteriaResults[] = []
        const criteriaResult = _.map(leagueResults, function (leagueResult: LeagueResults) {
            let criteriaResult: CriteriaResults = new CriteriaResults();
            criteriaResult.league = leagueResult.league;
            criteriaResult.results4Team = ProspectComponent.getNotLosingTeams(leagueResult.results);
            criteriaResults.push(criteriaResult);
        });
        if (criteriaResult instanceof CriteriaResults) {
            criteriaResults.push(criteriaResult);
        }
        return criteriaResults;
    }

    public filterCriteriaResults(criteriaResults: CriteriaResults[], selectedGames: number): CriteriaResults[] {
        let results4Team: Results4Team[];
        let filteredCriteriaResults: CriteriaResults[] = [];
        criteriaResults.forEach(criteriaResult => {
            results4Team = _.filter(criteriaResult.results4Team, function (result4Team) {
                return result4Team.results.length === selectedGames;
            });
            if (results4Team.length !== 0) {
                let filteredCriteriaResult: CriteriaResults = new CriteriaResults();
                filteredCriteriaResult.league = criteriaResult.league;
                filteredCriteriaResult.results4Team = results4Team;
                filteredCriteriaResults.push(filteredCriteriaResult);
            }
        });
        return filteredCriteriaResults;
    }

    public mapLeagueResults(originalLeagueResults: LeagueResults[], criteriaMap: Map<string, any>): CriteriaResults[] {
        const leagueResults: LeagueResults[] = JSON.parse(JSON.stringify(originalLeagueResults));
        const selectedGames = criteriaMap.get('selectedGames');
        //1. reduce the number of results based on the selection criteria
        const filteredLeagueResults = this.filterResultsOfLeagueResults(leagueResults, selectedGames);
        const resultGame = criteriaMap.get('resultGames');
        //2. retrieve all teams for the league who have not lost or won with their results
        let criteriaResults: CriteriaResults[]
        if (resultGame === 'won') {
            criteriaResults = this.retrieveAllWinningTeamsWithResult(filteredLeagueResults);
        } else if (resultGame === 'notLost') {
            criteriaResults = this.retrieveAllNotLosingTeamsWithResults(filteredLeagueResults);
        }
        //3. filter to match correct number of requested
        if (selectedGames === 1) {
            return criteriaResults;
        } else {
            return this.filterCriteriaResults(criteriaResults, +selectedGames);
        }
    }

    public static getWinningTeams(results: Result[]): Results4Team[] {
        let results4TeamArray: Results4Team[] = [];
        let results4Team: Results4Team;
        let teamMap: Map<string, Results4Team> = new Map<string, Results4Team>();
        results.forEach(result => {
            if (result.goalsAwayTeam > result.goalsHomeTeam) {
                if (teamMap.has(result.awayTeam.teamId)) {
                    results4Team = teamMap.get(result.awayTeam.teamId);
                    results4Team.results.push(result);
                } else {
                    results4Team = new Results4Team();
                    results4Team.team = result.awayTeam;
                    results4Team.results = [];
                    results4Team.results.push(result);
                    teamMap.set(result.awayTeam.teamId, results4Team)
                    results4TeamArray.push(results4Team);
                }
            } else if (result.goalsHomeTeam > result.goalsAwayTeam) {
                if (teamMap.has(result.homeTeam.teamId)) {
                    results4Team = teamMap.get(result.homeTeam.teamId);
                    results4Team.results.push(result);
                } else {
                    results4Team = new Results4Team();
                    results4Team.team = result.homeTeam;
                    results4Team.results = [];
                    results4Team.results.push(result);
                    teamMap.set(result.homeTeam.teamId, results4Team)
                    results4TeamArray.push(results4Team);
                }
            }
        });

        return results4TeamArray;
    }

    public static getNotLosingTeams(results: Result[]): Results4Team[] {
        let results4TeamArray: Results4Team[] = [];
        let results4Team: Results4Team;
        let teamMap: Map<string, Results4Team> = new Map<string, Results4Team>();
        results.forEach(result => {
            if (result.goalsAwayTeam >= result.goalsHomeTeam) {
                if (teamMap.has(result.awayTeam.teamId)) {
                    results4Team = teamMap.get(result.awayTeam.teamId);
                    results4Team.results.push(result);
                } else {
                    results4Team = new Results4Team();
                    results4Team.team = result.awayTeam;
                    results4Team.results = [];
                    results4Team.results.push(result);
                    teamMap.set(result.awayTeam.teamId, results4Team)
                    results4TeamArray.push(results4Team);
                }
            } else if (result.goalsHomeTeam >= result.goalsAwayTeam) {
                if (teamMap.has(result.homeTeam.teamId)) {
                    results4Team = teamMap.get(result.homeTeam.teamId);
                    results4Team.results.push(result);
                } else {
                    results4Team = new Results4Team();
                    results4Team.team = result.homeTeam;
                    results4Team.results = [];
                    results4Team.results.push(result);
                    teamMap.set(result.homeTeam.teamId, results4Team)
                    results4TeamArray.push(results4Team);
                }
            }
        });

        return results4TeamArray;
    }

    private createNodeTree(criteriaResultsToBeMapped: CriteriaResults[]): any {
        let parentNodes: any = [];
        criteriaResultsToBeMapped.forEach(criteriaResults => {
            parentNodes.push(this.createParentNodes(criteriaResults));
        });
        return parentNodes;
    }

    private createParentNodes(criteriaResults: CriteriaResults): any {
        let parentNode:any =  {
            "label": criteriaResults.league.name,
            //"data": criteriaResults.league,
            "expandedIcon": "pi pi-folder-open",
            "collapsedIcon": "pi pi-folder",
            "children": this.createTeamNodes(criteriaResults.results4Team)
        }
        return parentNode;
    }


    private createTeamNodes(results4Team: Results4Team[]) : any {
        let childNodeTree: any = [];
        results4Team.forEach(results4Team => {
            let teamNode: any = {
                "label": results4Team.team.name,
                //"data": results4Team.team,
                "expandedIcon": "pi pi-folder-open",
                "collapsedIcon": "pi pi-folder",
                "children": this.createResultNodes(results4Team.results)
            }
            childNodeTree.push(teamNode);
        });
        return childNodeTree;
    }

    private createResultNodes(results: Result[]) : any {
        let childNodeTree: any = [];
        results.forEach(result => {
            let resultNode: any = {
                "label": this.createResultNodeLabel(result),
                //"data": result,
                "expandedIcon": "pi pi-folder-open",
                "collapsedIcon": "pi pi-folder"
            }
            childNodeTree.push(resultNode);

        });
        return childNodeTree;
    }

    private createResultNodeLabel(result: Result): string {
        const labelTeam: string = result.homeTeam.name+' - '+result.awayTeam.name+' : '+result.goalsHomeTeam+' - '+result.goalsAwayTeam
        const labelRound: string = ' '+result.round+' ';
        const label: string = labelTeam+labelRound;
        return label;
    }

}
