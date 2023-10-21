CREATE SCHEMA LOG;
CREATE SCHEMA ED;
CREATE SCHEMA MD;

create table LOG.APP
(
    ID          INTEGER                not null,
    LOG_DATE    TIMESTAMP              not null,
    LOG_LEVEL   CHARACTER VARYING(10)  not null,
    LOG_MESSAGE CHARACTER LARGE OBJECT not null,
    constraint APP_LOG_PKEY
        primary key (ID)
);

create table LOG.ED
(
    ID          INTEGER                not null,
    LOG_DATE    TIMESTAMP              not null,
    LOG_LEVEL   CHARACTER VARYING(10)  not null,
    LOG_MESSAGE CHARACTER LARGE OBJECT not null,
    constraint ED_LOG_PKEY
        primary key (ID)
);

create table ED.FACTIONS
(
    ID   INTEGER auto_increment,
    NAME CHARACTER LARGE OBJECT not null,
    constraint "FACTIONS_pk"
        primary key (ID)
);

create table MD.FILE
(
    FILENAME CHARACTER LARGE OBJECT not null,
    LASTLINE INTEGER,
    LASTSIZE INTEGER                not null,
    "FileID" INTEGER auto_increment,
    constraint "FILE_pk"
        primary key ("FileID")
);

create table LOG.MD
(
    ID          INTEGER                not null,
    LOG_DATE    TIMESTAMP              not null,
    LOG_LEVEL   CHARACTER VARYING(10)  not null,
    LOG_MESSAGE CHARACTER LARGE OBJECT not null,
    constraint MD_LOG_PKEY
        primary key (ID)
);

create table ED.SOURCESYSTEMS
(
    NAME CHARACTER LARGE OBJECT not null,
    ID   INTEGER auto_increment,
    constraint "SOURCESYSTEMS_pk"
        primary key (ID)
);

create table ED.STATIONS
(
    ID       INTEGER auto_increment,
    NAME     CHARACTER LARGE OBJECT not null,
    SYSTEMID INTEGER                not null,
    constraint "STATIONS_pk"
        primary key (ID),
    constraint "STATIONS_SOURCESYSTEMS_ID_fk"
        foreign key (SYSTEMID) references ED.SOURCESYSTEMS
);

create table ED.STATIONFACTION
(
    STATIONID INTEGER not null,
    FACTIONID INTEGER not null,
    ID        INTEGER auto_increment,
    constraint STATIONFACTION_PK
        primary key (ID),
    constraint "STATIONFACTION_uk"
        unique (STATIONID, FACTIONID),
    constraint STATIONFACTION_FACTIONS_FACTIONID_FK
        foreign key (FACTIONID) references ED.FACTIONS,
    constraint STATIONFACTION_STATIONS_STATIONID_FK
        foreign key (STATIONID) references ED.STATIONS
);

create table ED.TARGETSYSTEMS
(
    ID   INTEGER auto_increment,
    NAME CHARACTER LARGE OBJECT not null,
    constraint "TARGETSYSTEMS_pk"
        primary key (ID)
);

create table ED."TargetFactions"
(
    ID         INTEGER auto_increment,
    "Name"     CHARACTER LARGE OBJECT,
    "SystemID" INTEGER,
    constraint "TargetFactions_pk"
        primary key (ID),
    constraint "TargetFactions_TARGETSYSTEMS_ID_fk"
        foreign key ("SystemID") references ED.TARGETSYSTEMS
);

create table ED.CLUSTERS
(
    SOURCESYSTEMID  INTEGER not null,
    TARGETFACTIONID INTEGER not null,
    ID              INTEGER auto_increment,
    constraint CLUSTERS_PK
        primary key (ID),
    constraint "CLUSTERS_pk"
        unique (SOURCESYSTEMID, TARGETFACTIONID),
    constraint CLUSTERS_SOURCESYSTEMS_ID_FK
        foreign key (SOURCESYSTEMID) references ED.SOURCESYSTEMS,
    constraint "CLUSTERS_TargetFactions_ID_fk"
        foreign key (TARGETFACTIONID) references ED."TargetFactions"
);

create table ED.MISSIONS
(
    MISSIONID      INTEGER                not null,
    "SourceID"     INTEGER                not null,
    "ClusterID"    INTEGER                not null,
    KILLSNEEDED    INTEGER                not null,
    KILLSSOFAR     INTEGER default 0,
    REWARD         REAL                   not null,
    WINGED         INTEGER                not null,
    ACCEPTANCETIME CHARACTER LARGE OBJECT not null,
    EXPIRESAT      CHARACTER LARGE OBJECT not null,
    KILLSLEFT      INTEGER generated always as "KILLSNEEDED" - "KILLSSOFAR",
    COMPLETED      BOOLEAN generated always as ("KILLSNEEDED" - "KILLSSOFAR") <= 0,
    constraint MISSIONS_PK
        primary key (MISSIONID),
    constraint "MISSIONS_CLUSTERS_ID_fk"
        foreign key ("ClusterID") references ED.CLUSTERS,
    constraint "MISSIONS_STATIONFACTION_ID_fk"
        foreign key ("SourceID") references ED.STATIONFACTION
);