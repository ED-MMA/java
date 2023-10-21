package com.github.aklakina.edmma.database.orms;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "FACTION", schema = "ED")
public class Faction {
    public Faction() {

    }

    private Long id;
    private String name;
    private Set<MissionSource> missionSources;
    private Set<Cluster> clusters;

    @Id
    public Long getID() {
        return id;
    }

    public void setID(Long id) {
        this.id = id;
    }

    @Basic(optional=false)
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
        return id.hashCode();
    }

    public boolean equals(Object o) {
        return o instanceof Faction && id.equals(((Faction) o).id);
    }

}
