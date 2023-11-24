package com.github.aklakina.edmma.database.orms;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "MISSIONSOURCE", schema = "ED")
public class MissionSource {
    private Long id;
    private Faction faction;
    private Station station;
    private Set<Mission> missions;
    private Cluster cluster;
    public MissionSource() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getID() {
        return id;
    }

    public void setID(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction sourceFaction) {
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

    @Transient
    public Set<Mission> getNotCompletedMissions() {
        return missions.stream().filter(m -> !m.isCompleted()).collect(java.util.stream.Collectors.toSet());
    }

    public int hashCode() {
        return id.hashCode();
    }

    public boolean equals(Object o) {
        return o instanceof MissionSource && id.equals(((MissionSource) o).id);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

}
