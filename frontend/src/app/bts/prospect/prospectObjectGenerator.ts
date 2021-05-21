import {Teams} from "../domain/teams";
import {League} from "../domain/league";
import {Round} from "../domain/round";
import {Result} from "../domain/result";
import * as _ from 'underscore';

export class ProspectObjectGenerator {

    private static _league: League;
    private static _teams: Teams[];
    private static _rounds: Round[];
    private static _results: Result[];

    static get league(): League {
        if(this._league === null || this._league === undefined) {
            this._league = this.generateLeague();
        }
        return this._league;
    }

    static get teams(): Teams[] {
        if(this._teams === null || this._teams === undefined) {
            this._teams = this.generateTeams();
        }
        return this._teams;
    }

    static get rounds(): Round[] {
        if(this._rounds === null || this._rounds === undefined) {
            this._rounds = this.generateRounds();
        }
        return this._rounds;
    }

    static get results(): Result[] {
        if(this._results === null || this._results === undefined) {
            this._results = this.generateResults();
        }
        return this._results;
    }


    private static findTeamById(teamId: string) : Teams {
        const teamFound = _.find(this.teams, function (team) {
            return team.teamId === teamId;
        });
        return teamFound;
    }

     private static generateTeams(): Teams[] {
        return [
            {
                city: "Anvers",
                LeagueDto: this.league,
                logo:'',
                name: "Beerschot Wilrijk",
                stadiumCapacity: 13405,
                stadiumName: "Olympisch Stadion",
                standing: null,
                teamId: "263",
            },
            {
                city: "Heverlee",
                LeagueDto: this.league,
                logo: "https://media.api-sports.io/football/teams/260.png",
                name: "OH Leuven",
                stadiumCapacity: 12500,
                stadiumName: "King Power at Den Dreef Stadion",
                standing: null,
                teamId: "260"
            },
            {
                city: "Malines",
                LeagueDto: this.league,
                logo: "https://media.api-sports.io/football/teams/266.png",
                name: "KV Mechelen",
                stadiumCapacity: 16715,
                stadiumName: "AFAS-stadion Achter de Kazerne",
                standing: null,
                teamId: "266"
            },
            {
                city: "Brussel",
                LeagueDto: this.league,
                logo: "https://media.api-sports.io/football/teams/554.png",
                name: "Anderlecht",
                stadiumCapacity: 28063,
                stadiumName: "Lotto Park",
                standing: null,
                teamId: "554"
            },
            {
                city: "Brugge",
                LeagueDto: this.league,
                logo: "https://media.api-sports.io/football/teams/569.png",
                name: "Club Brugge KV",
                stadiumCapacity: 29062,
                stadiumName: "Jan Breydelstadion",
                standing: null,
                teamId: "569",
            },
            {
                city: "Waregem",
                LeagueDto: this.league,
                logo: "https://media.api-sports.io/football/teams/569.png",
                name: "Zulte Waregem",
                stadiumCapacity: 29062,
                stadiumName: "Jan Breydelstadion",
                standing: null,
                teamId: "600",
            },
            {
                city: "Oostende",
                LeagueDto: this.league,
                logo: "https://media.api-sports.io/football/teams/569.png",
                name: "KV Oostende",
                stadiumCapacity: 29062,
                stadiumName: "Jan Breydelstadion",
                standing: null,
                teamId: "624",
            },
            {
                city: "Gent",
                LeagueDto: this.league,
                logo: "https://media.api-sports.io/football/teams/569.png",
                name: "Gent",
                stadiumCapacity: 29062,
                stadiumName: "Jan Breydelstadion",
                standing: null,
                teamId: "631",
            },
            {
                city: "Luik",
                LeagueDto: this.league,
                logo: "https://media.api-sports.io/football/teams/569.png",
                name: "Standard Liege",
                stadiumCapacity: 29062,
                stadiumName: "Jan Breydelstadion",
                standing: null,
                teamId: "733",
            },
            {
                city: "Kortrijk",
                LeagueDto: this.league,
                logo: "https://media.api-sports.io/football/teams/569.png",
                name: "Kv Kortrijk",
                stadiumCapacity: 29062,
                stadiumName: "Jan Breydelstadion",
                standing: null,
                teamId: "734",
            },
            {
                city: "Sint-Truiden",
                LeagueDto: this.league,
                logo: "https://media.api-sports.io/football/teams/569.png",
                name: "STVV",
                stadiumCapacity: 29062,
                stadiumName: "Jan Breydelstadion",
                standing: null,
                teamId: "735",
            },
            {
                city: "Charleroi",
                LeagueDto: this.league,
                logo: "https://media.api-sports.io/football/teams/569.png",
                name: "Charleroi",
                stadiumCapacity: 29062,
                stadiumName: "Jan Breydelstadion",
                standing: null,
                teamId: "736",
            },
            {
                city: "Beveren",
                LeagueDto: this.league,
                logo: "https://media.api-sports.io/football/teams/569.png",
                name: "Beveren",
                stadiumCapacity: 29062,
                stadiumName: "Jan Breydelstadion",
                standing: null,
                teamId: "738",
            },
            {
                city: "Eupen",
                LeagueDto: this.league,
                logo: "https://media.api-sports.io/football/teams/569.png",
                name: "Eupen",
                stadiumCapacity: 29062,
                stadiumName: "Jan Breydelstadion",
                standing: null,
                teamId: "739",
            },
            {
                city: "Antwerp",
                LeagueDto: this.league,
                logo: "https://media.api-sports.io/football/teams/569.png",
                name: "Antwerp",
                stadiumCapacity: 29062,
                stadiumName: "Jan Breydelstadion",
                standing: null,
                teamId: "740",
            },
            {
                city: "Cercle Brugge",
                LeagueDto: this.league,
                logo: "https://media.api-sports.io/football/teams/569.png",
                name: "Cercle Brugge",
                stadiumCapacity: 29062,
                stadiumName: "Jan Breydelstadion",
                standing: null,
                teamId: "741",
            },
            {
                city: "Genk",
                LeagueDto: this.league,
                logo: "https://media.api-sports.io/football/teams/569.png",
                name: "Genk",
                stadiumCapacity: 29062,
                stadiumName: "Jan Breydelstadion",
                standing: null,
                teamId: "742",
            },
            {
                city: "Moeskroen",
                LeagueDto: this.league,
                logo: "https://media.api-sports.io/football/teams/569.png",
                name: "Moeskroen",
                stadiumCapacity: 29062,
                stadiumName: "Jan Breydelstadion",
                standing: null,
                teamId: "743",
            }
        ]
    }

