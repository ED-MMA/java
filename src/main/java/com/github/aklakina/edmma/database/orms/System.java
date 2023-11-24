package com.github.aklakina.edmma.database.orms;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "SYSTEM", schema = "ED")
public class System {

    private Set<Station> stations;
    private String name;
    private Set<Cluster> clusters;
    private GalacticPosition galacticPosition;
    public System() {
    }

    @OneToMany(mappedBy = Station_.SYSTEM)
    public Set<Station> getStations() {
        return stations;
    }

    public void setStations(Set<Station> stations) {
        this.stations = stations;
    }

    @Id
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = Cluster_.TARGET_SYSTEM)
    public Set<Cluster> getClusters() {
        return clusters;
    }

    public void setClusters(Set<Cluster> clusters) {
        this.clusters = clusters;
    }

    @OneToOne(mappedBy = GalacticPosition_.SYSTEM, optional = true)
    public GalacticPosition getGalacticPosition() {
        return galacticPosition;
    }

    public void setGalacticPosition(GalacticPosition galacticPosition) {
        this.galacticPosition = galacticPosition;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object o) {
        return o instanceof System && name.equals(((System) o).name);
    }

}
