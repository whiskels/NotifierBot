update receivable
set amount_rub = 0
where amount is null;

alter table receivable
    alter column amount_rub set not null;