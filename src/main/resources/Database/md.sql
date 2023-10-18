create table md.file
(
    fileName TEXT    not null
        constraint metadata_pk
            primary key,
    lastLine integer,
    lastSize INTEGER not null
)
    without rowid;