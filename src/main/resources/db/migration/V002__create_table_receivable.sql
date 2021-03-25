CREATE TABLE receivable
(
    id                   INTEGER DEFAULT nextval('global_seq') PRIMARY KEY,
    crm_id               INTEGER          NOT NULL,
    date                 DATE             NOT NULL,
    currency             VARCHAR          NOT NULL,
    amount               DOUBLE PRECISION NOT NULL,
    amount_usd           DOUBLE PRECISION,
    amount_rub           DOUBLE PRECISION,
    bank                 VARCHAR,
    bank_account         VARCHAR,
    legal_name           VARCHAR,
    contractor            VARCHAR          NOT NULL,
    type                 VARCHAR          ,
    contractor_account   VARCHAR,
    contractor_legal_name VARCHAR,
    category             VARCHAR NOT NULL,
    subcategory          VARCHAR,
    project              VARCHAR,
    office               VARCHAR,
    description          VARCHAR,
    load_date            DATE             NOT NULL DEFAULT current_date
);

CREATE UNIQUE INDEX receivable_unique_crm_id_idx ON receivable (crm_id);