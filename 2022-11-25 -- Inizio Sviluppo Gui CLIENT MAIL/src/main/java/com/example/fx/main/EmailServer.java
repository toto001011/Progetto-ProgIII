package com.example.fx.main; /**
 @author Cay Horstmann
 @version 1.20 2004-08-03
 modificata...
 */

import java.io.*;
import java.net.*;
import java.util.*;

import com.example.fx.ui.ServerController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 This program implements a multithreaded server that listens to port 8990 and echoes back
 all client input.
 */
public class EmailServer extends Application {
    public static void main(String[] args ) {
        System.out.println("Finestra del server: ");
        launch();



    }





    @Override
    public void start(Stage stage) throws Exception {
        //URL clientUrl = EmailClientMain.class.getResource("newServer.fxml");
        URL clientUrl = EmailServer.class.getResource("newServer.fxml");

        FXMLLoader fxmlLoader = new FXMLLoader(clientUrl);


        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
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


