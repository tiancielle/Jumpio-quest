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
 * Game End Screen - shown when player wins or loses.
 * Displays result message and two buttons: New Game and Exit.
 */
public class GameEndScreen {
    private Stage stage;
    private ScoreManager scoreManager;
    private boolean isWon;

    public GameEndScreen(Stage stage, ScoreManager scoreManager, boolean isWon) {
        this.stage = stage;
        this.scoreManager = scoreManager;
        this.isWon = isWon;
    }

    /**
     * Show the end screen with appropriate message and buttons.
     */
    public void show() {
        // Result text at the top
        String resultText = isWon ? "YOU WIN!" : "GAME OVER";
        Label resultLabel = new Label(resultText);
        resultLabel.setFont(Font.font("Arial", 64));
        resultLabel.setTextFill(isWon ? Color.GOLD : Color.RED);
        resultLabel.setStyle("-fx-font-weight: bold;");

        // Score display
        Label scoreDisplayLabel = new Label("Score: " + scoreManager.getCurrentScore());
        scoreDisplayLabel.setFont(Font.font("Arial", 28));
        scoreDisplayLabel.setTextFill(Color.WHITE);

        Label bestScoreLabel = new Label("Best: " + scoreManager.getHighScore());
        bestScoreLabel.setFont(Font.font("Arial", 28));
        bestScoreLabel.setTextFill(Color.WHITE);

        // Buttons layout: New Game and Exit side by side
        VBox scoreBox = new VBox(10, scoreDisplayLabel, bestScoreLabel);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setPadding(new Insets(20));

        // New Game button with image
        Button newGameBtn = createImageButton("newgame.png", 100, 100);
        newGameBtn.setStyle("-fx-cursor: hand;");
        newGameBtn.setOnAction(e -> {
            // Go back to main menu
            MainMenu.show(stage, new ScoreManager());
        });

        // Exit button with image
        Button exitBtn = createImageButton("exit.png", 100, 100);
        exitBtn.setStyle("-fx-cursor: hand;");
        exitBtn.setOnAction(e -> {
            // Close the game
            System.exit(0);
        });

        // Button container
        VBox buttonContainer = new VBox(20, newGameBtn, exitBtn);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(30));

        // Main layout: title, score, buttons
        VBox mainLayout = new VBox(30, resultLabel, scoreBox, buttonContainer);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(40));
        mainLayout.setStyle("-fx-background-color: #1a1a1a;");

        StackPane root = new StackPane(mainLayout);
        root.setStyle("-fx-background-color: #1a1a1a;");

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Jumpio Quest - Game End");
        stage.show();
    }

    /**
     * Create a button with an image sprite.
     * @param imageName name of the image file in res/
     * @param width button width
     * @param height button height
     * @return a styled button with the image
     */
    private Button createImageButton(String imageName, double width, double height) {
        Button btn = new Button();
        btn.setPrefWidth(width);
        btn.setPrefHeight(height);
        btn.setStyle("-fx-padding: 0; -fx-background-color: transparent;");

        try {
            File f = new File("res/" + imageName);
            if (f.exists()) {
                Image img = new Image(f.toURI().toString());
                ImageView imgView = new ImageView(img);
                imgView.setFitWidth(width);
                imgView.setFitHeight(height);
                imgView.setPreserveRatio(true);
                btn.setGraphic(imgView);
            }
        } catch (Exception e) {
            System.out.println("Could not load button image " + imageName + ": " + e.getMessage());
            // Fallback: use text
            btn.setText(imageName.replace(".png", "").toUpperCase());
        }

        return btn;
    }
}