    private static generateLeague(): League {
        return {
            league_id: '2660',
            name: 'Jupiler Pro League',
            season: 2020,
            startSeason: new Date('2020-08-08'),
            endSeason: new Date('2021-04-16'),
            selected: true,
            countryCode: 'BE',
            roundDtos: null,
            isCurrent: true,
            teamDtos: null,
        }
    }

    private static generateRounds(): Round[] {
        return [
            {
                roundId: '61',
                playRound: '26',
                round: 'Regular_Season_-_26',
                season: 2020,
                current: true,
                currentDate: new Date('2020-02-18'),
                leagueDto: null
            },
            {
                roundId: '60',
                playRound: '25',
                round: 'Regular_Season_-_25',
                season: 2020,
                current: false,
                currentDate: new Date('2020-02-07'),
                leagueDto: null
            },
            {
                roundId: '59',
                playRound: '24',
                round: 'Regular_Season_-_24',
                season: 2020,
                current: false,
                currentDate: new Date('2020-02-04'),
                leagueDto: null
            }
        ]
    }

    public static generateResults(): Result[] {
        return [
            {
                //oostende - genk : 3-1
                awayTeam: this.findTeamById('742'),
                homeTeam: this.findTeamById('624'),
                eventDate: new Date('2021-02-17'),
                result_id: '5963',
                round: 'Regular_Season_-_26',
                goalsHomeTeam: 3,
                goalsAwayTeam: 1,
                matchStatus: 'Match Finished'
            },
            {
                // beveren - eupen : 1-0
                awayTeam: this.findTeamById('739'),
                homeTeam: this.findTeamById('738'),
                eventDate: new Date('2021-02-17'),
                result_id: '5992',
                round: 'Regular_Season_-_26',
                goalsHomeTeam: 1,
                goalsAwayTeam: 0,
                matchStatus: 'Match Finished'
            },
            {
                // gent - moeskroen : 4-0
                awayTeam: this.findTeamById('743'),
                homeTeam: this.findTeamById('631'),
                eventDate: new Date('2021-02-17'),
                result_id: '5961',
                round: 'Regular_Season_-_26',
                goalsHomeTeam: 4,
                goalsAwayTeam: 0,
                matchStatus: 'Match Finished'
            },
            {
                // cercle brugge - anderlecht  : 0-0
                awayTeam: this.findTeamById('554'),
                homeTeam: this.findTeamById('741'),
                eventDate: new Date('2021-02-14'),
                result_id: '5960',
                round: 'Regular_Season_-_26',
                goalsAwayTeam: 0,
                goalsHomeTeam: 0,
                matchStatus: 'Match Finished'
            },
            {
                // Beerschot - KV Mechelen: 1-2
                awayTeam: this.findTeamById('266'),
                homeTeam: this.findTeamById('263'),
                eventDate: new Date('2021-02-15'),
                result_id: '5958',
                round: 'Regular_Season_-_26',
                goalsHomeTeam: 1,
                goalsAwayTeam: 2,
                matchStatus: 'Match Finished'
            },
            {
                // Standard - Antwerp: 1 - 1
                awayTeam: this.findTeamById('740'),
                homeTeam: this.findTeamById('733'),
                eventDate: new Date('2021-02-15'),
                result_id: '5957',
                round: 'Regular_Season_-_26',
                goalsAwayTeam: 1,
                goalsHomeTeam: 1,
                matchStatus: 'Match Finished'
            },
            {
                // STVV - Zulte Waregem: 1 - 2
                awayTeam: this.findTeamById('600'),
                homeTeam: this.findTeamById('735'),
                eventDate: new Date('2021-02-15'),
                result_id: '5955',
                round: 'Regular_Season_-_26',
                goalsHomeTeam: 1,
                goalsAwayTeam: 2,
                matchStatus: 'Match Finished'
            },
            {
                // OHL - KV Kortrijk: 3 - 1
                awayTeam: this.findTeamById('734'),
                homeTeam: this.findTeamById('260'),
                eventDate: new Date('2021-02-15'),
                result_id: '5955',
                round: 'Regular_Season_-_26',
                goalsAwayTeam: 1,
                goalsHomeTeam: 3,
                matchStatus: 'Match Finished'
            },
            {
                // Club Brugge - Cercle Brugge: 3 - 0
                awayTeam: this.findTeamById('741'),
                homeTeam: this.findTeamById('569'),
                eventDate: new Date('2021-02-16'),
                result_id: '5955',
                round: 'Regular_Season_-_23',
                goalsHomeTeam: 0,
                goalsAwayTeam: 3,
                matchStatus: 'Match Finished'
            },
            // end first 9 results
            {
                // Genk - Anderlecht: 1 - 2
                awayTeam: this.findTeamById('554'),
                homeTeam: this.findTeamById('742'),
                eventDate: new Date('2021-02-16'),
                result_id: '5955',
                round: 'Regular_Season_-_24',
                goalsHomeTeam: 1,
                goalsAwayTeam: 2,
                matchStatus: 'Match Finished'
            },
            {
                // Charleroi - Zulte Wargem: 1 - 1
                awayTeam: this.findTeamById('600'),
                homeTeam: this.findTeamById('736'),
                eventDate: new Date('2021-02-16'),
                result_id: '5955',
                round: 'Regular_Season_-_24',
                goalsHomeTeam: 1,
                goalsAwayTeam: 1,
                matchStatus: 'Match Finished'
            },
            {
                // Beerschot - Antwerp: 1 - 2
                awayTeam: this.findTeamById('740'),
                homeTeam: this.findTeamById('263'),
                eventDate: new Date('2021-02-16'),
                result_id: '5955',
                round: 'Regular_Season_-_24',
                goalsHomeTeam: 1,
                goalsAwayTeam: 2,
                matchStatus: 'Match Finished'
            },
            {
                // Standard - OHL: 1 - 1
                awayTeam: this.findTeamById('260'),
                homeTeam: this.findTeamById('733'),
                eventDate: new Date('2021-02-16'),
                result_id: '5955',
                round: 'Regular_Season_-_24',
                goalsHomeTeam: 1,
                goalsAwayTeam: 1,
                matchStatus: 'Match Finished'
            },
            {
                // Beveren - Club Brugge: 0 - 2
                awayTeam: this.findTeamById('569'),
                homeTeam: this.findTeamById('738'),
                eventDate: new Date('2021-02-16'),
                result_id: '5955',
                round: 'Regular_Season_-_24',
                goalsHomeTeam: 0,
                goalsAwayTeam: 2,
                matchStatus: 'Match Finished'
            },
            {
                // StVV - Cercle Brugge: 3 - 0
                homeTeam: this.findTeamById('735'),
                awayTeam: this.findTeamById('741'),
                eventDate: new Date('2021-02-16'),
                result_id: '595',
                round: 'Regular_Season_-_24',
                goalsHomeTeam: 3,
                goalsAwayTeam: 0,
                matchStatus: 'Match Finished'
            },
            {
                // Moeskroen - Kortrijk: 0 - 3
                awayTeam: this.findTeamById('734'),
                homeTeam: this.findTeamById('743'),
                eventDate: new Date('2021-02-16'),
                result_id: '5955',
                round: 'Regular_Season_-_24',
                goalsHomeTeam: 0,
                goalsAwayTeam: 3,
                matchStatus: 'Match Finished'
            },
            {
                // Gent - Eupen: 2 - 2
                awayTeam: this.findTeamById('739'),
                homeTeam: this.findTeamById('631'),
                eventDate: new Date('2021-02-15'),
                result_id: '5955',
                round: 'Regular_Season_-_24',
                goalsHomeTeam: 2,
                goalsAwayTeam: 2,
                matchStatus: 'Match Finished'
            },
            {
                // Club Brugge - Standard: 3 - 1
                awayTeam: this.findTeamById('733'),
                homeTeam: this.findTeamById('569'),
                eventDate: new Date('2021-02-15'),
                result_id: '5951',
                round: 'Regular_Season_-_23',
                goalsHomeTeam: 3,
                goalsAwayTeam: 1,
                matchStatus: 'Match Finished'
            }

        ];
    }


}
