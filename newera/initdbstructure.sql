set client_min_messages = NOTICE;
set session client_min_messages = WARNING;

--create or replace function initdbstructure(integer) RETURNS integer as $bigf$
DROP TABLE IF EXISTS "NodeNames" CASCADE;

DROP TABLE IF EXISTS "Relations" CASCADE;

CREATE TABLE "NodeNames" ( "ID" SERIAL PRIMARY KEY, "Name" CHARACTER VARYING(256) UNIQUE NOT NULL );

CREATE TABLE "Relations" (
        "ParentID" integer NOT NULL REFERENCES "NodeNames" ("ID") MATCH FULL
                ON DELETE CASCADE ON UPDATE CASCADE,
        "ChildID" integer NOT NULL REFERENCES "NodeNames" ("ID") MATCH FULL
                ON DELETE CASCADE ON UPDATE CASCADE
);

--create function a(integer) RETURNS record as $$
--select * from "NodeNames";
--select * from "Relations";
--select * from "NodeNames";
--$$ LANGUAGE SQL;

drop view if exists "ShowRel" cascade;

create or replace view "ShowRel" as
        -- warning changin '"Parent"' here means changing '"Parent"' near the far below rule using the same text: '"Parent"'
        select n1."Name" as "Parent", n2."Name" as "Child"
                from "NodeNames" n1,"NodeNames" n2,"Relations" r
                where n1."ID" = r."ParentID" AND n2."ID" = r."ChildID";

drop function if exists GetID("NodeNames"."Name"%TYPE) cascade;
create or replace function GetID ("NodeNames"."Name"%TYPE)
        RETURNS "NodeNames"."ID"%TYPE as $$
-- always returns 1 row; when no ID is found the ID number is missing ie.use if (empty(trim($res)))
                select "ID" from "NodeNames" where "Name"=$1;
$$        LANGUAGE SQL;

drop function if exists DelID("NodeNames"."ID"%TYPE) cascade;
create or replace function DelID ("NodeNames"."ID"%TYPE)
        RETURNS boolean as $$
        DECLARE
                idd ALIAS FOR $1;
        BEGIN
                delete from "NodeNames" where "ID"=idd;
                if FOUND then
                        return true;
                else
                        return false;
                end if;
        END;
$$        LANGUAGE PLPGSQL;

/*create or replace function g ("NodeNames"."Name"%TYPE)
        RETURNS RECORD as $$
        DECLARE
                nam ALIAS FOR $1;
                rec RECORD;
        BEGIN
                SELECT INTO rec "ID" from "NodeNames" WHERE "Name"=nam;
                IF NOT FOUND THEN
                        RAISE NOTICE 'hi';
                        RETURN NULL;
                ELSE
                        RETURN rec;
                END IF;
        END;
$$ LANGUAGE PLPGSQL;
*/


drop function if exists DelName("NodeNames"."Name"%TYPE) cascade;
create or replace function DelName("NodeNames"."Name"%TYPE) RETURNS boolean as $$
        DECLARE
                nam ALIAS FOR $1;
        BEGIN
                DELETE FROM "NodeNames" WHERE "Name"=nam;
                if FOUND then
                        return true;
                else
                        return false;
                end if;
        END;
$$ LANGUAGE PLPGSQL;

drop function if exists EnsureName("NodeNames"."Name"%TYPE) cascade;
create or replace function EnsureName("NodeNames"."Name"%TYPE) RETURNS "NodeNames"."ID"%TYPE as $moo$
        -- returns ID of "Name"
        DECLARE
                rec RECORD;
                nam ALIAS FOR $1;
        BEGIN
                SELECT INTO rec * FROM "NodeNames" WHERE "Name"=nam;
                IF NOT FOUND THEN
                --BEGIN
                        INSERT INTO "NodeNames" ( "Name" ) VALUES (nam);
                  --      EXCEPTION
                    --            WHEN unique_violation THEN
                      --                  NULL;-- do nothing
                --END;
                        RETURN GetID(nam) as "ID";
                ELSE
                        RETURN rec."ID";
                END IF;
        END;
        $moo$ LANGUAGE PLPGSQL;

create or replace rule "insert_in_ShowRel" as on insert to "ShowRel" do instead
        insert into "Relations" values (EnsureName(NEW."Parent"), EnsureName(NEW."Child"));

--$bigf$ LANGUAGE SQL;
