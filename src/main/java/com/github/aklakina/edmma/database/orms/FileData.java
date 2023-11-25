package com.github.aklakina.edmma.database.orms;

import com.github.aklakina.edmma.base.Globals;
import jakarta.persistence.*;

import java.io.File;
import java.nio.file.Paths;

/**
 * The FileData class represents a file in the database.
 * It contains information about the name of the file, the last line read, and the last size of the file.
 * It also contains methods to check if the file has changed, recalculate the size of the file, load the file, and get the file.
 */
@Entity
@Table(name = "FILE", schema = "MD")
public class FileData {

    /**
     * The file object.
     */
    @Transient
    private File file;

    /**
     * The name of the file.
     */
    private String name;

    /**
     * The last line read from the file.
     */
    private Integer lastLineRead;

    /**
     * The last size of the file.
     */
    private long lastSize;

    /**
     * Default constructor.
     */
    public FileData() {
    }

    /**
     * Constructor with parameters.
     *
     * @param name the name of the file
     * @param lastLineRead the last line read from the file
     * @param lastSize the last size of the file
     */
    public FileData(String name, Integer lastLineRead, long lastSize) {
        this.file = new File(Paths.get(Globals.ELITE_LOG_HOME, name).toString());
        this.lastLineRead = lastLineRead;
        this.name = name;
        this.lastSize = lastSize;
    }

    /**
     * Returns the last line read from the file.
     *
     * @return the last line read from the file
     */
    @Basic
    public Integer getLastLineRead() {
        return lastLineRead;
    }

    /**
     * Sets the last line read from the file.
     *
     * @param lastLineRead the new last line read from the file
     */
    public void setLastLineRead(Integer lastLineRead) {
        this.lastLineRead = lastLineRead;
    }

    /**
     * Checks if the file has changed.
     *
     * @return true if the file has changed, false otherwise
     */
    @Transient
    public boolean getChanged() {
        return file.length() != lastSize;
    }

    /**
     * Sets the changed status of the file.
     * This method is not used.
     *
     * @param changed the new changed status of the file
     */
    private void setChanged(boolean changed) {
    }

    /**
     * Recalculates the size of the file.
     */
    @Transient
    public void reCalcSize() {
        lastSize = file.length();
    }

    /**
     * Returns the name of the file.
     *
     * @return the name of the file
     */
    @Id
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the file.
     *
     * @param name the new name of the file
     */
    public void setName(String name) {
        this.name = name;
        this.file = new File(Paths.get(Globals.ELITE_LOG_HOME, name).toString());
    }

    /**
     * Loads the file.
     */
    @Transient
    public void loadFile() {
        this.file = new File(Paths.get(Globals.ELITE_LOG_HOME, name).toString());
    }

    /**
     * Returns the file object.
     *
     * @return the file object
     */
    @Transient
    public File getFile() {
        return file;
    }

    /**
     * Sets the file object.
     *
     * @param file the new file object
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Returns the last size of the file.
     *
     * @return the last size of the file
     */
    @Basic
    public long getLastSize() {
        return lastSize;
    }

    /**
     * Sets the last size of the file.
     *
     * @param lastSize the new last size of the file
     */
    public void setLastSize(long lastSize) {
        this.lastSize = lastSize;
    }

    /**
     * Returns the hash code of the file.
     *
     * @return the hash code of the file
     */
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Checks if the given object is equal to this file.
     *
     * @param o the object to compare
     * @return true if the given object is equal to this file, false otherwise
     */
    public boolean equals(Object o) {
        return o instanceof FileData && name.equals(((FileData) o).name);
    }

}