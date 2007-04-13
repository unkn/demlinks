<?php

require_once("dmlDBL1.php");

        exceptifnot( $dmlDB=new dmlDBL1 );
        //debug_zval_dump($dmlDB);


        if ($dmlDB->fFirstTime) {
                report("First time run!");
        } else {
                report("...using prev. defined table");
        }
        $res=split("[ .,/\\\"\?\<\>&!;|\#\$\*\+\{\}=\(\)'`\n\-]",file_get_contents("dmlDBL0def.php"));
        exceptif(1===count($res));
        //exceptifnot( $dmlDB->OpenTransaction() );
        $aborted=false;
        $cnt=0;
        foreach ($res as $val) {
                     $val=trim($val);
                if (!empty($val)) {//ie. non-empty

                   try {

                        $ret=$dmlDB->AddName($val);
                        //echo "!".retValue($ret)."!";
                        /*if (in_array(no,$ret))  {
                                $dmlDB->AbortTransaction();
                                $aborted=true;
                                break;
                        }*/
                        exceptifnot($ret);

                        if (in_array(kAlready,$ret)) {
                                echo redcol;
                        } else {
                                echo greencol;
                        }
                        echo $val.nocol." ";//.nl;
                        usleep(100000);
                        $cnt++;//echo "cnt=".$cnt.nl;

                        //if ($cnt % 15 == 0) {
                                //_yntIFnot( $dmlDB->CloseTransaction() );
                        //}

                   }
                   catch(PDOException $e) {
                                //echo purplecol.$e->getmessage().nocol.nl;
                                //exceptifnot( $dmlDB->AbortTransaction());
                                $aborted=true;
                                break;
                   }
                   catch(Exception $e) {
                                //echo purplecol.$e->getmessage().nocol.nl;
                                //exceptifnot( $dmlDB->AbortTransaction());
                                $aborted=true;
                                break;
                   }
                } //fi
        }//foreach

        echo nocol.nl;
        if ($aborted) {
                //exceptifnot( $dmlDB->CloseTransaction() );
                report("aborted for some reason");
        }
        echo nocol.nl;

        exceptifnot( $dmlDB->IsName("if") );

        exceptifnot( $dmlDB->Show($into) );
        $arr=$into->fetchAll();
        $count=count($arr);
        report( "Before del: $count times.");

        exceptifnot($dmlDB->DelName("if") );
        $dmlDB->IsName("if");

        exceptifnot( $dmlDB->Show($into) );
        $arr=$into->fetchAll();
        $count=count($arr);
        report( "After del:  $count times.");

        $dmlDB=null;//ie. dispose()


?>
