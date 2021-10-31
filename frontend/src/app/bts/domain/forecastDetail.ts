import {Teams} from "./teams";
import {Result} from "./result";

export class ForecastDetail {
    team: Teams;
    nextOpponent: Teams;
    results: Result[];
    nextResult: Result;
    score: number;
    info: string;
}
