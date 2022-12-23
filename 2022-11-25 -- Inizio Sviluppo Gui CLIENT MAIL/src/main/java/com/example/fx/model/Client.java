package com.example.fx.model;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Classe Client, conterrà la lista di mail che sarà il model
 */

public class Client {

    private final ListProperty<Email> inbox;


    private final ObservableList<Email> inboxContent;
    //private ObservableP CvsDim;
    private final StringProperty emailAddress;

    //private final FloatProperty inboxDim;
  // private static  File emails= new File("C:/Users/asus/Desktop/UniTo/A.A. 22-23/ProgIII/Progetto ProgIII/2022-11-25 -- Inizio Sviluppo Gui CLIENT MAIL/src/main/resources/csv/emails.txt");
    private static   File emails;
    //= new File("C:/Users/asus/Desktop/UniTo/A.A. 22-23/ProgIII/Progetto ProgIII/2022-11-25 -- Inizio Sviluppo Gui CLIENT MAIL/src/main/resources/csv/emails.txt");

    @FXML
    private Stage stage;

    /*private    Socket s;

    {
        try {
            s = new Socket("localhost", 8990);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/

    // public long idEmail;
    /**
     * Costruttore della classe.
     *
     * @param emailAddress   indirizzo email
     *
     */

    public Client(String emailAddress) {
        this.inboxContent = FXCollections.observableList(new LinkedList<>());
        this.inbox = new SimpleListProperty<>();
        this.inbox.set(inboxContent);
       // System.out.println("EMAIL ADDRESS"+emailAddress);
        this.emailAddress = new SimpleStringProperty(emailAddress);
        this.emails=new File("C:/Users/asus/Desktop/UniTo/A.A. 22-23/ProgIII/Progetto ProgIII/2022-11-25 -- Inizio Sviluppo Gui CLIENT MAIL/src/main/resources/csv/emails_"+this.emailAddress.getValue()+".txt");
        System.out.println("FILE EMAIL CLIENT"+ Path.of(emails.toURI()).toString());
            System.out.println("TRY TO SEND SOCKET");
            //sendSocket(new Email(null,this.emailAddress.getValue(), List.of(""), "", ""));
            System.out.println("THIS ADDRESS"+this.emailAddress.getValue());
            System.out.println("SOCKET SENDED");

    }



    /**
     * @return      lista di email
     *
     */
    public ListProperty<Email> inboxProperty() {
        return inbox;
    }

    /**
     *
     * @return   indirizzo email della casella postale
     *
     */
    public StringProperty emailAddressProperty() {
        return emailAddress;
    }

    /**
     *
     * @return email adress di questo particolare casella
     */




/*    public void replyToAll(Email email){

        //Email newEmail=new Email(email.getReceivers(),email.getSender(), )
    }
*/

    /**
     * Carico da file csv una eventuale lista di mail
     *
     */


    public  void loadEmail() throws IOException {
       File emails= new File("C:/Users/asus/Desktop/UniTo/A.A. 22-23/ProgIII/Progetto ProgIII/2022-11-25 -- Inizio Sviluppo Gui CLIENT MAIL/src/main/resources/csv/emails_"+this.emailAddress.getValue()+".txt");
        Scanner emailReader = new Scanner(emails);
        System.out.println("EMAIL ADDRESS-->"+emailAddress);
        System.out.println("EMAIL PATH LOAD"+emails+"\n\n");


        while (emailReader.hasNextLine()) {

            String data = emailReader.nextLine();

            String[] dataSplitten= data.split(";");

            String id=dataSplitten[0];

            System.out.println("DATA"+dataSplitten[4]+" "+emailReader.hasNextLine());
            Email email = new Email(id,
                    dataSplitten[1], Collections.singletonList(dataSplitten[2]),dataSplitten[3],dataSplitten[4]);
            inboxContent.add(email);

        }
        System.out.println(emailReader.hasNextLine());


        emailReader.close();
    }





    public  void refreshEmail(){
        inboxContent.clear();
       // System.out.println("REFRESH EMAIL"+emails);
        try {
            loadEmail();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


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
        File emails= new File("C:/Users/asus/Desktop/UniTo/A.A. 22-23/ProgIII/Progetto ProgIII/2022-11-25 -- Inizio Sviluppo Gui CLIENT MAIL/src/main/resources/csv/emails_"+this.emailAddress.getValue()+".txt");

        BufferedReader emailReader = new BufferedReader(new FileReader(emails));
        BufferedWriter emailWriter=new BufferedWriter(new FileWriter(tempEmails));

        String data;

        boolean trovato=false;
        //  System.out.println(emailReader.readLine());
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
        //emails=tempEmails;//per permettere al listener di avviso ricezione mail che il file "è cambiato"

        System.out.println("RENAMED");


    }



    /**
     *
     * @param email
     * @return rec
     *
     * Funzione che carica "individualmente i riceventi nwi socket e
     * prende in input una lista di riceventi separati da virgola
     * restituisce un ArrayList di riceventi
     */

    /*public void sendSocket(Email email) throws IOException {

        PrintWriter
                out = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(
                                s.getOutputStream())),
                true);

      //  out.println("PROVA INVIO");

        out.flush();

    }*/

    public void loadToInbox(Email email){
        inboxContent.add(email);
    }


}

