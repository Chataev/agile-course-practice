package ru.unn.agile.Metrics.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(final Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("DistanceCalculator.fxml"));
        primaryStage.setTitle("Distance calculator");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
