//<?php
//dmlDBL0functions
//header starts
#ifndef DMLDBL0FUN_PHP
#define DMLDBL0FUN_PHP

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
* Description: demlinks level 0 (lowest level)
*
***************************************************************************}}}*/


#include "shortdef.php"
#include "debugL0.php"
#include "dmlDBL0def.php"
#include "color.php"


class dmlDBL0
{
        protected $qNodeNames,$qRelations,$qNodeName,$qParentNodeID,$qChildNodeID,$qNodeID;//q from quote
        protected static $fDBHandle=null;
        public $fFirstTime;//created table

        private $fParamNodeID;

        private $sqlGetNodeName;
        private $fPrepGetNodeName;

        private $sqlDelID;
        private $fPrepDelID;

        /* quote functions {{{*/
        function fieldquote($whatfield)
        {
                //since we're in sqlite we're gonna quote the field with "" and the value with ''
                return '"'.$whatfield.'"';
        }

        function valquote($whatval)
        {
                return $this->fDBHandle->quote($whatval);
        }

        function tablequote($whattable)
        {
                return $this->valquote($whattable);
        }/*}}}*/

        funcL0 (__construct(), dconstr)/*{{{*/
        {
                // create a SQLite3 database file with PDO and return a database handle (Object Oriented)
                __( $this->fDBHandle = new PDO('sqlite:'.dbasename,''/*user*/,''/*pwd*/,
                                array(PDO::ATTR_PERSISTENT => true)) );//singleton?

                $this->qNodeNames = $this->tablequote(dNodeNames);
                $this->qRelations = $this->tablequote(dRelations);
                $this->qNodeName = $this->fieldquote(dNodeName);
                $this->qParentNodeID = $this->fieldquote(dParentNodeID);
                $this->qChildNodeID = $this->fieldquote(dChildNodeID);
                $this->qNodeID = $this->fieldquote(dNodeID);

                __( $rret=$this->CreateDB() );

                _ynif( $this->CreateDB() ) {
                        $this->fFirstTime=yes;
                }else{
                        $this->fFirstTime=no;
                }

                //--------- get Name by ID
                $this->sqlGetNodeName = 'SELECT * FROM '.$this->qNodeNames.' WHERE '.$this->qNodeID.' = '.paramNodeID;
                _yntIFnot( $this->fPrepGetNodeName = $this->fDBHandle->prepare($this->sqlGetNodeName) );
                _yntIFnot( $this->fPrepGetNodeName->bindParam(paramNodeID, $this->fParamNodeID, PDO::PARAM_STR) ); //, PDO::PARAM_INT);
                //--------- del by ID
                $this->sqlDelID = 'DELETE FROM '.$this->qNodeNames.' WHERE '.$this->qNodeID.' = '.paramNodeID;
                _yntIFnot( $this->fPrepDelID = $this->fDBHandle->prepare($this->sqlDelID) );
                _yntIFnot( $this->fPrepDelID->bindParam(paramNodeID, $this->fParamNodeID, PDO::PARAM_STR) );
                //---------
        }endfuncL0(yes)/*}}}*/

        funcL0 (__destruct(), ddestr)/*{{{*/
        {
                $fDBHandle=null;
        }endfuncL0(yes)/*}}}*/

        funcL0 (CreateDB(),dcrea)/*{{{*/
        {

                $sqlNodeNames = 'CREATE TABLE '.$this->qNodeNames.
                            ' ('.$this->qNodeID.' INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, '.$this->qNodeName.' VARCHAR(256) UNIQUE NOT NULL)';
                $sqlNodeNamesIndex12 = 'CREATE INDEX indexname12 ON '.$this->qNodeNames.'('.$this->qNodeID./*",".$this->qNodeName.*/")";
                $sqlNodeNamesIndex21 = 'CREATE INDEX indexname21 ON '.$this->qNodeNames.'('.$this->qNodeName./*",".$this->qNodeID.*/")";

                $sqlRelations = 'CREATE TABLE '.$this->qRelations.
                            ' ('.$this->qParentNodeID.' INTEGER PRIMARY KEY , '.$this->qChildNodeID.' INTEGER SECONDARY KEY)';
                _yntIFnot( $this->OpenTransaction());

                _ynif ( $res= $this->fDBHandle->exec($sqlNodeNames) ) {
                        _yntIFnot( $this->fDBHandle->exec($sqlNodeNamesIndex12) );
                        _yntIFnot( $this->fDBHandle->exec($sqlNodeNamesIndex21) );
                        addretflagL0(kCreatedDBNodeNames);
                        $wecommit=yes;
                }
                _ynif( $res = $this->fDBHandle->exec($sqlRelations) ) {
                        addretflagL0(kCreatedDBRelations);
                        $wecommit=yes;
                }

                _ynif ($wecommit) { //at least one dbase was created, the other one could already exist perhaps.
                        _yntIFnot( $this->CloseTransaction() );
                        addretflagL0(ok);
                }else{
                        _yntIFnot( $this->AbortTransaction() );
                        addretflagL0(bad);
                }
        } endfuncL0()/*}}}*/

//------------------------ transactions/*{{{*/
        funcL0 (OpenTransaction(), dbegtr) //only one active transaction at a time; PDO limitation?!/*{{{*/
        {
                _yntIFnot( $this->fDBHandle->beginTransaction() );
        }endfuncL0(ok)/*}}}*/

        funcL0 (CloseTransaction(),dendtr)/*{{{*/
        {
                _yntIFnot( $this->fDBHandle->commit() );
        }endfuncL0(ok)/*}}}*/

        funcL0 (AbortTransaction(), dabtr)/*{{{*/
        {
                _yntIFnot( $this->fDBHandle->rollBack() );
        }endfuncL0(ok)/*}}}*/
//------------------------/*}}}*/


        funcL0 (IsID($id), dis)/*{{{*/
        {
                _yntIF(ynIsNotGood($id));
                __( $exists=$this->GetName($name,$id) );
                _yntIF(yes===ynIsGood($exists) && yes===ynIsNotGood($name) );
                //print_r($exists);
        }endfuncL0($exists)/*}}}*/

        funcL0 (GetName(&$name,$id),dget)// returns Name by ID /*{{{*/
        {
                _yntIFnot(ynIsGood($id));
                $this->fParamNodeID = $id;
                _yntIFnot( $this->fPrepGetNodeName->execute() );
                __( $ar=$this->fPrepGetNodeName->FetchAll() );
                $name=(string)$ar[dNodeName];
                if (empty($ar) || empty($name)) {
                        addretflagL0(no);
                }
        }endfuncL0()/*}}}*/


        funcL0 (DelID($id), ddel)/*{{{*/
        {
                _yntIF(ynIsNotGood($id));
                $this->fParamNodeID = $id;
                _yntIFnot( $this->fPrepDelID->execute() );
        }endfuncL0(ok)/*}}}*/

        funcL0 (Show(&$result),dshow)//temp/*{{{*/
        {
                $sqlGetView = 'SELECT * FROM '.$this->qNodeNames;
                _yntIFnot( $result=$this->fDBHandle->query($sqlGetView) );
        }endfuncL0(ok)/*}}}*/
//------------------------
        funcL0 (SetRelation($parentName, $childName))/*{{{*/
        {
        }endfuncL0(ok)/*}}}*/
//------------------------
} //class

#endif //header ends
// vim: fdm=marker
//?>
