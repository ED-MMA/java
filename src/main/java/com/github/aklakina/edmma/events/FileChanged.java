package com.github.aklakina.edmma.events;

public class FileChanged implements Event {

    private final String path;

    public FileChanged(String path) {
        this.path = path;
    }

    @Override
    public void process() {

    }
}
