CREATE TABLE log.app (
    id serial NOT NULL,
    log_date timestamp without time zone NOT NULL,
    log_level character varying(10) NOT NULL,
    log_message text NOT NULL,
    CONSTRAINT app_log_pkey PRIMARY KEY (id)
);

CREATE TABLE log.ed (
    id serial NOT NULL,
    log_date timestamp without time zone NOT NULL,
    log_level character varying(10) NOT NULL,
    log_message text NOT NULL,
    CONSTRAINT ed_log_pkey PRIMARY KEY (id)
);

CREATE TABLE log.md (
    id serial NOT NULL,
    log_date timestamp without time zone NOT NULL,
    log_level character varying(10) NOT NULL,
    log_message text NOT NULL,
    CONSTRAINT md_log_pkey PRIMARY KEY (id)
);
