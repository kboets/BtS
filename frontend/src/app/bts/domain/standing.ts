import {SubStanding} from "./subStanding";

export interface Standing {
    league_id:string;
    team_id:string;
    rank: number;
    points:number;
    lastUpdate:Date;
    homeSubStanding?:SubStanding;
    awaySubStanding?:SubStanding;
    allSubStanding?:SubStanding;
}
