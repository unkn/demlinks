<?php

require_once("dmldbl1.php");

        exceptifnot( $dmldb=new dmldbl1 );
        //debug_zval_dump($dmldb);


        $res=split("[ .,/\\\"\?\<\>&!;|\#\$\*\+\{\}=\(\)'`\n\-]",file_get_contents("dmldbdef.php"));
        exceptif(1===count($res));
        $tries=10;
        while ($tries > 0) {
        exceptifnot( $dmldb->OpenTransaction() );
        $aborted=false;
        $cnt=0;
        foreach ($res as $val) {
                     $val=trim($val);
                if (!empty($val)) {//ie. non-empty

                   //try {

                        $ret=$dmldb->EnsureName($val);
                        //echo "!".retValue($ret)."!";
                        /*if (in_array(no,$ret))  {
                                $dmldb->AbortTransaction();
                                $aborted=true;
                                break;
                        }*/
                        if (failed($ret)) {
                                $aborted=true;
                                break;
                        }
                        //exceptifnot($ret);

                        if (in_array(kAlready,$ret)) {
                                echo redcol;
                        } else {
                                echo greencol;
                        }
                        echo $val.nocol." ";//.nl;
                        usleep(10000);
                        $cnt++;//echo "cnt=".$cnt.nl;

                        //if ($cnt % 15 == 0) {
                                //_yntIFnot( $dmldb->CloseTransaction() );
                        //}

                   /*}
                   catch(PDOException $e) {
                                //echo purplecol.$e->getmessage().nocol.nl;
                                //exceptifnot( $dmldb->AbortTransaction());
                                $aborted=true;
                                break;
                   }
                   catch(Exception $e) {
                                //echo purplecol.$e->getmessage().nocol.nl;
                                //exceptifnot( $dmldb->AbortTransaction());
                                $aborted=true;
                                break;
                   }*/
                } //fi
        }//foreach

        echo nocol.nl;
        if ($aborted) {
                report("aborted for some reason");
                $dmldb->AbortTransaction();//this fails
                usleep(1000000);
                --$tries;
                report("going for another try ! tries left:$tries");
        } else {
                exceptifnot( $dmldb->CloseTransaction() );
                break;//while tries
        }
        echo nocol.nl;
        }//while

        show( $dmldb->IsName("if") );

        exceptifnot( $dmldb->Show($into) );
        $line = pg_fetch_array($into, null, PGSQL_ASSOC);
        //print_r($line);
        //$count=count($line);
        $count=pg_num_rows($into);
        exceptifnot(pg_free_result($into));//must free this or will core dump
        report( "Before del: $count times.");

        exceptifnot($dmldb->DelName("if") );
        show($dmldb->IsName("if"));//fails

        exceptifnot( $dmldb->Show($into) );
        //$line = pg_fetch_array($into, null, PGSQL_ASSOC);
        //$count=count($line);
        $count=pg_num_rows($into);
        exceptifnot(pg_free_result($into));//must free this or will core dump
        report( "After del:  $count times.");

        //$dmldb=null;//ie. dispose()


?>
