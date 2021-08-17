package boets.bts.backend.web.standing;

public class SubStandingDto {

    private Integer matchPlayed;
    private Integer win;
    private Integer lose;
    private Integer draw;
    private Integer goalsFor;
    private Integer goalsAgainst;

    public Integer getMatchPlayed() {
        if(matchPlayed == null) {
            matchPlayed = 0;
        }
        return matchPlayed;
    }

    public void setMatchPlayed(Integer matchPlayed) {
        this.matchPlayed = matchPlayed;
    }

    public Integer getWin() {
        if(win == null) {
            win = 0;
        }
        return win;
    }

    public void setWin(Integer win) {
        this.win = win;
    }

    public Integer getLose() {
        if(lose == null) {
            lose = 0;
        }
        return lose;
    }

    public void setLose(Integer lose) {
        this.lose = lose;
    }

    public Integer getDraw() {
        if(draw == null) {
            draw = 0;
        }
        return draw;
    }

    public void setDraw(Integer draw) {
        this.draw = draw;
    }

    public Integer getGoalsFor() {
        if(goalsFor == null) {
            goalsFor = 0;
        }
        return goalsFor;
    }

    public void setGoalsFor(Integer goalsFor) {
        this.goalsFor = goalsFor;
    }

    public Integer getGoalsAgainst() {
        if(goalsAgainst == null) {
            goalsAgainst = 0;
        }
        return goalsAgainst;
    }

    public void setGoalsAgainst(Integer goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }
}
