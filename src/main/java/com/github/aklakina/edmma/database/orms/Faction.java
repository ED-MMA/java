package com.github.aklakina.edmma.database.orms;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "FACTION", schema = "ED")
public class Faction {
    public Faction() {

    }
    private String name;
    private Set<MissionSource> missionSources;
    private Set<Cluster> clusters;

    @Id
    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = Cluster_.TARGET_FACTION)
    public Set<Cluster> getClusters() {
        return clusters;
    }

    public void setClusters(Set<Cluster> clusters) {
        this.clusters = clusters;
    }

    @OneToMany(mappedBy = MissionSource_.FACTION)
    public Set<MissionSource> getMissionSources() {
        return missionSources;
    }

    public void setMissionSources(Set<MissionSource> missionSources) {
        this.missionSources = missionSources;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object o) {
        return o instanceof Faction && name.equals(((Faction) o).name);
    }

    @Transient
    public int getStackHeight() {
        return missionSources.stream().mapToInt(ms ->
                ms.getNotCompletedMissions().stream().mapToInt(Mission::getKillsLeft).sum()).sum();
    }

}
