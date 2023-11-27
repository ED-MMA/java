package com.github.aklakina.edmma.base;

import com.github.aklakina.edmma.humanInterface.main_window;

import java.util.HashMap;
import java.util.Map;

/**
 * The SingletonFactory class is used to manage singleton instances.
 * It maintains a map of singleton instances, keyed by their class.
 * The getSingleton method is used to get the singleton instance of a class.
 * If the instance does not exist, it is created and added to the map.
 * If the class is not marked with the @Singleton annotation, an exception is thrown.
 */
public class SingletonFactory {
    /**
     * A map to hold singleton instances, keyed by their class.
     */
    private static final Map<Class<?>, Object> singletons = new HashMap<>();

    /**
     * Returns the singleton instance of the specified class.
     * If the instance does not exist, it is created and added to the map.
     * If the class is not marked with the @Singleton annotation, an exception is thrown.
     *
     * @param clazz the class of the singleton instance
     * @param <T> the type of the singleton instance
     * @return the singleton instance of the specified class
     * @throws IllegalArgumentException if the class is not marked with the @Singleton annotation
     * @throws RuntimeException if the singleton instance cannot be created
     */
    public static <T> T getSingleton(Class<T> clazz) {
        if (!clazz.isAnnotationPresent(Singleton.class)) {
            throw new IllegalArgumentException("Class must be marked with @Singleton annotation");
        }

        if (!singletons.containsKey(clazz)) {
            try {
                T instance = clazz.getDeclaredConstructor().newInstance();
                singletons.put(clazz, instance);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create a singleton instance for " + clazz, e);
            }
        }

        return clazz.cast(singletons.get(clazz));
    }

    public static <T> void setSingleton(Class<T> clazz, T instance) {
        if (!clazz.isAnnotationPresent(Singleton.class)) {
            throw new IllegalArgumentException("Class must be marked with @Singleton annotation");
        }
        /*if (singletons.containsKey(clazz)) {
            throw new IllegalArgumentException("Singleton instance already exists for " + clazz);
        }*/
        singletons.put(clazz, instance);
    }
}