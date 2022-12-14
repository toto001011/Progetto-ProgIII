 package com.example.fx.ui;
import com.example.fx.functions.functions;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import com.example.fx.model.Client;
import com.example.fx.model.Email;
import javafx.scene.layout.BorderPane;
import com.example.fx.functions.functions;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private static final int SERVER_PORT = 8990;


    @FXML
    public void initialize() throws IOException {
        if (this.model != null)
            throw new IllegalStateException("Model can only be initialized once");
        //istanza nuovo client
        model = new Client("studente@unito.it");
        model.loadEmail();
      //  model.generateRandomEmails(10);

        selectedEmail = null;

        //binding tra lstEmails e inboxProperty
        lstEmails.itemsProperty().bind(model.inboxProperty());
        lstEmails.setOnMouseClicked(this::showSelectedEmail);
        lblUsername.textProperty().bind(model.emailAddressProperty());
        lblUsernameSend.textProperty().bind(model.emailAddressProperty());

        emptyEmail = new Email(-1,"", List.of(""), "", "");

        updateDetailView(emptyEmail);
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
    protected void onbtUpdateViewlButtonClick(){

            model.refreshEmail();
            updateDetailView(emptyEmail);


    }

    protected Email newMail(){
        long id=20;
        //String sender= String.valueOf(lblUsernameSend);//"sender";
        String sender=lblUsernameSend.textProperty().getValue();

        List<String> receivers=new ArrayList<String>();
        receivers=loadReceiver(txtSendTo.textProperty().getValue());

        String subject=txtSendObj.textProperty().getValue();

        String text=txtEmailContentSend.textProperty().getValue();
        //model.sendSocket( new Email(id, sender,  receivers,  subject,  text));
        Email emailsend = new Email(id, sender,  receivers,  subject,  text);
        System.out.println("EMAIL CLIENT CONTROLLER-->"+emailsend+"-- "+emailsend.getReceivers()+"-- "+emailsend.getText());
        return emailsend;
    }
    public static List<String> loadReceiver(String receivers){

        ArrayList<String> rec= new ArrayList<>();
        //System.out.println("RECeivers"+receivers);

        String[] rvalue = receivers.split(",");

        for (String rsplit : rvalue) {
            rec.add(rsplit);

        }
      //  System.out.println("REC"+rec);
        return rec;

    }
    @FXML
    protected void onSendButtonClick() throws IOException {

        Socket s =
                new Socket("localhost",SERVER_PORT );
        //definisco l'imput stream del socket client

        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());

       // out.println(newMail());
        out.writeObject(newMail());
        out.flush();
        //OSS email composta perche lo necessitava il metodo del model, innrealta per adesso mando una stringa

    }

    @FXML
    protected void onReplyButtonClick() {

    }

    @FXML
    protected void onReplyAllButtonClick() {
        //model.deleteEmail(selectedEmail);
        updateDetailView(emptyEmail);
    }

    @FXML
    protected void onbtnNewMailButtonClick() {


        pnlReadMessage.visibleProperty().set(false);
        pnlNewMessage.visibleProperty().set(true);
        txtEmailContent.visibleProperty().set(false);
        txtEmailContentSend.visibleProperty().set(true);


        //lblFrom.setText(String.valueOf(lblUsername));

    }
    @FXML
    protected void onForwardButtonClick() {
        model.deleteEmail(selectedEmail);
        updateDetailView(emptyEmail);
    }

    @FXML
    protected void onInviaButtonClick() {
        model.deleteEmail(selectedEmail);
        updateDetailView(emptyEmail);
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

}
