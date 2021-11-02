ALTER TABLE load_audit ADD COLUMN load_date_time timestamp;
ALTER TABLE load_audit RENAME COLUMN date TO load_date;