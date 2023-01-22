import {ForecastDetail} from "../domain/forecastDetail";
import {Teams} from "../domain/teams";

export class ForecastUtility {
    protected static forecastUtility : ForecastUtility;

    public isSameHomeTeam(forecastDetail: ForecastDetail): boolean {
        return ForecastUtility.isSameTeam(forecastDetail.team, forecastDetail.nextGame.homeTeam);
    }

    static isSameTeam(detailTeam: Teams, otherTeam: Teams): boolean {
        return detailTeam.name === otherTeam.name;
    }

    public isSameAwayTeam(forecastDetail: ForecastDetail) : boolean {
        return ForecastUtility.isSameTeam(forecastDetail.team, forecastDetail.nextGame.awayTeam);
    }

    static getInstance() : ForecastUtility{
        if(!this.forecastUtility){
            this.forecastUtility=new ForecastUtility();
        }
        return this.forecastUtility;
    }
}
