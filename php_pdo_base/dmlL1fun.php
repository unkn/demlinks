//<?php
//dmlL1functions
//header starts
#ifndef DMLL1FUN_PHP
#define DMLL1FUN_PHP

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
#include "debugL0.php"
//#include "dmlL0def.php"
#include "dmlL0fun.php"
#include "color.php"


class dmlL1 extends dmlL0
{
        private $fParamNodeName;
        private $sqlGetNodeID;
        private $fPrepGetNodeID;

        private $sqlNewNode;
        private $fPrepNewNode;//prepared statement handler

        func (__construct(), dconstr)/*{{{*/
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

        }endfunc(yes)/*}}}*/

        func (__destruct(), ddestr)/*{{{*/
        {
                __( parent::__destruct() );
        }endfunc(yes)/*}}}*/

        func (AddName($nodename),dadd)/*{{{*/
        {
                _yntIFnot( isGood($nodename) );//must not be empty or so; if it is then maybe's a bug outside this func provided user shall never call this func with an empty param value
                _ynifnot ($this->GetID($id,$nodename)) {
                        deb(ddbadd,"attempting physical addition: ".$nodename);
                        $this->fParamNodeName=$nodename;
                        _yntIFnot( $this->fPrepNewNode->execute() );//error here? it probably already exists! error in GetID maybe
                        deb(ddbadd,greencol."succeded".nocol." physical addition: ".$nodename);
                        retflag(kAdded);
                } else {
                        retflag(kAlready);
                }//fielse
        }endfunc(ok)/*}}}*/

        func (GetID(&$id,$nodename),dget)// returns ID by Name /*{{{*/
        {
                _yntIFnot($nodename);//_yntIFnot() uses isGood($nodename) to evaluate the params instead of plain 'if'
                $this->fParamNodeName = $nodename;
                _ynif ( $this->fPrepGetNodeID->execute() ) {
                        _ynif( $ar=$this->fPrepGetNodeID->FetchAll() ) {
                                $id=(string)$ar[0][dNodeID];
                                retflag(yes);
                        } else {
                                retflag(no,kEmpty);
                        }
                } else {
                        //$id='';
                        retflag(no);
                }
        }endfunc()/*}}}*/

        func (IsName($nodename), dis)/*{{{*/
        {
                _yntIFnot($nodename);
                _ynif( $this->GetID($id,$nodename) ) {
                        _yntIF( isNotGood($id) );
                        endnow(yes);
                }
        }endfunc(no)/*}}}*/

        func (DelName($nodename), ddel)/*{{{*/
        {
                _yntIF(isNotGood($nodename));
                _ynif ($this->GetID($id,$nodename)) {
                        _yntIFnot( $this->DelID($id) );
                }
        }endfunc(ok)/*}}}*/

} //class

#endif //header ends
// vim: fdm=marker
//?>
