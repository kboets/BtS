package boets.bts.backend.service.forecast;

import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.team.TeamDto;

import java.util.List;

public class ForecastDetail {

        private TeamDto team;
        private TeamPerformanceQualifier teamPerformanceQualifier;
        private List<ResultDto> results;
        private ResultDto nextResult;
        private TeamDto nextOpponent;
        private int score;

        public TeamDto getTeam() {
                return team;
        }

        public void setTeam(TeamDto team) {
                this.team = team;
        }

        public TeamPerformanceQualifier getPerformanceQualifier() {
                return teamPerformanceQualifier;
        }

        public void setPerformanceQualifier(TeamPerformanceQualifier teamPerformanceQualifier) {
                this.teamPerformanceQualifier = teamPerformanceQualifier;
        }

        public List<ResultDto> getResults() {
                return results;
        }

        public void setResults(List<ResultDto> results) {
                this.results = results;
        }

        public ResultDto getNextResult() {
                return nextResult;
        }

        public void setNextResult(ResultDto nextResult) {
                this.nextResult = nextResult;
        }

        public int getScore() {
                return score;
        }

        public void setScore(int score) {
                this.score = score;
        }

        public TeamDto getNextOpponent() {
                return nextOpponent;
        }

        public void setNextOpponent(TeamDto nextOpponent) {
                this.nextOpponent = nextOpponent;
        }
}
