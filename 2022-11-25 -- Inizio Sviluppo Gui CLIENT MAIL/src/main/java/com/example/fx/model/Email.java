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
    private String id;


    /**
     * Costruttore della classe.
     *
     * @param sender     email del mittente
     * @param receivers  emails dei destinatari
     * @param subject    oggetto della mail
     * @param text       testo della mail
     * @param id          id univoco della mail generara con una librerria specifica
     */


    public Email(String id,String sender, List<String> receivers, String subject, String text) {
        this.id=id;
        this.sender = sender;
        this.subject = subject;
        this.text = text;
        this.receivers = new ArrayList<>(receivers);
    }
    public Email(){}

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

    public String getId() {
        return id;
    }





    public void sendMailToInbox(Email email) throws IOException {

        String rcvsString="";
        int i=0;
        while(i<receivers.size()-1) {
            rcvsString =""+rcvsString+receivers.get(i)+",";
            i++;
        }
        rcvsString =""+rcvsString+receivers.get(i);
        System.out.println("RECEIVERS"+rcvsString);
        i=0;
        while(i<receivers.size()) {
            //String filePath = "C:/Users/asus/Desktop/UniTo/A.A. 22-23/ProgIII/Progetto ProgIII/2022-11-25 -- Inizio Sviluppo Gui CLIENT MAIL/src/main/resources/csv/emails.txt";
            String filePath = "C:/Users/asus/Desktop/UniTo/A.A. 22-23/ProgIII/Progetto ProgIII/2022-11-25 -- Inizio Sviluppo Gui CLIENT MAIL/src/main/resources/csv/emails_" + receivers.get(i) + ".txt";
            try (FileWriter fw = new FileWriter(filePath, true);
                 BufferedWriter emailWriter = new BufferedWriter(fw);) {

                System.out.println("ID:" + id + " TO:" + email.receivers.get(i));


                emailWriter.append(email.getId() + ";" + email.getSender() + ";" + rcvsString + ";" + email.getSubject() + ";" + email.getText() + ";\n");
                // emailWriter.newLine();


            }
            i++;
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


    public boolean findInbox(String email){
        boolean exist=false;
        String filePath = "C:/Users/asus/Desktop/UniTo/A.A. 22-23/ProgIII/Progetto ProgIII/2022-11-25 -- Inizio Sviluppo Gui CLIENT MAIL/src/main/resources/csv/emails_" + email + ".txt";

        File f = new File(filePath);

        try {
            exist= f.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return exist;
    }

}
