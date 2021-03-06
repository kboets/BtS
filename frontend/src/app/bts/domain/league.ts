/** League entity */
import {Round} from "./round";
import {Teams} from "./teams";


export interface League {
    league_id: string;
    name: string;
    season: number;
    startSeason: Date;
    endSeason: Date;
    logo?:string;
    flag?:string;
    countryCode:string;
    country?:string
    isCurrent:boolean;
    selected:boolean;
    roundDtos: Round[];
    teamDtos: Teams[];
}
