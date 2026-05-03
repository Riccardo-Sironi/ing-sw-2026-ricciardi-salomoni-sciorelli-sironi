package it.polimi.gc06.mesos.network.leaderboard;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

/**
 * The class is static and this application should be the only one accessing the DB, if this is not respected the
 * DAO won't be consistent with external updates. Right now the DAO has two states:
 * 1. Application just started, DB is created and at the first read request all the data is extracted from DB.
 * 2. First read request already happened, local data is up to date, no need to request DB update.
 */
public class LeaderboardDAO {

    private final static List<Leaderboard> leaderboards = new ArrayList<>();
    private final static String JSON_CONFIG_URL = "/it/polimi/gc06/mesos/jsons/dbConfig.json";
    private static Connection connection = null;

    public synchronized static void init() throws IOException, SQLException {

        //gets DB config
        String base_url;
        String db_name;
        String user;
        String password;
        try (InputStream input = LeaderboardDAO.class.getResourceAsStream(JSON_CONFIG_URL)) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(input);
            base_url = root.get("base_url").asText();
            db_name = root.get("db_name").asText();
            user = root.get("user").asText();
            password = root.get("password").asText();
        }

        //creates DB if necessary using config
        try (Connection c = DriverManager.getConnection(base_url, user, password)){
            String query = "CREATE DATABASE IF NOT EXISTS " + db_name;
            c.createStatement().execute(query);
        }

        //sets up the connection to DB
        connection = DriverManager.getConnection(base_url+db_name, user, password);
        try {
            String query = "CREATE TABLE IF NOT EXISTS leaderboards(" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "numOfPlayers INT NOT NULL, " +
                    "tstamp TIMESTAMP NOT NULL)";
            connection.createStatement().execute(query);
            query = "CREATE TABLE IF NOT EXISTS scores(" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nickname VARCHAR(255) NOT NULL, " +
                    "prestigeScore INT NOT NULL," +
                    "foodScore INT NOT NULL," +
                    "leaderboardId INT NOT NULL REFERENCES leaderboards(id) ON UPDATE CASCADE ON DELETE NO ACTION)";
            connection.createStatement().execute(query);

        } catch(SQLException e){
            connection = null; //init should be called again
            throw e;
        }
    }

    public synchronized static List<Leaderboard> getLeaderboards() throws IOException, SQLException {

        if(connection == null) init();

        //only if current list is empty it calls the DB to request the leaderboards
        if(leaderboards.isEmpty()){
            String query = "SELECT l.id AS id, numOfPlayers, tstamp, nickname, score " +
                    "FROM leaderboards AS l JOIN scores AS s ON l.id = s.leaderboardId " +
                    "ORDER BY l.id";
            ResultSet result = connection.prepareStatement(query).executeQuery();
            Set<Integer> idSet = new HashSet<>();
            Leaderboard tempLb = null;
            while(result.next()){

                //if necessary creates new leaderboard and saves the previous (if present)
                if(!idSet.contains(result.getInt("id"))){
                    idSet.add(result.getInt("id"));

                    if(tempLb != null) leaderboards.add(tempLb);
                    tempLb = new Leaderboard();
                    tempLb.setTimestamp(result.getTimestamp("tstamp"));
                }

                Score score = new Score();
                score.setPrestigeScore(result.getInt("prestigeScore"));
                score.setFoodScore(result.getInt("foodScore"));
                score.setNickname(result.getString("nickname"));
                tempLb.addScore(score);
            }
            if(tempLb != null) leaderboards.add(tempLb); //saves the last unsaved leaderboard if present
        }

        //returns the leaderboards
        leaderboards.sort(Leaderboard::compareTo);
        return Collections.unmodifiableList(leaderboards);
    }

    public synchronized static void saveLeaderboard(Leaderboard l) throws IOException, SQLException {

        if(connection == null) init();

        //request to save the leaderboard to DB
        connection.setAutoCommit(false);
        try {
            String lbQuery = "INSERT INTO leaderboards (numOfPlayers, tstamp) " +
                    "VALUES (?,?)";
            PreparedStatement ps = connection.prepareStatement(lbQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1,l.getNumOfPlayers());
            ps.setTimestamp(2,l.getTimestamp());
            ps.executeUpdate();
            ResultSet result = ps.getGeneratedKeys();

            if(result.next()) {

                String sQuery = "INSERT INTO scores (nickname, prestigeScore, foodScore, leaderboardId) " +
                        "VALUES ( ?, ?, ?, ?)"; //prevents nickname SQL injection
                ps = connection.prepareStatement(sQuery);

                int id = result.getInt(1);
                for (Score s : l.getScores()) {
                    ps.setString(1, s.getNickname());
                    ps.setInt(2, s.getPrestigeScore());
                    ps.setInt(3, s.getFoodScore());
                    ps.setInt(4, id);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            else{
                connection.rollback();
            }
            connection.commit();

        } catch(SQLException e){
            //if something goes wrong we do a rollback
            connection.rollback();
            throw e;
        }
        finally {
            connection.setAutoCommit(true);
        }

        //saves the leaderboard locally
        leaderboards.add(l);
    }
}
