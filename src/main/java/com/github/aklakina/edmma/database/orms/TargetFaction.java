package com.github.aklakina.edmma.database.orms;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "TARGETFACTION", schema = "ED")
public class TargetFaction {
    public TargetFaction() {

    }

    private Long id;
    private String name;
    private TargetSystem system;
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

    @ManyToOne(fetch = FetchType.LAZY)
    public TargetSystem getSystem() {
        return system;
    }

    public void setSystem(TargetSystem system) {
        this.system  = system;
    }

    @OneToMany(mappedBy = Cluster_.TARGET_FACTION)
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
        return o instanceof TargetFaction && id.equals(((TargetFaction) o).id);
    }

}
