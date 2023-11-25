package com.github.aklakina.edmma.database.orms;

import jakarta.persistence.*;

import java.util.Set;

/**
 * The Station class represents a station in the database.
 * It contains information about the system, name, galactic position, and mission sources of the station.
 * It also contains methods to get and set these attributes.
 */
@Entity
@Table(name = "STATION", schema = "ED")
public class Station {

    /**
     * The system of the station.
     */
    private System system;

    /**
     * The name of the station.
     */
    private String name;

    /**
     * The galactic position of the station.
     */
    private GalacticPosition galacticPosition;

    /**
     * The mission sources of the station.
     */
    private Set<MissionSource> missionSources;

    /**
     * Default constructor.
     */
    public Station() {
    }

    /**
     * Returns the system of the station.
     *
     * @return the system of the station
     */
    @ManyToOne(fetch = FetchType.LAZY)
    public System getSystem() {
        return system;
    }

    /**
     * Sets the system of the station.
     *
     * @param system the new system of the station
     */
    public void setSystem(System system) {
        this.system = system;
    }

    /**
     * Returns the mission sources of the station.
     *
     * @return the mission sources of the station
     */
    @OneToMany(mappedBy = MissionSource_.STATION)
    public Set<MissionSource> getMissionSources() {
        return missionSources;
    }

    /**
     * Sets the mission sources of the station.
     *
     * @param missionSources the new mission sources of the station
     */
    public void setMissionSources(Set<MissionSource> missionSources) {
        this.missionSources = missionSources;
    }

    /**
     * Returns the name of the station.
     *
     * @return the name of the station
     */
    @Id
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the station.
     *
     * @param name the new name of the station
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the galactic position of the station.
     *
     * @return the galactic position of the station
     */
    @OneToOne(mappedBy = GalacticPosition_.STATION, optional = true)
    public GalacticPosition getGalacticPosition() {
        return galacticPosition;
    }

    /**
     * Sets the galactic position of the station.
     *
     * @param galacticPosition the new galactic position of the station
     */
    public void setGalacticPosition(GalacticPosition galacticPosition) {
        this.galacticPosition = galacticPosition;
    }

    /**
     * Returns the hash code of the station.
     *
     * @return the hash code of the station
     */
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Checks if the given object is equal to this station.
     *
     * @param o the object to compare
     * @return true if the given object is equal to this station, false otherwise
     */
    public boolean equals(Object o) {
        return o instanceof Station && name.equals(((Station) o).name);
    }

}