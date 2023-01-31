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



}

