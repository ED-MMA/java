package com.github.aklakina.edmma.database.orms;

import jakarta.persistence.*;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "MISSION", schema = "ED")
public class Mission {

    public Mission() {
    }

    private Long id;
    private Cluster cluster;
    private MissionSource source;

    private double reward;
    private boolean shareable;
    private int killsRequired;
    private int progress;

    private String expiry;
    private String acceptTime;
    private boolean completed;
    private int killsLeft;


    @Id
    public Long getID() {
        return id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Cluster getCluster() {
        return cluster;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public MissionSource getSource() {
        return source;
    }

    @Basic(optional = false)
    public double getReward() {
        return reward;
    }

    @Basic(optional = false)
    public boolean isShareable() {
        return shareable;
    }

    @Basic(optional = false)
    public int getKillsRequired() {
        return killsRequired;
    }

    @Basic
    public int getProgress() {
        return progress;
    }

    @Formula("progress >= killsRequired")
    public boolean isCompleted() {
        return completed;
    }

    @Formula("killsRequired - progress")
    public int getKillsLeft() {
        return killsLeft;
    }

    @Basic(optional = false)
    public String getExpiry() {
        return expiry;
    }

    @Basic(optional = false)
    public String getAcceptTime() {
        return acceptTime;
    }

    public void setID(Long id) {
        this.id = id;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public void setSource(MissionSource source) {
        this.source = source;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public void setShareable(boolean shareable) {
        this.shareable = shareable;
    }

    public void setKillsRequired(int killsRequired) {
        this.killsRequired = killsRequired;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setKillsLeft(int killsLeft) {
        this.killsLeft = killsLeft;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }

    public int hashCode() {
        return id.hashCode();
    }

    public boolean equals(Object o) {
        return o instanceof Mission && id.equals(((Mission) o).id);
    }

}
