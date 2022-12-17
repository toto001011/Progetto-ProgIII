 package com.example.fx.ui;
import javafx.beans.property.FloatProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import com.example.fx.model.Client;
import com.example.fx.model.Email;
import javafx.scene.layout.BorderPane;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

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
    private FloatProperty inboxDim;
    private Client client;
    private Socket s;

    //AtomicLong idNewEmail=new AtomicLong();
    //private  ObservableList<File> inboxCsv;




    private static final int SERVER_PORT = 8990;


    //private static  File emails= new File("C:/Users/asus/Desktop/UniTo/A.A. 22-23/ProgIII/Progetto ProgIII/2022-11-25 -- Inizio Sviluppo Gui CLIENT MAIL/src/main/resources/csv/emails_"+model.emailAddressProperty().getValue()+"txt");

    @FXML
    public void initialize(Client client) throws IOException {
        if (this.model != null)
            throw new IllegalStateException("Model can only be initialized once");
        //istanza nuovo client
        model = client;//new Client("email");
        System.out.println("CLIENT-->"+model.emailAddressProperty());
      //  model=client;
        model.loadEmail();
        //listenInbox();

        selectedEmail = null;
        //System.out.println("LIST EMAIL"+lstEmails);
        //binding tra lstEmails e inboxProperty
        lstEmails.itemsProperty().bind(model.inboxProperty());
        lstEmails.setOnMouseClicked(this::showSelectedEmail);
        lblUsername.textProperty().bind(model.emailAddressProperty());
        lblUsernameSend.textProperty().bind(model.emailAddressProperty());

        emptyEmail = new Email(null,"", List.of(""), "", "");

        //inboxDim=model.amountDueProperty();







      //  listenInbox();






    }

    /**
     * Elimina la mail selezionata
     */
    @FXML
    protected void onDeleteButtonClick() {
        model.deleteEmail(selectedEmail);
        //File emails= new File("C:/Users/asus/Desktop/UniTo/A.A. 22-23/ProgIII/Progetto ProgIII/2022-11-25 -- Inizio Sviluppo Gui CLIENT MAIL/src/main/resources/csv/emails_"+model.emailAddressProperty().getValue()+".txt");

        updateDetailView(emptyEmail);
    }

    @FXML
    protected void onbtUpdateViewlButtonClick(){

            model.refreshEmail();
            System.out.println(("MODEL EMAIL "+model.emailAddressProperty()));
            updateDetailView(emptyEmail);

    }


    protected Email newMail(){
         String idNewEmail = UUID.randomUUID().toString();
        //String sender= String.valueOf(lblUsernameSend);//"sender";
        String sender=lblUsernameSend.textProperty().getValue();
        txtSendTo.textProperty().getValue();
        List<String> receivers=new ArrayList<String>();
        receivers=loadReceiver(txtSendTo.textProperty().getValue());

        String subject=txtSendObj.textProperty().getValue();

        String text=txtEmailContentSend.textProperty().getValue();
        //model.sendSocket( new Email(id, sender,  receivers,  subject,  text));
        Email emailsend = new Email(idNewEmail, sender,  receivers,  subject,  text);
        System.out.println("EMAIL CLIENT CONTROLLER-->"+idNewEmail+emailsend+"-- "+emailsend.getReceivers()+"-- "+emailsend.getText());
       // idNewEmail.getAndIncrement();

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
       // File emails= new File("C:/Users/asus/Desktop/UniTo/A.A. 22-23/ProgIII/Progetto ProgIII/2022-11-25 -- Inizio Sviluppo Gui CLIENT MAIL/src/main/resources/csv/emails_"+model.emailAddressProperty().getValue()+".txt");

        s = new Socket("localhost",SERVER_PORT );

        //definisco l'imput stream del socket client
        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
        out.writeObject(newMail());
        out.flush();



        //OSS email composta perche lo necessitava il metodo del model, innrealta per adesso mando una stringa
        //inboxDim.setValue(Files.size(emails.toPath()));
        //System.out.println("PROPERTIES-->"+inboxDim);
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


/*    public void listenInbox(){
        inboxDim.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                System.out.println("NEW MESSAGES"+t1);
                model.refreshEmail();
            }
        });

}*/


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
}
