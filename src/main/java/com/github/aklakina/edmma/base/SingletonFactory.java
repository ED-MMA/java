package com.github.aklakina.edmma.base;

import java.util.HashMap;
import java.util.Map;

public class SingletonFactory {
    private static final Map<Class<?>, Object> singletons = new HashMap<>();

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

}
