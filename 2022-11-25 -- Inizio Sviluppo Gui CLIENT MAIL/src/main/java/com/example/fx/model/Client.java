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

    private static   File emails;

    @FXML
    private Stage stage;



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
        this.emailAddress = new SimpleStringProperty(emailAddress);
       // this.emails=new File("C:/Users/asus/Desktop/UniTo/A.A. 22-23/ProgIII/Progetto ProgIII/2022-11-25 -- Inizio Sviluppo Gui CLIENT MAIL/src/main/resources/csv/emails_"+this.emailAddress.getValue()+".txt");

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



    /**
     * Carico da file csv una eventuale lista di mail
     *
     */

/*
    public  void loadEmail() throws IOException {
       File emails= new File("C:/Users/asus/Desktop/UniTo/A.A. 22-23/ProgIII/Progetto ProgIII/2022-11-25 -- Inizio Sviluppo Gui CLIENT MAIL/src/main/resources/csv/emails_"+this.emailAddress.getValue()+".txt");
        Scanner emailReader = new Scanner(emails);


        while (emailReader.hasNextLine()) {

            String data = emailReader.nextLine();

            String[] dataSplitten= data.split(";");

            String id=dataSplitten[0];

            Email email = new Email(id,
                    dataSplitten[1], Collections.singletonList(dataSplitten[2]),dataSplitten[3],dataSplitten[4]);
            inboxContent.add(email);

        }

        emailReader.close();
    }
*/



/*
    public  void refreshEmail(){
        inboxContent.clear();
        try {
            loadEmail();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }*/

    /**
     *
     * @return   elimina l'email specificata
     *
     */
    /*
    public void deleteEmail(Email email) {

        inboxContent.remove(email);
        try {
            deleteMail(email.getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    */

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



    /**
     *
     * @param email
     * @return rec
     *
     * Funzione che carica "individualmente i riceventi nwi socket e
     * prende in input una lista di riceventi separati da virgola
     * restituisce un ArrayList di riceventi
     */



    public void loadToInbox(Email email){
        inboxContent.add(email);
    }


}

