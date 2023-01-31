package com.example.fx.ui;
import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import com.example.fx.model.Client;
import com.example.fx.model.Email;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
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


             //   System.err.println("SERVER OFFLINE");
                if (!offline) {
                    Platform.runLater(() -> {

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(lblUsername.textProperty().getValue() + "Inb ox");
                        alert.setHeaderText("SERVER OFFLINE");
                        alert.show();
                        if(alertOnline>=0)
                            alertOnline=0;
                    });
                }
                offline=true;

            }

            if(socket!=null && socket.isConnected()) {
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
              //  System.out.println("OBJECT STREAM CREATO STREAM ->"+out.toString());

                if(alertOnline<0) {

                    Email emailInit = new Email(null, lblUsername.textProperty().getValue(), List.of(lblUsername.textProperty().getValue()), "", "");
                 //   System.out.println("REQUEST INBOX EMAIL INIT" + emailInit);
                    out.writeObject(emailInit);
                    if(loadMailSocket(socket))
                        alertOnline++;

                }else{
                    Email emailInit = new Email("-1", lblUsername.textProperty().getValue(), List.of(lblUsername.textProperty().getValue()), "", "");
              //      System.out.println("REQUEST NEW MAIL" + emailInit);
                    out.writeObject(emailInit);
                    if(loadNewMailSocket(socket))
                        alertOnline++;

                }




           //     System.out.println("LOAD INBOX");

            //    System.out.println("INBOX LOADED");
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
      //  System.out.println(" LOAD MAIL INBOX inizio");


        ObjectInputStream  getInboxResponse= null;
        try {
            getInboxResponse = new ObjectInputStream(s.getInputStream());
           // System.out.println(" LOAD MAIL INBOX object");
            emailsInbox= (ArrayList<Email>) getInboxResponse.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


        for(Email email: emailsInbox)
            inboxContent.add(email);

        Platform.runLater(()-> {
            model.inboxProperty().set(inboxContent);
        });
        ;
        return emailsInbox!=null;
    }
    public boolean loadNewMailSocket(Socket s)  {

        ArrayList<Email> emailsInbox=new ArrayList<>();
     //   System.out.println(" LOAD MAIL NEW INBOX inizio");

        try {
            ObjectInputStream  getInboxResponse=new ObjectInputStream(s.getInputStream());
       //     System.out.println(" LOAD MAIL INBOX object");
            emailsInbox= (ArrayList<Email>) getInboxResponse.readObject();

            Platform.runLater(() -> {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(lblUsername.textProperty().getValue() + "Inbox");
                alert.setHeaderText("HAI NUOVI MESSAGGI");
                alert.show();
            });
        } catch (IOException e) {
           //System.err.println("IOException in loadNewMailSocket");
        } catch (ClassNotFoundException e) {
           // System.err.println("ClassNotFoundException in loadNewMailSocket");
        }finally {
            ArrayList<Email> finalEmailsInbox = emailsInbox;
            Platform.runLater(()-> {
                for(Email email: finalEmailsInbox)
                    inboxContent.add(email);

                model.inboxProperty().set(inboxContent);
            });


        }




        return emailsInbox!=null;
    }
    /**
     * Elimina la mail selezionata
     */
    @FXML
    protected void onDeleteButtonClick() {

        Socket delete=null;
        try {
            delete= new Socket("localhost",SERVER_PORT);


            ObjectOutputStream emailToDelete=new ObjectOutputStream(delete.getOutputStream());
            Email emailInit = new Email("-2", lblUsername.textProperty().getValue(), new ArrayList<>(), "", selectedEmail.getId());
            emailToDelete.writeObject(emailInit);
            emailToDelete.close();
            emailToDelete.close();
            delete.close();
            //System.out.println("RICHIESTA DI ELIMINAZIONE INVIATA AL SERVER");
            inboxContent.remove(selectedEmail);

        } catch (IOException e) {
            Platform.runLater(() -> {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(lblUsername.textProperty().getValue() + "Inb ox");
                alert.setHeaderText("ATTENZIONE IL SERVER E' OFFLINE");
                alert.setContentText("Eliminare il messaggio quando il server è online per assicurarsi che venga inviato correttamente");
                alert.show();
            });
        }






        updateDetailView(emptyEmail);
    }




    protected Email newMail(){
        String idNewEmail = UUID.randomUUID().toString();
        String sender=lblUsernameSend.textProperty().getValue();
        txtSendTo.textProperty().getValue();
        List<String> receivers;
        receivers=loadReceiver(txtSendTo.textProperty().getValue());

        String subject=txtSendObj.textProperty().getValue();

        String text=txtEmailContentSend.textProperty().getValue();
        Email emailsend = new Email(idNewEmail, sender,  receivers,  subject,  text);

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
    protected void onSendButtonClick() {
        Email emailToSend=newMail();
        Socket sendMail= null;
        try {
            sendMail = new Socket("localhost",SERVER_PORT );
            if (sendMail != null) {
                System.out.println("EMAIL IS CORRECT--> "+EmailIsCorrect(emailToSend.getReceivers()));
                if (EmailIsCorrect(emailToSend.getReceivers())==true) {
                    ObjectOutputStream out = new ObjectOutputStream(sendMail.getOutputStream());

                    out.writeObject(emailToSend);

                    ObjectInputStream response = new ObjectInputStream(sendMail.getInputStream());

                    Email emailResponse = (Email) response.readObject();
                    if (/*emailResponse.getId()=="-3" &&*/ emailResponse.getText() == "") {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(lblUsername.textProperty().getValue() + "Inbox");
                        alert.setHeaderText("MESSAGGIO INVIATO CORRETTAMENTE");
                        alert.show();
                    } else  {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(lblUsername.textProperty().getValue() + "Inbox");
                        alert.setHeaderText("EMAIL " + emailResponse.getText().toUpperCase() + " NON ESISTENTE");
                        alert.show();
                    }

                }else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("ATTENZIONE EMAIL DI DESTINAZIONE NON CORRETTA");
                    alert.show();

                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("SERVER OFFLINE");
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
        }catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }



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

    public boolean EmailIsCorrect(List<String> socketMailTo) {
        int i = 0;
        boolean correct = true;
        while (i < socketMailTo.size() && correct) {
            System.out.println(socketMailTo.get(i));
            if (socketMailTo.get(i).lastIndexOf("@") == -1 || socketMailTo.get(i).lastIndexOf(".it") == -1) {
                correct = false;
            }
            i++;
        }
        return correct;
    }






}
