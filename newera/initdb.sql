set client_min_messages = NOTICE;
set session client_min_messages = WARNING;
DROP DATABASE IF EXISTS demlinks_db;
DROP USER IF EXISTS demlinks_user;
CREATE USER demlinks_user WITH NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT LOGIN ENCRYPTED PASSWORD 'dml' ;
CREATE DATABASE demlinks_db WITH OWNER=demlinks_user;
