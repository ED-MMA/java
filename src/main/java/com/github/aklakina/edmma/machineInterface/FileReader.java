package com.github.aklakina.edmma.machineInterface;

import com.github.aklakina.edmma.base.Singleton;
import com.github.aklakina.edmma.database.ORMConfig;
import com.github.aklakina.edmma.database.orms.FileData;
import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.logicalUnit.DataFactory;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

@Singleton
public class FileReader {

    private boolean shouldClose = false;

    private static class ReadingData implements Runnable {
        public BufferedReader reader;
        public Integer currentLine;
        private final FileData file;

        private boolean firstRun = true;

        public synchronized void changed() {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isEmpty()) {
                        System.out.println("Empty line");
                        currentLine++;
                        continue;
                    }
                    SingletonFactory.getSingleton(DataFactory.class).spawnEvent(new JSONObject(line));
                    currentLine++;
                }
            } catch (Exception e) {
                System.out.println("Error reading file");
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }

        }

        public ReadingData(FileData file) {
            this.file = file;
            this.currentLine = 0;
            try {
                FileInputStream fileInputStream = new FileInputStream(file.toNative());
                reader = new BufferedReader(new InputStreamReader(fileInputStream));
                while (currentLine < file.getLastLineRead()) {
                    reader.readLine();
                    currentLine++;
                }
            } catch (Exception e) {
                System.out.println("Error reading file: " + file);
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (!SingletonFactory.getSingleton(FileReader.class).shouldClose) {
                    if (firstRun) {
                        firstRun = false;
                    } else {
                        synchronized (Thread.currentThread()) {
                            Thread.currentThread().wait();
                            System.out.println("File changed: " + file);
                        }
                    }
                    changed();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            file.setLastLineRead(currentLine);
            file.setLastSize(file.toNative().length());
            try {
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private final HashMap<FileData, Thread> readers;

    public synchronized void processEvent(FileData file) {
        if (!readers.containsKey(file)) {
            System.out.println("New file detected: " + file);
            ReadingData readingData = new ReadingData(file);
            Thread thread = new Thread(readingData);
            thread.start();
            readers.put(file, thread);
            System.out.println("New reader started on " + file);
        }
        synchronized (readers.get(file)) {
            readers.get(file).notify();
        }
    }

    private final SessionFactory sessionFactory;

    public FileReader() {
        this.sessionFactory = ORMConfig.sessionFactory;
        readers = new HashMap<>();
    }

    public void close() {
        SingletonFactory.getSingleton(FileReader.class).shouldClose = true;
        for (FileData fileData: readers.keySet()) {
            Thread thread = readers.get(fileData);
            synchronized (thread) {
                thread.notify();
            }
            try {
                thread.join();
                System.out.println("File reader joined: " + fileData.getName() + " " + fileData.getLastLineRead() + " " + fileData.getLastSize());
                EntityManager entityManager = this.sessionFactory.createEntityManager();
                entityManager.getTransaction().begin();
                entityManager.merge(fileData);
                entityManager.getTransaction().commit();
                entityManager.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
