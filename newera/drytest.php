<?php
// Connecting, selecting database

require_once("shortdef.php");

function pg_die($msg)
{
        debug_print_backtrace();
        die($msg .": ". pg_last_error());
}

/*$dbconn = pg_connect("host=localhost dbname=postgres user=postgres password=dml")
    or pg_die('Could not connect');
go('DROP DATABASE IF EXISTS demlinks_db');
go('CREATE DATABASE demlinks_db WITH OWNER=demlinks_user');

pg_close($dbconn);
 */


$dbconn = pg_connect("host=localhost dbname=demlinks_db user=demlinks_user password=dml")
    or pg_die('Could not connect');
pg_trace('./pg_trace.log');

function go($query)
{
        //@$res= pg_query($query) or pg_die('Query("'.$query.'") failed');
        $res=get($query);
        pg_free_result($res);//exec this only if not failed
}

function get($query)
{
        @$res= pg_query($query) or pg_die('Query("'.$query.'") failed');
        return $res;
}
/*
// Performing SQL query
go('DROP TABLE IF EXISTS "NodeNames" CASCADE');//case sensitivity is preserved with quotes
go('DROP TABLE IF EXISTS "Relations" CASCADE');//case sensitivity is preserved with quotes
go('CREATE TABLE "NodeNames" ( "ID" SERIAL PRIMARY KEY, "Name" CHARACTER VARYING(256) UNIQUE NOT NULL )');
go('CREATE TABLE "Relations" ( "ParentID" integer NOT NULL REFERENCES "NodeNames" ("ID") MATCH FULL ON DELETE CASCADE ON UPDATE CASCADE,
                             "ChildID" integer NOT NULL REFERENCES "NodeNames" ("ID") MATCH FULL ON DELETE CASCADE ON UPDATE CASCADE)');

go('drop view if exists showrel cascade');
go('create or replace view showrel as select w1."Name" as "Parent", w2."Name" as "Child" from "NodeNames" w1,"NodeNames" w2,"Relations" r where w1."ID" = r."ParentID" AND w2."ID" = r."ChildID"');
go('drop function if exists getID(character) cascade');
go('create or replace function getID (character) RETURNS integer as $$ select "ID" from "NodeNames" where "Name"=$1; $$ LANGUAGE SQL;');

go('create or replace function ensureName(character) RETURNS integer as $$
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
        END; $$ LANGUAGE PLPGSQL');

//go('drop function foobar(character, character) cascade');
//go('create or replace function foobar (character, character, OUT integer, OUT integer) as $$ select getID($1),getID($2); $$ LANGUAGE SQL');
go('create or replace rule insert_in_showrel as on insert to showrel do instead insert into "Relations" values (ensureName(NEW."Parent"), ensureName(NEW."Child"))');
*/
//go('CREATE OR REPLACE RULE returnID AS ON INSERT TO "NodeNames" DO ALSO SELECT NEW."ID"');

$res=split("[ .,/\\\"\?\<\>&!;|\#\$\*\+\{\}=\(\)'`\n\-]",file_get_contents("dmldbdef.php"));
        foreach ($res as $val) {
                $val=trim($val);
                if (!empty($val)) {//ie. non-empty
                        echo $val." ";
                        $res=get('SELECT * from "NodeNames" WHERE "Name"=\''.$val.'\'');
                        $status=pg_fetch_array($res,null,PGSQL_ASSOC);
                        pg_free_result($res);
                        if (empty($status)) {
                                go('INSERT INTO "NodeNames" ( "Name" ) VALUES (\''.$val.'\')');
                                go('Insert into "ShowRel" values (\'main.cpp\', \''.$val.'\')');
                                if (isset($lastval)) {go('insert into "ShowRel" values(\''.$lastval.'\', \''.$val.'\')');
                                }
                                $lastval=$val;
                                //go('INSERT INTO "Relations" ( "ParentID", "ChildID" ) VALUES (\''.$status["ID"].'\', \''.$status["ID"].'\')');
                        }
                }//fi
        }//foreach


/*$res= pg_query($query) or die('Query failed: ' . pg_last_error());
pg_free_result($res);*/
//$query = 
    echo nl.nl;
$result=get('SELECT * from "NodeNames"');
//$result = pg_query($query) or pg_die('Query("'.$query.'") failed');
 
// Printing results in HTML
//echo "<table>\n";
while ($line = pg_fetch_array($result, null, PGSQL_ASSOC)) {
    //echo "\t<tr>\n";
echo $line["Name"]." ";
/*    foreach ($line as $key=>$col_value) {
        //echo "\t\t<td>$col_value</td>\n";
        //echo "$key=>$col_value\n";
        echo "$col_value ";
}
    echo "\n";
 */
    //echo "\t</tr>\n";
}
pg_free_result($result);
/*echo "</table>\n";
 */
// Free resultset

// Closing connection
pg_close($dbconn);
?>
