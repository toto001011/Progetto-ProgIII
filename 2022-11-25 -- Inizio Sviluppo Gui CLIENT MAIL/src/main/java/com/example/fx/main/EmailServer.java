package com.example.fx.main; /**
 @author Cay Horstmann
 @version 1.20 2004-08-03
 modificata...
 */

import java.io.*;
import java.net.*;
import java.util.*;

/**
 This program implements a multithreaded server that listens to port 8189 and echoes back
 all client input.
 */
public class EmailServer {
    public static void main(String[] args ) {
        System.out.println("Finestra del server: ");
        try {
            int i = 1;
            ServerSocket s = new ServerSocket(8180);

            while (true) {
                Socket incoming = s.accept(); // si mette in attesa di richiesta di connessione e la apre
                System.out.println("Spawning " + i);
                Runnable r = new ThreadedEchoHandler(incoming, i);
                new Thread(r).start();//OSS. Nel progetto passo il runnable non direttamente al thread ma all'executor(precedentemente definito)
                i++;
            }
        }
        catch (IOException e) {e.printStackTrace();}
    }
}

/**
 This class handles the client input for one server socket connection.
 */
class ThreadedEchoHandler implements Runnable {

    private Socket incoming;
    private int counter;

    /**
     Constructs a handler.
     @param in the incoming socket
     @param c the counter for the handlers (used in prompts)
     */
    public ThreadedEchoHandler(Socket in, int c) {
        incoming = in;
        counter = c;
    }

    public void run() {
        try {
            try {
                InputStream inStream = incoming.getInputStream();
                OutputStream outStream = incoming.getOutputStream();
                //ObjectOutputStream dOut=  dOut. incoming.getOutputStream();

                Scanner in = new Scanner(inStream);
                PrintWriter out = new PrintWriter(outStream, true /* autoFlush */);



                //out.println( "Hello! Enter BYE to exit." );

                // out.println(in.nextLine());
                // echo client input
                boolean done = false;
                while (!done && in.hasNextLine()) {
                    System.out.println("ENTRATO");
                    System.out.println("Soket value-->"+in.nextLine());
                    String line = in.nextLine();
                    out.println("Echo: " + line);
                    System.out.println("ECHO: "+ line);
                    if (line.trim().equals("BYE"))
                        done = true;
                }
            }
            finally {
                incoming.close();
            }
        }
        catch (IOException e) {e.printStackTrace();}
    }

}

