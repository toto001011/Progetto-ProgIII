package com.example.fx.ui;
import javafx.fxml.FXML;

import javafx.scene.control.TextArea;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Classe Controller
 */

public class ServerController  {

    private static final int NUM_THREADS = 10;
    private static final int SERVER_PORT = 8990;
    @FXML
    private TextArea logArea;


    @FXML
    public void onBtnActivate() {
       // activateServer();
    }
    @FXML
    public void initialize() throws IOException {

          System.out.println("Finestra del server: ");
        ExecutorService exec = Executors.newFixedThreadPool(NUM_THREADS);
        Vector<FutureTask<Socket>> tasks = new Vector<>();

        FutureTask<Socket> ft=new FutureTask<>(new ServerTasks());
        tasks.add(ft);
        exec.execute(ft);



    }
    public class ServerTasks implements Callable<Socket> {
        public Socket call() throws IOException {
            int port;//porta alla quale si connette il socket
            //public ServerTasks(int port){this.port=port;}


            ServerSocket s = new ServerSocket(SERVER_PORT);

            Socket income = s.accept();

           // OutputStream outStream = income.getOutputStream();
            InputStream inStream = income.getInputStream();

           // PrintWriter out = new PrintWriter(outStream, true /* autoFlush */);
            Scanner in = new Scanner(inStream);


            System.out.println("INIZIO computazione ");



             boolean done =false;
            while (!done && in.hasNextLine()) {
                System.out.println("ENTRATO");
                String outLine=in.nextLine();
                System.out.println("Soket value-->"+outLine );

                //logArea.appendText(outLine +" invia mail");

            }

            System.out.println("FINE computazione " + ": risultato = ");
            //logArea.appendText(income.);
            return income;
        }

    }
   /* @Override
    public ServerSocket call() throws Exception {

        System.out.println("INIZIO computazione " );

            //to do thing

        System.out.println("FINE computazione "  + ": risultato = " + 2 );
        return new ServerSocket();
    }

*/
}
