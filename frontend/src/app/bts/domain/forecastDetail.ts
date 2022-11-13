import {Teams} from "./teams";
import {Result} from "./result";
import {ForecastResult} from "./forecastResult";

export class ForecastDetail {
    team: Teams;
    opponent: Teams;
    nextGame: Result;
    finalScore: number;
    homeScore: number;
    awayScore: number;
    opponentScore: number;
    forecastResult:ForecastResult;
    message: string;
    errorMessage: string;
}
