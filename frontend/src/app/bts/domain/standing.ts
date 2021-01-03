import {SubStanding} from "./subStanding";
import {Teams} from "./teams";

export interface Standing {
    team: Teams;
    rank: number;
    points:number;
    lastUpdate:Date;
    homeSubStanding?:SubStanding;
    awaySubStanding?:SubStanding;
    allSubStanding?:SubStanding;
}
