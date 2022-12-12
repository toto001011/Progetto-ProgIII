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

    public long idEmail;
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
            Email email = new Email(id,
                   dataSplitten[1], Collections.singletonList(dataSplitten[2]),dataSplitten[3],dataSplitten[4]);
            System.out.println("EMAIL COMPOSED-->"+dataSplitten[3]+"++>"+email.getText());
            inboxContent.add(email);
        }

        emailReader.close();
    }
    /**
     *
     * @param receivers
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

