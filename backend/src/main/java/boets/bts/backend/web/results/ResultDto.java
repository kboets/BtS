package boets.bts.backend.web.results;

import boets.bts.backend.web.team.TeamDto;

import java.time.LocalDate;

public class ResultDto {

    private String result_id;
    private LocalDate eventDate;
    private String round;
    private TeamDto homeTeam;
    private TeamDto awayTeam;
    private int goalsHomeTeam;
    private int goalsAwayTeam;
    private String matchStatus;

    public String getResult_id() {
        return result_id;
    }

    public void setResult_id(String result_id) {
        this.result_id = result_id;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public TeamDto getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(TeamDto homeTeam) {
        this.homeTeam = homeTeam;
    }

    public TeamDto getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(TeamDto awayTeam) {
        this.awayTeam = awayTeam;
    }

    public int getGoalsHomeTeam() {
        return goalsHomeTeam;
    }

    public void setGoalsHomeTeam(int goalsHomeTeam) {
        this.goalsHomeTeam = goalsHomeTeam;
    }

    public int getGoalsAwayTeam() {
        return goalsAwayTeam;
    }

    public void setGoalsAwayTeam(int goalsAwayTeam) {
        this.goalsAwayTeam = goalsAwayTeam;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }


    public static final class ResultDtoBuilder {
        private String result_id;
        private LocalDate eventDate;
        private String round;
        private TeamDto homeTeam;
        private TeamDto awayTeam;
        private int goalsHomeTeam;
        private int goalsAwayTeam;
        private String matchStatus;

        private ResultDtoBuilder() {
        }

        public static ResultDtoBuilder aResultDto() {
            return new ResultDtoBuilder();
        }

        public ResultDtoBuilder withResult_id(String result_id) {
            this.result_id = result_id;
            return this;
        }

        public ResultDtoBuilder withEventDate(LocalDate eventDate) {
            this.eventDate = eventDate;
            return this;
        }

        public ResultDtoBuilder withRound(String round) {
            this.round = round;
            return this;
        }

        public ResultDtoBuilder withHomeTeam(TeamDto homeTeam) {
            this.homeTeam = homeTeam;
            return this;
        }

        public ResultDtoBuilder withAwayTeam(TeamDto awayTeam) {
            this.awayTeam = awayTeam;
            return this;
        }

        public ResultDtoBuilder withGoalsHomeTeam(int goalsHomeTeam) {
            this.goalsHomeTeam = goalsHomeTeam;
            return this;
        }

        public ResultDtoBuilder withGoalsAwayTeam(int goalsAwayTeam) {
            this.goalsAwayTeam = goalsAwayTeam;
            return this;
        }

        public ResultDtoBuilder withMatchStatus(String matchStatus) {
            this.matchStatus = matchStatus;
            return this;
        }

        public ResultDto build() {
            ResultDto resultDto = new ResultDto();
            resultDto.setResult_id(result_id);
            resultDto.setEventDate(eventDate);
            resultDto.setRound(round);
            resultDto.setHomeTeam(homeTeam);
            resultDto.setAwayTeam(awayTeam);
            resultDto.setGoalsHomeTeam(goalsHomeTeam);
            resultDto.setGoalsAwayTeam(goalsAwayTeam);
            resultDto.setMatchStatus(matchStatus);
            return resultDto;
        }
    }
}
