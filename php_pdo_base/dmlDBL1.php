//<?php
//dmlL1functions
//header starts
#ifndef DMLDBL1FUN_PHP
#define DMLDBL1FUN_PHP

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
* Description: demlinks level 1 (based on level 0)
*
***************************************************************************}}}*/


#include "shortdef.php"
//#include "debugL1.php"
//#include "dmlDBL0def.php"
#include "dmlDBL0.php"
#include "color.php"


class dmlDBL1 extends dmlDBL0
{
        private $fParamNodeName;
        private $sqlGetNodeID;
        private $fPrepGetNodeID;

        private $sqlNewNode;
        private $fPrepNewNode;//prepared statement handler

        funcL1 (__construct,(), dconstr)/*{{{*/
        {
                __( parent::__construct() );
                //--------- get ID by Name
                $this->sqlGetNodeID = 'SELECT '.$this->qNodeID.' FROM '.$this->qNodeNames.' WHERE '.$this->qNodeName.' = '.paramNodeName;
                _yntIFnot( $this->fPrepGetNodeID = $this->fDBHandle->prepare($this->sqlGetNodeID) );
                _yntIFnot( $this->fPrepGetNodeID->bindParam(paramNodeName, $this->fParamNodeName, PDO::PARAM_STR) );
                //---------
                $this->sqlNewNode = 'INSERT INTO '.$this->qNodeNames.' ('.$this->qNodeName.') VALUES ('.paramNodeName.')';//table name needs to be quoted

                _yntIFnot( $this->fPrepNewNode = $this->fDBHandle->prepare($this->sqlNewNode) );//can't prepare unless the table already exists!
                _yntIFnot( $this->fPrepNewNode->bindParam(paramNodeName, $this->fParamNodeName, PDO::PARAM_STR) ); //, PDO::PARAM_INT);
                //---------

        }endfuncL1(yes)/*}}}*/

        funcL1 (__destruct,(), ddestr)/*{{{*/
        {
                __( parent::__destruct() );
        }endfuncL1(yes)/*}}}*/

        funcL1 (AddName,($nodename),dadd)/*{{{*/
        {
                _artIFnot( $this->TestElementInvariants($nodename) );//must not be empty or so; if it is then maybe's a bug outside this funcL1 provided user shall never call this funcL1 with an empty param value
                _arifnot ($this->GetID($id,$nodename)) {
                        deb(ddbadd,"attempting physical addition: ".$nodename);
                        $this->fParamNodeName=$nodename;
                        _yntIFnot( $this->fPrepNewNode->execute() );//error here? it probably already exists! error in GetID maybe
                        deb(ddbadd,greencol."succeded".nocol." physical addition: ".$nodename);
                        addretflagL1(kAdded);
                } else {
                        addretflagL1(kAlready);
                }//fielse
        }endfuncL1(ok)/*}}}*/

        funcL1 (GetID,(&$id,$nodename),dget)// returns ID by Name /*{{{*/
        {
                _artIFnot( $this->TestElementInvariants($nodename) );
                $this->fParamNodeName = $nodename;
                _ynif ( $this->fPrepGetNodeID->execute() ) {
                        _ynif( $ar=$this->fPrepGetNodeID->FetchAll() ) {
                                $id=(string)$ar[0][dNodeID];
                                addretflagL1(yes);
                        } else {
                                addretflagL1(no,kEmpty);
                        }
                } else {
                        addretflagL1(no);
                }
        }endfuncL1()/*}}}*/

        funcL1 (IsName,($nodename), dis)/*{{{*/
        {
                _artIFnot( $this->TestElementInvariants($nodename) );
                _arif( $this->GetID($id,$nodename) ) {
                        _artIFnot( $this->TestElementInvariants($id) );
                        endnowL1(yes);
                }
        }endfuncL1(no)/*}}}*/

        funcL1 (DelName,($nodename), ddel)/*{{{*/
        {
                _artIFnot( $this->TestElementInvariants($nodename) );
                _arif ($this->GetID($id,$nodename)) {
                        _artIFnot( $this->DelID($id) );
                }
        }endfuncL1(ok)/*}}}*/

} //class

#endif //header ends
// vim: fdm=marker
//?>
