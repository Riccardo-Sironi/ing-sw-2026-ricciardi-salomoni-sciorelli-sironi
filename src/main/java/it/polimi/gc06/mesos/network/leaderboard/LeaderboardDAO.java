package it.polimi.gc06.mesos.network.leaderboard;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The class is static and this application should be the only one accessing the DB, if this is not respected the
 * DAO won't be consistent with external updates. Right now the DAO has two states:
 * 1. Application just started, DB is created and at the first read request all the data is extracted from DB.
 * 2. First read request already happened, local data is up to date, no need to request DB update
 */
public class LeaderboardDAO {

    private final static List<Leaderboard> leaderboards = new ArrayList<>();
    private final static String JSON_CONFIG_URL = "/it/polimi/gc06/mesos/jsons/dbConfig.json";
    private static Connection connection = null;

    private synchronized static void init() throws IOException, SQLException {

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
    }

    private synchronized static List<Leaderboard> getLeaderboards() throws IOException, SQLException {

        if(connection == null) init();

        //only if current list is empty it calls the DB to request the necessary leaderboards

        //returns the leaderboards
        leaderboards.sort(Leaderboard::compareTo);
        return Collections.unmodifiableList(leaderboards);
    }

    public synchronized static void saveLeaderboard(Leaderboard leaderboard) throws IOException, SQLException {

        if(connection == null) init();

        //request to save the leaderboard to DB

        //saves the leaderboard locally
        leaderboards.add(leaderboard);
    }
}
