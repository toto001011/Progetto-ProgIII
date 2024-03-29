package com.example.fx.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;


public class EmailServer extends Application {
    public static void main(String[] args ) {
        System.out.println("Finestra del server: ");
        launch();



    }





    @Override
    public void start(Stage stage) throws Exception {
        URL clientUrl = EmailServer.class.getResource("newServer.fxml");

        FXMLLoader fxmlLoader = new FXMLLoader(clientUrl);


        Scene scene = new Scene(fxmlLoader.load(), 500, 400);
        stage.setTitle("Email server");
        stage.setScene(scene);
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


}


