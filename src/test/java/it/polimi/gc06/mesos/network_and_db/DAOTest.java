package it.polimi.gc06.mesos.network_and_db;

import it.polimi.gc06.mesos.network.leaderboard.Leaderboard;
import it.polimi.gc06.mesos.network.leaderboard.LeaderboardDAO;
import it.polimi.gc06.mesos.network.leaderboard.Score;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.List;
import java.util.Random;

public class DAOTest {

    final int SAVES_BEFORE_PRINT = 10;
    final String[] names = {
            "Alessandro", "Beatrice", "Claudio", "Daniele", "Elena",
            "Francesco", "Giulia", "Ilaria", "Lorenzo", "Marco",
            "Nicola", "Ottavia", "Paolo", "Riccardo", "Sofia",
            "Tommaso", "Valentina", "Valerio", "Zoe", "Adriano",
            "Chiara", "Davide", "Edoardo", "Federico", "Gaia",
            "Leonardo", "Martina", "Matteo", "Noemi", "Pietro",
            "Raffaele", "Sara", "Simone", "Teresa", "Umberto",
            "Vincenzo", "Alessia", "Bernardo", "Camilla", "Domenico",
            "Enrico", "Fabio", "Giorgia", "Ludovica", "Marianna",
            "Michele", "Patrizia", "Roberta", "Stefano", "Veridiana"
    };

    @Test
    void testInit(){

        try {
            LeaderboardDAO.init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void testSaveLeaderboard(){
        Random r = new Random();
        Leaderboard l = new Leaderboard();
        l.setNumOfPlayers(r.nextInt(2,6));
        l.setTimestamp(new Timestamp(r.nextLong(0L, (long) 10e10)));
        for(int i=0;i<l.getNumOfPlayers();i++){
            Score s = new Score();
            s.setNickname(names[r.nextInt(0,50)]);
            s.setScore(r.nextInt(0,401)-200);
            l.addScore(s);
        }
        try {
            LeaderboardDAO.saveLeaderboard(l);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetLeaderboards(){

        for(int i=0;i< SAVES_BEFORE_PRINT;i++) testSaveLeaderboard();
        try {
            List<Leaderboard> ls = LeaderboardDAO.getLeaderboards();
            for (Leaderboard l : ls) {
                System.out.println(l.getTimestamp() + ":");
                for (int w = 0; w < l.getScores().size(); w++) {
                    Score s = l.getScores().get(w);
                    System.out.println("\t" + s.getNickname() + ": " + s.getScore());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
