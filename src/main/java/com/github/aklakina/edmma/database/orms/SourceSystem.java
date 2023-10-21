package com.github.aklakina.edmma.database.orms;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "SOURCESYSTEM", schema = "ED")
public class SourceSystem {

    public SourceSystem() {
    }

    private Long id;

    private Set<Station> station;
    private String name;
    private Set<Cluster> clusters;

    @OneToMany(mappedBy = Station_.SOURCE_SYSTEM)
    public Set<Station> getStation() {
        return station;
    }

    public void setStation(Set<Station> station) {
        this.station = station;
    }

    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic(optional=false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = Cluster_.SOURCE_SYSTEM)
    public Set<Cluster> getClusters() {
        return clusters;
    }

    public void setClusters(Set<Cluster> clusters) {
        this.clusters = clusters;
    }

    public int hashCode() {
        return id.hashCode();
    }

    public boolean equals(Object o) {
        return o instanceof SourceSystem && id.equals(((SourceSystem) o).id);
    }

}
