<?php
//dmlDBL0functions

/*LICENSE*GNU*GPL************************************************************{{{
*
*                             dmental links
*    Copyright (C) 2006 AtKaaZ, AtKaaZ at users.sourceforge.net
*
*  ========================================================================
*
*    This program is free software; you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation; either version 2 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program; if not, write to the Free Software
*    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*
*  ========================================================================
*
* Description: demlinks sqlite dbase level 0 (lowest level)
*
***************************************************************************}}}*/

require_once("shortdef.php");
require_once("debug.php");
require_once("dmldbdef.php");
require_once("color.php");

function goq($query)
{
        $res=getq($query);
        pg_free_result($res);//this gets executed only if the above did not fail
}

function getq($query) //executes a query in current open db connection
{
        @$res= pg_query($query) or pg_die('Query("'.$query.'") failed');
        return $res;
}


class dmlDBL0
{
        protected static $fDBHandle=null;

        function TestElementInvariants(&$elem)/*{{{*/
        {
                //$ret=array(kReturnStateList_type);
                initret($ret);
                if (is_string($elem) && !empty($elem)) {//we DO NOT allow empty string as a valid element id && !empty($elem)
                        ensureexists($ret,yes);
                } else {
                        report("DB, TestElementInvariants: var that failed test(~string&notEmpty) is \" ".retValue($elem)."\"");
                        ensureexists($ret,no);
                }
                return $ret;
        }/*}}}*/

        function __construct()/*{{{*/
        {
                $this->fDBHandle = pg_connect("host=".dbhost." dbname=".dbname." user=".dbuser." password=".dbpwd)
                            or pg_die('Could not connect');
                pg_trace(dbtracefile);

                pg_prepare($this->fDBHandle,dGetName,'SELECT "Name" from "NodeNames" WHERE "ID"=$1');// is it necessary to pg_free_result() the result of this function?
                pg_prepare($this->fDBHandle,dDelID,'SELECT DelID($1)');
                pg_prepare($this->fDBHandle,dShow,'SELECT * from "NodeNames"');// is it necessary to pg_free_result() the result of this function?
                $result=pg_query($this->fDBHandle,"SET SESSION CHARACTERISTICS AS TRANSACTION ISOLATION LEVEL READ COMMITTED READ WRITE");
                exceptifnot($result);

                //---------
        }/*}}}*/

        function __destruct()/*{{{*/
        {
                $fDBHandle=null;
        }/*}}}*/

        function emptyTables()/*{{{*/
        {
                initret($ret);

                goq('DROP TABLE IF EXISTS "NodeNames" CASCADE');

                goq('DROP TABLE IF EXISTS "Relations" CASCADE');

                goq('CREATE TABLE "NodeNames" ( "ID" SERIAL PRIMARY KEY, "Name" CHARACTER VARYING(256) UNIQUE NOT NULL )');
                goq('CREATE TABLE "Relations" (
        "ParentID" integer NOT NULL REFERENCES "NodeNames" ("ID") MATCH FULL
                ON DELETE CASCADE ON UPDATE CASCADE,
        "ChildID" integer NOT NULL REFERENCES "NodeNames" ("ID") MATCH FULL
                ON DELETE CASCADE ON UPDATE CASCADE
)');

                ensureexists($ret,ok);
                return $ret;
        } /*}}}*/

//------------------------ transactions/*{{{*/
        function OpenTransaction() //only one active transaction at a time; PDO limitation?!/*{{{*/
        {
                initret($ret);
                goq("BEGIN TRANSACTION ISOLATION LEVEL READ COMMITTED READ WRITE");
                //exceptifnot( $this->fDBHandle->beginTransaction() );
                ensureexists($ret,ok);
                return $ret;
        }/*}}}*/

        function CloseTransaction()/*{{{*/
        {
                initret($ret);
                goq("COMMIT TRANSACTION");
                //exceptifnot( $this->fDBHandle->commit() );
                ensureexists($ret,ok);
                return $ret;
        }/*}}}*/

        function AbortTransaction()/*{{{*/
        {
                initret($ret);
                /*$rr=$this->fDBHandle->rollBack();
                if( failed( $rr )) {
                        ensureexists($ret,bad);
                } else {
                        ensureexists($ret,ok);
                }*/
                goq("ROLLBACK TRANSACTION");
                ensureexists($ret,ok);
                return $ret;
        }/*}}}*/
//------------------------/*}}}*/


        function IsID($id)/*{{{*/
        {
                exceptifnot($this->TestElementInvariants($id));
                $ret=$this->GetName($name,$id);//could throw
                exceptif(in_array(yes,$exists) && in_array(no,$this->TestElementInvariants($name)) );
                return $ret;
        }/*}}}*/

        function GetName(&$name,$id)// returns Name by ID /*{{{*/
        {
                initret($ret);
                exceptifnot($this->TestElementInvariants($id));
                //$this->fParamNodeID = $id;

                $result=pg_execute($this->fDBHandle,dGetName,array($id));
                exceptifnot($result);

                $line = pg_fetch_array($result, null, PGSQL_ASSOC);
                //exceptifnot($line);//shouldn't use this!
                        $line2 = pg_fetch_array($result, null, PGSQL_ASSOC);
                        exceptif($line2);//can't have more than one row !
                $name=$line[dName];

                //exceptifnot( $this->fPrepGetNodeName->execute() );
                /*$ar=$this->fPrepGetNodeName->FetchAll();//can throw
                $name=(string)$ar[dNodeName];*/
                if (empty($line) || empty($name)) {
                        ensureexists($ret,no);
                        //addretflagL1(no);
                } else {
                        ensureexists($ret,yes);
                        //addretflagL1(yes);
                }
                exceptifnot(pg_free_result($result));
                return $ret;
        }/*}}}*/


        function DelID($id)/*{{{*/
        {
                initret($ret);
                exceptifnot($this->TestElementInvariants($id));

                $result=pg_execute($this->fDBHandle,dDelID,array($id));
                exceptifnot($result);
                exceptifnot(pg_free_result($result));
                /*$this->fParamNodeID = $id;
                exceptifnot( $this->fPrepDelID->execute() );*/
                //FIXME:
                ensureexists($ret,ok);
                return $ret;
        }/*}}}*/

        function Show(&$result)//temp/*{{{*/
        {
                initret($ret);
                //$sqlGetView = 'SELECT * FROM '.$this->qNodeNames;
                //exceptifnot( $result=$this->fDBHandle->query($sqlGetView) );
                //FIXME:
                $result=pg_execute($this->fDBHandle,dShow,array());
                if (failed($result)) {
                        ensureexists($ret,no);
                } else {
                        ensureexists($ret,ok);
                }
                return $ret;
        }/*}}}*/
//------------------------
} //class


// vim: fdm=marker
//?>
