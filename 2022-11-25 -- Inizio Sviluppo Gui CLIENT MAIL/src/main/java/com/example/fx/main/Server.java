package com.example.fx.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    /**
     * Inizializza il server
     */
    public void runServer() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        int serverPort = 8990;
        try {
            System.out.println("Starting Server");
            ServerSocket emailserverSocket = new ServerSocket(8990);

            while (true) {
                System.out.println("Waiting for request");
                try {
                    Socket s = emailserverSocket.accept();
                    System.out.println("Processing request");
                    executorService.submit(new ServiceRequest(s));
                } catch (IOException ioe) {
                    System.out.println("Error accepting connection");
                    ioe.printStackTrace();

                }
            }
        } catch (IOException e) {
            System.out.println("Error starting Server on " + serverPort);
            e.printStackTrace();
        }
    }

    /**
     * Logica di funzionamento del server
     */
    class ServiceRequest implements Runnable {

        private Socket socket;

        public ServiceRequest(Socket connection) {
            this.socket = connection;
        }

        public void run() {


            //logArea.appendText("Testo di Log");
            try {
                socket.close();
            } catch (IOException ioe) {
                System.out.println("Error closing client connection");
            }
        }

    }
}
