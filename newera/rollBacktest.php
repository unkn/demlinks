<?php
require_once("color.php");

 $db = new PDO('sqlite:demlinks6.3sql',''/*user*/,''/*pwd*/);

$crea='CREATE TABLE \'NodeNames\' ("NodeID" INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, "NodeName" VARCHAR(256) UNIQUE NOT NULL);';
//$crea2='CREATE TABLE \'Relations\' ("ParentNodeID" INTEGER PRIMARY KEY , "ChildNodeID" INTEGER SECONDARY KEY);';

 $db->exec($crea);
// $db->exec($crea2);

                $getter="zTest";
                //--------- get Name by ID
                $pgn = $db->prepare('SELECT * FROM \'NodeNames\' WHERE "NodeName" = :node13');
                $pgn->bindParam(":node13", $getter, PDO::PARAM_STR);


function read($now) {
        global $getter;
        $getter=$now;
        global $pgn;
        $pgn->execute();//execute above SELECT
        $ar=$pgn->FetchAll();//get array of results
        //print_r($ar);
        if (empty($ar)) {
                return false;
        }
        return true;
}
//read("zTest");
//it prolly doesn't exist so let's add it:

//$writter="zTest";
$pnn=$db->prepare('INSERT INTO \'NodeNames\' ("NodeName") VALUES (:node14)');
$pnn->bindParam(":node14", $getter, PDO::PARAM_STR);

function write($now) {
        global $getter;
        $getter=$now;
        global $pnn;
        $pnn->execute();//write it!
}

/*
        $res=split("[ .,/\\\"\?\<\>&!;|\#\$\*\+\{\}=\(\)'`\n\-]",file_get_contents("dmlDBL0def.php"));
$db->beginTransaction();
        foreach ($res as $val) {
                usleep(10000);
                $val=trim($val);
                if (!empty($val)) {//ie. non-empty
                        if (false===read($val)) {
                                echo greencol;
                                write($val);
                        } else {
                                echo redcol;
                        }
                        echo $val." ";
                }//if
        }//foreach
*/

$db->beginTransaction();
echo read("zTest");
write("zTest");
/*echo read("zTest");
write("zTest");
echo read("zTest");*/
echo "waiting...";
                usleep(2000000);
//echo read("zTest");
//write("zTest");

 //$db->
echo "\n";
 $db->rollBack();//this doesn't do it's job
 $db->beginTransaction();//here it fails, when running this program twice at the same time; 'There is already an active transaction'

?>
