package it.polimi.gc06.mesos.network.leaderboard;

import java.sql.*;
import java.util.*;

public class LeaderboardDAO {

    private final static List<Leaderboard> leaderboards = new ArrayList<>();
    private static Connection connection = null;
    private static String dbUrl = "jdbc:mysql://localhost:3306/"; //default for mysql server on local host
    private static String dbName = "GC06_Mesos_DB";
    private static String dbUser = null;
    private static String dbPassword = null;

    /**
     * Sets the info for the db connection. Must be called before any other function.
     *
     * @param user the user of the db for this application, must have full access to db cannot be {@code null}
     * @param password the password for the user, cannot be {@code null}
     * @param databaseName can be {@code null} and default name (GC06_Mesos_DB) will be used
     * @param location can be {@code null} if it is on localHost
     * @throws IllegalArgumentException if {@code user} or {@code password} are null.
     */
    public static void setDbInfo(String user, String password, String databaseName, String location) throws IllegalArgumentException{
        if(user == null || password == null) throw new IllegalArgumentException();
        if(location!=null) dbUrl = "jdbc:mysql://"+location+"/";
        if(databaseName!=null) dbName = databaseName;
        dbUser = user;
        dbPassword = password;
    }

    /**
     * Connect to db server and creates the db
     *
     * @throws SQLException
     */
    public synchronized static void init() throws SQLException {

        if(connection != null) return; //already created

        //creates DB if necessary using config
        try (Connection c = DriverManager.getConnection(dbUrl, dbUser, dbPassword)){
            String query = "CREATE DATABASE IF NOT EXISTS " + dbName;
            c.createStatement().execute(query);
        }

        //sets up the connection to DB
        connection = DriverManager.getConnection(dbUrl+dbName, dbUser, dbPassword);
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

    /**
     * Get all leaderboards saved with db. Before that, if not present, tries to create the db.
     *
     * @return the leaderboards. {@code null} if db info have not been set.
     * @throws SQLException if, when not saved locally, fails to get leaderboards from the db.
     */
    public synchronized static List<Leaderboard> getLeaderboards() throws SQLException {

        if(dbUser == null) return null;
        if(connection == null) init();

        //only if current list is empty it calls the DB to request the leaderboards
        if(leaderboards.isEmpty()){
            String query = "SELECT l.id AS id, numOfPlayers, tstamp, nickname, foodScore, prestigeScore " +
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

    /**
     * Saves a leaderboard to the db. Before that, if not present, tries to create the db.
     *
     * @param l the leaderboard.
     * @return {@code false} if db info have not been set.
     * @throws SQLException if it fails to save {@code l} on the db.
     */
    public synchronized static boolean saveLeaderboard(Leaderboard l) throws SQLException {

        if(dbUser == null) return false;
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
                throw new IllegalStateException("Query did not generate a valid key.");
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

        return true;
    }
}
