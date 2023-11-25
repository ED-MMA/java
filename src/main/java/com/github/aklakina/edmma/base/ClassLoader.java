package com.github.aklakina.edmma.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class for loading and parsing classes.
 */
public abstract class ClassLoader {

    // Logger for logging information and debug messages
    protected final static Logger logger = LogManager.getLogger(ClassLoader.class);

    // Set of registered classes
    public Set<Class<?>> registered = new HashSet<>();

    // Set of classes that failed to register
    public Set<Class<?>> notRegistered = new HashSet<>();

    /**
     * Retrieves class files from a given package.
     *
     * @param packageName the name of the package to load classes from
     * @param filter a FilenameFilter to filter the files in the package
     * @return an array of File objects representing the class files in the package
     */
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

    /**
     * Abstract method for parsing a class.
     *
     * @param clazz the class to parse
     */
    public abstract void parse(Class<?> clazz);

    /**
     * Filters files based on their name.
     *
     * @param dir the directory in which the file is located
     * @param name the name of the file
     * @return true if the file ends with ".class" and does not end with "_.class", false otherwise
     */
    public boolean fileFilter(File dir, String name) {
        return name.endsWith(".class") && !name.endsWith("_.class");
    }

    /**
     * Loads and parses classes from a given package.
     *
     * @param packageName the name of the package to load classes from
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the class cannot be located
     */
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

}