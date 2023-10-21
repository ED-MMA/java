package com.github.aklakina.edmma.database.orms;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "STATION", schema = "ED")
public class Station {

    public Station() {
    }

    private SourceSystem sourceSystem;

    private Long id;
    private String name;

    private Set<MissionSource> missionSources;

    @Id
    public Long getID() {
        return id;
    }

    public void setID(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public SourceSystem getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(SourceSystem sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    @OneToMany(mappedBy = MissionSource_.STATION)
    public Set<MissionSource> getMissionSources() {
        return missionSources;
    }

    public void setMissionSources(Set<MissionSource> missionSources) {
        this.missionSources = missionSources;
    }

    @Basic(optional=false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int hashCode() {
        return id.hashCode();
    }

    public boolean equals(Object o) {
        return o instanceof Station && id.equals(((Station) o).id);
    }

}
