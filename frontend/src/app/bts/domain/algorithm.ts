export interface Algorithm {
    algorithm_id: number;
    type: string,
    name: string,
    current: boolean;
    threshold: number;
    homeWin: number;
    homeDraw: number;
    homeLost: number;
    homeBonus: number;
    awayWin: number;
    awayDraw: number;
    awayLost: number;
    awayMalus: number;
}
