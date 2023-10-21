package com.github.aklakina.edmma.database.orms;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "TARGETSYSTEM", schema = "ED")
public class TargetSystem {

    private Long id;
    private String name;
    private Set<TargetFaction> targets;

    public TargetSystem() {
    }

    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic(optional=false)
    private String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = TargetFaction_.SYSTEM)
    public Set<TargetFaction> getTargets() {
        return targets;
    }

    public void setTargets(Set<TargetFaction> targets) {
        this.targets = targets;
    }

    public int hashCode() {
        return id.hashCode();
    }

    public boolean equals(Object o) {
        return o instanceof TargetSystem && id.equals(((TargetSystem) o).id);
    }
}
