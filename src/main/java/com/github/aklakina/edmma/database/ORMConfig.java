package com.github.aklakina.edmma.database;

import com.github.aklakina.edmma.base.ClassLoader;
import com.github.aklakina.edmma.base.Singleton;
import com.github.aklakina.edmma.base.SingletonFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ORMConfig {
    public static SessionFactory sessionFactory;

    public static ClassLoader ORMLoader = new ClassLoader() {
        @Override
        public void parse(Class<?> clazz) {
            if (clazz.isAnnotationPresent(jakarta.persistence.Entity.class)) {
                registered.add(clazz);
            } else {
                notRegistered.add(clazz);
            }
        }
        @Override
        public boolean fileFilter(java.io.File dir, String name) {
            if (name.endsWith(".class") && !name.endsWith("_.class")) {
                return true;
            }
            return false;
        }
    };

    static {
        Configuration builder = new Configuration();

        try {
            ORMLoader.loadClassesFromPackage("com.github.aklakina.edmma.database.orms");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        for (Class<?> clazz : ORMLoader.registered) {
            builder.addAnnotatedClass(clazz);
        }
        sessionFactory = builder
                .setProperty("hibernate.connection.url", "jdbc:h2:./EDMMA")
                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                .setProperty(AvailableSettings.CONNECTION_PROVIDER, "org.hibernate.hikaricp.internal.HikariCPConnectionProvider")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .setProperty(AvailableSettings.JAKARTA_HBM2DDL_CREATE_SCHEMAS, "true")
                .setProperty(AvailableSettings.SHOW_SQL, "true")
                .setProperty(AvailableSettings.FORMAT_SQL, "true")
                .setProperty(AvailableSettings.HIGHLIGHT_SQL, "true")
                .buildSessionFactory();
    }

}
