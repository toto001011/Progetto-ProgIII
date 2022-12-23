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

                    System.out.println("THREAD INITIALIZE -- I:"+i);
                    incomes.add(s.accept());

                    ServerTasks incomeTask=new ServerTasks(incomes.get(i));
                    ExecutorService exec = Executors.newFixedThreadPool(NUM_THREADS);
                    Vector<FutureTask<Socket>> tasks = new Vector<>();

                    FutureTask<Socket> ft = new FutureTask<>(incomeTask);
                    tasks.add(ft);
                    exec.execute(ft);
                    i++;
                    System.out.println("THREAD FINISH --I:"+i+"ID STUDENTE O-->"+socketToId.get("studente.0@edu.it")+"\n");
                }

            }
        };
        new Thread(task).start();





    }




    /*public void initilizeServer(Socket income) throws IOException {

        ServerTasks incomeTask=new ServerTasks(income);

        ExecutorService exec = Executors.newFixedThreadPool(NUM_THREADS);

        Vector<FutureTask<Socket>> tasks = new Vector<>();


            FutureTask<Socket> ft = new FutureTask<>(incomeTask);
            tasks.add(ft);
            exec.execute(ft);






    }*/



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



            while(income.isClosed()==false ) {
                System.out.println("CALL TASK INIZIO ESECUZIONE");
                ObjectInputStream inStream = new ObjectInputStream(income.getInputStream());


                   //Scanner in = new Scanner(inStream);
                //Outputstream per avvisare il client della mail che è stata ricevuta
                // ObjectOutputStream outStream = new ObjectOutputStream(income.getOutputStream());







                Email email = (Email) inStream.readObject();
                socketToId.put(email.getSender(), income);
                System.out.println("EMAIL RECEIVED "+email.getSender());




                //InputStream inStream = income.getInputStream();

                if(email.getId()==null) {
                    System.out.println("Finestra TASK ");

                   /* PrintWriter  out = new PrintWriter(
                            new BufferedWriter(
                                    new OutputStreamWriter(
                                            socketToId.get(email.getReceivers().get(0)).getOutputStream())),
                            true);;
                    System.out.println("CALL SERVER TASK SOCKET-->" + socketToId.get(email.getSender()));



                    out.println("INIT MESSAGE RECEIVED");*/
                    // outInit.println("INIT MESSAGE RECEIVED");

                   // out.flush();
                    //outInit.close();
                }




                //System.out.println("EMAIL VALIDITY "+ ExistEmail(email.getReceivers()));
                if (ExistEmail(email.getReceivers()) && email.getId()!=null  ) {
                    System.out.println("-----EMAIL TO SEND-----");


                    /*Mail CORRETTA pronta per l'invio*/
                    model = email;
                    logArea.appendText(email.getSender() + " Invia Mai a " + email.getReceivers() + "\n");

                    int i = 0;
                    while (i < email.getReceivers().size()) {
                        System.out.println("OUTPUT STREAM SERVER TO->" + email.getReceivers().get(i));


                        ObjectOutputStream outMsg = new ObjectOutputStream(socketToId.get(email.getReceivers().get(i)).getOutputStream());
                        System.out.println("OUTPUT STREAM SERVER CREATED");

                        // outMsg.println("NUOVO MESSAGGIO");
                        // outMsg.flush();
                        outMsg.writeObject(email);

                        // outStream.writeObject(model);
                        i++;

                    }
                    model.sendMailToInbox(email);
                    } else{
                        if (email.getId() != null)
                            logArea.appendText(email.getSender() + " Mail di destinazione errata\n");
                    }

                    System.out.println("CALL TASK FINE ESECUZIONE");


            }

            return income;
        }

    }



    public boolean ExistEmail(List<String> socketMailTo){
        int i=0;
        //socketToId.get(socketMailTo.get(i));
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
                // model.refreshEmail();
                // model.loadToInbox();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("ATTENZIONE MAIL NON ESISTENTE");
                // alert.setContentText("I have a great message for you!");
                alert.show();
            });
        }


        return correct;

    }
/*
    public String getSocketEmail(Socket income) throws IOException, ClassNotFoundException {
        System.out.println("AVVIO STREAM");
        ObjectInputStream inStream =new ObjectInputStream(income.getInputStream());
        System.out.println("STREAM AVVIATO");
       Scanner in = new Scanner(inStream);
        System.out.println("STREAM AVVIATO");
        //Outputstream per avvisare il client della mail che è stata ricevuta


        Email email = (Email) inStream.readObject();
        inStream.close();
        //System.out.println("EMAIL "+ email.getText());
        //System.out.println("SOCKET FROM:" + email.getSender());
        return email.getSender();

    }*/
/*
    private numOfReceivers(Email email){
        email.getReceivers().size();
    }*/
}
