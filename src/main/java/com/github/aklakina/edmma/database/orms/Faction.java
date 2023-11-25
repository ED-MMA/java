package com.github.aklakina.edmma.database.orms;

import jakarta.persistence.*;

import java.util.Set;

/**
 * The Faction class represents a faction in the database.
 * It contains information about the name of the faction, mission sources, and clusters.
 * It also contains methods to get the stack height of the faction.
 */
@Entity
@Table(name = "FACTION", schema = "ED")
public class Faction {
    private String name;
    private Set<MissionSource> missionSources;
    private Set<Cluster> clusters;

    /**
     * Default constructor.
     */
    public Faction() {

    }

    /**
     * Returns the name of the faction.
     *
     * @return the name of the faction
     */
    @Id
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the faction.
     *
     * @param name the new name of the faction
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the clusters of the faction.
     *
     * @return the clusters of the faction
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = Cluster_.TARGET_FACTION)
    public Set<Cluster> getClusters() {
        return clusters;
    }

    /**
     * Sets the clusters of the faction.
     *
     * @param clusters the new clusters of the faction
     */
    public void setClusters(Set<Cluster> clusters) {
        this.clusters = clusters;
    }

    /**
     * Returns the mission sources of the faction.
     *
     * @return the mission sources of the faction
     */
    @OneToMany(mappedBy = MissionSource_.FACTION)
    public Set<MissionSource> getMissionSources() {
        return missionSources;
    }

    /**
     * Sets the mission sources of the faction.
     *
     * @param missionSources the new mission sources of the faction
     */
    public void setMissionSources(Set<MissionSource> missionSources) {
        this.missionSources = missionSources;
    }

    /**
     * Returns the hash code of the faction.
     *
     * @return the hash code of the faction
     */
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Checks if the given object is equal to this faction.
     *
     * @param o the object to compare
     * @return true if the given object is equal to this faction, false otherwise
     */
    public boolean equals(Object o) {
        return o instanceof Faction && name.equals(((Faction) o).name);
    }

    /**
     * Returns the stack height of the faction.
     * The stack height is the sum of the kills left for each not completed mission of each mission source.
     *
     * @return the stack height of the faction
     */
    @Transient
    public int getStackHeight() {
        return missionSources.stream().mapToInt(ms ->
                ms.getNotCompletedMissions().stream().mapToInt(Mission::getKillsLeft).sum()).sum();
    }

}