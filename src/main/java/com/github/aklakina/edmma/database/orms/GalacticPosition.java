package com.github.aklakina.edmma.database.orms;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "GALACTIC_POSITION", schema = "ED")
public class GalacticPosition {

    private System system;
    private Station station;
    private Long id;
    public GalacticPosition() {
        id = 0L;
    }
    public GalacticPosition(System system, Station station) {
        this.system = system;
        this.station = station;
        id = 0L;
    }

    @Id
    public Long getID() {
        return id;
    }

    public void setID(Long id) {
        this.id = id;
    }

    @OneToOne
    public System getSystem() {
        return system;
    }

    public void setSystem(System system) {
        this.system = system;
    }

    @OneToOne
    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }
}
