package com.example.fx.ui;
import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import com.example.fx.model.Client;
import com.example.fx.model.Email;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;


// Client su diversi jvm(progetti)
//Nuova connessione = nuova richiesta= nuovo socket
//richiesta mail passare dal server
/**
 * Classe Controller
 */

public class ClientController {
    @FXML
    private Label lblFrom;

    @FXML
    private Label lblTo;

    @FXML
    private Label lblSubject;

    @FXML
    private Label lblUsername;

    @FXML
    private Label lblUsernameSend;

    @FXML
    private TextArea txtEmailContent;

    @FXML
    private TextArea txtEmailContentSend;

    @FXML
    private ListView<Email> lstEmails;


    @FXML
    private BorderPane pnlNewMessage;

    @FXML
    private BorderPane pnlReadMessage;


    @FXML
    private TextArea txtSendTo;
    @FXML
    private TextArea txtSendObj;


    private Client model;
    private Email selectedEmail;
    private Email emptyEmail;
    private Socket socket;

    private LinkedList<Email> emailList;

    private boolean tryToReconnect=true;
    private boolean offline=false;
    private int alertOnline=-1;
    private boolean inboxInit=false;
    private ObservableList<Email> inboxContent = FXCollections.observableList(new LinkedList<>());


    private static final int SERVER_PORT = 8990;


