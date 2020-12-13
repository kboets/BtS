import {League} from "./league";

export interface Rounds {
    roundId: string;
    round: string;
    season: number;
    current: boolean;
    currentDate: Date;
    leagueDto : League;
}
