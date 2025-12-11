package com.jumpiquest.main;

import com.jumpiquest.engine.Engine;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainMenu {
    public static void show(Stage stage, ScoreManager scoreManager) {
    Label title = new Label("JUMPIO\nQUEST");
    title.setFont(Font.font(48));
    title.getStyleClass().add("title");

    Label scoreLabel = new Label("Best: " + scoreManager.getHighScore());
        scoreLabel.setFont(Font.font(18));
        scoreLabel.getStyleClass().add("score-label");

        ToggleGroup tg = new ToggleGroup();
        RadioButton rbEasy = new RadioButton("Facile");
        rbEasy.setToggleGroup(tg);
        rbEasy.setUserData(GameSettings.Difficulty.FACILE);
        RadioButton rbMedium = new RadioButton("Moyen");
        rbMedium.setToggleGroup(tg);
        rbMedium.setUserData(GameSettings.Difficulty.MOYEN);
        RadioButton rbHard = new RadioButton("Difficile");
        rbHard.setToggleGroup(tg);
        rbHard.setUserData(GameSettings.Difficulty.DIFFICILE);
        rbEasy.setSelected(true);

        HBox difficultyBox = new HBox(20, rbEasy, rbMedium, rbHard);
        difficultyBox.setAlignment(Pos.CENTER);
        difficultyBox.getStyleClass().add("difficulty-row");

        // style radio buttons to look like pixel toggles
        rbEasy.getStyleClass().add("pixel-toggle");
        rbMedium.getStyleClass().add("pixel-toggle");
        rbHard.getStyleClass().add("pixel-toggle");

        // Create START button with play.png image
        Button startBtn = new Button();
        startBtn.setPrefWidth(120);
        startBtn.setPrefHeight(120);
        startBtn.setStyle("-fx-padding: 0; -fx-background-color: transparent;");
        
        // Load and set play.png image
        try {
            java.io.File playFile = new java.io.File("res/play.png");
            if (playFile.exists()) {
                javafx.scene.image.Image playImage = new javafx.scene.image.Image(playFile.toURI().toString());
                javafx.scene.image.ImageView playImageView = new javafx.scene.image.ImageView(playImage);
                playImageView.setFitWidth(120);
                playImageView.setFitHeight(120);
                playImageView.setPreserveRatio(true);
                startBtn.setGraphic(playImageView);
            } else {
                startBtn.setText("START");
                startBtn.setMinWidth(160);
                startBtn.getStyleClass().add("pixel-button");
            }
        } catch (Exception ex) {
            startBtn.setText("START");
            startBtn.setMinWidth(160);
            startBtn.getStyleClass().add("pixel-button");
        }
        
        startBtn.getStyleClass().add("pixel-button");
        startBtn.setOnAction(e -> {
            // store chosen difficulty
            if (tg.getSelectedToggle() != null) {
                GameSettings.setDifficulty((GameSettings.Difficulty) tg.getSelectedToggle().getUserData());
            } else {
                GameSettings.setDifficulty(GameSettings.Difficulty.FACILE);
            }

            // Reset score for new game and create engine with scoreManager
            scoreManager.resetCurrentScore();
            
            // create and launch game scene using existing game code
            Canvas canvas = new Canvas(800, 600);
            Engine engine = new Engine(canvas, scoreManager, stage);
            StackPane root = new StackPane(canvas);
            Scene gameScene = new Scene(root);
            engine.attachInput(gameScene);

            stage.setScene(gameScene);
            stage.setTitle("Jumpio Quest - " + GameSettings.getDifficulty().name());
            stage.show();

            engine.start();
        });

        VBox vbox = new VBox(18, title, scoreLabel, difficultyBox, startBtn);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(24));
        vbox.getStyleClass().add("menu-panel");

        StackPane rootStack = new StackPane(vbox);
        rootStack.getStyleClass().add("menu-root");
        rootStack.setAlignment(Pos.CENTER);

        Scene menuScene = new Scene(rootStack, 800, 600);

        // Load the pixel theme stylesheet (uses landscape.jpg from resources)
        try {
            String css = MainMenu.class.getResource("/menu.css").toExternalForm();
            menuScene.getStylesheets().add(css);
        } catch (Exception ex) {
            System.out.println("Could not load menu stylesheet: " + ex.getMessage());
        }

        stage.setScene(menuScene);
        stage.setTitle("Jumpio Quest - Menu");
        stage.show();
    }
}
