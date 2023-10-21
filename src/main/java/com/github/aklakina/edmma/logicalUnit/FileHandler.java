package com.github.aklakina.edmma.logicalUnit;

import com.github.aklakina.edmma.base.*;
import com.github.aklakina.edmma.machineInterface.FileReader;

import java.nio.file.Path;
import java.util.TreeMap;

@Singleton
public class FileHandler {

    private TreeMap<Path, FileReader> fileReaders = new TreeMap<>();

    public void changed(Path path) {
        if (fileReaders.containsKey(path)) {
            fileReaders.get(path).changed();
        } else {
            //FileData temp = SingletonFactory.getSingleton(Getter.class).getFile(path.getFileName().toString());
            //fileReaders.put(path, new FileReader(temp));
            System.out.println("Not implemented yet");
        }
    }

}
