<?php
// Connecting, selecting database
$dbconn = pg_connect("host=localhost dbname=demlinks_db user=demlinks_user password=dml")
    or die('Could not connect: ' . pg_last_error());

function pg_die($msg)
{
        debug_print_backtrace();
        die($msg .": ". pg_last_error());
}

function go($query)
{
        @$res= pg_query($query) or pg_die('Query("'.$query.'") failed');
        pg_free_result($res);//exec this only if not failed
}

// Performing SQL query
go('DROP TABLE IF EXISTS "NodeNames" CASCADE');//case sensitivity is preserved with quotes
go('DROP TABLE IF EXISTS "Relations" CASCADE');//case sensitivity is preserved with quotes
/*$res= pg_query($query) or die('Query failed: ' . pg_last_error());
pg_free_result($res);
 */
go('CREATE TABLE "NodeNames" ( "ID" SERIAL PRIMARY KEY, "Name" CHARACTER VARYING(256) UNIQUE NOT NULL )');
go('CREATE TABLE "Relations" ( "ParentID" integer NOT NULL REFERENCES "NodeNames" ("ID") MATCH FULL ON DELETE CASCADE ON UPDATE CASCADE,
                             "ChildID" integer NOT NULL REFERENCES "NodeNames" ("ID") MATCH FULL ON DELETE CASCADE ON UPDATE CASCADE)');
/*$res= pg_query($query) or die('Query failed: ' . pg_last_error());
pg_free_result($res);*/
/*$query = 'SELECT * from NodeNames';
$result = pg_query($query) or die('Query failed: ' . pg_last_error());
 
// Printing results in HTML
echo "<table>\n";
while ($line = pg_fetch_array($result, null, PGSQL_ASSOC)) {
    echo "\t<tr>\n";
    foreach ($line as $col_value) {
        echo "\t\t<td>$col_value</td>\n";
    }
    echo "\t</tr>\n";
}
echo "</table>\n";
 */
// Free resultset

// Closing connection
pg_close($dbconn);
?>

