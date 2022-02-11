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
            ct.start();
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

                String username = (String) ois.readObject();

                oos.writeObject("¡Bienvenido al chat! Historial de chat:\n");
                System.out.println("Mensaje inicial enviado al cliente");

                oos.writeObject(chat.getChat(0));
                System.out.println(">> Se ha enviado el chat al usuario '" + username + "'");

                while (true) {
                    String clientResponse = String.valueOf(ois.readObject());
                    System.out.println("----------- " + clientResponse);

                    String svResponse;
                    if (clientResponse.equals("bye")) break;
                    else if (clientResponse.startsWith("message:")) {
                        String currentTime = String.valueOf(java.time.LocalDateTime.now());
                        chat.putMessage(username + " [" + currentTime.substring(11,19) + "]" +
                                clientResponse.substring(("message:").length()));

                        svResponse = "Mensaje enviado.";
                        System.out.println(">> Se ha agregado un mensaje de '" + username + "' al chat...");

                    } else {
                        svResponse = "Mensaje incorrecto, ignorando...";
                        System.out.println("Mensaje de '" + username + "' incorrecto, ignorando...");
                    }

                    oos.writeObject(svResponse);

                    int clientMessagesCount = (int) ois.readObject();

                    if (chat.getChatSize() > clientMessagesCount) {
                        oos.writeObject(chat.getFilteredChat(clientMessagesCount, username));
                    } else {
                        oos.writeObject("");
                    }
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
}
