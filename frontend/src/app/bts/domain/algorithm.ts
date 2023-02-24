import {AlgorithmPoints} from "./algorithmPoints";

export interface Algorithm {
    algorithm_id: number;
    type: string,
    name: string,
    current: boolean;
    threshold: number;
    homePoints: AlgorithmPoints;
    homeBonus: number;
    awayPoints: AlgorithmPoints;
    awayMalus: number;
    booster: number;

}
