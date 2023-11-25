package com.github.aklakina.edmma.database.orms;

import jakarta.persistence.*;

import java.util.Set;

/**
 * The System class represents a system in the database.
 * It contains information about the name of the system, stations, clusters, and galactic position.
 * It also contains methods to get and set these attributes.
 */
@Entity
@Table(name = "SYSTEM", schema = "ED")
public class System {

    /**
     * The stations of the system.
     */
    private Set<Station> stations;

    /**
     * The name of the system.
     */
    private String name;

    /**
     * The clusters of the system.
     */
    private Set<Cluster> clusters;

    /**
     * The galactic position of the system.
     */
    private GalacticPosition galacticPosition;

    /**
     * Default constructor.
     */
    public System() {
    }

    /**
     * Returns the stations of the system.
     *
     * @return the stations of the system
     */
    @OneToMany(mappedBy = Station_.SYSTEM)
    public Set<Station> getStations() {
        return stations;
    }

    /**
     * Sets the stations of the system.
     *
     * @param stations the new stations of the system
     */
    public void setStations(Set<Station> stations) {
        this.stations = stations;
    }

    /**
     * Returns the name of the system.
     *
     * @return the name of the system
     */
    @Id
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the system.
     *
     * @param name the new name of the system
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the clusters of the system.
     *
     * @return the clusters of the system
     */
    @OneToMany(mappedBy = Cluster_.TARGET_SYSTEM)
    public Set<Cluster> getClusters() {
        return clusters;
    }

    /**
     * Sets the clusters of the system.
     *
     * @param clusters the new clusters of the system
     */
    public void setClusters(Set<Cluster> clusters) {
        this.clusters = clusters;
    }

    /**
     * Returns the galactic position of the system.
     *
     * @return the galactic position of the system
     */
    @OneToOne(mappedBy = GalacticPosition_.SYSTEM, optional = true)
    public GalacticPosition getGalacticPosition() {
        return galacticPosition;
    }

    /**
     * Sets the galactic position of the system.
     *
     * @param galacticPosition the new galactic position of the system
     */
    public void setGalacticPosition(GalacticPosition galacticPosition) {
        this.galacticPosition = galacticPosition;
    }

    /**
     * Returns the hash code of the system.
     *
     * @return the hash code of the system
     */
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Checks if the given object is equal to this system.
     *
     * @param o the object to compare
     * @return true if the given object is equal to this system, false otherwise
     */
    public boolean equals(Object o) {
        return o instanceof System && name.equals(((System) o).name);
    }

}