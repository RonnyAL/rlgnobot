import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.EmbedBuilder;
import java.awt.Color;
import java.util.ArrayList; //


public class Main extends ListenerAdapter {

    final static String homeUrl = System.getenv("homeUrl");
    final static String selfTeamName = System.getenv("selfTeamName");

    public static void main(String[] args) throws LoginException {

        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(System.getenv("token"));
        builder.addEventListener(new Main());
        builder.build();

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw();

        if(content.equals("!hallo")) {
            event.getChannel().sendMessage("Hallo, " + event.getAuthor().getName() + "!").queue();
        } else if (content.equals("!kamp")) {
            getNextMatch(event.getChannel());
        } else if (content.equals("!kamper")) {
            Elements kamper = getAllMatches();
            String msg = "";
            if (kamper != null) {
                for (Element kamp : kamper) {
                    msg += kamp.text() + "\n";
                }
            } else {
                msg = "Fant ingen kamper!";
            }
            event.getChannel().sendMessage(msg).queue();
        } else if (content.toLowerCase().contains("god natt") && !event.getAuthor().isBot()) {
            event.getChannel().sendMessage("God natt! :heart:").queue();
        }
    }

    private Elements getAllMatches() {
        Elements matches = null;
        try {
            Document doc = Jsoup.connect(homeUrl).get();
            matches = doc.select("li.match");

        } catch (Exception e) {
            e.printStackTrace();
            return matches;
        }
        return matches;
    }

    private static Match getMatchObject(String matchUrl) {
        
        try {
            Document doc = Jsoup.connect(matchUrl).get();
            Match match = new Match();
            match.setMatchUrl(matchUrl);


            // PARSE TEAM NAME + TEAM URL
            Team team = new Team();
            String teamUrl = "https://www.gamer.no";
            String teamName;

            Element homeTeam = doc.selectFirst("div.esport-box-group > div.home-team > a > span");
            Element awayTeam = doc.selectFirst("div.esport-box-group > div.away-team > a > span");
            
            if (homeTeam.text() == selfTeamName) {
                teamUrl += doc.selectFirst("div.esport-box-group > div.away-team > a").attr("href");
                teamName = awayTeam.text();
            } else {
                teamUrl += doc.selectFirst("div.esport-box-group > div.home-team > a").attr("href");
                teamName = homeTeam.text();
            }



            team.setName(teamName);
            team.setTeamUrl(teamUrl);

            // PARSE PLAYERS
            ArrayList<Player> players = new ArrayList<Player>();
            Document teamDoc = Jsoup.connect(teamUrl).get();
            String matchImg = "http:" + teamDoc.select("figure.avatar > img").attr("src");
            match.setMatchImg(matchImg);


            Elements playerDocs = teamDoc.select("div.squad-member > span.user-name");
            for (Element playerDoc : playerDocs) {
                String playerName = playerDoc.select("a").first().text();
                String playerSteamId = playerDoc.select("a.steam-id").attr("href");
                playerSteamId = playerSteamId.substring(playerSteamId.lastIndexOf("/")+1);
                players.add(new Player(playerName, playerSteamId));
            }
            team.setPlayers(players);
            match.setTeam(team);

            // PARSE MATCH TIME
            Element matchTime = doc.select("div.start-timer>div.large-font+div.large-font").first();
            match.setMatchTime(matchTime.text());
            return match;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void embedNextMatch(MessageChannel channel, Match match) {
        Team t = match.getTeam();

        String title = t.getName();
        String matchUrl = match.getMatchUrl();
        String description = match.getMatchTime();
        ArrayList<Player> players = t.getPlayers();
        String playerlist = "";
        String matchImg = match.getMatchImg();

        for (Player player : players) {
            playerlist += "• [" + player.getUsername() + "](" + "https://rltracker.pro/profiles/" + player.getSteamId() + "/steam)\n";
        }


        channel.sendMessage(new MessageBuilder()
                .setEmbed(new EmbedBuilder()
                        .setTitle(title, matchUrl)
                        .setDescription(description)
                        .setColor(new Color(1952094))
                        .setImage(matchImg)
                        .addField("Spillere", playerlist, false)
                        .build())
                .build()).queue();
    }




    private final void getNextMatch(MessageChannel channel) {
        String matchUrl = null;
        try {
            Document doc = Jsoup.connect(homeUrl).get();
            Elements matches = doc.select("li.match");
            matchUrl = "https://gamer.no" + matches.select("a").attr("href");
            Match match = getMatchObject(matchUrl);
            if (match != null) {
                embedNextMatch(channel, match);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
