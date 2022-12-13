package com.example.fx.ui;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;

import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

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
    private BorderPane serverPane;


    @FXML
    public void onBtnActivate() throws IOException {

    }
    @FXML
    public void initialize() throws IOException {

          System.out.println("Finestra del server: ");

        Task task = new Task<Void>() {
            @Override public Void call() throws IOException {
                ServerSocket s = new ServerSocket(SERVER_PORT);
                while (true) {

                    System.out.println("THREAD INITILIZE 1 ");


                    Socket income = s.accept();


                    initilizeServer(income);
                    System.out.println("THREAD INITILIZE 2");
                }

            }
        };



        new Thread(task).start();


    }




    public void initilizeServer(Socket income) throws IOException {

        ServerTasks incomeTask=new ServerTasks(income);

        System.out.println("\n \nFinestra START \n \n");

        ExecutorService exec = Executors.newFixedThreadPool(NUM_THREADS);
        Vector<FutureTask<Socket>> tasks = new Vector<>();


            FutureTask<Socket> ft = new FutureTask<>(incomeTask);
            tasks.add(ft);
            exec.execute(ft);



    }



    public class ServerTasks implements Callable<Socket> {
        Socket income;
        public ServerTasks(Socket income){
            this.income=income;
        }
        public Socket call() throws IOException {
            int port;//porta alla quale si connette il socket
            //public ServerTasks(int port){this.port=port;}
            System.out.println("Finestra TASK ");
           //Socket income= startServer(SERVER_PORT);



            InputStream inStream = income.getInputStream();

                Scanner in = new Scanner(inStream);


                System.out.println("INIZIO computazione ");


                boolean done = false;
                while (!done && in.hasNextLine()) {
                    String outLine = in.nextLine();
                    System.out.println("Soket value-->" + outLine);

                    logArea.appendText("\n" + outLine +" invia mail");

                }
            //income.close();
                return income;


            //logArea.appendText(income.);


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
