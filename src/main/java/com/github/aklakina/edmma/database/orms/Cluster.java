package com.github.aklakina.edmma.database.orms;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "CLUSTER", schema = "ED")
public class Cluster {
    public Cluster() {
    }
    private Faction targetFaction;
    private Set<MissionSource> missionSources;
    private System targetSystem;

    private Long id;

    private Set<Mission> missions;

    @Id
    public Long getID() {
        return id;
    }

    public void setID(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Faction getTargetFaction() {
        return targetFaction;
    }

    public void setTargetFaction(Faction targetFaction) {
        this.targetFaction = targetFaction;
    }

    @OneToMany(mappedBy = Mission_.CLUSTER)
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
        return o instanceof Cluster && id.equals(((Cluster) o).id);
    }

    @OneToMany(mappedBy = MissionSource_.CLUSTER)
    public Set<MissionSource> getMissionSources() {
        return missionSources;
    }

    public void setMissionSources(Set<MissionSource> missionSources) {
        this.missionSources = missionSources;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public System getTargetSystem() {
        return targetSystem;
    }

    public void setTargetSystem(System targetSystem) {
        this.targetSystem = targetSystem;
    }

}
