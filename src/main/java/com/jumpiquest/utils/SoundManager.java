package com.jumpiquest.utils;

import java.io.File;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Utility class to manage background music and short SFX.
 */
public final class SoundManager {
    private static MediaPlayer bgPlayer = null;
    private static final Object LOCK = new Object();

    // Cached Media objects for short sounds
    private static Media mediaWin = null;
    private static Media mediaGameOver = null;
    private static Media mediaJump = null;
    private static Media mediaCoin = null;
    private static Media mediaCollision = null;

    private SoundManager() { }

    public static void preloadSfx() {
        // load media objects once (silent if files missing)
        try {
            mediaWin = loadMediaIfExists("res/sounds/win.wav");
            mediaGameOver = loadMediaIfExists("res/sounds/game_over_loss.wav");
            mediaJump = loadMediaIfExists("res/sounds/jump.wav");
            mediaCoin = loadMediaIfExists("res/sounds/coin.wav");
            mediaCollision = loadMediaIfExists("res/sounds/collision.wav");
        } catch (Exception e) {
            System.out.println("Could not preload SFX: " + e.getMessage());
        }
    }

    private static Media loadMediaIfExists(String path) {
        File f = new File(path);
        if (!f.exists()) return null;
        return new Media(f.toURI().toString());
    }

    /**
     * Start playing the background music (looped). Safe to call multiple times.
     */
    public static void playBackground() {
        synchronized (LOCK) {
            try {
                if (bgPlayer != null) {
                    MediaPlayer.Status st = bgPlayer.getStatus();
                    if (st == MediaPlayer.Status.PLAYING) return;
                    if (st == MediaPlayer.Status.PAUSED || st == MediaPlayer.Status.STOPPED) {
                        bgPlayer.play();
                        return;
                    }
                }

                File f = new File("res/sounds/platformer.wav");
                if (!f.exists()) {
                    System.out.println("Background music file not found: " + f.getPath());
                    return;
                }

                Media media = new Media(f.toURI().toString());
                bgPlayer = new MediaPlayer(media);
                bgPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                bgPlayer.setVolume(0.35);
                bgPlayer.setOnError(() -> {
                    System.out.println("Background music error: " + bgPlayer.getError());
                });
                bgPlayer.play();

                // Ensure SFX are preloaded when music starts (so media subsystem warms up)
                preloadSfx();
            } catch (Exception e) {
                System.out.println("Could not start background music: " + e.getMessage());
            }
        }
    }

    /**
     * Stop and dispose the background music player.
     */
    public static void stopBackground() {
        synchronized (LOCK) {
            try {
                if (bgPlayer != null) {
                    try { bgPlayer.stop(); } catch (Exception ignore) {}
                    try { bgPlayer.dispose(); } catch (Exception ignore) {}
                    bgPlayer = null;
                }
            } catch (Exception e) {
                System.out.println("Could not stop background music: " + e.getMessage());
            }
        }
    }

    // Helper to play a one-shot sound. Creates a temporary MediaPlayer and disposes it when finished.
    private static void playOnce(Media media, double volume) {
        if (media == null) return;
        try {
            MediaPlayer mp = new MediaPlayer(media);
            mp.setVolume(volume);
            mp.setOnError(() -> System.out.println("SFX error: " + mp.getError()));
            mp.setOnEndOfMedia(() -> {
                try { mp.dispose(); } catch (Exception ignore) {}
            });
            mp.play();
        } catch (Exception e) {
            System.out.println("Could not play SFX: " + e.getMessage());
        }
    }

    public static void playWin() {
        // stop background first to emphasize the win sound
        stopBackground();
        playOnce(mediaWin, 0.5);
    }

    public static void playGameOver() {
        stopBackground();
        playOnce(mediaGameOver, 0.5);
    }

    public static void playJump() {
        playOnce(mediaJump, 0.45);
    }

    public static void playCoin() {
        playOnce(mediaCoin, 0.45);
    }

    public static void playCollision() {
        playOnce(mediaCollision, 0.5);
    }
}
