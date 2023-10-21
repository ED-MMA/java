package com.github.aklakina.edmma.database.orms;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "SOURCESTATION", schema = "ED")
public class SourceFaction {
    public SourceFaction() {
    }

    private Long id;
    private String name;
    private Set<MissionSource> sources;

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

    @OneToMany(mappedBy = MissionSource_.FACTION)
    public Set<MissionSource> getSources() {
        return sources;
    }

    public void setSources(Set<MissionSource> sources) {
        this.sources = sources;
    }

    public int hashCode() {
        return id.hashCode();
    }

    public boolean equals(Object o) {
        return o instanceof SourceFaction && id.equals(((SourceFaction) o).id);
    }
}
