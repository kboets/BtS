package boets.bts.backend.service.standing;

import boets.bts.backend.web.league.LeagueDto;
import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.standing.StandingDto;
import boets.bts.backend.web.standing.SubStandingDto;
import boets.bts.backend.web.team.TeamDto;

import java.util.*;
import java.util.stream.Collectors;

public class StandingCalculator {

    public List<StandingDto> calculateStandings(LeagueDto leagueDto, List<ResultDto> results, int roundNumber) {
        Map<String, StandingDto> standingForTeamMap = new HashMap<>();
        List<TeamDto> teams = leagueDto.getTeamDtos();
        //sort results on round number
        List<ResultDto> sortedResults = results.stream().sorted(Comparator.comparing(ResultDto::getRoundNumber)).collect(Collectors.toList());
        for(TeamDto team: teams) {
            //get all results for this team
            List<ResultDto> resultForTeam = sortedResults.stream()
                    .filter(result -> result.getHomeTeam().getTeamId().equals(team.getTeamId()) || result.getAwayTeam().getTeamId().equals(team.getTeamId()))
                    .collect(Collectors.toList());
            // calculate standing for team
            StandingDto standingForTeam = standingForTeamMap.getOrDefault(team.getTeamId(), new StandingDto());
            StandingDto standingDto = calculateStandingForTeam(team, standingForTeam, resultForTeam, roundNumber, leagueDto);
            standingForTeamMap.put(team.getId(), standingDto);
        }

        // calculate ranking
        List<StandingDto> standingDtos = new ArrayList<>(standingForTeamMap.values());
        standingDtos.sort(new StandingDtoComparator().reversed());
        int i = 1;
        for(StandingDto standingDto: standingDtos) {
            standingDto.setRank(i);
            i++;
        }
        return standingDtos;
    }

   private StandingDto calculateStandingForTeam(TeamDto teamDto, StandingDto standingDto, List<ResultDto> results, int roundNumber, LeagueDto leagueDto) {
        for(ResultDto resultDto: results) {
            standingDto.setSeason(leagueDto.getSeason());
            standingDto.setRoundNumber(roundNumber);
            standingDto.setTeam(teamDto);
            standingDto.setLastUpdate(new Date());
            //standingDto.setLeague(result.getLeague());
            SubStandingDto allSubStandingDto = calculateSubStanding(teamDto, standingDto.getAllSubStanding(), resultDto);
            allSubStandingDto.setMatchPlayed(allSubStandingDto.getMatchPlayed()+1);
            if(resultDto.getHomeTeam().getTeamId().equals(teamDto.getTeamId())) {
                SubStandingDto homeSubStandingDto = calculateSubStanding(teamDto, standingDto.getHomeSubStanding(), resultDto);
                homeSubStandingDto.setMatchPlayed(homeSubStandingDto.getMatchPlayed()+1);
                homeSubStandingDto.setGoalsFor(homeSubStandingDto.getGoalsFor()+resultDto.getGoalsHomeTeam());
                homeSubStandingDto.setGoalsAgainst(homeSubStandingDto.getGoalsAgainst()+resultDto.getGoalsAwayTeam());
                allSubStandingDto.setGoalsFor(allSubStandingDto.getGoalsFor() + resultDto.getGoalsHomeTeam());
                allSubStandingDto.setGoalsAgainst(allSubStandingDto.getGoalsAgainst() + resultDto.getGoalsAwayTeam());
                standingDto.setHomeSubStanding(homeSubStandingDto);
                if(hasWon(resultDto, resultDto.getHomeTeam())) {
                    standingDto.setPoints(standingDto.getPoints()+3);
                } else if (isDraw(resultDto)) {
                    standingDto.setPoints(standingDto.getPoints()+1);
                }
            } else if(resultDto.getAwayTeam().getTeamId().equals(teamDto.getTeamId())) {
                SubStandingDto awaySubStandingDto = calculateSubStanding(teamDto, standingDto.getAwaySubStanding(), resultDto);
                awaySubStandingDto.setMatchPlayed(awaySubStandingDto.getMatchPlayed()+1);
                awaySubStandingDto.setGoalsFor(awaySubStandingDto.getGoalsFor()+resultDto.getGoalsAwayTeam());
                awaySubStandingDto.setGoalsAgainst(awaySubStandingDto.getGoalsAgainst()+resultDto.getGoalsHomeTeam());
                allSubStandingDto.setGoalsFor(allSubStandingDto.getGoalsFor()+resultDto.getGoalsAwayTeam());
                allSubStandingDto.setGoalsAgainst(allSubStandingDto.getGoalsAgainst()+resultDto.getGoalsHomeTeam());
                standingDto.setAwaySubStanding(awaySubStandingDto);
                if(hasWon(resultDto, resultDto.getAwayTeam())) {
                    standingDto.setPoints(standingDto.getPoints()+3);
                } else if (isDraw(resultDto)) {
                    standingDto.setPoints(standingDto.getPoints()+1);
                }
            }
            standingDto.setAllSubStanding(allSubStandingDto);
//            if(hasWon(resultDto, teamDto)) {
//                standingDto.setPoints(standingDto.getPoints()+3);
//            } else if(isDraw(resultDto)) {
//                standingDto.setPoints(standingDto.getPoints()+1);
//            }
        }

        return standingDto;
    }

    private SubStandingDto calculateSubStanding(TeamDto teamDto, SubStandingDto subStandingDto, ResultDto resultDto) {
        if(hasWon(resultDto, teamDto)) {
            subStandingDto.setWin(subStandingDto.getWin()+1);
        } else if(hasLost(resultDto, teamDto)) {
            subStandingDto.setLose(subStandingDto.getLose()+1);
        } else {
            subStandingDto.setDraw(subStandingDto.getDraw()+1);
        }
        return subStandingDto;
    }


    private boolean hasWon(ResultDto result, TeamDto team) {
        if(result.getGoalsHomeTeam() > result.getGoalsAwayTeam()) {
            return team.getTeamId().equals(result.getHomeTeam().getTeamId());
        } else if(result.getGoalsAwayTeam() > result.getGoalsHomeTeam()) {
            return team.getTeamId().equals(result.getAwayTeam().getTeamId());
        }
        return false;
    }

    private boolean hasLost(ResultDto result, TeamDto team) {
        if(result.getGoalsHomeTeam() > result.getGoalsAwayTeam()) {
            return team.getTeamId().equals(result.getAwayTeam().getTeamId());
        } else if(result.getGoalsAwayTeam() > result.getGoalsHomeTeam()) {
            return team.getTeamId().equals(result.getHomeTeam().getTeamId());
        }
        return false;
    }


    private boolean isDraw(ResultDto result) {
        return result.getGoalsHomeTeam() == result.getGoalsAwayTeam();
    }

}
