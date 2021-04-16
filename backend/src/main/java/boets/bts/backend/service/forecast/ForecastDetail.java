package boets.bts.backend.service.forecast;

import boets.bts.backend.domain.Result;
import boets.bts.backend.domain.Team;

import java.util.List;

public class ForecastDetail {

        private Team team;
        private PerformanceQualifier performanceQualifier;
        private List<Result> results;
        private Result nextResult;
        private Team nextOpponent;
        private int score;

        public Team getTeam() {
                return team;
        }

        public void setTeam(Team team) {
                this.team = team;
        }

        public PerformanceQualifier getPerformanceQualifier() {
                return performanceQualifier;
        }

        public void setPerformanceQualifier(PerformanceQualifier performanceQualifier) {
                this.performanceQualifier = performanceQualifier;
        }

        public List<Result> getResults() {
                return results;
        }

        public void setResults(List<Result> results) {
                this.results = results;
        }

        public Result getNextResult() {
                return nextResult;
        }

        public void setNextResult(Result nextResult) {
                this.nextResult = nextResult;
        }

        public int getScore() {
                return score;
        }

        public void setScore(int score) {
                this.score = score;
        }

        public Team getNextOpponent() {
                return nextOpponent;
        }

        public void setNextOpponent(Team nextOpponent) {
                this.nextOpponent = nextOpponent;
        }
}
