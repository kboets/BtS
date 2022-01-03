package boets.bts.backend.domain;

import javax.persistence.Embeddable;

@Embeddable
public class AlgorithmPoints {

    private Integer win;
    private Integer lose;
    private Integer draw;

    public AlgorithmPoints() {
    }

    public AlgorithmPoints(Integer win, Integer lose, Integer draw) {
        this.win = win;
        this.lose = lose;
        this.draw = draw;
    }

    public Integer getWin() {
        return win;
    }

    public void setWin(Integer win) {
        this.win = win;
    }

    public Integer getLose() {
        return lose;
    }

    public void setLose(Integer lose) {
        this.lose = lose;
    }

    public Integer getDraw() {
        return draw;
    }

    public void setDraw(Integer draw) {
        this.draw = draw;
    }
}
