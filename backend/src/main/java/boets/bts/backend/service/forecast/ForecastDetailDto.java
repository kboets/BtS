package boets.bts.backend.service.forecast;

import boets.bts.backend.web.results.ResultDto;
import boets.bts.backend.web.team.TeamDto;

import java.math.BigInteger;
import java.util.List;

public class ForecastDetailDto {

        private TeamDto team;
        private List<ResultDto> results;
        private ResultDto nextResult;
        private TeamDto nextOpponent;
        private BigInteger resultScore;
        private int score;
        private String info;


        public TeamDto getTeam() {
                return team;
        }

        public void setTeam(TeamDto team) {
                this.team = team;
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

        public BigInteger getResultScore() {
                return resultScore;
        }

        public void setResultScore(BigInteger resultScore) {
                this.resultScore = resultScore;
        }

        public String getInfo() {
                return info;
        }

        public void setInfo(String info) {
                this.info = info;
        }
}
