package com.github.aklakina.edmma.base;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClassLoader {

    public List<Class<?>> loadClassesFromPackage(String packageName,  java.io.FilenameFilter filter) throws IOException, ClassNotFoundException {
        System.out.println("Loading classes from package " + packageName);
        java.lang.ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        URL packageURL = classLoader.getResource(path);

        if (packageURL == null) {
            System.out.println("Package " + packageName + " not found");
            return new ArrayList<>();
        }

        File packageDir = new File(packageURL.getFile());
        File[] classFiles = packageDir.listFiles(filter);

        List<Class<?>> classes = new ArrayList<>();

        if (classFiles == null) {
            System.out.println("No classes found in package " + packageName);
            return classes;
        }

        for (File classFile : classFiles) {
            String className = packageName + '.' + classFile.getName().replace(".class", "");

            System.out.println("Loading class " + className);

            classes.add(Class.forName(className));

        }

        return classes;
    }

    public HashMap<Class<?>, Boolean> registered = new HashMap<>();

}
