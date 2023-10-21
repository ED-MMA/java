package com.github.aklakina.edmma.database.orms;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "MISSIONSOURCE", schema = "ED")
public class MissionSource {
    public MissionSource() {
    }

    private Long id;
    private SourceFaction faction;
    private Station station;
    private Set<Mission> missions;

    @Id
    public Long getID() {
        return id;
    }

    public void setID(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public SourceFaction getFaction() {
        return faction;
    }

    public void setFaction(SourceFaction sourceFaction) {
        this.faction = sourceFaction;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    @OneToMany(mappedBy = Mission_.SOURCE)
    public Set<Mission> getMissions() {
        return missions;
    }

    public void setMissions(Set<Mission> missions) {
        this.missions = missions;
    }

    public int hashCode() {
        return id.hashCode();
    }

    public boolean equals(Object o) {
        return o instanceof MissionSource && id.equals(((MissionSource) o).id);
    }

}
