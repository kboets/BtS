package boets.bts.backend.web.round;

import boets.bts.backend.web.league.LeagueDto;

public class RoundDto {

    private String roundId;
    private String round;
    private int season;
    private LeagueDto leagueDto;

    public String getRoundId() {
        return roundId;
    }

    public void setRoundId(String roundId) {
        this.roundId = roundId;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public LeagueDto getLeagueDto() {
        return leagueDto;
    }

    public void setLeagueDto(LeagueDto leagueDto) {
        this.leagueDto = leagueDto;
    }
}
