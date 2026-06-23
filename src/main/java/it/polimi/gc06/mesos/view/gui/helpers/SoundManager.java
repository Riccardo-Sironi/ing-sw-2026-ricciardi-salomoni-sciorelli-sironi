package it.polimi.gc06.mesos.view.gui.helpers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class SoundManager {
    private static SoundManager instance;
    private boolean isMuted = false;
    private final double TARGET_VOLUME = 0.15;
    private static final double VOLUME_SCALING_FACTOR = 0.5;

    private double userVolume = TARGET_VOLUME;

    private final AudioClip clickSound;
    private final AudioClip errorSound;
    private final AudioClip totemSound;
    private final AudioClip cardFlipSound;
    private final AudioClip pickCardSound;


    private final Media lobbyMedia;
    private final Media gameMedia;
    private final Media startMedia;
    private final Media era3Media;

    private Media backgroundMusic;

    private MediaPlayer currentMusicPlayer;

    private SoundManager() {
        clickSound = new AudioClip(getClass().getResource("/sounds/button_pressed.mp3").toExternalForm());
        errorSound = new AudioClip(getClass().getResource("/sounds/error.m4a").toExternalForm());
        totemSound = new AudioClip(getClass().getResource("/sounds/place_item.mp3").toExternalForm());
        cardFlipSound = new AudioClip(getClass().getResource("/sounds/card_flip.mp3").toExternalForm());
        pickCardSound = new AudioClip(getClass().getResource("/sounds/pick_card.mp3").toExternalForm());

        startMedia = new Media(getClass().getResource("/sounds/start_bgm.mp3").toExternalForm());
        lobbyMedia = new Media(getClass().getResource("/sounds/lobby_bgm.mp3").toExternalForm());
        gameMedia = new Media(getClass().getResource("/sounds/game_bgm.m4a").toExternalForm());
        era3Media = new Media(getClass().getResource("/sounds/era3_bgm.mp3").toExternalForm());

        backgroundMusic = startMedia;

    }

    /**
     * Get the singleton instance of the SoundManager class. If the instance does not exist, it will be created.
     *
     * @return the {@link SoundManager} instance
     */
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    /**
     * Play the start music track. This method sets the background music to the start media and switches to it.
     */
    public void playStartMusic() {
        backgroundMusic = startMedia;
        switchMusic(startMedia);
    }

    /**
     * Play the lobby music track. This method sets the background music to the lobby media and switches to it.
     */
    public void playLobbyMusic() {
        backgroundMusic = lobbyMedia;
        switchMusic(lobbyMedia);
    }

    /**
     * Play the game music track. This method sets the background music to the game media and switches to it.
     */
    public void playGameMusic() {
        backgroundMusic = gameMedia;
        switchMusic(gameMedia);
    }

    /**
     * Play the era 3 music track. This method sets the background music to the era 3 media and switches to it.
     */
    public void playEraIIIMusic() {
        backgroundMusic = era3Media;
        switchMusic(era3Media);
    }

    /**
     * Play the click sound effect.
     */
    public void playClick() {
        if (!isMuted) clickSound.play();
    }

    /**
     * Play the error sound effect.
     */
    public void playError() {
        if (!isMuted) errorSound.play();
    }

    /**
     * Play the totem sound effect.
     */
    public void playTotem() {
        if (!isMuted) totemSound.play();
    }

    /**
     * Play the card flip sound effect.
     */
    public void playCardFlip() {
        if (!isMuted) {
            //cardFlipSound.stop();
            cardFlipSound.play();
        }
    }

    /**
     * Play the pick card sound effect.
     */
    public void playPickCard() {
        if (!isMuted) {
            //pickCardSound.stop();
            pickCardSound.play();
        }
    }

    /**
     * Switch the background music to a new track. If the new track is the same as the current one, no action is taken.
     * If there is a current track playing, it will fade out before switching to the new track.
     *
     * @param newMedia the {@link Media} object representing the new music track to switch to
     */
    private void switchMusic(Media newMedia) {
        if (isMuted) return;

        if (currentMusicPlayer == null) {
            startNewTrack(newMedia);
            return;
        }

        if (currentMusicPlayer.getMedia().getSource().equals(newMedia.getSource())) {
            return;
        }

        Timeline fadeOut = new Timeline(
                new KeyFrame(Duration.seconds(1.5), new KeyValue(currentMusicPlayer.volumeProperty(), 0.0))
        );

        fadeOut.setOnFinished(event -> {
            currentMusicPlayer.stop();
            startNewTrack(newMedia);
        });

        fadeOut.play();
    }

    /**
     * Start a new music track.
     *
     * @param media the {@link Media} object representing the music track to start
     */
    private void startNewTrack(Media media) {
        currentMusicPlayer = new MediaPlayer(media);
        currentMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        currentMusicPlayer.setVolume(0.0);
        currentMusicPlayer.play();

        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.seconds(2.0), new KeyValue(currentMusicPlayer.volumeProperty(), userVolume * VOLUME_SCALING_FACTOR))
        );
        fadeIn.play();
    }

    /**
     * Start the background music for the game.
     */
    public void startBackgroundMusic() {
        if (backgroundMusic != null && !isMuted) {
            switchMusic(backgroundMusic);
        }
    }

    /**
     * Stop the background music for the game.
     */
    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            currentMusicPlayer.stop();
            currentMusicPlayer = null;
        }
    }

    /**
     * Raise in game volume.
     *
     * @param increment the amount to increase the volume by, in the range [0.0, 1.0]
     */
    public void raiseVolume(double increment) {
        userVolume += increment;
        if (userVolume > 1.0) {
            userVolume = 1.0;
        }

        if (currentMusicPlayer != null && !isMuted) {
            currentMusicPlayer.setVolume(userVolume * VOLUME_SCALING_FACTOR);
        }
    }

    /**
     * Lower in game volume.
     *
     * @param decrement the amount to decrease the volume by, in the range [0.0 ,1.0]
     */
    public void lowerVolume(double decrement) {
        userVolume -= decrement;
        if (userVolume < 0.0) {
            userVolume = 0.0;
        }

        if (currentMusicPlayer != null && !isMuted) {
            currentMusicPlayer.setVolume(userVolume * VOLUME_SCALING_FACTOR);
        }
    }

    /**
     * Get the current user volume.
     *
     * @return the double value representing the current volume.
     */
    public double getVolume() {
        return userVolume;
    }

    /**
     * Get if the sound is muted or not.
     *
     * @return true if the sound is muted, false otherwise.
     */
    public boolean isMuted() {
        return isMuted;
    }

    /**
     * Toggle the mute for the in game sounds in general.
     */
    public void toggleMute() {
        isMuted = !isMuted;
        if (isMuted) {
            stopBackgroundMusic();
        } else {
            startBackgroundMusic();
        }
    }
}