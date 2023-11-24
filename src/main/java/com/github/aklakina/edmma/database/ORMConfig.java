package com.github.aklakina.edmma.database;

import com.github.aklakina.edmma.base.ClassLoader;
import com.github.aklakina.edmma.base.Globals;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;

import java.io.IOException;

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
            return name.endsWith(".class") && !name.endsWith("_.class");
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
                .setProperty("hibernate.connection.url", Globals.DATABASE_URL)
                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                .setProperty(AvailableSettings.CONNECTION_PROVIDER, "org.hibernate.hikaricp.internal.HikariCPConnectionProvider")
                .setProperty("hibernate.hbm2ddl.auto", "update")
                .setProperty(AvailableSettings.JAKARTA_HBM2DDL_CREATE_SCHEMAS, "true")
                .setProperty(AvailableSettings.FORMAT_SQL, "true")
                .setProperty(AvailableSettings.HIGHLIGHT_SQL, "true")
                .buildSessionFactory();

    }

    public static void close() {
        sessionFactory.close();
    }

}
