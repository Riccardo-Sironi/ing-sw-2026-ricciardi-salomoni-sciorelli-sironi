package it.polimi.gc06.mesos.network.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import it.polimi.gc06.mesos.dtos.snapshots.GameSnapshot;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.InstancesManager.ModelInstancesManager;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;
import it.polimi.gc06.mesos.network.server.matches.Match;
import it.polimi.gc06.mesos.network.server.matches.MatchManager;
import it.polimi.gc06.mesos.network.server.matches.RestoredMatch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PersistenceService implements Runnable {

    public static final String backupFileName = "matchBackup.json";
    public static final String tempFileName = "matchBackup.json.tmp";
    /**
     * In seconds
     */
    public static final long backupCooldown = 60;
    private final File backupFile;
    private final File tmpFile;
    private final Map<Integer, GameModel> backupMatches;
    private MatchManager matchManager;

    public PersistenceService() throws IOException {
        //creates matchBackup.json if not present
        try {
            Files.createDirectories(ServerMain.appDirectory);
            Files.createFile(ServerMain.appDirectory.resolve(backupFileName));
            Files.createFile(ServerMain.appDirectory.resolve(tempFileName));
        } catch (FileAlreadyExistsException _) {
        }

        this.backupFile = new File(ServerMain.appDirectory.resolve(backupFileName).toUri());
        this.tmpFile = new File(ServerMain.appDirectory.resolve(tempFileName).toUri());
        this.backupMatches = new HashMap<>();
        this.matchManager = null;

        try {
            //fill temp map
            ObjectMapper mapper = new ObjectMapper();

            Map<Integer, GameSnapshot> snapshotMap = new HashMap<>();
            if (backupFile.length() > 0) {
                snapshotMap = mapper.readValue(backupFile, new TypeReference<Map<Integer, GameSnapshot>>() {
                });
            }

            //creates registry
            ModifierBuildingsRegistry registry = new ModifierBuildingsRegistry();
            InputStream input = PersistenceService.class.getResourceAsStream(ModelInstancesManager.JSON_PATH + "modifierCards.json");
            List<ModifierBuildingCard> modifierCards = mapper.readValue(input, new TypeReference<List<ModifierBuildingCard>>() {
            });
            modifierCards.forEach(registry::register);

            //fill backupMatches
            for (Map.Entry<Integer, GameSnapshot> entry : snapshotMap.entrySet()) {
                backupMatches.put(entry.getKey(), RestoredMatch.restoreGame(entry.getValue(), registry));
            }
        } catch (MismatchedInputException e) {
            if (backupFile.length() > 0) {
                System.err.println("[PersistenceService] Error while trying to load from backup file. The file might be corrupted.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the biggest match id saved in the backup.
     *
     * @return 0 if the file is empty, otherwise the biggest match id saved.
     */
    public int getBiggestBackupMatchId() {
        return backupMatches.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
    }

    /**
     * Restores all matches to previous state. Needs the match manager.
     *
     * @throws IllegalStateException if the matchManager has not been provided.
     */
    public void restoreMatches() throws IllegalStateException {
        if (matchManager == null) throw new IllegalStateException("Match manager not set.");
        backupMatches.forEach(matchManager::restoreMatch);
    }

    /**
     * Match manager setter.
     *
     * @param matchManager the {@link MatchManager}
     */
    public void setMatchManager(MatchManager matchManager) {
        this.matchManager = matchManager;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(Duration.of(backupCooldown, ChronoUnit.SECONDS));
                //overrides backupFile with new values
                ObjectMapper mapper = new ObjectMapper();
                mapper.enable(SerializationFeature.INDENT_OUTPUT); //to make JSON file more readable for debugging
                mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS); //for phase correct save
                Map<Integer, GameSnapshot> matchMap = new HashMap<>();
                for (Match m : matchManager.getActiveMatches()) {
                    GameSnapshot gs = m.getSnapshot();
                    if (gs != null) matchMap.put(m.getMatchId(), gs);
                }
                if (!matchMap.isEmpty()) {
                    //first writes to tmp file than ask SO to move all content to backupFile to avoid corruption due to crash
                    mapper.writeValue(tmpFile, matchMap);
                    Files.move(tmpFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                    System.out.println("Persistence service saved matches successfully (" + matchMap.keySet().stream().map(i -> String.valueOf(i))
                            .collect(Collectors.joining(", ")) + ")");
                }
            }
        } catch (InterruptedException _) {
        } catch (IOException e) {
            System.err.println("PersistenceService failed: ");
            e.printStackTrace();
        }
    }
}
