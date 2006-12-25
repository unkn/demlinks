//<?php
//dmlL0functions
//header starts
#ifndef DMLL0FUN_PHP
#define DMLL0FUN_PHP

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
#include "debug.php"
#include "dmlL0def.php"
#include "color.php"


class dmlL0
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

        func (__construct(), dconstr)/*{{{*/
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

                _if( $this->CreateDB() ) {
                        $this->fFirstTime=yes;
                }else{
                        $this->fFirstTime=no;
                }

                //--------- get Name by ID
                $this->sqlGetNodeName = 'SELECT * FROM '.$this->qNodeNames.' WHERE '.$this->qNodeID.' = '.paramNodeID;
                _tIFnot( $this->fPrepGetNodeName = $this->fDBHandle->prepare($this->sqlGetNodeName) );
                _tIFnot( $this->fPrepGetNodeName->bindParam(paramNodeID, $this->fParamNodeID, PDO::PARAM_STR) ); //, PDO::PARAM_INT);
                //--------- del by ID
                $this->sqlDelID = 'DELETE FROM '.$this->qNodeNames.' WHERE '.$this->qNodeID.' = '.paramNodeID;
                _tIFnot( $this->fPrepDelID = $this->fDBHandle->prepare($this->sqlDelID) );
                _tIFnot( $this->fPrepDelID->bindParam(paramNodeID, $this->fParamNodeID, PDO::PARAM_STR) );
                //---------
        }endfunc(yes)/*}}}*/

        func (__destruct(), ddestr)/*{{{*/
        {
                $fDBHandle=null;
        }endfunc(yes)/*}}}*/

        func (CreateDB(),dcrea)/*{{{*/
        {

                $sqlNodeNames = 'CREATE TABLE '.$this->qNodeNames.
                            ' ('.$this->qNodeID.' INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, '.$this->qNodeName.' VARCHAR(256) UNIQUE NOT NULL)';
                $sqlNodeNamesIndex12 = 'CREATE INDEX indexname12 ON '.$this->qNodeNames.'('.$this->qNodeID./*",".$this->qNodeName.*/")";
                $sqlNodeNamesIndex21 = 'CREATE INDEX indexname21 ON '.$this->qNodeNames.'('.$this->qNodeName./*",".$this->qNodeID.*/")";

                $sqlRelations = 'CREATE TABLE '.$this->qRelations.
                            ' ('.$this->qParentNodeID.' INTEGER PRIMARY KEY , '.$this->qChildNodeID.' INTEGER SECONDARY KEY)';
                _tIFnot( $this->OpenTransaction());

                _if ( $res= $this->fDBHandle->exec($sqlNodeNames) ) {
                        _tIFnot( $this->fDBHandle->exec($sqlNodeNamesIndex12) );
                        _tIFnot( $this->fDBHandle->exec($sqlNodeNamesIndex21) );
                        retflag(kCreatedDBNodeNames);
                        $wecommit=yes;
                }
                _if( $res = $this->fDBHandle->exec($sqlRelations) ) {
                        retflag(kCreatedDBRelations);
                        $wecommit=yes;
                }

                _if ($wecommit) { //at least one dbase was created, the other one could already exist perhaps.
                        _tIFnot( $this->CloseTransaction() );
                        retflag(ok);
                }else{
                        _tIFnot( $this->AbortTransaction() );
                        retflag(bad);
                }
        } endfunc()/*}}}*/

//------------------------ transactions/*{{{*/
        func (OpenTransaction(), dbegtr) //only one active transaction at a time; PDO limitation?!/*{{{*/
        {
                _tIFnot( $this->fDBHandle->beginTransaction() );
        }endfunc(ok)/*}}}*/

        func (CloseTransaction(),dendtr)/*{{{*/
        {
                _tIFnot( $this->fDBHandle->commit() );
        }endfunc(ok)/*}}}*/

        func (AbortTransaction(), dabtr)/*{{{*/
        {
                _tIFnot( $this->fDBHandle->rollBack() );
        }endfunc(ok)/*}}}*/
//------------------------/*}}}*/


        func (IsID($id), dis)/*{{{*/
        {
                _tIF(isNotGood($id));
                __( $exists=$this->GetName($name,$id) );
                _tIF(yes===isGood($exists) && yes===isNotGood($name) );
                //print_r($exists);
        }endfunc($exists)/*}}}*/

        func (GetName(&$name,$id),dget)// returns Name by ID /*{{{*/
        {
                _tIFnot(isGood($id));
                $this->fParamNodeID = $id;
                _tIFnot( $this->fPrepGetNodeName->execute() );
                __( $ar=$this->fPrepGetNodeName->FetchAll() );
                $name=(string)$ar[dNodeName];
                if (empty($ar) || empty($name)) {
                        retflag(no);
                }
        }endfunc()/*}}}*/


        func (DelID($id), ddel)/*{{{*/
        {
                _tIF(isNotGood($id));
                $this->fParamNodeID = $id;
                _tIFnot( $this->fPrepDelID->execute() );
        }endfunc(ok)/*}}}*/

        func (Show(&$result),dshow)//temp/*{{{*/
        {
                $sqlGetView = 'SELECT * FROM '.$this->qNodeNames;
                _tIFnot( $result=$this->fDBHandle->query($sqlGetView) );
        }endfunc(ok)/*}}}*/
//------------------------
        func (SetRelation($parentName, $childName))/*{{{*/
        {
        }endfunc(ok)/*}}}*/
//------------------------
} //class

#endif //header ends
// vim: fdm=marker
//?>
