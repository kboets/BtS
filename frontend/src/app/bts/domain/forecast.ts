import {League} from "./league";
import {ForecastDetail} from "./forecastDetail";

export class Forecast {
    league: League;
    forecastType: string;
    forecastDetails: ForecastDetail[];
}
