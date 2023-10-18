package com.github.aklakina.edmma.events;

import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.logicalUnit.DataFactory;
import com.github.aklakina.edmma.logicalUnit.FileHandler;
import org.json.JSONObject;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileChanged implements Event {

    private final Path path;

    public FileChanged(JSONObject event) {
        this.path = Paths.get(event.getString("path"));
    }

    public static boolean registered = SingletonFactory.getSingleton(DataFactory.class).registerEventFactory("fileChanged", FileChanged::new);

    @Override
    public void run() {
        SingletonFactory.getSingleton(FileHandler.class).changed(path);
    }
}
