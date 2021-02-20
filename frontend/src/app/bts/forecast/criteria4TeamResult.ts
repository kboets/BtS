import {League} from "../domain/league";
import {Results4Team} from "./results4Team";
import {Result4Team} from "./result4Team";

export class Criteria4TeamResult {
    league: League;
    teamResults: Result4Team[];
}
