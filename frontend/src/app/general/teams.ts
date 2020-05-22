import {League} from "./league";
import {Standing} from "./standing";

export interface Teams {
    teamId: string;
    name: string;
    logo: string;
    LeagueDto: League;
    stadiumName: string;
    stadiumCapacity: number;
    city: string;
    standing?: Standing;
}
