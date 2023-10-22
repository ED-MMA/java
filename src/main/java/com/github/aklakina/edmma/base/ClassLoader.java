package com.github.aklakina.edmma.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public abstract class ClassLoader {

    protected final static Logger logger = LogManager.getLogger(ClassLoader.class);

    private static File[] getClassFiles(String packageName, FilenameFilter filter) {
        logger.info("Loading classes from package " + packageName);
        java.lang.ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        URL packageURL = classLoader.getResource(path);

        if (packageURL == null) {
            logger.debug("Package " + packageName + " not found");
            return null;
        }

        File packageDir = new File(packageURL.getFile());

        return packageDir.listFiles(filter);

    }
    public abstract void parse(Class<?> clazz);

    public boolean fileFilter(File dir, String name) {
        if (name.endsWith(".class") && !name.endsWith("_.class")) {
            return true;
        }
        return false;
    }

    public void loadClassesFromPackage(String packageName) throws IOException, ClassNotFoundException {
        File[] files = getClassFiles(packageName, ClassLoader.this::fileFilter);
        if (files == null) {
            return;
        }

        for (File file : files) {
            String className = packageName + '.' + file.getName().replace(".class", "");
            parse(Class.forName(className));
        }

    }

    public Set<Class<?>> registered = new HashSet<>();

    public Set<Class<?>> notRegistered = new HashSet<>();

}
