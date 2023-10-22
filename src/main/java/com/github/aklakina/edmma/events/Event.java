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

public abstract class Event implements Runnable {

    protected final SessionFactory sessionFactory = ORMConfig.sessionFactory;

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
                        System.out.println("Class " + clazz.getSimpleName() + " is successfully parsed.");
                    } else {
                        if (registered.contains(clazz))
                            System.out.println("Class " + clazz.getSimpleName() + " is already registered!");
                        else {
                            System.out.println("Class " + clazz.getSimpleName() + " could not be registered!");
                            notRegistered.add(clazz);
                        }

                    }
                } catch (NoSuchMethodException e) {
                    notRegistered.add(clazz);
                    System.out.println("Class " + clazz.getSimpleName() + " does not have a constructor that takes a JSONObject");
                    e.printStackTrace();
                }

            } else {
                System.out.println("Class " + clazz.getSimpleName() + " is not an Event!");
                notRegistered.add(clazz);
            }
        }

        @Override
        public boolean fileFilter(File dir, String name) {
            if (name.endsWith(".class") && !name.endsWith("_.class") && !name.equals("Event.class")) {
                return true;
            }
            return false;
        }

    };

    static {
        try {
            eventLoader.loadClassesFromPackage("com.github.aklakina.edmma.events");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
