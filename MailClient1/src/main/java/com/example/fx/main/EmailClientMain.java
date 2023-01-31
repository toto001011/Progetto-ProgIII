package com.example.fx.main;

import com.example.fx.model.Client;
import com.example.fx.ui.ClientController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;

public class EmailClientMain extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        Client client;
        FXMLLoader fxmlLoader;
        Scene scene;

        ClientController controller;
        URL clientUrl = EmailClientMain.class.getResource("newMail.fxml");

            client=new Client("studente.0@edu.it");


            fxmlLoader=new FXMLLoader(clientUrl);
            scene=new Scene(fxmlLoader.load(),700,600);
            stage=new Stage();
            stage.setTitle("Email client di studente.0@edu.it");
            stage.setScene(scene);


            controller=fxmlLoader.getController();

            controller.initialize(client);
        stage.show();


        //Serve per far terminare il processo alla chiusura della finestra
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });




    }

    public static void main(String[] args) {
        launch();
    }
}