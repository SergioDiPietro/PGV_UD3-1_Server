package com.dipisoft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chat extends Thread {
    List<String> chat = Collections.synchronizedList(new ArrayList<>());

    public Chat () {}

    public synchronized void putMessage(String message) {
        chat.add(message);
    }

    public String getChat(int fromIndex) {
        String chatString = "";
        for (int i = fromIndex; i < chat.size(); i++) {
            chatString += chat.get(i) + "\n";
        }
        return chatString;
    }

    public int getChatSize() {
        return chat.size();
    }

    public String getFilteredChat(int fromIndex, String username) {
        String chatString = "";
        for (int i = fromIndex; i < chat.size(); i++) {
            if (!(chat.get(i).contains(username))) chatString += chat.get(i) + "\n";
        }
        return chatString;
    }
}
