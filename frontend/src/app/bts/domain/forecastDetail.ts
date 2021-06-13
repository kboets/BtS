import {Teams} from "./teams";
import {Result} from "./result";
import {TeamPerformanceQualifier} from "./teamPerformanceQualifier";

export class ForecastDetail {
    team: Teams;
    nextOpponent: Teams;
    results: Result[];
    nextResult: Result;
    teamPerformanceQualifier: TeamPerformanceQualifier;
    score: number;
}
