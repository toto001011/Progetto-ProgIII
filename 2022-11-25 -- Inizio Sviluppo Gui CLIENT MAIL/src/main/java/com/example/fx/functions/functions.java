package com.example.fx.functions;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import com.example.fx.model.Email;
public class functions {

    /**
     * Carico da file csv una eventuale lista di mail
     *
     */
    public static void loadEmail() throws IOException {
        File emails= new File("C:/Users/asus/Desktop/UniTo/A.A. 22-23/ProgIII/Progetto ProgIII/2022-11-25 -- Inizio Sviluppo Gui CLIENT MAIL/src/main/resources/csv/emails.txt");
        Scanner emailReader = new Scanner(emails);
        while (emailReader.hasNextLine()) {
            String data = emailReader.nextLine();
            String[] from= data.split(";");

            //for (String a : from[])
                System.out.println(from[0]);
        }
        emailReader.close();
    }


    public static void loadEmail1() throws IOException {
        File emails= new File("C:/Users/asus/Desktop/UniTo/A.A. 22-23/ProgIII/Progetto ProgIII/2022-11-25 -- Inizio Sviluppo Gui CLIENT MAIL/src/main/resources/csv/emails.txt");
        Scanner emailReader = new Scanner(emails);
        while (emailReader.hasNextLine()) {
            String data = emailReader.nextLine();

            String[] dataSplitten= data.split(";");
            long id=Long.parseLong(dataSplitten[0]);
            Email email = new Email(id,
                    dataSplitten[1], Collections.singletonList(dataSplitten[2]),dataSplitten[3],dataSplitten[4]);
            System.out.println("EMAIL COMPOSED-->"+dataSplitten[3]+"++>"+email.getText());

        }

        emailReader.close();
    }

    /**
     * Cancella la mail dal file csv Ricopiando quelle che devono rimanere in un altro file temporaneo che poi
     * viene rinominato in quello originale
     * @param idMail: indica il codice identificativo (progressivo) univoco della mail
     *!!!!ATTENZIONE NON INOMINA IL FIME TEMPORANEO IN emails.txt!!!!
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
     * @param receivers
     * @return rec
     *
     * Funzione che carica "individualmente i riceventi nwi socket e
     * prende in input una lista di riceventi separati da virgola
     * restituisce un ArrayList di riceventi
     */
    public static List<String> loadReceiver(String receivers){

        ArrayList<String> rec= new ArrayList<>();

        String[] rvalue = receivers.split(",");

        for (String rsplit : rvalue) {
            rec.add(rsplit);

        }
        System.out.println(rec);
        return rec;

    }

    public static void main(String []args) throws IOException {
        //loadEmail1();
        deleteMail(0);

    }
}
