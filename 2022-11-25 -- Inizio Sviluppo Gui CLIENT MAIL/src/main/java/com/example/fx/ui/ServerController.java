package com.example.fx.ui;

import com.example.fx.model.Email;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
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
    private Email model;

    @FXML
    private TextArea logArea;

    @FXML
    private BorderPane serverPane;




    @FXML
    public void initialize() throws IOException {

          System.out.println("Finestra del server: ");

          /*
            Creo un nuovo thread che ha il compito di essere sempre in attesa di accettare connessioni
           */
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

        /*
        * Funzione Call() che viene chiamata dal vettore dei futureTask e specifica cosa fare
        * */
        public Socket call() throws IOException, ClassNotFoundException {


            int port;//porta alla quale si connette il socket
           // System.out.println("Finestra TASK ");



            //InputStream inStream = income.getInputStream();
            ObjectInputStream inStream = new ObjectInputStream(income.getInputStream());
                Scanner in = new Scanner(inStream);
            Email email = (Email) inStream.readObject();

                //System.out.println("EMAIL "+ email.getText());
                if(verifyEmail(email.getReceivers())) {
                    /*Mail CORRETTA pronta per l'invio*/
                    model=email;
                    logArea.appendText(email.getSender() + " Invia Mail\n");
                    model.sendMailToInbox(email);



                }else{
                    logArea.appendText(email.getSender() + " Mail di destinazione errata\n");
                }




            income.close();
                return income;




        }

    }

    public boolean verifyEmail(List<String> socketMailTo){
        int i=0;
        boolean correct=true;
        while (i<socketMailTo.size() && correct){
           // System.out.println(socketMailTo.get(i));
            if(socketMailTo.get(i).lastIndexOf("@")==-1 || socketMailTo.get(i).lastIndexOf(".it")==-1){
                correct=false;
            }
            i++;
        }



        return correct;

    }



}
