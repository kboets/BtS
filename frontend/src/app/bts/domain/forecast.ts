import {League} from "./league";
import {ForecastDetail} from "./forecastDetail";
import {ForecastResult} from "./forecastResult";
import {Algorithm} from "./algorithm";

export class Forecast {
    id: number;
    league: League;
    round: number;
    season: number;
    forecastResult: ForecastResult;
    message: string;
    algorithmDto: Algorithm;
    forecastDetails: ForecastDetail[];
}
