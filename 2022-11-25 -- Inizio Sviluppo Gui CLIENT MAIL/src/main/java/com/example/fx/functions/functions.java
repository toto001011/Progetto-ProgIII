package com.example.fx.functions;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
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

            Email email = new Email(
                    dataSplitten[0], Collections.singletonList(dataSplitten[1]),dataSplitten[2],dataSplitten[3]);
            System.out.println("EMAIL COMPOSED-->"+dataSplitten[3]+"++>"+email.getText());


        }

        emailReader.close();
    }

    public static void main(String []args) throws IOException {
        loadEmail1();

    }
}
