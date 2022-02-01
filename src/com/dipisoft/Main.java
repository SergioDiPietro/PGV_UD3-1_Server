package com.dipisoft;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        Socket s = null;
        Chat chat = new Chat();

        int port = 8080;
        ServerSocket ss = new ServerSocket(port);
        System.out.println(">> Servidor escuchando en el puerto " + port + "...");

        while (true) {
            s = ss.accept();
            Hilo worker = new Hilo(s, chat);
            worker.start();
        }
    }

    static class Hilo extends Thread {
        private Socket s = null;
        private ObjectInputStream ois = null;
        private ObjectOutputStream oos = null;
        private final Chat chat;

        public Hilo(Socket socket, Chat chat) {
            this.s = socket;
            this.chat = chat;
        }

        public void run() {
            System.out.println(">> Conexión recibida desde " + s.getInetAddress());

            try {
                ois = new ObjectInputStream(s.getInputStream());
                oos = new ObjectOutputStream(s.getOutputStream());

                String username = (String) ois.readObject();

                oos.writeObject(chat.getChat());
                System.out.println("Se ha enviado el chat al usuario '" + username + "'");

                String receivedText = (String)ois.readObject();

                if (receivedText.startsWith("message:")) {
                    chat.putMessage("[" + System.currentTimeMillis() + "] " + username +
                            receivedText.substring(("message").length()));
                    System.out.println(">> Se ha agregado un mensaje al chat...");
                } else if (receivedText.equals("bye")) {
                    oos.writeObject("Goodbye!");
                    System.out.println(">> Se ha finalizado la conexión con '" + username + "'");
                } else {
                    oos.writeObject("Mensaje incorrecto e ignorado...");
                    System.out.println(">> Se ha ignorado un mensaje...");
                }

                System.out.println(chat.getChat());

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (oos != null) oos.close();
                    if (oos != null) ois.close();
                    if (s != null) ois.close();
                    System.out.println(">> Conexión cerrada...");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
