package com.example.fx.ui;
import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
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

    @FXML
    private Button close;

    @FXML
    private BorderPane popUp;
    private Client model;
    private Email selectedEmail;
    private Email emptyEmail;
    private Socket socket;

    private boolean tryToReconnect=true;
    private boolean offline=false;
    private static final int SERVER_PORT = 8990;


    private void connect(){
        try {
            socket=new Socket("localhost",SERVER_PORT );
            if(socket!=null) {
              //  onSendButtonClick();
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                Email emailInit = new Email(null, lblUsername.textProperty().getValue(), List.of(lblUsername.textProperty().getValue()), "", "");
                System.out.println("EMAIL INIT"+emailInit);
                out.writeObject(emailInit);
                alertNewMail(socket);
                offline=false;
                Platform.runLater(() -> {

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(lblUsername.textProperty().getValue() + "Inbox");
                    alert.setHeaderText("SERVER ONLINE");
                    alert.show();
                });

            }

        } catch (UnknownHostException e) {
        } catch (IOException e) {

            //System.err.println("SERVER OFFLINE");
            if (!offline) {
                Platform.runLater(() -> {

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(lblUsername.textProperty().getValue() + "Inbox");
                    alert.setHeaderText("SERVER OFFLINE");
                    alert.show();
                });
            }
            offline=true;
        }
    }
    @FXML
    public void initialize(Client client) throws IOException {


        if (this.model != null)
            throw new IllegalStateException("Model can only be initialized once");


            Thread heartbeatThread = new Thread() {
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
                                sleep(5000);
                        } catch (InterruptedException e) {

                        }catch (IOException e) {
                           //tr to reconnect to server
                           connect();
                       }
                    }
                };
            };
            heartbeatThread.start();





        //istanza nuovo client
        model = client;//new Client("email");
        System.out.println("CLIENT-->"+model.emailAddressProperty());
        //  model=client;
        model.loadEmail();

        selectedEmail = null;
        //binding tra lstEmails e inboxProperty
        lstEmails.itemsProperty().bind(model.inboxProperty());
        lstEmails.setOnMouseClicked(this::showSelectedEmail);
        lblUsername.textProperty().bind(model.emailAddressProperty());
        lblUsernameSend.textProperty().bind(model.emailAddressProperty());
        emptyEmail = new Email(null,"", List.of(""), "", "");

    }

    /**
     * Elimina la mail selezionata
     */
    @FXML
    protected void onDeleteButtonClick() {
        model.deleteEmail(selectedEmail);

        updateDetailView(emptyEmail);
    }
    @FXML
    protected void onbtnClose(){

    }
    @FXML
    protected void onbtUpdateViewlButtonClick(){

        model.refreshEmail();
        System.out.println(("MODEL EMAIL "+model.emailAddressProperty()));
        updateDetailView(emptyEmail);

    }
    private void alertNewMail(Socket s){
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
    protected void onSendButtonClick() throws IOException {
        Email emailToSend=newMail();

        //System.out.println(" PREPARAZIONE INVIO OGGETTO AL SERVER");
        //definisco l'imput stream del socket client
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        if(ExistEmail(emailToSend.getReceivers())) {
            out.writeObject(emailToSend);
        }
       // System.out.println("OGGETTO INVIATO AL SERVER");

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

}