    private void connect(){
        try {
            try {
                socket=new Socket("localhost",SERVER_PORT );
            } catch (IOException e) {


                System.err.println("SERVER OFFLINE");
                if (!offline) {
                    Platform.runLater(() -> {

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(lblUsername.textProperty().getValue() + "Inb ox");
                        alert.setHeaderText("SERVER OFFLINE");
                        alert.show();
                        alertOnline=0;
                    });
                }
                offline=true;
       /* } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);*/
            }

            if(socket!=null && socket.isConnected()) {
                //  onSendButtonClick();
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                System.out.println("OBJECT STREAM CREATO STREAM ->"+out.toString());

                //ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                if(alertOnline<0) {

                    Email emailInit = new Email(null, lblUsername.textProperty().getValue(), List.of(lblUsername.textProperty().getValue()), "", "");
                    System.out.println("REQUEST INBOX EMAIL INIT" + emailInit);
                    out.writeObject(emailInit);
                    if(loadMailSocket(socket))
                        alertOnline++;

                }else{
                    Email emailInit = new Email("-1", lblUsername.textProperty().getValue(), List.of(lblUsername.textProperty().getValue()), "", "");
                    System.out.println("REQUEST NEW MAIL" + emailInit);
                    out.writeObject(emailInit);
                    if(loadNewMailSocket(socket))
                        alertOnline++;

                }

                // alertNewMail(socket);



                System.out.println("LOAD INBOX");

                System.out.println("INBOX LOADED");
                offline=false;

                if(alertOnline==1) {
                    Platform.runLater(() -> {

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(lblUsername.textProperty().getValue() + "Inbox");
                        alert.setHeaderText("SERVER ONLINE");
                        alert.show();

                    });
                }
                socket.close();
                out.close();

            }else{

            }

        } catch (UnknownHostException e) {

        } catch (IOException e) {
            // throw new RuntimeException(e);
        }
    }
    @FXML
    public void initialize(Client client) throws IOException {


        if (this.model != null)
            throw new IllegalStateException("Model can only be initialized once");


        Thread heartbeatThread = new Thread() { //metodo per testare la connessione ogni tot di tempo
            public void run() {
                while (tryToReconnect) {

                    if(socket==null )
                        connect();
                    //send a test signal
                    try {

                        if(socket!=null) {
                            ObjectOutputStream outRetry = new ObjectOutputStream(socket.getOutputStream());

                            outRetry.writeObject(new Email("-1","", List.of(""), "", ""));


                        }
                        sleep(10000);
                    } catch (InterruptedException e) {

                    }catch (IOException e) {
                        //tr to reconnect to server
                        connect();
                        try {
                            sleep(10000);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            };
        };
        heartbeatThread.start();





        //istanza nuovo client
        model = client;//new Client("email");
        //  model=client;
        // model.loadEmail();


        selectedEmail = null;
        //binding tra lstEmails e inboxProperty
        lstEmails.itemsProperty().bind(model.inboxProperty());
        lstEmails.setOnMouseClicked(this::showSelectedEmail);
        lblUsername.textProperty().bind(model.emailAddressProperty());
        lblUsernameSend.textProperty().bind(model.emailAddressProperty());
        emptyEmail = new Email(null,"", List.of(""), "", "");

    }

    public boolean loadMailSocket(Socket s) {

        ArrayList<Email> emailsInbox;
        System.out.println(" LOAD MAIL INBOX inizio");

        //ObjectOutputStream getInboxRequest = new ObjectOutputStream(income.getOutputStream());
        //  Email getEmailInbox = new Email("-1", null, null, null, null);
        // getInboxRequest.writeObject(getEmailInbox);
        ObjectInputStream  getInboxResponse= null;
        try {
            getInboxResponse = new ObjectInputStream(s.getInputStream());
            System.out.println(" LOAD MAIL INBOX object");
            emailsInbox= (ArrayList<Email>) getInboxResponse.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


        for(Email email: emailsInbox)
            inboxContent.add(email);

        System.out.println("RISPOSTA--> "+emailsInbox);
        Platform.runLater(()-> {
            model.inboxProperty().set(inboxContent);
        });
        //    model.inboxProperty().set((ObservableList<Email>) inStream.readObject());
        //getInboxResponse.close();
        return emailsInbox!=null;
    }
    public boolean loadNewMailSocket(Socket s)  {

        ArrayList<Email> emailsInbox=new ArrayList<>();
        System.out.println(" LOAD MAIL NEW INBOX inizio");
        //ObservableList<Email> inboxContent = FXCollections.observableList(new LinkedList<>());

        //ObjectOutputStream getInboxRequest = new ObjectOutputStream(income.getOutputStream());
        //  Email getEmailInbox = new Email("-1", null, null, null, null);
        // getInboxRequest.writeObject(getEmailInbox);
        try {
            ObjectInputStream  getInboxResponse=new ObjectInputStream(s.getInputStream());
            System.out.println(" LOAD MAIL INBOX object");
            emailsInbox= (ArrayList<Email>) getInboxResponse.readObject();
            Platform.runLater(() -> {
                // model.refreshEmail();
                // model.loadToInbox();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(lblUsername.textProperty().getValue() + "Inbox");
                alert.setHeaderText("HAI NUOVI MESSAGGI");
                // alert.setContentText("I have a great message for you!");
                alert.show();
            });
        } catch (IOException e) {
            System.err.println("IOException in loadNewMailSocket");
        } catch (ClassNotFoundException e) {
            System.err.println("ClassNotFoundException in loadNewMailSocket");
        }finally {
            ArrayList<Email> finalEmailsInbox = emailsInbox;
            Platform.runLater(()-> {
                for(Email email: finalEmailsInbox)
                    inboxContent.add(email);

                model.inboxProperty().set(inboxContent);
            });

            System.out.println("RISPOSTA--> "+emailsInbox);

        }



        //    model.inboxProperty().set((ObservableList<Email>) inStream.readObject());
        //getInboxResponse.close();
        return emailsInbox!=null;
    }
    /**
     * Elimina la mail selezionata
     */
    @FXML
    protected void onDeleteButtonClick() {
        deleteEmail(selectedEmail);


        updateDetailView(emptyEmail);
    }


    /* private void alertNewMail(Socket s){

        // loadMailSocket(s);
         Task alertTask = new Task<Void>() {
             @FXML
             @Override public Void call() throws IOException {
                 System.out.println("INIZIO THREAD ASCOLTO ");

                 //System.out.println("    GET SCANNER"+in);



                 while (true) {
                     //ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
                     ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());

                     //Serve per "leggere" i byte inviati dal server(o da altra parte)

                     System.out.println("    GET INPUT STREAM "+inStream);

                     System.out.println("STREAM IN ASCOLTO");
                     Email newEmail;
                     //while (in.hasNextLine()) {
                     //String line = in.nextLine();
                     //System.out.println("    "+line);
                     //popUp newMsg= new popUp(new Stage());

                     try {
                         newEmail=(Email) inStream.readObject();
                     } catch (ClassNotFoundException e) {
                         throw new RuntimeException(e);
                     }
                     if(newEmail.getId()!=null) {
                         //if(inStream.readObject()) {
                         Platform.runLater(() -> {
                             // model.refreshEmail();
                             // model.loadToInbox();
                             model.loadToInbox(newEmail);
                             Alert alert = new Alert(Alert.AlertType.INFORMATION);
                             alert.setTitle(lblUsername.textProperty().getValue() + "Inbox");
                             alert.setHeaderText("NUOVI MESSAGGI");
                             // alert.setContentText("I have a great message for you!");
                             alert.show();
                         });
                     }else{
                         Platform.runLater(() -> {
                             // model.refreshEmail();
                             // model.loadToInbox();
                             model.loadToInbox(newEmail);
                             Alert alert = new Alert(Alert.AlertType.INFORMATION);
                             alert.setTitle(lblUsername.textProperty().getValue() + "Inbox");
                             alert.setHeaderText("EMAIL DI DESTINAZIONE NON ESISTENTE");
                             // alert.setContentText("I have a great message for you!");
                             alert.show();
                         });
                     }





                     //  }
                     System.out.println("FINE STREAM  ASCOLTO");
                     // inStream.close();




                 }
                 //  System.out.println("FINE THREAD ASCOLTO ");

             }
         };
         new Thread(alertTask).start();
     }
 */
    protected Email newMail(){
        String idNewEmail = UUID.randomUUID().toString();
        String sender=lblUsernameSend.textProperty().getValue();
        txtSendTo.textProperty().getValue();
        List<String> receivers;
        receivers=loadReceiver(txtSendTo.textProperty().getValue());

        String subject=txtSendObj.textProperty().getValue();

        String text=txtEmailContentSend.textProperty().getValue();
        Email emailsend = new Email(idNewEmail, sender,  receivers,  subject,  text);
        //connessione server
        //output stream della socket

        return emailsend;
    }
    public static List<String> loadReceiver(String receivers){

        ArrayList<String> rec= new ArrayList<>();

        String[] rvalue = receivers.split(",");

        for (String rsplit : rvalue) {
            rec.add(rsplit);

        }
        return rec;

    }
    @FXML
    protected void onSendButtonClick()  {
        Email emailToSend=newMail();
        Socket sendMail= null;
        try {
            sendMail = new Socket("localhost",SERVER_PORT );
            if (sendMail != null) {
                ObjectOutputStream out = new ObjectOutputStream(sendMail.getOutputStream());
                if (ExistEmail(emailToSend.getReceivers())) {
                    out.writeObject(emailToSend);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                //alert.setTitle(lblUsername.textProperty().getValue() + "Inbox");
                alert.setHeaderText("SERVER OFFLINE");
                // alert.setContentText("I have a great message for you!");
                alert.show();
            }
            System.out.println("OGGETTO INVIATO AL SERVER");

        } catch (IOException e) {
            Platform.runLater(() -> {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(lblUsername.textProperty().getValue() + "Inb ox");
                alert.setHeaderText("ATTENZIONE IL SERVER E' OFFLINE");
                alert.setContentText("Inviare il messaggio quando il server è online per assicurarsi che venga inviato correttamente");
                alert.show();
            });
        }

        //System.out.println(" PREPARAZIONE INVIO OGGETTO AL SERVER");
        //definisco l'imput stream del socket client



    }

    @FXML
    protected void onReplyButtonClick() {
        pnlReadMessage.visibleProperty().set(false);
        pnlNewMessage.visibleProperty().set(true);
        txtEmailContent.visibleProperty().set(false);
        txtEmailContentSend.visibleProperty().set(true);

        txtSendTo.setText(lblFrom.getText());
        txtSendTo.setEditable(false);

    }

    @FXML
    protected void onReplyAllButtonClick() {
        pnlReadMessage.visibleProperty().set(false);
        pnlNewMessage.visibleProperty().set(true);
        txtEmailContent.visibleProperty().set(false);
        txtEmailContentSend.visibleProperty().set(true);


        txtSendTo.setText(lblFrom.getText()+","+lblTo.getText());
        txtSendTo.setEditable(false);

    }

    @FXML
    protected void onbtnNewMailButtonClick() {


        pnlReadMessage.visibleProperty().set(false);
        pnlNewMessage.visibleProperty().set(true);
        txtSendTo.setEditable(true);
        txtSendTo.setText("");
        txtSendObj.setText("");

        txtEmailContent.visibleProperty().set(false);
        txtEmailContentSend.visibleProperty().set(true);


        //lblFrom.setText(String.valueOf(lblUsername));

    }
    @FXML
    protected void onForwardButtonClick() {
        pnlReadMessage.visibleProperty().set(false);
        pnlNewMessage.visibleProperty().set(true);
        txtEmailContent.visibleProperty().set(false);
        txtEmailContentSend.visibleProperty().set(true);

        txtEmailContentSend.setText(txtEmailContent.getText());
        txtSendObj.setText(lblSubject.getText());



        txtSendTo.setEditable(true);
    }




    /**
     * Mostra la mail selezionata nella vista
     */
    protected void showSelectedEmail(MouseEvent mouseEvent) {
        pnlNewMessage.visibleProperty().set(false);
        pnlReadMessage.visibleProperty().set(true);

        txtEmailContent.visibleProperty().set(true);
        txtEmailContentSend.visibleProperty().set(false);

        Email email = lstEmails.getSelectionModel().getSelectedItem();
        selectedEmail = email;
        updateDetailView(email);
    }

    /**
     * Aggiorna la vista con la mail selezionata
     */
    protected void updateDetailView(Email email) {
        if(email != null) {
            lblFrom.setText(email.getSender());
            lblTo.setText(String.join(", ", email.getReceivers()));
            lblSubject.setText(email.getSubject());
            txtEmailContent.setText(email.getText());
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
    public boolean ExistEmail(List<String> socketMailTo) {
        int i = 0;
        boolean correct = true;
        while (i < socketMailTo.size() && correct) {
            System.out.println(socketMailTo.get(i));
            if (socketMailTo.get(i).lastIndexOf("@") == -1 || socketMailTo.get(i).lastIndexOf(".it") == -1) {
                correct = false;
            }
            i++;
        }
        if(!correct){
            Platform.runLater(() -> {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("ATTENZION MAIL NON CORRETTA");
                alert.show();
            });
        }
        return correct;
    }
    /**
     *
     * @return   elimina l'email specificata
     *
     */
    public void deleteEmail(Email email) {

        inboxContent.remove(email);
        try {
            deleteMail(email.getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Cancella la mail dal file csv Ricopiando quelle che devono rimanere in un altro file temporaneo che poi
     * viene rinominato in quello originale
     * @param idMail: indica il codice identificativo (progressivo) univoco della mail
     *
     */
    public  void deleteMail(String idMail)throws IOException{
        File tempEmails = new File("C:/Users/asus/Desktop/UniTo/A.A. 22-23/ProgIII/Progetto ProgIII/2022-11-25 -- Inizio Sviluppo Gui CLIENT MAIL/src/main/resources/csv/Tempemails.txt");
        File emails= new File("C:/Users/asus/Desktop/UniTo/A.A. 22-23/ProgIII/Progetto ProgIII/2022-11-25 -- Inizio Sviluppo Gui CLIENT MAIL/src/main/resources/csv/emails_"+model.emailAddressProperty().getValue()+".txt");

        BufferedReader emailReader = new BufferedReader(new FileReader(emails));
        BufferedWriter emailWriter=new BufferedWriter(new FileWriter(tempEmails));

        String data;

        boolean trovato=false;
        while ( ((data=emailReader.readLine() )!=null)  ) {

            String[] dataSplitten= data.split(";");
            String id=dataSplitten[0];
            System.out.println("ID"+id);
            if(id.compareTo(idMail)==0 ){
                trovato=true;

            }else{
                emailWriter.write(data );
                emailWriter.newLine();
                System.out.println("EMAIL COPIED");
            }



        }
        emailReader.close();
        emailWriter.close();
        emails.delete();
        boolean successful = tempEmails.renameTo(emails);



    }

}

