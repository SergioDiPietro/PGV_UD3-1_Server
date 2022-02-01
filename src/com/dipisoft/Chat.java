package com.dipisoft;

public class Chat {
    private String chat = "";
    private boolean empty = true;

    public synchronized void putMessage(String message) {
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
}
