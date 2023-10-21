package com.github.aklakina.edmma.database.orms;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "CLUSTER", schema = "ED")
public class Cluster {
    public Cluster() {
    }

    private SourceSystem sourceSystem;
    private TargetFaction targetFaction;

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
    public SourceSystem getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(SourceSystem sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public TargetFaction getTargetFaction() {
        return targetFaction;
    }

    public void setTargetFaction(TargetFaction targetFaction) {
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

}
