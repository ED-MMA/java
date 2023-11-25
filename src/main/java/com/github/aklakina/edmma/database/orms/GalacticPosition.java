package com.github.aklakina.edmma.database.orms;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * The GalacticPosition class represents a galactic position in the database.
 * It contains information about the system and the station of the galactic position.
 * It also contains methods to get and set the system, the station, and the ID of the galactic position.
 */
@Entity
@Table(name = "GALACTIC_POSITION", schema = "ED")
public class GalacticPosition {

    /**
     * The system of the galactic position.
     */
    private System system;

    /**
     * The station of the galactic position.
     */
    private Station station;

    /**
     * The ID of the galactic position.
     */
    private Long id;

    /**
     * Default constructor.
     * Initializes the ID of the galactic position to 0.
     */
    public GalacticPosition() {
        id = 0L;
    }

    /**
     * Constructor with parameters.
     * Initializes the system, the station, and the ID of the galactic position.
     *
     * @param system the system of the galactic position
     * @param station the station of the galactic position
     */
    public GalacticPosition(System system, Station station) {
        this.system = system;
        this.station = station;
        id = 0L;
    }

    /**
     * Returns the ID of the galactic position.
     *
     * @return the ID of the galactic position
     */
    @Id
    public Long getID() {
        return id;
    }

    /**
     * Sets the ID of the galactic position.
     *
     * @param id the new ID of the galactic position
     */
    public void setID(Long id) {
        this.id = id;
    }

    /**
     * Returns the system of the galactic position.
     *
     * @return the system of the galactic position
     */
    @OneToOne
    public System getSystem() {
        return system;
    }

    /**
     * Sets the system of the galactic position.
     *
     * @param system the new system of the galactic position
     */
    public void setSystem(System system) {
        this.system = system;
    }

    /**
     * Returns the station of the galactic position.
     *
     * @return the station of the galactic position
     */
    @OneToOne
    public Station getStation() {
        return station;
    }

    /**
     * Sets the station of the galactic position.
     *
     * @param station the new station of the galactic position
     */
    public void setStation(Station station) {
        this.station = station;
    }
}