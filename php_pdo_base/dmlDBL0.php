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
#include "debugL1.php"
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

        funcL1 (TestElementInvariants,(&$elem) ,dtest)
        {
                _if (is_string($elem)) {//we allow empty string as a valid element id && !empty($elem) ) {
                        addretflagL1(yes);
                } else {
                        debnl(dtestcrit, "TestElementInvariants: var that failed test is \" ".retValue($elem)."\"");
                        addretflagL1(no);
                }
        }endfuncL1()

        funcL1 (__construct,(), dconstr)/*{{{*/
        {
                // create a SQLite3 database file with PDO and return a database handle (Object Oriented)
                _yntIFnot( $this->fDBHandle = new PDO('sqlite:'.dbasename,''/*user*/,''/*pwd*/,
                                array(PDO::ATTR_PERSISTENT => true)) );//singleton?

                $this->qNodeNames = $this->tablequote(dNodeNames);
                $this->qRelations = $this->tablequote(dRelations);
                $this->qNodeName = $this->fieldquote(dNodeName);
                $this->qParentNodeID = $this->fieldquote(dParentNodeID);
                $this->qChildNodeID = $this->fieldquote(dChildNodeID);
                $this->qNodeID = $this->fieldquote(dNodeID);

                _arif( $this->CreateDB() ) {
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
        }endfuncL1(yes)/*the return is for the endfunc internal test that requeires either yes or no on return*/ /*}}}*/

        funcL1 (__destruct,(), ddestr)/*{{{*/
        {
                $fDBHandle=null;
        }endfuncL1(yes)/*}}}*/

        funcL1 (CreateDB,(),dcrea)/*{{{*/
        {

                $sqlNodeNames = 'CREATE TABLE '.$this->qNodeNames.
                            ' ('.$this->qNodeID.' INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, '.$this->qNodeName.' VARCHAR(256) UNIQUE NOT NULL)';
                $sqlNodeNamesIndex12 = 'CREATE INDEX indexname12 ON '.$this->qNodeNames.'('.$this->qNodeID./*",".$this->qNodeName.*/")";
                $sqlNodeNamesIndex21 = 'CREATE INDEX indexname21 ON '.$this->qNodeNames.'('.$this->qNodeName./*",".$this->qNodeID.*/")";

                $sqlRelations = 'CREATE TABLE '.$this->qRelations.
                            ' ('.$this->qParentNodeID.' INTEGER PRIMARY KEY , '.$this->qChildNodeID.' INTEGER SECONDARY KEY)';
                _artIFnot( $this->OpenTransaction());

                _ynif ( $res= $this->fDBHandle->exec($sqlNodeNames) ) {
                        _yntIFnot( $this->fDBHandle->exec($sqlNodeNamesIndex12) );
                        _yntIFnot( $this->fDBHandle->exec($sqlNodeNamesIndex21) );
                        addretflagL1(kCreatedDBNodeNames);
                        $wecommit=yes;
                }
                _ynif( $res = $this->fDBHandle->exec($sqlRelations) ) {
                        addretflagL1(kCreatedDBRelations);
                        $wecommit=yes;
                }

                _if (yes===$wecommit) { //at least one dbase was created, the other one could already exist perhaps.
                        _artIFnot( $this->CloseTransaction() );
                        addretflagL1(ok);//ok and yes point to the same "yes"
                }else{
                        _artIFnot( $this->AbortTransaction() );
                        addretflagL1(bad);//bad~no
                }
        } endfuncL1()/*}}}*/

//------------------------ transactions/*{{{*/
        funcL1 (OpenTransaction,(), dbegtr) //only one active transaction at a time; PDO limitation?!/*{{{*/
        {
                _yntIFnot( $this->fDBHandle->beginTransaction() );
        }endfuncL1(ok)/*}}}*/

        funcL1 (CloseTransaction,(),dendtr)/*{{{*/
        {
                _yntIFnot( $this->fDBHandle->commit() );
        }endfuncL1(ok)/*}}}*/

        funcL1 (AbortTransaction,(), dabtr)/*{{{*/
        {
                _yntIFnot( $this->fDBHandle->rollBack() );
        }endfuncL1(ok)/*}}}*/
//------------------------/*}}}*/


        funcL1 (IsID,($id), dis)/*{{{*/
        {
                _artIFnot($this->TestElementInvariants($id));
                __( $exists=$this->GetName($name,$id) );
                _tIF(isL1YesReturn($exists) && isL1NoReturn($this->TestElementInvariants($name)) );
        }endfuncL1($exists)/*}}}*/

        funcL1 (GetName,(&$name,$id),dget)// returns Name by ID /*{{{*/
        {
                _artIFnot($this->TestElementInvariants($id));
                $this->fParamNodeID = $id;
                _yntIFnot( $this->fPrepGetNodeName->execute() );
                __( $ar=$this->fPrepGetNodeName->FetchAll() );
                $name=(string)$ar[dNodeName];
                if (empty($ar) || empty($name)) {
                        addretflagL1(no);
                } else {
                        addretflagL1(yes);
                }
        }endfuncL1()/*}}}*/


        funcL1 (DelID,($id), ddel)/*{{{*/
        {
                _artIFnot($this->TestElementInvariants($id));
                $this->fParamNodeID = $id;
                _yntIFnot( $this->fPrepDelID->execute() );
        }endfuncL1(ok)/*}}}*/

        funcL1 (Show,(&$result),dshow)//temp/*{{{*/
        {
                $sqlGetView = 'SELECT * FROM '.$this->qNodeNames;
                _yntIFnot( $result=$this->fDBHandle->query($sqlGetView) );
        }endfuncL1(ok)/*}}}*/
//------------------------
} //class

#endif //header ends
// vim: fdm=marker
//?>
