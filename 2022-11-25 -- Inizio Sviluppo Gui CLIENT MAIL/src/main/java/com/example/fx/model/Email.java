package com.example.fx.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta una mail
 */

public class Email implements Serializable {

    private String sender;
    private List<String> receivers;
    private String subject;
    private String text;
    private long id;

    private Email() {}

    /**
     * Costruttore della classe.
     *
     * @param sender     email del mittente
     * @param receivers  emails dei destinatari
     * @param subject    oggetto della mail
     * @param text       testo della mail
     * @param id          id univoco della mail in quella inbox
     */


    public Email(long id,String sender, List<String> receivers, String subject, String text) {
        this.id=id;
        this.sender = sender;
        this.subject = subject;
        this.text = text;
        this.receivers = new ArrayList<>(receivers);
    }

    public String getSender() {
        return sender;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public String getSubject() {
        return subject;
    }

    public String getText() {
        return text;
    }

    public long getId() {
        return id;
    }


    public void sendMailToInbox(Email email) throws IOException {
       // File emails= open File("");
        //String textToAppend = "Happy Learning !!";
        String filePath = "C:/Users/asus/Desktop/UniTo/A.A. 22-23/ProgIII/Progetto ProgIII/2022-11-25 -- Inizio Sviluppo Gui CLIENT MAIL/src/main/resources/csv/emails.txt";

        try(FileWriter fw = new FileWriter(filePath, true);
            BufferedWriter emailWriter = new BufferedWriter(fw);) {




//        BufferedWriter emailWriter=new BufferedWriter(new FileWriter(emails));


            System.out.println("ID:" + id+" TO:"+email.receivers);

            emailWriter.append(email.getId()+";"+email.getSender()+";"+email.receivers.get(0)+";"+email.getSubject()+";"+email.getText()+";\n");
           // emailWriter.newLine();

        }




        System.out.println("EMAIL SEND");



    }

    /**
     * @return      stringa composta dagli indirizzi e-mail del mittente più destinatari
     */
    @Override
    public String toString() {
        return String.join(" - ", List.of(this.sender,this.subject));
    }


/*
    public String toString() {
        return String.join(" - ", List.of(this.sender,this.subject));

        //return String.join(" - ", List.of(this.sender,this.subject,this.text));
        //return ""+this.id+" - "+this.sender+" - "+receivers.get(0)+" - "+this.subject+" - "+this.text;
    }*/
}
