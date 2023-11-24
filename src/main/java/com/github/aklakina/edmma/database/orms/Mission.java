package com.github.aklakina.edmma.database.orms;

import jakarta.persistence.*;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "MISSION", schema = "ED")
public class Mission {

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
    public Mission() {
    }

    @Id
    public Long getID() {
        return id;
    }

    public void setID(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public MissionSource getSource() {
        return source;
    }

    public void setSource(MissionSource source) {
        this.source = source;
    }

    @Basic(optional = false)
    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    @Basic(optional = false)
    public boolean isShareable() {
        return shareable;
    }

    public void setShareable(boolean shareable) {
        this.shareable = shareable;
    }

    @Basic(optional = false)
    public int getKillsRequired() {
        return killsRequired;
    }

    public void setKillsRequired(int killsRequired) {
        this.killsRequired = killsRequired;
    }

    @Basic
    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Formula("progress >= killsRequired")
    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Formula("killsRequired - progress")
    public int getKillsLeft() {
        return killsLeft;
    }

    public void setKillsLeft(int killsLeft) {
        this.killsLeft = killsLeft;
    }

    @Basic(optional = false)
    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    @Basic(optional = false)
    public String getAcceptTime() {
        return acceptTime;
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
