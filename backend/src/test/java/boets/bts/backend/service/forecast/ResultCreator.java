package boets.bts.backend.service.forecast;

import boets.bts.backend.domain.League;
import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Team;
import boets.bts.backend.web.league.LeagueDto;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.team.TeamDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ResultCreator {

    public static List<String> jupilerLeagueNames = new ArrayList<>(
            Arrays.asList("OH Leuven", "Royal Excel Mouscron", "Beerschot Wilrijk", "KV Mechelen",
                    "Anderlecht", "Club Brugge KV", "Zulte Waregem", "Oostende", "Gent", "Standard Liege", "Kortrijk", "St. Truiden",
                    "Charleroi", "Waasland-beveren", "AS Eupen", "Antwerp", "Cercle Brugge", "Genk")
    );

    public static Team createTeam4Name(String name) {
        League league = League.LeagueBuilder.aLeague().withCountryCode("BE").withName("Jupiler Pro League").build();
        return Team.TeamBuilder.aTeam()
                .withName(name)
                .withLeague(league)
                .withTeamId((long) jupilerLeagueNames.indexOf(name))
                .build();
    }

    public static TeamDto createTeamDTO4Name(String name) {
        LeagueDto leagueDto = LeagueDto.LeagueDtoBuilder.aLeagueDto().withCountryCode("BE").withName("Jupiler Pro League").build();
        return TeamDto.TeamDtoBuilder.aTeamDto()
                .withName(name)
                .withLeagueDto(leagueDto)
                .withTeamId(Integer.toString(jupilerLeagueNames.indexOf(name)))
                .withId(Integer.toString(jupilerLeagueNames.indexOf(name)))
                .build();
    }

    public static List<Result> createWinningResult(String teamName, int total, boolean isHomeTeam) {
        List<Result> winningResults = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for(int i=0;i<total;i++) {
            if(isHomeTeam) {
                winningResults.add(Result.ResultBuilder.aResult()
                        .withEventDate(today.minusWeeks(total-i))
                        .withGoalsHomeTeam(createRandomNumber())
                        .withGoalsAwayTeam(0)
                        .withHomeTeam(createTeam4Name(teamName))
                        .withAwayTeam(getOtherTeamFromList(teamName))
                        .build());

            } else {
                winningResults.add(Result.ResultBuilder.aResult()
                        .withEventDate(today.minusWeeks(total-i))
                        .withGoalsAwayTeam(createRandomNumber())
                        .withGoalsHomeTeam(0)
                        .withAwayTeam(createTeam4Name(teamName))
                        .withHomeTeam(getOtherTeamFromList(teamName))
                        .build());
            }

        }
        return winningResults;
    }

    public static List<ResultDto> createWinningResultDTOS(String teamName, int total, boolean isHomeTeam) {
        List<ResultDto> winningResults = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for(int i=0;i<total;i++) {
            if(isHomeTeam) {
                winningResults.add(ResultDto.ResultDtoBuilder.aResultDto()
                        .withEventDate(today.minusWeeks(total-i))
                        .withGoalsHomeTeam(createRandomNumber())
                        .withGoalsAwayTeam(0)
                        .withHomeTeam(createTeamDTO4Name(teamName))
                        .withAwayTeam(getOtherTeamDTOFromList(teamName))
                        .build());

            } else {
                winningResults.add(ResultDto.ResultDtoBuilder.aResultDto()
                        .withEventDate(today.minusWeeks(total-i))
                        .withGoalsAwayTeam(createRandomNumber())
                        .withGoalsHomeTeam(0)
                        .withAwayTeam(createTeamDTO4Name(teamName))
                        .withHomeTeam(getOtherTeamDTOFromList(teamName))
                        .build());
            }

        }
        return winningResults;
    }

    public static List<Result> createLosingResult(String teamName, int total, boolean isHomeTeam) {
        List<Result> losingResults = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for(int i=0;i<total;i++) {
            if(isHomeTeam) {
                losingResults.add(Result.ResultBuilder.aResult()
                        .withEventDate(today.minusWeeks(total-i))
                        .withGoalsHomeTeam(0)
                        .withGoalsAwayTeam(createRandomNumber())
                        .withHomeTeam(createTeam4Name(teamName))
                        .withAwayTeam(getOtherTeamFromList(teamName))
                        .build());

            } else {
                losingResults.add(Result.ResultBuilder.aResult()
                        .withEventDate(today.minusWeeks(total-i))
                        .withGoalsAwayTeam(0)
                        .withGoalsHomeTeam(createRandomNumber())
                        .withAwayTeam(createTeam4Name(teamName))
                        .withHomeTeam(getOtherTeamFromList(teamName))
                        .build());
            }

        }
        return losingResults;
    }


    public static List<ResultDto> createLosingResultDTO(String teamName, int total, boolean isHomeTeam) {
        List<ResultDto> losingResults = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for(int i=0;i<total;i++) {
            if(isHomeTeam) {
                losingResults.add(ResultDto.ResultDtoBuilder.aResultDto()
                        .withEventDate(today.minusWeeks(total-i))
                        .withGoalsHomeTeam(0)
                        .withGoalsAwayTeam(createRandomNumber())
                        .withHomeTeam(createTeamDTO4Name(teamName))
                        .withAwayTeam(getOtherTeamDTOFromList(teamName))
                        .build());

            } else {
                losingResults.add(ResultDto.ResultDtoBuilder.aResultDto()
                        .withEventDate(today.minusWeeks(total-i))
                        .withGoalsAwayTeam(0)
                        .withGoalsHomeTeam(createRandomNumber())
                        .withAwayTeam(createTeamDTO4Name(teamName))
                        .withHomeTeam(getOtherTeamDTOFromList(teamName))
                        .build());
            }

        }
        return losingResults;
    }

    public static List<Result> createDrawResult(String teamName, int total, boolean isHomeTeam) {
        List<Result> drawResults = new ArrayList<>();
        LocalDate today = LocalDate.now();
        int result = createRandomNumber();
        for(int i=0;i<total;i++) {
            if(isHomeTeam) {
                drawResults.add(Result.ResultBuilder.aResult()
                        .withEventDate(today.minusWeeks(total-i))
                        .withGoalsHomeTeam(result)
                        .withGoalsAwayTeam(result)
                        .withHomeTeam(createTeam4Name(teamName))
                        .withAwayTeam(getOtherTeamFromList(teamName))
                        .build());

            } else {
                drawResults.add(Result.ResultBuilder.aResult()
                        .withEventDate(today.minusWeeks(total-i))
                        .withGoalsAwayTeam(result)
                        .withGoalsHomeTeam(result)
                        .withAwayTeam(createTeam4Name(teamName))
                        .withHomeTeam(getOtherTeamFromList(teamName))
                        .build());
            }

        }
        return drawResults;
    }


    public static List<ResultDto> createDrawResultDTO(String teamName, int total, boolean isHomeTeam) {
        List<ResultDto> drawResults = new ArrayList<>();
        LocalDate today = LocalDate.now();
        int result = createRandomNumber();
        for(int i=0;i<total;i++) {
            if(isHomeTeam) {
                drawResults.add(ResultDto.ResultDtoBuilder.aResultDto()
                        .withEventDate(today.minusWeeks(total-i))
                        .withGoalsHomeTeam(result)
                        .withGoalsAwayTeam(result)
                        .withHomeTeam(createTeamDTO4Name(teamName))
                        .withAwayTeam(getOtherTeamDTOFromList(teamName))
                        .build());

            } else {
                drawResults.add(ResultDto.ResultDtoBuilder.aResultDto()
                        .withEventDate(today.minusWeeks(total-i))
                        .withGoalsAwayTeam(result)
                        .withGoalsHomeTeam(result)
                        .withAwayTeam(createTeamDTO4Name(teamName))
                        .withHomeTeam(getOtherTeamDTOFromList(teamName))
                        .build());
            }

        }
        return drawResults;
    }

    private static int createRandomNumber() {
        Random random = new Random();
        return random.nextInt(5) + 1;
    }

    private static Team getOtherTeamFromList(String name) {
        Random random = new Random();
        int nextTeamIndex = random.nextInt(18);
        String otherTeamName = jupilerLeagueNames.get(nextTeamIndex);
        while(otherTeamName.equals(name)) {
            nextTeamIndex = random.nextInt(18);
            otherTeamName = jupilerLeagueNames.get(nextTeamIndex);
        }
        return createTeam4Name(otherTeamName);
    }

    private static TeamDto getOtherTeamDTOFromList(String name) {
        Random random = new Random();
        int nextTeamIndex = random.nextInt(18);
        String otherTeamName = jupilerLeagueNames.get(nextTeamIndex);
        while(otherTeamName.equals(name)) {
            nextTeamIndex = random.nextInt(18);
            otherTeamName = jupilerLeagueNames.get(nextTeamIndex);
        }
        return createTeamDTO4Name(otherTeamName);
    }

}
