import {League} from "./league";
import {Teams} from "./teams";

export interface Result {

    result_id: string;
    league: League;
    eventDate: Date;
    round: string;
    homeTeam: Teams;
    awayTeam:Teams;
    goalsHomeTeam:number;
    goalsAwayTeam:number;
    matchStatus:number;
}
