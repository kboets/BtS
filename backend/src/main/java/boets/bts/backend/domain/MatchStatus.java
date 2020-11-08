package boets.bts.backend.domain;

public enum MatchStatus {

    FINISHED("Match Finished"), POSTPONED("Match Postponed"), CANCELLED("Match Cancelled"), NOT_STARTED("Not Started");

    private String name;

    MatchStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static MatchStatus getMatchStatusByName(String name) {
        for (MatchStatus e : values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return null;
    }
}
