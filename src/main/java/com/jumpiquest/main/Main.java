package com.jumpiquest.main;

import com.jumpiquest.engine.Engine;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(800, 600);
        Engine engine = new Engine(canvas);

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root);

        engine.attachInput(scene);

        stage.setScene(scene);
        stage.setTitle("Jumpio Quest - Minimal");
        stage.show();

        engine.start();
    }
}
