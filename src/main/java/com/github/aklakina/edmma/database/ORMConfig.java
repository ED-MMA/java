package com.github.aklakina.edmma.database;

import com.github.aklakina.edmma.base.ClassLoader;
import com.github.aklakina.edmma.base.Singleton;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ORMConfig {

    public static final ClassLoader classLoader = new ClassLoader();

    public static SessionFactory sessionFactory;

    static {
        Configuration builder = new Configuration();
        List<Class<?>> classes;
        try {
            classes = classLoader.loadClassesFromPackage("com.github.aklakina.edmma.database.orms",(dir, name) -> {
                if (name.endsWith(".class") && !name.endsWith("_.class")) {
                    return true;
                }
                return false;
            });
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            classes = new ArrayList<>();
        }
        //ORMConfig.class.getClassLoader().loadClass("com.github.aklakina.edmma.database.orms.");
        for (Class<?> clazz : classes) {
            builder.addAnnotatedClass(clazz);
            classLoader.registered.put(clazz, true);
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
