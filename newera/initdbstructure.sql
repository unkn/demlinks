set client_min_messages = NOTICE;
set session client_min_messages = WARNING;

DROP TABLE IF EXISTS "NodeNames" CASCADE;

DROP TABLE IF EXISTS "Relations" CASCADE;

CREATE TABLE "NodeNames" ( "ID" SERIAL PRIMARY KEY, "Name" CHARACTER VARYING(256) UNIQUE NOT NULL );

CREATE TABLE "Relations" (
        "ParentID" integer NOT NULL REFERENCES "NodeNames" ("ID") MATCH FULL
                ON DELETE CASCADE ON UPDATE CASCADE,
        "ChildID" integer NOT NULL REFERENCES "NodeNames" ("ID") MATCH FULL
                ON DELETE CASCADE ON UPDATE CASCADE
);

drop view if exists showrel cascade;

create or replace view showrel as
        select w1."Name" as "Parent", w2."Name" as "Child"
                from "NodeNames" w1,"NodeNames" w2,"Relations" r
                where w1."ID" = r."ParentID" AND w2."ID" = r."ChildID";

drop function if exists getID(character) cascade;

create or replace function getID (character)
        RETURNS integer as $$
                select "ID" from "NodeNames" where "Name"=$1;
        $$ LANGUAGE SQL;


create or replace function ensureName(character) RETURNS integer as $$
        -- returns ID of "Name"
        DECLARE
                rec RECORD;
                nam ALIAS FOR $1;
        BEGIN
                SELECT INTO rec * FROM "NodeNames" WHERE "Name"=nam;
                IF NOT FOUND THEN
                        INSERT INTO "NodeNames" ( "Name" ) VALUES (nam);
                        RETURN getID(nam);
                ELSE
                        RETURN rec."ID";
                END IF;
        END;
        $$ LANGUAGE PLPGSQL;

create or replace rule insert_in_showrel as on insert to showrel do instead
        insert into "Relations" values (ensureName(NEW."Parent"), ensureName(NEW."Child"));

