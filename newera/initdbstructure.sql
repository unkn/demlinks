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

drop view if exists "ShowRel" cascade;

create or replace view "ShowRel" as
        -- warning changin '"Parent"' here means changing '"Parent"' near the far below rule using the same text: '"Parent"'
        select n1."Name" as "Parent", n2."Name" as "Child"
                from "NodeNames" n1,"NodeNames" n2,"Relations" r
                where n1."ID" = r."ParentID" AND n2."ID" = r."ChildID";

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

create or replace rule "insert_in_ShowRel" as on insert to "ShowRel" do instead
        insert into "Relations" values (ensureName(NEW."Parent"), ensureName(NEW."Child"));

