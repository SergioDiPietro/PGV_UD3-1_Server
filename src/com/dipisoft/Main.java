package com.dipisoft;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        Socket s = null;

        int port = 8080;
        ServerSocket ss = new ServerSocket(port);
        System.out.println(">> Servidor escuchando en el puerto " + port + "...");

        while (true) {
            s = ss.accept();
            Hilo worker = new Hilo(s);
            worker.start();
        }
    }

    static class Hilo extends Thread {
        private Socket s = null;
        private ObjectInputStream ois = null;
        private ObjectOutputStream oos = null;

        public Hilo(Socket socket) {
            s = socket;
        }

        public void run() {
        }
    }
}
