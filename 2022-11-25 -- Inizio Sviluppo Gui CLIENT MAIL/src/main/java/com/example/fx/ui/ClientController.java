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
    private FloatProperty inboxDim;
    private Client client;
    private Socket socket;

    //AtomicLong idNewEmail=new AtomicLong();
    //private  ObservableList<File> inboxCsv;




    private static final int SERVER_PORT = 8990;


    //private static  File emails= new File("C:/Users/asus/Desktop/UniTo/A.A. 22-23/ProgIII/Progetto ProgIII/2022-11-25 -- Inizio Sviluppo Gui CLIENT MAIL/src/main/resources/csv/emails_"+model.emailAddressProperty().getValue()+"txt");

    @FXML
    public void initialize(Client client) throws IOException {


        if (this.model != null)
            throw new IllegalStateException("Model can only be initialized once");
        socket = new Socket("localhost",SERVER_PORT );

            




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




        // this.onSendButtonClick();

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        Email emailInit=new Email(null,lblUsername.textProperty().getValue(), List.of(lblUsername.textProperty().getValue()), "", "");
       // System.out.println("EMAIL INIT"+emailInit.getSender());
        out.writeObject(emailInit);



       Task task = new Task<Void>() {
           @FXML
            @Override public Void call() throws IOException {
                System.out.println("INIZIO THREAD ASCOLTO ");

                //System.out.println("    GET SCANNER"+in);



               while (true) {
                   ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());

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
                   //if(inStream.readObject()) {
                   Platform.runLater(() -> {
                       // model.refreshEmail();
                       // model.loadToInbox();
                       model.loadToInbox(newEmail);
                       Alert alert = new Alert(Alert.AlertType.INFORMATION);
                       alert.setTitle(lblUsername.textProperty().getValue()+"Inbox");
                       alert.setHeaderText("New Messagge");
                       // alert.setContentText("I have a great message for you!");
                       alert.show();
                   });





                  //  }
                    System.out.println("FINE STREAM  ASCOLTO");
                   // inStream.close();




                }
              //  System.out.println("FINE THREAD ASCOLTO ");

            }
        };
        new Thread(task).start();







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
    protected void onbtnClose(){

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
        List<String> receivers;
        receivers=loadReceiver(txtSendTo.textProperty().getValue());

        String subject=txtSendObj.textProperty().getValue();

        String text=txtEmailContentSend.textProperty().getValue();
        //model.sendSocket( new Email(id, sender,  receivers,  subject,  text));
        Email emailsend = new Email(idNewEmail, sender,  receivers,  subject,  text);
        //System.out.println("EMAIL CLIENT CONTROLLER-->"+idNewEmail+emailsend+"-- "+emailsend.getReceivers()+"-- "+emailsend.getText());
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



        //definisco l'imput stream del socket client
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(newMail());
        // out.flush();



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
