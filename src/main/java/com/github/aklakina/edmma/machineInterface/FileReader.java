package com.github.aklakina.edmma.machineInterface;

import com.github.aklakina.edmma.base.Globals;
import com.github.aklakina.edmma.base.Singleton;
import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.database.ORMConfig;
import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.FileData;
import com.github.aklakina.edmma.logicalUnit.DataFactory;
import com.github.aklakina.edmma.logicalUnit.Init;
import com.github.aklakina.edmma.logicalUnit.threading.CloserMethods;
import com.github.aklakina.edmma.logicalUnit.threading.RegisteredThread;
import com.github.aklakina.edmma.logicalUnit.threading.ResourceReleasingRunnable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.RollbackException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * Singleton class responsible for reading files.
 * It manages file readers and processes file events.
 */
@Singleton
public class FileReader {
    // Logger instance for this class
    private static final Logger logger = LogManager.getLogger(FileReader.class);
    // Map to store file readers
    private final HashMap<FileData, RegisteredThread> readers;
    // Session factory for database operations
    private final SessionFactory sessionFactory;
    // Flag to indicate if the file reader should close
    private boolean shouldClose = false;

    /**
     * Default constructor.
     * It initializes the readers map and the session factory.
     */
    public FileReader() {
        this.sessionFactory = ORMConfig.sessionFactory;
        readers = new HashMap<>();
    }

    /**
     * Processes a file event.
     * If the file is new, it starts a new reader thread for the file.
     * If the file is already being read, it notifies the reader thread.
     * @param file The file to process.
     */
    public synchronized void processEvent(FileData file) {
        if (!readers.containsKey(file)) {
            logger.debug("New file detected: " + file.getName());
            ReadingData readingData = new ReadingData(file);
            RegisteredThread thread = new RegisteredThread(readingData, CloserMethods.NOTIFY);
            thread.setNamed("FileReader-" + file.getName()).start();
            readers.put(file, thread);
            logger.debug("New reader started on " + file.getName());
        }
        synchronized (readers.get(file)) {
            readers.get(file).notify();
            logger.debug("Notified reader on " + file.getName());
        }
    }

    /**
     * Waits for the reader thread on a file to finish.
     * @param f The file to wait for.
     */
    public void waitForThreadOnFile(File f) {
        EntityManager entityManager = sessionFactory.createEntityManager();
        FileData file = Queries_.getFileDataByName(entityManager, f.getName());
        entityManager.close();
        if (file == null) {
            logger.debug("File not found: " + f.getName());
            return;
        }
        RegisteredThread thread = readers.get(file);
        if (thread == null) {
            logger.debug("File reader not found: " + f.getName());
            return;
        }
        synchronized (thread) {
            try {
                thread.wait();
            } catch (InterruptedException e) {
                logger.error("Error waiting for thread: " + thread.getName());
                logger.error("Error: " + e.getMessage());
                logger.trace(e.getStackTrace());
            }
        }
    }

    /**
     * Waits for every thread to finish.
     */
    public void waitForAllThreads() {
        for (RegisteredThread thread : readers.values()) {
            if (thread.isWaiting()) {
                return;
            }
            synchronized (thread) {
                try {
                    thread.wait();
                } catch (InterruptedException e) {
                    logger.error("Error waiting for thread: " + thread.getName());
                    logger.error("Error: " + e.getMessage());
                    logger.trace(e.getStackTrace());
                }
            }
        }
    }

    /**
     * Closes the file reader.
     * It sets the shouldClose flag to true and closes all reader threads.
     */
    public void close() {
        SingletonFactory.getSingleton(FileReader.class).shouldClose = true;
        for (FileData file : readers.keySet()) {
            readers.get(file).exit();
            readers.remove(file);
        }
    }

    /**
     * Removes a file from the readers map and closes its reader thread.
     * @param file The file to remove.
     */
    public void removeFile(FileData file) {
        RegisteredThread thread = readers.get(file);
        if (thread == null) {
            logger.debug("File reader not found: " + file.getName());
            return;
        }
        thread.exit();
            /* Maybe we do not need to wait for it to close since if we remove it from the map it will be garbage collected
            if (Thread.currentThread() != thread) {
                logger.debug("Joining thread: " + file.getName());
                thread.join();
            }
            */
        readers.remove(file);
    }

