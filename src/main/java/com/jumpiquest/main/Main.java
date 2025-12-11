package com.jumpiquest.main;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Show main menu first. Create a ScoreManager and pass it to the menu.
        ScoreManager scoreManager = new ScoreManager();
        MainMenu.show(stage, scoreManager);
    }
}
