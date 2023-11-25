package com.github.aklakina.edmma.database.orms;

import jakarta.persistence.*;
import org.hibernate.annotations.Formula;

/**
 * The Mission class represents a mission in the database.
 * It contains information about the ID, cluster, source, reward, shareability, kills required, progress, expiry, accept time, completion status, and kills left of the mission.
 * It also contains methods to get and set these attributes.
 */
@Entity
@Table(name = "MISSION", schema = "ED")
public class Mission {

    /**
     * The ID of the mission.
     */
    private Long id;

    /**
     * The cluster of the mission.
     */
    private Cluster cluster;

    /**
     * The source of the mission.
     */
    private MissionSource source;

    /**
     * The reward of the mission.
     */
    private double reward;

    /**
     * The shareability of the mission.
     */
    private boolean shareable;

    /**
     * The kills required for the mission.
     */
    private int killsRequired;

    /**
     * The progress of the mission.
     */
    private int progress;

    /**
     * The expiry of the mission.
     */
    private String expiry;

    /**
     * The accept time of the mission.
     */
    private String acceptTime;

    /**
     * The completion status of the mission.
     */
    private boolean completed;

    /**
     * The kills left for the mission.
     */
    private int killsLeft;

    /**
     * Default constructor.
     */
    public Mission() {
    }

    /**
     * Returns the ID of the mission.
     *
     * @return the ID of the mission
     */
    @Id
    public Long getID() {
        return id;
    }

    /**
     * Sets the ID of the mission.
     *
     * @param id the new ID of the mission
     */
    public void setID(Long id) {
        this.id = id;
    }

    /**
     * Returns the cluster of the mission.
     *
     * @return the cluster of the mission
     */
    @ManyToOne(fetch = FetchType.LAZY)
    public Cluster getCluster() {
        return cluster;
    }

    /**
     * Sets the cluster of the mission.
     *
     * @param cluster the new cluster of the mission
     */
    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    /**
     * Returns the source of the mission.
     *
     * @return the source of the mission
     */
    @ManyToOne(fetch = FetchType.LAZY)
    public MissionSource getSource() {
        return source;
    }

    /**
     * Sets the source of the mission.
     *
     * @param source the new source of the mission
     */
    public void setSource(MissionSource source) {
        this.source = source;
    }

    /**
     * Returns the reward of the mission.
     *
     * @return the reward of the mission
     */
    @Basic(optional = false)
    public double getReward() {
        return reward;
    }

    /**
     * Sets the reward of the mission.
     *
     * @param reward the new reward of the mission
     */
    public void setReward(double reward) {
        this.reward = reward;
    }

    /**
     * Checks if the mission is shareable.
     *
     * @return true if the mission is shareable, false otherwise
     */
    @Basic(optional = false)
    public boolean isShareable() {
        return shareable;
    }

    /**
     * Sets the shareability of the mission.
     *
     * @param shareable the new shareability of the mission
     */
    public void setShareable(boolean shareable) {
        this.shareable = shareable;
    }

    /**
     * Returns the kills required for the mission.
     *
     * @return the kills required for the mission
     */
    @Basic(optional = false)
    public int getKillsRequired() {
        return killsRequired;
    }

    /**
     * Sets the kills required for the mission.
     *
     * @param killsRequired the new kills required for the mission
     */
    public void setKillsRequired(int killsRequired) {
        this.killsRequired = killsRequired;
    }

    /**
     * Returns the progress of the mission.
     *
     * @return the progress of the mission
     */
    @Basic
    public int getProgress() {
        return progress;
    }

    /**
     * Sets the progress of the mission.
     *
     * @param progress the new progress of the mission
     */
    public void setProgress(int progress) {
        this.progress = progress;
    }

    /**
     * Checks if the mission is completed.
     *
     * @return true if the mission is completed, false otherwise
     */
    @Formula("progress >= killsRequired")
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Sets the completion status of the mission.
     *
     * @param completed the new completion status of the mission
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * Returns the kills left for the mission.
     *
     * @return the kills left for the mission
     */
    @Formula("killsRequired - progress")
    public int getKillsLeft() {
        return killsLeft;
    }

    /**
     * Sets the kills left for the mission.
     *
     * @param killsLeft the new kills left for the mission
     */
    public void setKillsLeft(int killsLeft) {
        this.killsLeft = killsLeft;
    }

    /**
     * Returns the expiry of the mission.
     *
     * @return the expiry of the mission
     */
    @Basic(optional = false)
    public String getExpiry() {
        return expiry;
    }

    /**
     * Sets the expiry of the mission.
     *
     * @param expiry the new expiry of the mission
     */
    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    /**
     * Returns the accept time of the mission.
     *
     * @return the accept time of the mission
     */
    @Basic(optional = false)
    public String getAcceptTime() {
        return acceptTime;
    }

    /**
     * Sets the accept time of the mission.
     *
     * @param acceptTime the new accept time of the mission
     */
    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }

    /**
     * Returns the hash code of the mission.
     *
     * @return the hash code of the mission
     */
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Checks if the given object is equal to this mission.
     *
     * @param o the object to compare
     * @return true if the given object is equal to this mission, false otherwise
     */
    public boolean equals(Object o) {
        return o instanceof Mission && id.equals(((Mission) o).id);
    }

}