    private static class ReadingData extends ResourceReleasingRunnable {
        private static final Logger logger = LogManager.getLogger(ReadingData.class);
        private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        private final FileData file;
        public BufferedReader reader;
        public Integer currentLine;
        private boolean firstRun = true;
        private boolean fileAccessed = true;
        private final ScheduledFuture<?> scheduledFuture;

        /**
         * Default constructor for creating a reading data runnable.
         * This constructor will initialize the file, currentLine, and reader variables.
         * It will also schedule the checkAndRelease method.
         *
         * @see ReadingData#checkAndRelease()
         *
         * @param file The file to be read.
         */
        public ReadingData(FileData file) {
            this.file = file;
            this.currentLine = 0;
            try {
                FileInputStream fileInputStream = new FileInputStream(file.getFile());
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
            scheduledFuture = scheduler.scheduleAtFixedRate(this::checkAndRelease, 0, Globals.FILE_READER_CHECK_INTERVAL, Globals.FILE_READER_CHECK_INTERVAL_UNIT);
        }

        /**
         * Method to read the file.
         * This method will read the file line by line and spawn events.
         * This method will also update the currentLine variable.
         *
         * @see SingletonFactory#getSingleton(Class)
         * @see DataFactory#spawnEvent(JSONObject)
         */
        public synchronized void changed() {
            fileAccessed = true;
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

        /**
         * Method to check if the file has been accessed.
         * If the file has not been accessed, this method will call the exit method and remove the file from the singleton factory.
         * If the file has been accessed, this method will set the fileAccessed flag to false.
         */
        private void checkAndRelease() {
            if (!fileAccessed) {
                logger.debug("File not accessed for " + Globals.FILE_READER_CHECK_INTERVAL + " " + Globals.FILE_READER_CHECK_INTERVAL_UNIT.name() + ". Releasing resources");
                thread.exit();
                SingletonFactory.getSingleton(FileReader.class).removeFile(file);
                scheduledFuture.cancel(false);
            } else {
                fileAccessed = false;
            }
        }

        /**
         * Method to release resources.
         * This method will close the reader and update the database.
         * It will also cancel the scheduled future.
         */
        public void releaseResources() {
            try {
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            EntityManager entityManager = ORMConfig.sessionFactory.createEntityManager();

            FileData dbFile = Queries_.getFileDataByName(entityManager, file.getName());

            dbFile.setLastLineRead(currentLine);
            dbFile.setLastSize(file.getFile().length());
            entityManager.getTransaction().begin();
            try {
                entityManager.merge(dbFile);
                entityManager.getTransaction().commit();
            } catch (RollbackException e) {
                logger.error("Error saving file data");
                logger.error("Error: " + e.getMessage());
                logger.trace(e.getStackTrace());
                entityManager.getTransaction().rollback();
            } catch (NoResultException e) {
                logger.error("File not found in database");
                logger.error("Error: " + e.getMessage());
                logger.trace(e.getStackTrace());
                entityManager.getTransaction().rollback();
            }

            entityManager.close();

            scheduledFuture.cancel(false);

            synchronized (RegisteredThread.currentThread()) {
                RegisteredThread.currentThread().notifyAll();
            }

        }

        /**
         * Method to perform an action on thread notification.
         * It will call the changed method and notify the init class if it is initialized.
         *
         * @see ReadingData#changed()
         *
         * @see Init#processChanges()
         */
        @Override
        public void actionOnThreadNotify() {
            changed();
            Init init = SingletonFactory.getSingleton(Init.class);
            if (init.initState) {
                synchronized (init) {
                    init.notify();
                }
            }
        }

        /**
         * Method to wait for thread notification.
         * This method will notify all threads and then wait.
         * This method will also log the file name.
         * This method will not wait on the first run.
         *
         * @see RegisteredThread#currentThread()
         *
         * @see RegisteredThread#notifyAll()
         * @see RegisteredThread#wait()
         *
         * @see ReadingData#firstRun
         *
         * @throws InterruptedException
         */
        @Override
        protected void waitForThreadNotify() throws InterruptedException {
            if (firstRun) {
                firstRun = false;
            } else {
                synchronized (RegisteredThread.currentThread()) {
                    RegisteredThread.currentThread().notifyAll();
                    RegisteredThread.currentThread().wait();
                    logger.debug("File changed: " + file.getName());
                }
            }
        }
    }

}
