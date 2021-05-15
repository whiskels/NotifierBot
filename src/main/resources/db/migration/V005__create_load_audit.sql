CREATE TABLE load_audit
(
    id     INTEGER DEFAULT nextval('global_seq') PRIMARY KEY,
    date   DATE    NOT NULL,
    loader VARCHAR NOT NULL,
    count  INTEGER NOT NULL
)