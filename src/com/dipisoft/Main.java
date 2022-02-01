package com.dipisoft;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        Socket socket = null;
        Chat chat = new Chat();

        int port = 8080;
        ServerSocket ss = new ServerSocket(port);
        System.out.println(">> Servidor escuchando en el puerto " + port + "...");

        while (true) {
            socket = ss.accept();
            ChatThread ct = new ChatThread(socket, chat);
            ChatUpdateThread cut = new ChatUpdateThread(socket, chat);
            ct.start();
            cut.start();
        }
    }

    static class ChatThread extends Thread {
        private Socket s = null;
        private ObjectInputStream ois = null;
        private ObjectOutputStream oos = null;
        private final Chat chat;

        public ChatThread(Socket socket, Chat chat) {
            this.s = socket;
            this.chat = chat;
        }

        public void run() {
            System.out.println(">> Conexión recibida desde " + s.getInetAddress());

            try {
                ois = new ObjectInputStream(s.getInputStream());
                oos = new ObjectOutputStream(s.getOutputStream());

                oos.writeObject("Introduzca su nombre de usuario: ");
                String username = (String) ois.readObject();

                oos.writeObject("Historial de chat:\n" + chat.getChat());
                System.out.println(">> Se ha enviado el chat al usuario '" + username + "'");

                String receivedText = (String)ois.readObject();
                while (!receivedText.equals("bye")) {
                    if (receivedText.startsWith("message:")) {
                        chat.putMessage("[" + System.currentTimeMillis() + "] " + username +
                                receivedText.substring(("message").length()));
                        oos.writeObject("Mensaje enviado.");
                        System.out.println(">> Se ha agregado un mensaje de '" + username + "' al chat...");
                    } else {
                        oos.writeObject("Mensaje incorrecto, ignorando...");
                        System.out.println("Mensaje de '" + username + "' incorrecto, ignorando...");
                    }
                    receivedText = (String)ois.readObject();
                }
                oos.writeObject("goodbye");
                System.out.println(">> Se ha finalizado la conexión con '" + username + "'");

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

    // Experimento, thread adicional para controlar los mensajes nuevos
    private static class ChatUpdateThread extends Thread {
        private Socket s = null;
        private ObjectInputStream ois = null;
        private ObjectOutputStream oos = null;
        private final Chat chat;

        public ChatUpdateThread(Socket socket, Chat chat) {
            this.s = socket;
            this.chat = chat;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    String message = chat.getNewMessage();
                    //TODO notificar a clientes y enviar el último mensaje
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
