package com.github.aklakina.edmma.events;

import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.logicalUnit.DataFactory;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Event implements Runnable {

    public static List<Class<? extends Event>> loadClassesFromPackage() throws IOException, ClassNotFoundException {
        String packageName = "com.github.aklakina.edmma.events";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        URL packageURL = classLoader.getResource(path);

        if (packageURL == null) {
            return new ArrayList<>();
        }

        File packageDir = new File(packageURL.getFile());
        File[] classFiles = packageDir.listFiles((dir, name) -> name.endsWith(".class") && !name.equals("Event.class"));

        List<Class<? extends Event>> classes = new ArrayList<>();

        for (File classFile : classFiles) {
            String className = packageName + '.' + classFile.getName().replace(".class", "");
            Class<? extends Event> loadedClass = Class.forName(className).asSubclass(Event.class);
            classes.add(loadedClass);
        }

        return classes;
    }

    public static HashMap<Class<? extends Event>, Boolean> registered = new HashMap<>();
    static {
        List<Class<? extends Event>> classes;
        try {
            classes = loadClassesFromPackage();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            classes = new ArrayList<>();
        }
        for (Class<? extends Event> clazz : classes) {
            try {
                Constructor<? extends Event> constr = clazz.getDeclaredConstructor(JSONObject.class);
                registered.put(clazz,SingletonFactory.getSingleton(DataFactory.class).registerEventFactory(clazz.getSimpleName(), constr));
            } catch (NoSuchMethodException e) {
                System.out.println("Class " + clazz.getSimpleName() + " does not have a constructor that takes a JSONObject");
                e.printStackTrace();
            }
        }
    }

}
