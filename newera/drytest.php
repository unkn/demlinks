<?php
// Connecting, selecting database

function pg_die($msg)
{
        debug_print_backtrace();
        die($msg .": ". pg_last_error());
}

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

// Performing SQL query
go('DROP TABLE IF EXISTS "NodeNames" CASCADE');//case sensitivity is preserved with quotes
go('DROP TABLE IF EXISTS "Relations" CASCADE');//case sensitivity is preserved with quotes
/*$res= pg_query($query) or die('Query failed: ' . pg_last_error());
pg_free_result($res);
 */
go('CREATE TABLE "NodeNames" ( "ID" SERIAL PRIMARY KEY, "Name" CHARACTER VARYING(256) UNIQUE NOT NULL )');
go('CREATE TABLE "Relations" ( "ParentID" integer NOT NULL REFERENCES "NodeNames" ("ID") MATCH FULL ON DELETE CASCADE ON UPDATE CASCADE,
                             "ChildID" integer NOT NULL REFERENCES "NodeNames" ("ID") MATCH FULL ON DELETE CASCADE ON UPDATE CASCADE)');

$res=split("[ .,/\\\"\?\<\>&!;|\#\$\*\+\{\}=\(\)'`\n\-]",file_get_contents("dmlDBL0def.php"));
        foreach ($res as $val) {
                $val=trim($val);
                if (!empty($val)) {//ie. non-empty
                        echo $val." ";
                        $res=get('SELECT * from "NodeNames" WHERE "Name"=\''.$val.'\'');
                        $status=pg_fetch_array($res,null,PGSQL_ASSOC);
                        pg_free_result($res);
                        if (empty($status)) {
                                go('INSERT INTO "NodeNames" ( "Name" ) VALUES (\''.$val.'\')');
                        }
                }//fi
        }//foreach


/*$res= pg_query($query) or die('Query failed: ' . pg_last_error());
pg_free_result($res);*/
//$query = 
    echo "\n\n";
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

