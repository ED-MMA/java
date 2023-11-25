package com.github.aklakina.edmma.events;

import com.github.aklakina.edmma.base.ClassLoader;
import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.database.ORMConfig;
import com.github.aklakina.edmma.logicalUnit.DataFactory;
import org.hibernate.SessionFactory;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;

/**
 * This abstract class represents an Event that implements Runnable.
 * It provides a static ClassLoader to load and parse classes, and a SessionFactory for database operations.
 */
public abstract class Event implements Runnable {

    /**
     * This is a custom ClassLoader used to load and parse classes.
     * It checks if a class is assignable from Event, and if so, tries to register it.
     * If the class is already registered or cannot be registered, it logs a message.
     * If the class does not have a constructor that takes a JSONObject, it logs an error and does not register the class.
     * If the class is not an Event, it logs an error and does not register the class.
     */
    public static ClassLoader eventLoader = new ClassLoader() {
        @Override
        public void parse(Class<?> clazz) {

            if (Event.class.isAssignableFrom(clazz)) {
                try {
                    @SuppressWarnings("unchecked")
                    Class<? extends Event> parsed = (Class<? extends Event>) clazz;
                    Constructor<? extends Event> constr = parsed.getDeclaredConstructor(JSONObject.class);
                    if (SingletonFactory.getSingleton(DataFactory.class).registerEventFactory(clazz.getSimpleName(), constr)) {
                        registered.add(parsed);
                        logger.debug("Class " + clazz.getSimpleName() + " is successfully parsed.");
                    } else {
                        if (registered.contains(clazz))
                            logger.debug("Class " + clazz.getSimpleName() + " is already registered!");
                        else {
                            logger.warn("Class " + clazz.getSimpleName() + " could not be registered!");
                            notRegistered.add(clazz);
                        }

                    }
                } catch (NoSuchMethodException e) {
                    notRegistered.add(clazz);
                    logger.error("Class " + clazz.getSimpleName() + " does not have a constructor that takes a JSONObject");
                    //e.printStackTrace();
                }

            } else {
                logger.error("Class " + clazz.getSimpleName() + " is not an Event!");
                notRegistered.add(clazz);
            }
        }

        /**
         * This method is used to filter files based on their names.
         * It returns true if the file name ends with ".class" and does not end with "_.class".
         */
        @Override
        public boolean fileFilter(File dir, String name) {
            return name.endsWith(".class") && !name.endsWith("_.class");
        }

    };

    /**
     * This static block loads classes from the package "com.github.aklakina.edmma.events.dynamic" when the class is loaded.
     */
    static {
        try {
            eventLoader.loadClassesFromPackage("com.github.aklakina.edmma.events.dynamic");
        } catch (IOException | ClassNotFoundException e) {
            //logger.error("Error loading events\n Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This is a SessionFactory instance used for database operations.
     */
    protected final SessionFactory sessionFactory = ORMConfig.sessionFactory;


}