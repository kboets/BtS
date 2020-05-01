import {League} from "./league";

export interface Teams {
    teamId: string;
    name: string;
    logo: string;
    LeagueDto: League;
    stadiumName: string;
    stadiumCapacity: number;
    city: string;
}
