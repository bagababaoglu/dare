package com.salbe.dare.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Card {
    public String author;
    public String message;

    public Card() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Card(String author, String message) {
        this.author = author;
        this.message = message;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("author", author);
        result.put("message", message);

        return result;
    }

}