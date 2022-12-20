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
import java.util.ArrayList;

public class EmailClientMain extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        ArrayList<Client> clients=new ArrayList<>();
        ArrayList<FXMLLoader> fxmlLoaders=new ArrayList<>();
        ArrayList<Scene>scenes=new ArrayList<>();
        ArrayList<Stage> stages=new ArrayList<>();
        ArrayList<ClientController> controllers=new ArrayList<>();
        URL clientUrl = EmailClientMain.class.getResource("newMail.fxml");

        for(int i=0;i<3;i++) {
            clients.add(new Client("studente."+i+"@edu.it"));

            //FXMLLoader fxmlLoader2 = new FXMLLoader(clientUrl);
            fxmlLoaders.add(new FXMLLoader(clientUrl));
            scenes.add(new Scene(fxmlLoaders.get(i).load(), 900, 600));
            stages.add(new Stage());
            stages.get(i).setTitle("Email client"+i+"!");
            stages.get(i).setScene(scenes.get(i));


            controllers.add(fxmlLoaders.get(i).getController());

            controllers.get(i).initialize(clients.get(i));
            stages.get(i).show();
        }



    }

    public static void main(String[] args) {
        launch();
    }
}