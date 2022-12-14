package com.example.fx.model;

import com.example.fx.main.EmailClientMain;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * Classe Client, conterrà la lista di mail che sarà il model
 */

public class Client {
    private final ListProperty<Email> inbox;
    private final ObservableList<Email> inboxContent;
    private final StringProperty emailAddress;

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

    public void replyToAll(Email email){

        //Email newEmail=new Email(email.getReceivers(),email.getSender(), )
    }


    /**
     * Carico da file csv una eventuale lista di mail
     *
     */


    public  void loadEmail() throws IOException {
        File emails= new File("C:/Users/asus/Desktop/UniTo/A.A. 22-23/ProgIII/Progetto ProgIII/2022-11-25 -- Inizio Sviluppo Gui CLIENT MAIL/src/main/resources/csv/emails.txt");
        Scanner emailReader = new Scanner(emails);
        while (emailReader.hasNextLine()) {

            String data = emailReader.nextLine();

            String[] dataSplitten= data.split(";");

            long id=Long.parseLong(dataSplitten[0]);
            System.out.println("DATA"+dataSplitten[4]+" "+emailReader.hasNextLine());
            Email email = new Email(id,
                    dataSplitten[1], Collections.singletonList(dataSplitten[2]),dataSplitten[3],dataSplitten[4]);
            System.out.println("EMAIL COMPOSED-->"+dataSplitten[3]+"++>"+email.getText());
            inboxContent.add(email);
        }
        System.out.println(emailReader.hasNextLine());

        emailReader.close();
    }

    public  void refreshEmail(){
        inboxContent.clear();
        try {
            loadEmail();
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
    public static void deleteMail(long idMail)throws IOException{
        File emails= new File("C:/Users/asus/Desktop/UniTo/A.A. 22-23/ProgIII/Progetto ProgIII/2022-11-25 -- Inizio Sviluppo Gui CLIENT MAIL/src/main/resources/csv/emails.txt");
        File tempEmails = new File("C:/Users/asus/Desktop/UniTo/A.A. 22-23/ProgIII/Progetto ProgIII/2022-11-25 -- Inizio Sviluppo Gui CLIENT MAIL/src/main/resources/csv/Tempemails.txt");

        BufferedReader emailReader = new BufferedReader(new FileReader(emails));
        BufferedWriter emailWriter=new BufferedWriter(new FileWriter(tempEmails));

        String data;

        boolean trovato=false;
        //  System.out.println(emailReader.readLine());
        while ( ((data=emailReader.readLine() )!=null)  ) {

            String[] dataSplitten= data.split(";");
            long id=Long.parseLong(dataSplitten[0]);
            System.out.println("ID"+id);
            if(id==idMail){
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

    public void sendSocket(Email email) throws IOException {
        Socket s =
                new Socket("localhost", 8180);

        PrintWriter
                out = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(
                                s.getOutputStream())),
                true);

        out.println("PROVA INVIO");
        out.flush();

    }


}

