package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FlexMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("flexbox.fxml"));
        primaryStage.setTitle("HttpPost2 - 风的影子");
        primaryStage.setScene(new Scene(root, 640, 640));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
