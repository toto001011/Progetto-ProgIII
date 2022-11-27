package com.example.fx.model;

import com.example.fx.main.EmailClientMain;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
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
    public void generateRandomEmails(int n) {
        String[] people = new String[] {"Paolo", "Alessandro", "Enrico", "Giulia", "Gaia", "Simone"};
        String[] subjects = new String[] {
                "Importante", "A proposito della nostra ultima conversazione", "Tanto va la gatta al lardo",
                "Non dimenticare...", "Domani scuola" };
        String[] texts = new String[] {
                "È necessario che ci parliamo di persona, per mail rischiamo sempre fraintendimenti",
                "Ricordati di comprare il latte tornando a casa",
                "L'appuntamento è per domani alle 9, ci vediamo al solito posto",
                "Ho sempre pensato valesse 42, tu sai di cosa parlo"
        };
        Random r = new Random();
        for (int i=0; i<n; i++) {
            Email email = new Email(idEmail,
                    people[r.nextInt(people.length)],
                    List.of(people[r.nextInt(people.length)]),
                    subjects[r.nextInt(subjects.length)],
                    texts[r.nextInt(texts.length)]);
            inboxContent.add(email);
        }
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


}

