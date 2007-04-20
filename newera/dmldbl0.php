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

function getq($query)
{
        @$res= pg_query($query) or pg_die('Query("'.$query.'") failed');
        return $res;
}


class dmlDBL0
{
        protected static $fDBHandle=null;

        function TestElementInvariants(&$elem)
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
        }

        function __construct()/*{{{*/
        {
                $this->fDBHandle = pg_connect("host=".dbhost." dbname=".dbname." user=".dbuser." password=".dbpwd)
                            or pg_die('Could not connect');
                pg_trace(dbtracefile);

                // create a SQLite3 database file with PDO and return a database handle (Object Oriented)
                //$this->fDBHandle = new PDO('sqlite:'.dbasename,''/*user*/,''/*pwd*/,
                  //      array(PDO::ATTR_PERSISTENT => true/*singleton?*/, PDO::ATTR_AUTOCOMMIT => false/*, PDO::ATTR_ERRMODE => PDO::ERRMODE_SILENT seems to have no effect */));
                //$this->fDBHandle = new PDO('pgsql:host=localhost port=5432 dbname=demlinks_db','demlinks_user'/*user*/,'dml'/*pwd*/,
                  //      array(PDO::ATTR_PERSISTENT => true/*singleton?*/, PDO::ATTR_AUTOCOMMIT => false/*, PDO::ATTR_ERRMODE => PDO::ERRMODE_SILENT seems to have no effect */));
                //$this->fDBHandle->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_SILENT);
                //if (failed($this->fDBHandle)) {
                  //      except("failed to init db handle");
                //}

                $ar=$this->CreateDB();//the return is no in both of the following cases: tables exist | something failed(ie. syntax)

                //--------- get Name by ID
                $this->sqlGetNodeName = 'SELECT * FROM '.$this->qNodeNames.' WHERE '.$this->qNodeID.' = '.paramNodeID;
                exceptifnot( $this->fPrepGetNodeName = $this->fDBHandle->prepare($this->sqlGetNodeName) );
                exceptifnot( $this->fPrepGetNodeName->bindParam(paramNodeID, $this->fParamNodeID, PDO::PARAM_STR) ); //, PDO::PARAM_INT);
                //--------- del by ID
                $this->sqlDelID = 'DELETE FROM '.$this->qNodeNames.' WHERE '.$this->qNodeID.' = '.paramNodeID;
                exceptifnot( $this->fPrepDelID = $this->fDBHandle->prepare($this->sqlDelID) );
                exceptifnot( $this->fPrepDelID->bindParam(paramNodeID, $this->fParamNodeID, PDO::PARAM_STR) );
                //---------
        }/*the return is for the endfunc internal test that requeires either yes or no on return*/ /*}}}*/

        function __destruct()/*{{{*/
        {
                $fDBHandle=null;
        }/*}}}*/

        function CreateDB()/*{{{*/
        {
                initret($ret);

                $sqlNodeNames = 'CREATE TABLE '.$this->qNodeNames.
                            ' ('.$this->qNodeID.' INTEGER PRIMARY KEY UNIQUE, '.$this->qNodeName.' character varying (256) UNIQUE NOT NULL)';
                //$sqlNodeNames = 'CREATE TABLE '.$this->qNodeNames.
                  //          ' ('.$this->qNodeID.' INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, '.$this->qNodeName.' VARCHAR(256) UNIQUE NOT NULL)';
                $sqlNodeNamesIndex12 = 'CREATE INDEX indexname12 ON '.$this->qNodeNames.' ('.$this->qNodeID./*",".$this->qNodeName.*/")";
                $sqlNodeNamesIndex21 = 'CREATE INDEX indexname21 ON '.$this->qNodeNames.' ('.$this->qNodeName./*",".$this->qNodeID.*/")";

                //$sqlRelations = 'CREATE TABLE '.$this->qRelations.
                 //           ' ('.$this->qParentNodeID.' INTEGER PRIMARY KEY , '.$this->qChildNodeID.' INTEGER SECONDARY KEY)';
                $sqlRelations = 'CREATE TABLE '.$this->qRelations.
                            ' ('.$this->qParentNodeID.' INTEGER PRIMARY KEY , '.$this->qChildNodeID.' INTEGER )';
                //echo $sqlRelations;
                exceptifnot( $this->OpenTransaction() );

                $wecommit=false;
                $res= $this->fDBHandle->exec($sqlNodeNames);
                if (!failed($res) ) {
                        exceptifnot( $this->fDBHandle->exec($sqlNodeNamesIndex12) );
                        exceptifnot( $this->fDBHandle->exec($sqlNodeNamesIndex21) );
                        ensureexists($ret,kCreatedDBNodeNames);
                        $wecommit=true;
                }
                $res = $this->fDBHandle->exec($sqlRelations);
                if (!failed($res) ) {
                        ensureexists($ret,kCreatedDBRelations);
                        $wecommit=true;
                }

                if ($wecommit) { //at least one dbase was created, the other one could already exist perhaps.
                        exceptifnot( $this->CloseTransaction() );
                        ensureexists($ret,ok);//ok and yes point to the same "yes"
                }else{
                        exceptifnot( $this->AbortTransaction() );
                        ensureexists($ret,bad);//bad~no
                }
                return $ret;
        } /*}}}*/

//------------------------ transactions/*{{{*/
        function OpenTransaction() //only one active transaction at a time; PDO limitation?!/*{{{*/
        {
                initret($ret);
                exceptifnot( $this->fDBHandle->beginTransaction() );
                ensureexists($ret,ok);
                return $ret;
        }/*}}}*/

        function CloseTransaction()/*{{{*/
        {
                initret($ret);
                exceptifnot( $this->fDBHandle->commit() );
                ensureexists($ret,ok);
                return $ret;
        }/*}}}*/

        function AbortTransaction()/*{{{*/
        {
                initret($ret);
                $rr=$this->fDBHandle->rollBack();
                if( failed( $rr )) {
                        ensureexists($ret,bad);
                } else {
                        ensureexists($ret,ok);
                }
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
                $this->fParamNodeID = $id;
                exceptifnot( $this->fPrepGetNodeName->execute() );
                $ar=$this->fPrepGetNodeName->FetchAll();//can throw
                $name=(string)$ar[dNodeName];
                if (empty($ar) || empty($name)) {
                        ensureexists($ret,no);
                        //addretflagL1(no);
                } else {
                        ensureexists($ret,yes);
                        //addretflagL1(yes);
                }
                return $ret;
        }/*}}}*/


        function DelID($id)/*{{{*/
        {
                initret($ret);
                exceptifnot($this->TestElementInvariants($id));
                $this->fParamNodeID = $id;
                exceptifnot( $this->fPrepDelID->execute() );
                ensureexists($ret,ok);
                return $ret;
        }/*}}}*/

        function Show(&$result)//temp/*{{{*/
        {
                initret($ret);
                $sqlGetView = 'SELECT * FROM '.$this->qNodeNames;
                exceptifnot( $result=$this->fDBHandle->query($sqlGetView) );
                ensureexists($ret,ok);
                return $ret;
        }/*}}}*/
//------------------------
} //class


// vim: fdm=marker
//?>
