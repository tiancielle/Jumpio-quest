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
        // Image de fond floue (paysage de ferme)
        StackPane root = new StackPane();
        ImageView backgroundImage = null;
        try {
            File bgFile = new File("res/landscape.png");
            if (bgFile.exists()) {
                Image bgImg = new Image(bgFile.toURI().toString());
                backgroundImage = new ImageView(bgImg);
                backgroundImage.setFitWidth(800);
                backgroundImage.setFitHeight(600);
                backgroundImage.setPreserveRatio(false);
                backgroundImage.setStyle("-fx-effect: gaussianblur(25);");
            }
        } catch (Exception e) {
            System.out.println("Could not load background image: " + e.getMessage());
        }

        // Overlay léger pour garder l'ambiance colorée
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.35);");
        overlay.setPrefSize(800, 600);

        // Titre principal style jeu de plateforme
        Label resultLabel = new Label(win ? "🎉 YOU WIN! 🎉" : "GAME OVER");
        resultLabel.setFont(Font.font("Arial", 75));
        resultLabel.setTextFill(win ? Color.web("#FFD700") : Color.web("#FF6B6B"));
        resultLabel.setStyle(
            "-fx-font-weight: bold;" +
            "-fx-effect: dropshadow(gaussian, " + (win ? "#FF8C00" : "#8B0000") + ", 25, 0.6, 0, 6);"
        );

        // Conteneur de scores style carte colorée
        VBox scoreBox = new VBox(18);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setPadding(new Insets(35, 55, 35, 55));
        scoreBox.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.9);" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: " + (win ? "#4CAF50" : "#F44336") + ";" +
            "-fx-border-width: 4;" +
            "-fx-border-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 20, 0, 0, 5);"
        );

        Label scoreLabel = new Label("🌾 Score: " + scoreManager.getCurrentScore());
        scoreLabel.setFont(Font.font("Arial", 42));
        scoreLabel.setTextFill(Color.web("#2E7D32"));
        scoreLabel.setStyle("-fx-font-weight: bold;");

        Label bestLabel = new Label("🏆 Meilleur: " + scoreManager.getHighScore());
        bestLabel.setFont(Font.font("Arial", 28));
        bestLabel.setTextFill(Color.web("#F57C00"));
        bestLabel.setStyle("-fx-font-weight: bold;");

        scoreBox.getChildren().addAll(scoreLabel, bestLabel);

        // Bouton Nouvelle Partie - Style fermier vert
        Button newGameBtn = new Button(win ? "🌻 REJOUER" : "🔄 RÉESSAYER");
        newGameBtn.setPrefWidth(280);
        newGameBtn.setPrefHeight(60);
        newGameBtn.setFont(Font.font("Arial", 20));
        newGameBtn.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #66BB6A, #43A047);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 15;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: #2E7D32;" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 12, 0, 0, 4);"
        );
        newGameBtn.setOnAction(e -> {
            stage.hide();
            ScoreManager sm = new ScoreManager();
            MainMenu.show(stage, sm);
        });

        newGameBtn.setOnMouseEntered(e -> {
            newGameBtn.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #81C784, #66BB6A);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 15;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: #2E7D32;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 18, 0, 0, 6);"
            );
            newGameBtn.setScaleX(1.08);
            newGameBtn.setScaleY(1.08);
        });

        newGameBtn.setOnMouseExited(e -> {
            newGameBtn.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #66BB6A, #43A047);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 15;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: #2E7D32;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 12, 0, 0, 4);"
            );
            newGameBtn.setScaleX(1.0);
            newGameBtn.setScaleY(1.0);
        });

        // Bouton Quitter - Style bois/marron
        Button exitBtn = new Button("🚪 QUITTER");
        exitBtn.setPrefWidth(280);
        exitBtn.setPrefHeight(60);
        exitBtn.setFont(Font.font("Arial", 20));
        exitBtn.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #8D6E63, #6D4C41);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 15;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: #4E342E;" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 12, 0, 0, 4);"
        );
        exitBtn.setOnAction(e -> System.exit(0));

        exitBtn.setOnMouseEntered(e -> {
            exitBtn.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #A1887F, #8D6E63);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 15;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: #4E342E;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 18, 0, 0, 6);"
            );
            exitBtn.setScaleX(1.08);
            exitBtn.setScaleY(1.08);
        });

        exitBtn.setOnMouseExited(e -> {
            exitBtn.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #8D6E63, #6D4C41);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 15;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: #4E342E;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 12, 0, 0, 4);"
            );
            exitBtn.setScaleX(1.0);
            exitBtn.setScaleY(1.0);
        });

        // Conteneur de boutons
        VBox buttons = new VBox(22, newGameBtn, exitBtn);
        buttons.setAlignment(Pos.CENTER);

        // Layout principal
        VBox main = new VBox(45, resultLabel, scoreBox, buttons);
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