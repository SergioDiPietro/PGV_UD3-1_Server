package com.dipisoft;

public class Chat {
    private String chat = "";
    private String lastMessage = "";
    private boolean empty = true;
    private boolean newMessage = false;

    public synchronized void putMessage(String message) {
        lastMessage = message;
        newMessage = true;
        if (empty) {
            chat = message;
            empty = false;
        } else chat = chat + "\n" + message;
        notifyAll();
    }

    public String getChat() {
        if (empty) return "<Chat vacío>";
        else return chat;
    }

    public synchronized String getNewMessage() throws InterruptedException {
        while (!newMessage) wait();

        return lastMessage;
    }
}
