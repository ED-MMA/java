package com.github.aklakina.edmma.machineInterface;

import com.github.aklakina.edmma.database.orms.FileData;
import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.logicalUnit.DataFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class FileReader {

    private final FileData file;
    private Integer currentLine = 0;
    private BufferedReader reader;

    private Integer exponentialBackoff = 1;

    private void processNextLine(String line) throws InterruptedException {
        while (true) {
            try {
                if (currentLine > file.getLastLineRead()) {
                    SingletonFactory.getSingleton(DataFactory.class).spawnEvent(new JSONObject(line));
                    exponentialBackoff = 1;
                }
                break;
            } catch (JSONException e) {
                System.out.println("Error parsing line: " + line);
                System.out.println("Waiting for logger to finish writing");
                exponentialBackoff *= 2;
                Thread.sleep(exponentialBackoff);
            }
        }
    }

    public FileReader(FileData file) {
        this.file = file;
        try {
            FileInputStream fileInputStream = new FileInputStream(file.toNative());
            reader = new BufferedReader(new InputStreamReader(fileInputStream));
            this.changed();
        } catch (Exception e) {
            System.out.println("Error reading file: " + file);
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public synchronized void changed() {
        if (file.getChanged()) {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    processNextLine(line);
                    currentLine++;
                }
                file.setLastLineRead(currentLine);
                file.reCalcSize();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
