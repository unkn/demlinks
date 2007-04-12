<?php

require_once("dmlDBL1.php");

        exceptifnot( $dmlDB=new dmlDBL1 );
        //debug_zval_dump($dmlDB);


        if ($dmlDB->fFirstTime) {
                report("First time run!");
        } else {
                report("...using prev. defined table");
        }


?>
