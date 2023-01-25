package com.example.fx.ui;

import com.example.fx.model.Email;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
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

    Map<String,Socket> socketToId=new HashMap<>();


    @FXML
    public void initialize() throws IOException {

        System.out.println("Finestra del server: ");

          /*
            Creo un nuovo thread che ha il compito di essere sempre in attesa di accettare connessioni
           */
        Task task = new Task<Void>() {
            @Override public Void call() throws IOException {
                ServerSocket s = new ServerSocket(SERVER_PORT);
                ArrayList<Socket> incomes=new ArrayList<>();
                int i=0;
                while (true) {

                    incomes.add(s.accept());

                    ServerTasks incomeTask=new ServerTasks(incomes.get(i));
                    ExecutorService exec = Executors.newFixedThreadPool(NUM_THREADS);
                    Vector<FutureTask<Socket>> tasks = new Vector<>();

                    FutureTask<Socket> ft = new FutureTask<>(incomeTask);
                    tasks.add(ft);
                    exec.execute(ft);
                    i++;
                }

            }
        };
        new Thread(task).start();
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

            //int port;//porta alla quale si connette il socket



            while(income.isClosed()==false ) {
                System.out.println("CALL TASK INIZIO ESECUZIONE");
                System.out.println("    CREAZIONE STREAM");
                ObjectInputStream inStream = new ObjectInputStream(income.getInputStream());
                System.out.println("    STREAM CREATO");


                Email email = (Email) inStream.readObject();
                socketToId.put(email.getSender(), income);
                System.out.println("EMAIL RECEIVED "+email.getSender());





                if(email.getId()==null || email.getId().compareTo("-1")==0) {
                    continue;
                }




                //System.out.println("EMAIL VALIDITY "+ ExistEmail(email.getReceivers()));
                if (ExistEmail(email.getReceivers()) && email.getId()!=null  && email.getId().compareTo("-1")!=0)  {
                  //  System.out.println("-----EMAIL TO SEND-----");


                    /*Mail CORRETTA pronta per l'invio*/
                    model = email;
                    logArea.appendText(email.getSender() + " Invia Mai a " + email.getReceivers() + "\n");

                    int i = 0;
                    while (i < email.getReceivers().size()) {

                        ObjectOutputStream outMsg = new ObjectOutputStream(socketToId.get(email.getReceivers().get(i)).getOutputStream());
                        outMsg.writeObject(email);
                        i++;

                    }
                    model.sendMailToInbox(email);
                    } else{
                        if (email.getId() != null) {
                            //Mando l'avviso al Cient che la mail non esiste
                            ObjectOutputStream outMsg = new ObjectOutputStream(socketToId.get(email.getSender()).getOutputStream());
                            outMsg.writeObject(new Email(null,"", List.of(""), "", ""));
                        }
                            logArea.appendText(email.getSender() +" invia mail a:"+email.getReceivers()+ " Mail di destinazione non esistente\n");
                    }

                   // System.out.println("CALL TASK FINE ESECUZIONE");


            }

            return income;
        }

    }



    public boolean ExistEmail(List<String> socketMailTo){
        int i=0;
        boolean correct=true;
        while (i<socketMailTo.size() && correct){
            System.out.println(socketMailTo.get(i));
            if(socketToId.get(socketMailTo.get(i))==null){
                correct=false;
            }
            i++;
        }

        if(!correct){
            Platform.runLater(() -> {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("ATTENZIONE MAIL NON ESISTENTE");
                alert.show();
            });
        }


        return correct;

    }

}
