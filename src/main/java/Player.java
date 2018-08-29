public class Player {
    private String username;
    private String steamId;

    Player() {
        this.username = null;
        this.steamId = null;
    }

    Player(String username, String steamId) {
        this.username = username;
        this.steamId = steamId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSteamId() {
        return steamId;
    }

    public void setSteamId(String steamId) {
        this.steamId = steamId;
    }
}
