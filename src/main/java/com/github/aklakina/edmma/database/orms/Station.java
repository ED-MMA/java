package com.github.aklakina.edmma.database.orms;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "STATION", schema = "ED")
public class Station {

    private System system;
    private String name;
    private GalacticPosition galacticPosition;
    private Set<MissionSource> missionSources;
    public Station() {
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public System getSystem() {
        return system;
    }

    public void setSystem(System system) {
        this.system = system;
    }

    @OneToMany(mappedBy = MissionSource_.STATION)
    public Set<MissionSource> getMissionSources() {
        return missionSources;
    }

    public void setMissionSources(Set<MissionSource> missionSources) {
        this.missionSources = missionSources;
    }

    @Id
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToOne(mappedBy = GalacticPosition_.STATION, optional = true)
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
        return o instanceof Station && name.equals(((Station) o).name);
    }

}
