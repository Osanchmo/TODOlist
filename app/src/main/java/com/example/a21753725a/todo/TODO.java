package com.example.a21753725a.todo;

public class TODO {
    String text;
    String mediapath;

    public TODO() {
    }

    public TODO(String text, String mediapath) {

        this.text = text;
        this.mediapath = mediapath;
    }

    public String getMediapath() {
        return mediapath;
    }

    public void setMediapath(String mediapath) {
        this.mediapath = mediapath;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
