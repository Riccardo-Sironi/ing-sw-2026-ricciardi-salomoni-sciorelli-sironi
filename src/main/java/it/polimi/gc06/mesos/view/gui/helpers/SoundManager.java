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

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void playStartMusic() {
        backgroundMusic = startMedia;
        switchMusic(startMedia);
    }

    public void playLobbyMusic() {
        backgroundMusic = lobbyMedia;
        switchMusic(lobbyMedia);
    }

    public void playGameMusic() {
        backgroundMusic = gameMedia;
        switchMusic(gameMedia);
    }

    public void playEraIIIMusic() {
        backgroundMusic = era3Media;
        switchMusic(era3Media);
    }

    public void playClick() {
        if (!isMuted) clickSound.play();
    }

    public void playError() {
        if (!isMuted) errorSound.play();
    }

    public void playTotem() {
        if (!isMuted) totemSound.play();
    }

    public void playCardFlip() {
        if (!isMuted) {
            //cardFlipSound.stop();
            cardFlipSound.play();
        }
    }

    public void playPickCard() {
        if (!isMuted) {
            //pickCardSound.stop();
            pickCardSound.play();
        }
    }

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

    private void startNewTrack(Media media) {
        currentMusicPlayer = new MediaPlayer(media);
        currentMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        currentMusicPlayer.setVolume(0.0);
        currentMusicPlayer.play();

        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.seconds(2.0), new KeyValue(currentMusicPlayer.volumeProperty(), TARGET_VOLUME))
        );
        fadeIn.play();
    }

    public void startBackgroundMusic() {
        if (backgroundMusic != null && !isMuted) {
            switchMusic(backgroundMusic);
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            currentMusicPlayer.stop();
            currentMusicPlayer = null;
        }
    }

    public void toggleMute() {
        isMuted = !isMuted;
        if (isMuted) {
            stopBackgroundMusic();
        } else {
            startBackgroundMusic();
        }
    }
}