import {League} from "./league";

export interface Round {
    roundId: string;
    round: string;
    season: number;
    current: boolean;
    currentDate: Date;
    leagueDto: League;
    playRound: string;
}
