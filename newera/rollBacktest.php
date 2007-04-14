<?php
//to test this program u must run it twice at the same time in ie. two terminals
//this program suposedly creates a new dbase with table name NodeNames and one field in it named NodeName
//it then begins a transaction then attempts to read an element 'zTest' of field 'NodeName' which obv. doesn't exist, ignoring the returned errors
//then it writes it(since it wasn't there)
//then decides to rollBack the transaction and eventually try a new one
//because rollBack doesn't really work(apparently) for some unknown reason, beginTransaction fails saying 'There is already an active transaction'
$db = new PDO('sqlite:demlinks6.3sql',''/*user*/,''/*pwd*/);

$db->exec('CREATE TABLE \'NodeNames\' ("NodeName" VARCHAR(10));');

$db->beginTransaction();

$getter="zTest";
$pgn = $db->prepare('SELECT * FROM \'NodeNames\' WHERE "NodeName" = :node13');
$pgn->bindParam(":node13", $getter, PDO::PARAM_STR);
//read
$pgn->execute();//execute above SELECT
$ar=$pgn->FetchAll();//get array of results

$writter="zTest";
$pnn=$db->prepare('INSERT INTO \'NodeNames\' ("NodeName") VALUES (:node14)');
$pnn->bindParam(":node14", $writter, PDO::PARAM_STR);
//write
$pnn->execute();//write it!

echo "waiting...";
usleep(2000000);
echo "done\n";
$db->rollBack();//this doesn't do it's job
$db->beginTransaction();//here it fails, when running this program twice at the same time; 'There is already an active transaction'
//unreachable:
$db->commit();
?>
