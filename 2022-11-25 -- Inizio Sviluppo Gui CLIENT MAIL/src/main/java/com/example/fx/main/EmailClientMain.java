package com.example.fx.main;

import com.example.fx.model.Client;
import com.example.fx.ui.ClientController;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

public class EmailClientMain extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        int i=0;

            Client c1 = new Client("s@edu.it");
            URL clientUrl = EmailClientMain.class.getResource("newMail.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(clientUrl);

            Scene scene = new Scene(fxmlLoader.load(), 900, 600);
            stage.setTitle("Email client1");
            stage.setScene(scene);
            ClientController contr = fxmlLoader.getController();
            contr.initialize(c1);
            stage.show();
            i++;



            Client c2 = new Client("q@edu.it");

            FXMLLoader fxmlLoader2 = new FXMLLoader(clientUrl);
            Scene scene2 = new Scene(fxmlLoader2.load(), 900, 600);
            Stage stage2 = new Stage();
            stage2.setTitle("Email client1!");
            stage2.setScene(scene2);

            ClientController contr2 = fxmlLoader2.getController();

            contr2.initialize(c2);
            stage2.show();
            i++;



       /* FXMLLoader fxmlLoader2 = new FXMLLoader(EmailClientMain.class.getResource("newMail.fxml"));
        Scene scene2 = new Scene(fxmlLoader2.load(), 900, 600);
        Stage stage2 = new Stage();
        stage2.setTitle("Email client2");
        stage2.setScene(scene2);
        ClientController contr2 = new ClientController("s@edu.it");//fxmlLoader2.getController();
      //  contr2.initialize();
        stage2.show();
/*
        /*FXMLLoader fxmlLoader3 = new FXMLLoader(EmailClientMain.class.getResource("newMail.fxml"));
        Scene scene3 = new Scene(fxmlLoader2.load(), 900, 600);
        Stage stage3 = new Stage();
        stage2.setTitle("Email client2");
        stage2.setScene(scene2);
        ClientController contr3 = new ClientController("q@edu.it");//fxmlLoader2.getController();
       // contr3.initialize();
        stage3.show();*/

    }

    public static void main(String[] args) {
        launch();
    }
}