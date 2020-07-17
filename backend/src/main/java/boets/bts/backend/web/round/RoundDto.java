package boets.bts.backend.web.round;

import boets.bts.backend.web.league.LeagueDto;

import java.time.LocalDate;

public class RoundDto {

    private String roundId;
    private String round;
    private int season;
    private boolean current;
    private LocalDate currentDate;
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

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }
}
