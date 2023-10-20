package com.github.aklakina.edmma.database.orms;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;


/*
create table ed.missions
(
    missionID       integer not null
        constraint missions_pk
            primary key,
    sourceFactionID integer not null
        constraint missions_factions_factionID_fk
            references ed.factions,
    stationID       integer not null
        constraint missions_stations_stationID_fk
            references ed.stations,
    sourceSystemID  integer not null
        constraint missions_systems_ID_source_fk
            references ed.sourceSystems,
    targetFactionID integer not null
        constraint missions_factions_factionID_target_fk
            references ed.factions,
    targetSystemID  integer not null
        constraint missions_systems_ID_target_fk
            references ed.sourceSystems,
    killsNeeded     integer not null,
    killsSoFar      integer default 0,
    killsLeft       integer generated always as (killsNeeded - killsSoFar) virtual,
    reward          REAL    not null,
    completed       INTEGER generated always as (killsLeft <= 0) virtual,
    winged          integer not null,
    acceptanceTime  TEXT    not null,
    expiresAt       TEXT    not null
)
    without rowid;
 */

@Entity
public class Mission {
    @Id
    private long ID;

    private long sourceFactionID;

    private long stationID;

    private long sourceSystemID;

    private long targetFactionID;

    private long targetSystemID;

    private long killsNeeded;

    private long killsSoFar;

    private long killsLeft;

    private double reward;

    private long completed;

    private long winged;

    private String acceptanceTime;

    private String expiresAt;



}
