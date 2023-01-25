package com.example.fx.main;

import com.example.fx.model.Client;
import com.example.fx.ui.ClientController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

public class EmailClientMain extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        //ArrayList<Client> clients=new ArrayList<>();
        Client client;
     //   ArrayList<FXMLLoader> fxmlLoaders=new ArrayList<>();
        FXMLLoader fxmlLoader;
        //ArrayList<Scene>scenes=new ArrayList<>();
        Scene scene;
       // ArrayList<Stage> stages=new ArrayList<>();
       // Stage stage;
       // ArrayList<ClientController> controllers=new ArrayList<>();
        ClientController controller;
        URL clientUrl = EmailClientMain.class.getResource("newMail.fxml");

        //for(int i=0;i<3;i++) {
            //clients.add(new Client("studente."+i+"@edu.it"));
            client=new Client("studente.0@edu.it");

            //FXMLLoader fxmlLoader2 = new FXMLLoader(clientUrl);
            //fxmlLoaders.add(new FXMLLoader(clientUrl));
            fxmlLoader=new FXMLLoader(clientUrl);
            //scenes.add(new Scene(fxmlLoaders.get(i).load(), 900, 600));
            scene=new Scene(fxmlLoader.load(),700,600);
            //stages.add(new Stage());
            stage=new Stage();
            //stages.get(i).setTitle("Email client"+i+"!");
            stage.setTitle("Email client di studente.0@edu.it");
            //stages.get(i).setScene(scenes.get(i));
            stage.setScene(scene);


            //controllers.add(fxmlLoaders.get(i).getController());
            controller=fxmlLoader.getController();

            //controllers.get(i).initialize(clients.get(i));
            controller.initialize(client);
            //stages.get(i).show();
        stage.show();

      //  }


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