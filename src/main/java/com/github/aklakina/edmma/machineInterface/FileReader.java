package com.github.aklakina.edmma.machineInterface;

import com.github.aklakina.edmma.base.Singleton;
import com.github.aklakina.edmma.database.ORMConfig;
import com.github.aklakina.edmma.database.orms.FileData;
import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.logicalUnit.DataFactory;
import com.github.aklakina.edmma.logicalUnit.Init;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

@Singleton
public class FileReader {

    private static final Logger logger = LogManager.getLogger(FileReader.class);

    private boolean shouldClose = false;

    private static class ReadingData implements Runnable {
        private static final Logger logger = LogManager.getLogger(ReadingData.class);
        public BufferedReader reader;
        public Integer currentLine;
        private final FileData file;
        private boolean firstRun = true;
        public synchronized void changed() {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isEmpty()) {
                        logger.debug("Empty line");
                        currentLine++;
                        continue;
                    }
                    SingletonFactory.getSingleton(DataFactory.class).spawnEvent(new JSONObject(line));
                    currentLine++;
                }
            } catch (Exception e) {
                logger.error("Error reading file");
                logger.error("Error: " + e.getMessage());
                logger.trace(e.getStackTrace());
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
                logger.error("Error reading file");
                logger.error("Error: " + e.getMessage());
                logger.trace(e.getStackTrace());
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
                            logger.debug("File changed: " + file.getName());
                        }
                    }
                    changed();
                    Init init = SingletonFactory.getSingleton(Init.class);
                    if (init.initState) {
                        synchronized (init) {
                            init.notify();
                        }
                    }
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
            logger.debug("New file detected: " + file.getName());
            ReadingData readingData = new ReadingData(file);
            Thread thread = new Thread(readingData);
            thread.start();
            readers.put(file, thread);
            logger.debug("New reader started on " + file.getName());
        }
        synchronized (readers.get(file)) {
            readers.get(file).notify();
            logger.debug("Notified reader on " + file.getName());
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
                logger.info("File reader joined: " + fileData.getName() + " " + fileData.getLastLineRead() + " " + fileData.getLastSize());
                EntityManager entityManager = this.sessionFactory.createEntityManager();
                entityManager.getTransaction().begin();
                entityManager.merge(fileData);
                entityManager.getTransaction().commit();
                entityManager.close();
            } catch (InterruptedException e) {
                logger.debug("Error joining thread: " + e.getMessage());
                logger.trace(e.getStackTrace());
            }
        }
    }

}
