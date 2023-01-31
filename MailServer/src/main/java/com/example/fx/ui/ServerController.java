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

public class ServerController {

    private static final int NUM_THREADS = 10;
    private static final int SERVER_PORT = 8990;
    private Email model;

    @FXML
    private TextArea logArea;

    @FXML
    private BorderPane serverPane;

    Map<String,Socket> socketToId=new HashMap<>();

    String pathServer="C:/Users/asus/Desktop/UniTo/A.A. 22-23/ProgIII/Progetto ProgIII/MailServer/";

    private boolean inboxInit=false;

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

            System.out.println("CALL TASK INIZIO ESECUZIONE INBOX INIT");

            ObjectInputStream inStream = new ObjectInputStream(income.getInputStream());

          //  System.out.println("    STREAM CREATO"+income.toString()+"STREAM ->"+inStream.toString());

           // System.out.println("    WAITING MAIL FROM CLIENT " );

            Email email =(Email) inStream.readObject();
            socketToId.put(email.getSender(), income);



            if(email.getId()==null){
                System.out.println("    INVIO INBOX A " + email.getSender());
                ObjectOutputStream mailToInbox = new ObjectOutputStream(income.getOutputStream());
                mailToInbox.writeObject(loadEmail(email));
                System.out.println("    EMAIL RECEIVED " + email.getSender());
                System.out.println("    INBOX INVIATA A: " + email.getReceivers() + "  ->" +loadEmail( email));
                mailToInbox.close();
            } else if (email.getId().equals("-1")) { //RICHIESTA DI AGGIORNAMENTO DI INBOX(NUOVE MAIL)


            //    System.out.println("CALL TASK NEW INIZIO ESECUZIONE");
           //     System.out.println("    CREAZIONE NEW MAIL STREAM");
           //     System.out.println("    STREAM CREATO");


                socketToId.put(email.getSender(), income);
          //      System.out.println("    NEW EMAIL RECEIVED " + email.getSender());
         //       System.out.println("    NEW INVIO INBOX A " + email.getSender());


                /*metodo per madare le mail nuove al client*/
                if ( email.getId().equals("-1")) {
                    ObjectOutputStream mailToInbox = new ObjectOutputStream(income.getOutputStream());

                    ArrayList<Email> newMailToSend=new ArrayList<Email>();
                    System.out.println("newEmails LOADED ->"+ newMailToSend);

                    newMailToSend =loadNewEmail(email);

                    if(newMailToSend.size()>0) {
                        mailToInbox.writeObject(newMailToSend);
                    }else{
                   //     System.out.println("NO NEW MAIL");
                    }

                    mailToInbox.close();
                    income.close();
                }
            }else if(email.getId().equals("-2")) {
                deleteMail(email);
               // System.out.println("ELIMINO MAIL DA SERVER");

            }else{//RICHIESTA DI INVIO DI UNA NUOVA MAIL



              //  System.out.println("EMAIL VALIDITY "+ ExistEmail(email));
                ObjectOutputStream mailValidity= new ObjectOutputStream(income.getOutputStream());
                String invalidEmails=ExistEmail(email).getText();

                if (invalidEmails=="" && email.getId() != null && !email.getId().equals("-1")) {
                    Email emailValid= new Email("-3", "",new ArrayList<>(), "", invalidEmails);
                    mailValidity.writeObject(emailValid);



                    /*Mail CORRETTA pronta per l'invio*/
                    model = email;
                    logArea.appendText(email.getSender() + " Invia Mai a " + email.getReceivers() + "\n");
                    sendMailToNewQueue(email);




                    sendMailToInbox(email);

                } else {
                    Email emailInvalid = new Email("-3", "",new ArrayList<>(), "", invalidEmails);
                    mailValidity.writeObject(emailInvalid);
                    if (email.getId() != null) {
                        //Mando l'avviso al Cient che la mail non esiste
                        ObjectOutputStream outMsg = new ObjectOutputStream(socketToId.get(email.getSender()).getOutputStream());
                        outMsg.writeObject(new Email(null, "", List.of(""), "", ""));
                        outMsg.close();
                    }
                    logArea.appendText(email.getSender() + " invia mail a:" + email.getReceivers() + " Mail di destinazione non esistente\n");
                }



            }
            income.close();
            return null;
        }

    }



    public Email ExistEmail(Email email) {
        //   Email emailResult=new Email();
        String emailResult ="";
        for (int i = 0; i < email.getReceivers().size(); i++) {
            File inboxToSend = new File(pathServer+"src/main/resources/csv/emails_" + email.getReceivers().get(i) + ".txt");

            if (!inboxToSend.exists()) {
                emailResult = emailResult + ";" + email.getReceivers().get(i);
            }



        }
        return new Email("-3", "",new ArrayList<>(), "", emailResult);
    }





    public  ArrayList<Email> loadEmail(Email inboxID) throws IOException {
        ArrayList<Email> emailList= new ArrayList<Email>();
        File emails= new File(pathServer+"src/main/resources/csv/emails_"+inboxID.getSender()+".txt");
        Scanner emailReader = new Scanner(emails);


        while (emailReader.hasNextLine()) {

            String data = emailReader.nextLine();

            String[] dataSplitten= data.split(";");

            String id=dataSplitten[0];

            Email email = new Email(id,
                    dataSplitten[1], Collections.singletonList(dataSplitten[2]),dataSplitten[3],dataSplitten[4]);
            emailList.add(email);

        }

        emailReader.close();
        return emailList;
    }

    public  ArrayList<Email> loadNewEmail(Email inboxID)  {
        ArrayList<Email> emailList= new ArrayList<Email>();
        File emails= new File(pathServer+"src/main/resources/csv/newemails_"+inboxID.getSender()+".txt");
        if(emails.exists()) {
            Scanner emailReader = null;
            try {
                emailReader = new Scanner(emails);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }


            while (emailReader.hasNextLine()) {

                String data = emailReader.nextLine();

                String[] dataSplitten = data.split(";");

                String id = dataSplitten[0];

                Email email = new Email(id,
                        dataSplitten[1], Collections.singletonList(dataSplitten[2]), dataSplitten[3], dataSplitten[4]);
                emailList.add(email);

            }

            emailReader.close();
            emails.delete();
        }

        return emailList;
    }



    public void sendMailToNewQueue(Email email) throws IOException {

        String rcvsString="";
        int i=0;
        while(i<email.getReceivers().size()-1) {
            rcvsString =""+rcvsString+email.getReceivers().get(i)+",";
            i++;
        }
        rcvsString =""+rcvsString+email.getReceivers().get(i);
        System.out.println("RECEIVERS"+rcvsString);
        i=0;
        while(i<email.getReceivers().size()) {
            String filePath = pathServer+"src/main/resources/csv/newEmails_" + email.getReceivers().get(i) + ".txt";
            try (FileWriter fw = new FileWriter(filePath, true)){
                 BufferedWriter emailWriter = new BufferedWriter(fw);

                emailWriter.append(email.getId() + ";" + email.getSender() + ";" + rcvsString + ";" + email.getSubject() + ";" + email.getText() + ";\n");
                 emailWriter.close();


            }
            i++;
        }




        System.out.println("EMAIL SEND");



    }
    public void sendMailToInbox(Email email) throws IOException {

        String rcvsString="";
        int i=0;
        while(i<email.getReceivers().size()-1) {
            rcvsString =""+rcvsString+email.getReceivers().get(i)+",";
            i++;
        }
        rcvsString =""+rcvsString+email.getReceivers().get(i);
        System.out.println("RECEIVERS"+rcvsString);
        i=0;
        while(i<email.getReceivers().size()) {
            String filePath = pathServer+"src/main/resources/csv/emails_" + email.getReceivers().get(i) + ".txt";
            try (FileWriter fw = new FileWriter(filePath, true)){
                 BufferedWriter emailWriter = new BufferedWriter(fw);

                System.out.println("ID:" + email.getId() + " TO:" + email.getReceivers().get(i));


                emailWriter.append(email.getId() + ";" + email.getSender() + ";" + rcvsString + ";" + email.getSubject() + ";" + email.getText() + ";\n");
                emailWriter.close();

            }
            i++;
        }




        System.out.println("EMAIL SEND");



    }
    public  void deleteMail(Email emailToDelete)throws IOException{
        File tempEmails = new File(pathServer+"src/main/resources/csv/Tempemails.txt");
        File emails= new File(pathServer+"src/main/resources/csv/emails_"+emailToDelete.getSender()+".txt");

        BufferedReader emailReader = new BufferedReader(new FileReader(emails));
        BufferedWriter emailWriter=new BufferedWriter(new FileWriter(tempEmails));

        String data;

        boolean trovato=false;
        while ( ((data=emailReader.readLine() )!=null)  ) {

            String[] dataSplitten= data.split(";");
            String id=dataSplitten[0];
            System.out.println("ID"+id);
            if(id.compareTo(emailToDelete.getText())==0 ){
   //             System.out.println("EMAIL DELETED SOCKET "+emailToDelete);

            }else{
                emailWriter.write(data );
                emailWriter.newLine();

            }



        }
        emailReader.close();
        emailWriter.close();
        emails.delete();
        tempEmails.renameTo(emails);



    }



}