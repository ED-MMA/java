create table ed.factions
(
    ID   integer
        constraint factions_pk
            primary key,
    name TEXT
        constraint factions_pk2
            unique
);

create table ed.sourceSystems
(
    name TEXT not null
        constraint sourceSystems_pk
            unique,
    ID   integer
        constraint systems_pk
            primary key autoincrement
);

create table ed.stations
(
    ID       integer
        constraint stations_pk
            primary key,
    name     TEXT
        constraint stations_pk2
            unique,
    systemID integer
        constraint stations_systems_ID_fk
            references ed.sourceSystems
);

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

create table ed.stationFaction
(
    stationID integer not null
        constraint stationFaction_stations_stationID_fk
            references ed.stations,
    factionID integer not null
        constraint stationFaction_factions_factionID_fk
            references ed.factions,
    constraint stationFaction_pk
        primary key (factionID, stationID)
);

create table ed.targetSystems
(
    ID   integer
        constraint targetSystems_pk
            primary key autoincrement,
    name TEXT
        constraint targetSystems_pk2
            unique
);

create table ed.clusters
(
    sourceSystemID integer not null
        constraint clusters_sourceSystems_ID_fk
            references ed.sourceSystems,
    targetSystemID integer not null
        constraint clusters_targetSystems_ID_fk
            references ed.targetSystems,
    constraint clusters_pk
        primary key (targetSystemID, sourceSystemID)
)
    without rowid;