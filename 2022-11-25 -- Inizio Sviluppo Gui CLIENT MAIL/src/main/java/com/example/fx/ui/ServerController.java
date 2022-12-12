package com.example.fx.ui;
import javafx.fxml.FXML;

import javafx.scene.control.TextArea;

import java.io.*;
import java.util.*;

import java.io.IOException;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Classe Controller
 */

public class ServerController implements Callable<Socket> {

    private static final int NUM_THREAD = 3;
    @FXML
    private TextArea logArea;


    @FXML
    public void onBtnActivate() {
       // activateServer();
    }
    @FXML
    public void initialize() {

        //  System.out.println("Finestra del server: ");

    }



    @Override
    public Socket call() throws Exception {
        return null;
    }
}
