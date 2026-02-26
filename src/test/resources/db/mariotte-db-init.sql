-- Init script for Postgres container used in tests.
-- Выполняется при старте контейнера, чтобы гарантировать наличие БД `mariotte`.
drop
    database if exists mariotte;
create
    database mariotte;
