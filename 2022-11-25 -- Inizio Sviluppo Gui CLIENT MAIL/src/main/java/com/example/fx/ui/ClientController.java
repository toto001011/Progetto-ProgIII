 package com.example.fx.ui;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import com.example.fx.model.Client;
import com.example.fx.model.Email;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
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




    @FXML
    public void initialize() throws IOException {
        if (this.model != null)
            throw new IllegalStateException("Model can only be initialized once");
        //istanza nuovo client
        model = new Client("studente@unito.it");
        model.loadEmail();
        //model.generateRandomEmails(10);

        selectedEmail = null;

        //binding tra lstEmails e inboxProperty
        lstEmails.itemsProperty().bind(model.inboxProperty());
        lstEmails.setOnMouseClicked(this::showSelectedEmail);
        lblUsername.textProperty().bind(model.emailAddressProperty());
        lblUsernameSend.textProperty().bind(model.emailAddressProperty());

        emptyEmail = new Email("", List.of(""), "", "");

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
    protected void onSendButtonClick() {


    }

    @FXML
    protected void onReplyButtonClick() {

    }

    @FXML
    protected void onReplyAllButtonClick() {
        model.deleteEmail(selectedEmail);
        updateDetailView(emptyEmail);
    }

    @FXML
    protected void onbtnNewMailButtonClick() {


        pnlReadMessage.visibleProperty().set(false);
        pnlNewMessage.visibleProperty().set(true);
        txtEmailContent.setText(null);


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
