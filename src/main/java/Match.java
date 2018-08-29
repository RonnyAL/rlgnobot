import java.util.ArrayList;

public class Match {
    private Team team;
    private String matchTime = null;
    private String matchUrl = null;
    private String matchImg = null;

    Match() {}

    public Match(Team team, String matchTime, String matchUrl, String matchImg) {

        this.matchTime = matchTime;
        this.matchUrl = matchUrl;
    }

    public void setMatchTime(String matchTime) {
        this.matchTime = matchTime;
    }


    public String getMatchTime() {
        return matchTime;
    }

    public String getMatchUrl() {
        return matchUrl;
    }

    public void setMatchUrl(String matchUrl) {
        this.matchUrl = matchUrl;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getMatchImg() {
        return matchImg;
    }

    public void setMatchImg(String matchImg) {
        this.matchImg = matchImg;
    }
}
