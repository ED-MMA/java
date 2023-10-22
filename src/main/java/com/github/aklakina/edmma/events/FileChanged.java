package com.github.aklakina.edmma.events;

import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.FileData;
import com.github.aklakina.edmma.machineInterface.FileReader;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.json.JSONObject;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileChanged extends Event {

    private final Path path;

    public FileChanged(JSONObject event) {
        this.path = Paths.get(event.getString("path"));
    }

    @Override
    public void run() {
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        FileData fileData;
        try {
            fileData = Queries_.getFileDataByName(entityManager, path.getFileName().toString());
        } catch (NoResultException e) {
            System.out.println("New file detected: " + path.getFileName().toString());
            fileData = new FileData(path.getFileName().toString(), 0, 0);
            entityManager.getTransaction().begin();
            entityManager.persist(fileData);
            entityManager.getTransaction().commit();
        }
        entityManager.close();
        SingletonFactory.getSingleton(FileReader.class).processEvent(fileData);
    }
}
