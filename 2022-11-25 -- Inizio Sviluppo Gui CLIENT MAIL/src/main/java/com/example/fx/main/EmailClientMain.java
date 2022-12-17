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

            /*
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
*/

        ArrayList<Client> clients=new ArrayList<>();
        ArrayList<FXMLLoader> fxmlLoaders=new ArrayList<>();
        ArrayList<Scene>scenes=new ArrayList<>();
        ArrayList<Stage> stages=new ArrayList<>();
        ArrayList<ClientController> controllers=new ArrayList<>();
        URL clientUrl = EmailClientMain.class.getResource("newMail.fxml");

        for(int i=0;i<2;i++) {
            clients.add(new Client("studente."+i+"@edu.it"));

            //FXMLLoader fxmlLoader2 = new FXMLLoader(clientUrl);
            fxmlLoaders.add(new FXMLLoader(clientUrl));
            scenes.add(new Scene(fxmlLoaders.get(i).load(), 900, 600));
            stages.add(new Stage());
            stages.get(i).setTitle("Email client"+i+"!");
            stages.get(i).setScene(scenes.get(i));


            //ClientController contr2 = fxmlLoader2.getController();
        //    ClientController contr=new ClientController();
            controllers.add(fxmlLoaders.get(i).getController());

            controllers.get(i).initialize(clients.get(i));
            stages.get(i).show();
        }



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