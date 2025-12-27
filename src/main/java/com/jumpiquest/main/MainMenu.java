package com.jumpiquest.main;

import java.io.File;

import com.jumpiquest.engine.Engine;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainMenu {
    public static void show(Stage stage, ScoreManager scoreManager) {
        // Image de fond avec léger flou
        StackPane rootStack = new StackPane();
        ImageView backgroundImage = null;
        try {
            File bgFile = new File("res/landscape.jpg");
            if (bgFile.exists()) {
                Image bgImg = new Image(bgFile.toURI().toString());
                backgroundImage = new ImageView(bgImg);
                backgroundImage.setFitWidth(800);
                backgroundImage.setFitHeight(600);
                backgroundImage.setPreserveRatio(false);
                backgroundImage.setStyle("-fx-effect: gaussianblur(15);");
            }
        } catch (Exception e) {
            System.out.println("Could not load background image: " + e.getMessage());
        }

        // Overlay très léger
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(255, 255, 255, 0.15);");
        overlay.setPrefSize(800, 600);

        // Titre coloré et fun
        Label title = new Label("JUMPIO QUEST");
        title.setFont(Font.font("Arial", 75));
        title.setTextFill(Color.web("#FFD700"));
        title.setStyle(
            "-fx-font-weight: bold;" +
            "-fx-alignment: center;" +
            "-fx-effect: dropshadow(gaussian, rgba(255, 255, 255, 0.8), 15, 0.5, 0, 0)," +
            "           dropshadow(gaussian, rgba(0, 0, 0, 0.6), 20, 0, 0, 5);"
        );

        // Meilleur score
        Label scoreLabel = new Label("🏆 Record: " + scoreManager.getHighScore());
        scoreLabel.setFont(Font.font("Arial", 28));
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setStyle(
            "-fx-font-weight: bold;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 10, 0, 0, 3);"
        );

        // Panneau de difficulté coloré
        Label difficultyLabel = new Label("choose you level of difficulty:");
        difficultyLabel.setFont(Font.font("Arial", 24));
        difficultyLabel.setTextFill(Color.web("#FF6B6B"));
        difficultyLabel.setStyle("-fx-font-weight: bold;");

        ToggleGroup tg = new ToggleGroup();
        
        RadioButton rbEasy = createDifficultyButton("😊 Easy", GameSettings.Difficulty.FACILE, tg, true);
        RadioButton rbMedium = createDifficultyButton("😎 Medium", GameSettings.Difficulty.MOYEN, tg, false);
        RadioButton rbHard = createDifficultyButton("🔥 Hard", GameSettings.Difficulty.DIFFICILE, tg, false);

        HBox difficultyBox = new HBox(20, rbEasy, rbMedium, rbHard);
        difficultyBox.setAlignment(Pos.CENTER);

        VBox difficultyPanel = new VBox(15, difficultyLabel, difficultyBox);
        difficultyPanel.setAlignment(Pos.CENTER);
        difficultyPanel.setPadding(new Insets(30, 50, 30, 50));
        difficultyPanel.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.95);" +
            "-fx-background-radius: 25;" +
            "-fx-border-color: #FF6B6B;" +
            "-fx-border-width: 5;" +
            "-fx-border-radius: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 6);"
        );

        // Gros bouton JOUER coloré
        Button startBtn = new Button("PLAY NOW");
        startBtn.setPrefWidth(320);
        startBtn.setPrefHeight(80);
        startBtn.setFont(Font.font("Arial", 32));
        startBtn.setStyle(
            "-fx-background-color: #4CAF50;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 25;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 20, 0, 0, 6);"
        );
        
        startBtn.setOnAction(e -> {
            if (tg.getSelectedToggle() != null) {
                GameSettings.setDifficulty((GameSettings.Difficulty) tg.getSelectedToggle().getUserData());
            } else {
                GameSettings.setDifficulty(GameSettings.Difficulty.FACILE);
            }

            scoreManager.resetCurrentScore();
            
            Canvas canvas = new Canvas(800, 600);
            StackPane root = new StackPane(canvas);
            Engine engine = new Engine(canvas, scoreManager, stage, root);
            Scene gameScene = new Scene(root);
            engine.attachInput(gameScene);

            stage.setScene(gameScene);
            stage.setTitle("Jumpio Quest - " + GameSettings.getDifficulty().name());
            stage.show();

            engine.start();
        });

        startBtn.setOnMouseEntered(e -> {
            startBtn.setStyle(
                "-fx-background-color: #66BB6A;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 25;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 25, 0, 0, 8);"
            );
            startBtn.setScaleX(1.12);
            startBtn.setScaleY(1.12);
        });

        startBtn.setOnMouseExited(e -> {
            startBtn.setStyle(
                "-fx-background-color: #4CAF50;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 25;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 20, 0, 0, 6);"
            );
            startBtn.setScaleX(1.0);
            startBtn.setScaleY(1.0);
        });

        // Layout principal
        VBox vbox = new VBox(40, title, scoreLabel, difficultyPanel, startBtn);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(40));

        // Assemblage
        if (backgroundImage != null) {
            rootStack.getChildren().add(backgroundImage);
        }
        rootStack.getChildren().addAll(overlay, vbox);
        rootStack.setAlignment(Pos.CENTER);

        Scene menuScene = new Scene(rootStack, 800, 600);
        stage.setScene(menuScene);
        stage.setTitle("Jumpio Quest - Menu");
        stage.show();
    }

    private static RadioButton createDifficultyButton(String text, GameSettings.Difficulty difficulty, 
                                                      ToggleGroup tg, boolean selected) {
        RadioButton rb = new RadioButton(text);
        rb.setToggleGroup(tg);
        rb.setUserData(difficulty);
        rb.setSelected(selected);
        rb.setFont(Font.font("Arial", 20));
        rb.setMinWidth(140);
        rb.setPrefWidth(140);
        rb.setMaxWidth(140);
        rb.setStyle(
            "-fx-text-fill: #333333;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;"
        );

        // Effet hover sans scale pour éviter les bugs
        rb.setOnMouseEntered(e -> {
            rb.setStyle(
                "-fx-text-fill: #FF6B6B;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;" +
                "-fx-underline: true;" +
                "-fx-min-width: 140;" +
                "-fx-pref-width: 140;" +
                "-fx-max-width: 140;"
            );
        });

        rb.setOnMouseExited(e -> {
            rb.setStyle(
                "-fx-text-fill: #333333;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;" +
                "-fx-min-width: 140;" +
                "-fx-pref-width: 140;" +
                "-fx-max-width: 140;"
            );
        });

        return rb;
    }
}