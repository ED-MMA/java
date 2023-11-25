package com.github.aklakina.edmma.database.orms;

import jakarta.persistence.*;

import java.util.Set;

/**
 * The Cluster class represents a cluster in the database.
 * It contains information about the target faction, mission sources, target system, and missions.
 * It also contains methods to get not completed missions, completed missions, stack height, and stack width.
 */
@Entity
@Table(name = "CLUSTER", schema = "ED")
public class Cluster {
    private Faction targetFaction;
    private Set<MissionSource> missionSources;
    private System targetSystem;
    private Long id;
    private Set<Mission> missions;

    /**
     * Default constructor.
     */
    public Cluster() {
    }

    /**
     * Returns the ID of the cluster.
     *
     * @return the ID of the cluster
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getID() {
        return id;
    }

    /**
     * Sets the ID of the cluster.
     *
     * @param id the new ID of the cluster
     */
    public void setID(Long id) {
        this.id = id;
    }

    /**
     * Returns the target faction of the cluster.
     *
     * @return the target faction of the cluster
     */
    @ManyToOne(fetch = FetchType.LAZY)
    public Faction getTargetFaction() {
        return targetFaction;
    }

    /**
     * Sets the target faction of the cluster.
     *
     * @param targetFaction the new target faction of the cluster
     */
    public void setTargetFaction(Faction targetFaction) {
        this.targetFaction = targetFaction;
    }

    /**
     * Returns the missions of the cluster.
     *
     * @return the missions of the cluster
     */
    @OneToMany(mappedBy = Mission_.CLUSTER)
    public Set<Mission> getMissions() {
        return missions;
    }

    /**
     * Sets the missions of the cluster.
     *
     * @param missions the new missions of the cluster
     */
    public void setMissions(Set<Mission> missions) {
        this.missions = missions;
    }

    /**
     * Returns the not completed missions of the cluster.
     *
     * @return the not completed missions of the cluster
     */
    @Transient
    public Set<Mission> getNotCompletedMissions() {
        return missions.stream().filter(m -> !m.isCompleted()).collect(java.util.stream.Collectors.toSet());
    }

    /**
     * Returns the completed missions of the cluster.
     *
     * @return the completed missions of the cluster
     */
    @Transient
    public Set<Mission> getCompletedMissions() {
        return missions.stream().filter(Mission::isCompleted).collect(java.util.stream.Collectors.toSet());
    }

    /**
     * Returns the stack height of the cluster.
     *
     * @return the stack height of the cluster
     */
    @Transient
    public int getStackHeight() {
        return missionSources.stream().mapToInt(ms ->
                ms.getFaction().getStackHeight()).max().orElse(0);
    }

    /**
     * Returns the stack width of the cluster.
     *
     * @return the stack width of the cluster
     */
    @Transient
    public int getStackWidth() {
        return missionSources.stream().map(MissionSource::getFaction).collect(java.util.stream.Collectors.toSet()).size();
    }

    /**
     * Returns the hash code of the cluster.
     *
     * @return the hash code of the cluster
     */
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Checks if the given object is equal to this cluster.
     *
     * @param o the object to compare
     * @return true if the given object is equal to this cluster, false otherwise
     */
    public boolean equals(Object o) {
        return o instanceof Cluster && id.equals(((Cluster) o).id);
    }

    /**
     * Returns the mission sources of the cluster.
     *
     * @return the mission sources of the cluster
     */
    @OneToMany(mappedBy = MissionSource_.CLUSTER)
    public Set<MissionSource> getMissionSources() {
        return missionSources;
    }

    /**
     * Sets the mission sources of the cluster.
     *
     * @param missionSources the new mission sources of the cluster
     */
    public void setMissionSources(Set<MissionSource> missionSources) {
        this.missionSources = missionSources;
    }

    /**
     * Returns the target system of the cluster.
     *
     * @return the target system of the cluster
     */
    @ManyToOne(fetch = FetchType.LAZY)
    public System getTargetSystem() {
        return targetSystem;
    }

    /**
     * Sets the target system of the cluster.
     *
     * @param targetSystem the new target system of the cluster
     */
    public void setTargetSystem(System targetSystem) {
        this.targetSystem = targetSystem;
    }

}