package com.example.fx.main; /**
 @author Cay Horstmann
 @version 1.20 2004-08-03
 modificata...
 */

import java.io.*;
import java.net.*;
import java.util.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
/**
 This program implements a multithreaded server that listens to port 8189 and echoes back
 all client input.
 */
public class EmailServer extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        //URL clientUrl = EmailClientMain.class.getResource("newServer.fxml");
        URL clientUrl = EmailServer.class.getResource("newServer.fxml");

        FXMLLoader fxmlLoader = new FXMLLoader(clientUrl);
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Email server");
        stage.setScene(scene);
        stage.show();
    }
}

/**
 This class handles the client input for one server socket connection.
 */
class ThreadedEchoHandler implements Runnable {

    private Socket incoming;
    private int counter;

    /**
     Constructs a handler.
     @param in the incoming socket
     @param c the counter for the handlers (used in prompts)
     */


      public ThreadedEchoHandler(Socket in, int c) {
        incoming = in;
        counter = c;
    }
    public void run() {
        try {
            try {
                InputStream inStream = incoming.getInputStream();
                OutputStream outStream = incoming.getOutputStream();
                //ObjectOutputStream dOut=  dOut. incoming.getOutputStream();

                Scanner in = new Scanner(inStream);
                PrintWriter out = new PrintWriter(outStream, true  /*AutoFlush */);



                //out.println( "Hello! Enter BYE to exit." );

                // out.println(in.nextLine());
                // echo client input
                boolean done = false;
                while (!done && in.hasNextLine()) {
                    System.out.println("ENTRATO");
                    System.out.println("Soket value-->"+in.nextLine());
                    String line = in.nextLine();
                    out.println("Echo: " + line);
                    System.out.println("ECHO: "+ line);
                    if (line.trim().equals("BYE"))
                        done = true;
                }
            }
            finally {
                incoming.close();
            }
        }
        catch (IOException e) {e.printStackTrace();}
    }

}

