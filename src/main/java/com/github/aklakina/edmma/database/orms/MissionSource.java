package com.github.aklakina.edmma.database.orms;

import jakarta.persistence.*;

import java.util.Set;

/**
 * The MissionSource class represents a mission source in the database.
 * It contains information about the ID, faction, station, missions, and cluster of the mission source.
 * It also contains methods to get and set these attributes, and to get not completed missions.
 */
@Entity
@Table(name = "MISSIONSOURCE", schema = "ED")
public class MissionSource {
    /**
     * The ID of the mission source.
     */
    private Long id;

    /**
     * The faction of the mission source.
     */
    private Faction faction;

    /**
     * The station of the mission source.
     */
    private Station station;

    /**
     * The missions of the mission source.
     */
    private Set<Mission> missions;

    /**
     * The cluster of the mission source.
     */
    private Cluster cluster;

    /**
     * Default constructor.
     */
    public MissionSource() {
    }

    /**
     * Returns the ID of the mission source.
     *
     * @return the ID of the mission source
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getID() {
        return id;
    }

    /**
     * Sets the ID of the mission source.
     *
     * @param id the new ID of the mission source
     */
    public void setID(Long id) {
        this.id = id;
    }

    /**
     * Returns the faction of the mission source.
     *
     * @return the faction of the mission source
     */
    @ManyToOne(fetch = FetchType.LAZY)
    public Faction getFaction() {
        return faction;
    }

    /**
     * Sets the faction of the mission source.
     *
     * @param sourceFaction the new faction of the mission source
     */
    public void setFaction(Faction sourceFaction) {
        this.faction = sourceFaction;
    }

    /**
     * Returns the station of the mission source.
     *
     * @return the station of the mission source
     */
    @ManyToOne(fetch = FetchType.LAZY)
    public Station getStation() {
        return station;
    }

    /**
     * Sets the station of the mission source.
     *
     * @param station the new station of the mission source
     */
    public void setStation(Station station) {
        this.station = station;
    }

    /**
     * Returns the missions of the mission source.
     *
     * @return the missions of the mission source
     */
    @OneToMany(mappedBy = Mission_.SOURCE)
    public Set<Mission> getMissions() {
        return missions;
    }

    /**
     * Sets the missions of the mission source.
     *
     * @param missions the new missions of the mission source
     */
    public void setMissions(Set<Mission> missions) {
        this.missions = missions;
    }

    /**
     * Returns the not completed missions of the mission source.
     *
     * @return the not completed missions of the mission source
     */
    @Transient
    public Set<Mission> getNotCompletedMissions() {
        return missions.stream().filter(m -> !m.isCompleted()).collect(java.util.stream.Collectors.toSet());
    }

    /**
     * Returns the hash code of the mission source.
     *
     * @return the hash code of the mission source
     */
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Checks if the given object is equal to this mission source.
     *
     * @param o the object to compare
     * @return true if the given object is equal to this mission source, false otherwise
     */
    public boolean equals(Object o) {
        return o instanceof MissionSource && id.equals(((MissionSource) o).id);
    }

    /**
     * Returns the cluster of the mission source.
     *
     * @return the cluster of the mission source
     */
    @ManyToOne(fetch = FetchType.LAZY)
    public Cluster getCluster() {
        return cluster;
    }

    /**
     * Sets the cluster of the mission source.
     *
     * @param cluster the new cluster of the mission source
     */
    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

}