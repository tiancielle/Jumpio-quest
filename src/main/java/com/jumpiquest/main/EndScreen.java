package com.jumpiquest.main;

import java.io.File;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Reusable end screen for both win and lose states.
 */
public class EndScreen {

    /**
     * Show the end screen. If win==true shows "YOU WIN!" otherwise "GAME OVER".
     */
    public static void showEndScreen(Stage stage, ScoreManager scoreManager, boolean win) {
        // Image de fond avec léger flou
        StackPane root = new StackPane();
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

        // Overlay très léger pour ne pas assombrir
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(255, 255, 255, 0.15);");
        overlay.setPrefSize(800, 600);

        // Titre principal coloré et fun
        Label resultLabel = new Label(win ? " YOU WIN! " : " GAME OVER ");
        resultLabel.setFont(Font.font("Arial", 80));
        resultLabel.setTextFill(win ? Color.web("#FFD700") : Color.web("#FF6B6B"));
        resultLabel.setStyle(
            "-fx-font-weight: bold;" +
            "-fx-effect: dropshadow(gaussian, rgba(255, 255, 255, 0.8), 15, 0.5, 0, 0)," +
            "           dropshadow(gaussian, rgba(0, 0, 0, 0.6), 20, 0, 0, 5);"
        );

        // Carte de scores claire et colorée
        VBox scoreBox = new VBox(20);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setPadding(new Insets(40, 60, 40, 60));
        scoreBox.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.95);" +
            "-fx-background-radius: 25;" +
            "-fx-border-color: " + (win ? "#FFA500" : "#FF6B6B") + ";" +
            "-fx-border-width: 5;" +
            "-fx-border-radius: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 25, 0, 0, 8);"
        );

        Label scoreLabel = new Label("Score: " + scoreManager.getCurrentScore());
        scoreLabel.setFont(Font.font("Arial", 50));
        scoreLabel.setTextFill(win ? Color.web("#FF8C00") : Color.web("#FF6B6B"));
        scoreLabel.setStyle("-fx-font-weight: bold;");

        Label bestLabel = new Label("🏆 Record: " + scoreManager.getHighScore());
        bestLabel.setFont(Font.font("Arial", 32));
        bestLabel.setTextFill(Color.web("#444444"));
        bestLabel.setStyle("-fx-font-weight: bold;");

        scoreBox.getChildren().addAll(scoreLabel, bestLabel);

        // Bouton Rejouer - Coloré et fun
        Button newGameBtn = new Button(win ? "REPLAY" : "NEW GAME");
        newGameBtn.setPrefWidth(300);
        newGameBtn.setPrefHeight(70);
        newGameBtn.setFont(Font.font("Arial", 24));
        String mainColor = win ? "#4CAF50" : "#2196F3";
        String lightColor = win ? "#81C784" : "#64B5F6";
        newGameBtn.setStyle(
            "-fx-background-color: " + mainColor + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 5);"
        );
        newGameBtn.setOnAction(e -> {
            stage.hide();
            ScoreManager sm = new ScoreManager();
            MainMenu.show(stage, sm);
        });

        newGameBtn.setOnMouseEntered(e -> {
            newGameBtn.setStyle(
                "-fx-background-color: " + lightColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 20;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 20, 0, 0, 7);"
            );
            newGameBtn.setScaleX(1.1);
            newGameBtn.setScaleY(1.1);
        });

        newGameBtn.setOnMouseExited(e -> {
            newGameBtn.setStyle(
                "-fx-background-color: " + mainColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 20;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 5);"
            );
            newGameBtn.setScaleX(1.0);
            newGameBtn.setScaleY(1.0);
        });

        // Bouton Quitter - Simple et clair
        Button exitBtn = new Button("EXIT");
        exitBtn.setPrefWidth(300);
        exitBtn.setPrefHeight(70);
        exitBtn.setFont(Font.font("Arial", 24));
        exitBtn.setStyle(
            "-fx-background-color: #FF9800;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 5);"
        );
        exitBtn.setOnAction(e -> System.exit(0));

        exitBtn.setOnMouseEntered(e -> {
            exitBtn.setStyle(
                "-fx-background-color: #FFB74D;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 20;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 20, 0, 0, 7);"
            );
            exitBtn.setScaleX(1.1);
            exitBtn.setScaleY(1.1);
        });

        exitBtn.setOnMouseExited(e -> {
            exitBtn.setStyle(
                "-fx-background-color: #FF9800;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 20;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 5);"
            );
            exitBtn.setScaleX(1.0);
            exitBtn.setScaleY(1.0);
        });

        // Conteneur de boutons
        VBox buttons = new VBox(25, newGameBtn, exitBtn);
        buttons.setAlignment(Pos.CENTER);

        // Layout principal
        VBox main = new VBox(50, resultLabel, scoreBox, buttons);
        main.setAlignment(Pos.CENTER);
        main.setPadding(new Insets(50));

        // Assemblage
        if (backgroundImage != null) {
            root.getChildren().add(backgroundImage);
        }
        root.getChildren().addAll(overlay, main);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Jumpio Quest - " + (win ? "Victory" : "Defeat"));
        stage.show();
    }
}