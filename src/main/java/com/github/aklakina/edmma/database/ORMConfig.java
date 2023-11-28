package com.github.aklakina.edmma.database;

import com.github.aklakina.edmma.base.ClassLoader;
import com.github.aklakina.edmma.base.Globals;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;

import java.io.IOException;

/**
 * The ORMConfig class is used to configure the Object-Relational Mapping (ORM) for the application.
 * It contains a static SessionFactory object, a ClassLoader object, and a static initializer block.
 * The ClassLoader object is used to load classes from the "com.github.aklakina.edmma.database.orms" package.
 * The static initializer block is used to configure the SessionFactory object.
 * It also contains a static method to close the SessionFactory.
 */
public class ORMConfig {
    /**
     * The SessionFactory object used for ORM.
     */
    public static SessionFactory sessionFactory;

    /**
     * The ClassLoader object used to load classes from the "com.github.aklakina.edmma.database.orms" package.
     */
    public static ClassLoader ORMLoader = new ClassLoader() {
        /**
         * Parses a class and adds it to the registered or notRegistered list based on whether it is annotated with @Entity.
         *
         * @param clazz the class to parse
         */
        @Override
        public void parse(Class<?> clazz) {
            if (clazz.isAnnotationPresent(jakarta.persistence.Entity.class)) {
                registered.add(clazz);
            } else {
                notRegistered.add(clazz);
            }
        }

        /**
         * Filters files based on their name.
         *
         * @param dir the directory of the file
         * @param name the name of the file
         * @return true if the file ends with ".class" and does not end with "_.class", false otherwise
         */
        @Override
        public boolean fileFilter(java.io.File dir, String name) {
            return name.endsWith(".class") && !name.endsWith("_.class");
        }
    };

    /**
     * Static initializer block.
     * Configures the SessionFactory object.
     */
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
                .setProperty("hibernate.hbm2ddl.auto", Globals.DATABASE_REDEPLOY_METHOD)
                .setProperty(AvailableSettings.JAKARTA_HBM2DDL_CREATE_SCHEMAS, "true")
                .setProperty(AvailableSettings.FORMAT_SQL, "true")
                .setProperty(AvailableSettings.HIGHLIGHT_SQL, "true")
                .buildSessionFactory();

    }

    /**
     * Closes the SessionFactory.
     */
    public static void close() {
        sessionFactory.close();
    }

}