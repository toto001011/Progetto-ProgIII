package com.example.fx.ui;

import com.example.fx.main.EmailClientMain;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class popUp extends Application {

    public static void main(String[] args){
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        URL clientUrl1 = EmailClientMain.class.getResource("popUp.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(clientUrl1);

        Scene scene = new Scene(fxmlLoader.load(), 300, 300);
        stage.setTitle("New messages");
        stage.setScene(scene);
        stage.show();


        Task task = new Task<Void>() {
            @Override
            public Void call() throws IOException {
                System.out.println("INIZIO THREAD ASCOLTO ");


                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText("Look, an Information Dialog");
                    alert.setContentText("I have a great message for you!");
                    alert.show();
                });




                return null;

            }
        };
        new Thread(task).start();

    }



}
