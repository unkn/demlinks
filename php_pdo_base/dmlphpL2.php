//<?php
//header starts
#ifndef DMLPHPL2_PHP
#define DMLPHPL2_PHP

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
* Description: demlinks applied in coding(php) ie. using demlinks idea(s) to code in php
*               this is Level 2, the next higer level
*
***************************************************************************}}}*/


#include "shortdef.php"
#include "debugL0.php"
#include "color.php"
#include "dmlphpL1.php"


define('kParent','kParent');
define('kChild','kChild');

class dmlphpL2 extends dmlphpL1 {

        func1 (__construct())/*{{{*/
        {
                __( $ar=parent::__construct() );
                keepflags1($ar);
        }endfunc1()/*}}}*/

        func1 (__destruct())/*{{{*/
        {
                __( $ar=parent::__destruct() );
                keepflags1($ar);
        }endfunc1()/*}}}*/

                //PC=parent, child (the parameters are in this order)

        func1 (GetCursor_ofType_ofID(&$curs, $type, $domainID))
        {
                _yntIFnot( $this->ynTestTypeInvariants($type) );
                _yntIFnot( $this->ynTestElementInvariants($domainID) );
                __( $curs=new dmlphpL2_DomainCursor($this, $type, $domainID) );
        }endfunc1(yes)

        func1 (ynTestTypeInvariants(&$type))
        {
                if (($type===kParent) || ($type === kChild)) {
                        addretflag1(yes);
                } else {
                        show("php, TestTypeInvariants failed for: $type");
                        addretflag1(no);
                }
        }endfunc1()

/*        protected $stuff;
        function a() {
                $this->stuff++;
                $this->b();
        }
        function b() {
                show($this->stuff);
        }*/

}//endclass

class dmlphpL2_DomainPointer extends dmlphpL1_Pointer {
//can be either NULL or point to an element from Domain
        protected $fDomainID;
        func1 (__construct($dmlenv,$type,$domainid, $pointee/*=nil*/))/*{{{*/
        {
                //get normal pointer
                __( $ar=parent::__construct($dmlenv,$type, $pointee) );

                //add a domain
                _yntIFnot( $this->fDMLEnv->ynTestElementInvariants($domainid) );
                $this->fDomainID=$domainid;

                keepflags1($ar);
        }endfunc1()/*}}}*/

        func1 (__destruct())/*{{{*/
        {
                __( $ar=parent::__destruct() );
                keepflags1($ar);
        }endfunc1()/*}}}*/

}

class dmlphpL2_DomainCursor {//there's no non-domain cursor!
        protected $fDMLEnv;
        protected $fCursorType;
        protected $fDomainID;
        protected $fDPointer;

        func1 (__construct($dmlphp, $type, $domainID))/*{{{*/
        {
                //$dmlphp->a();
                $this->fDMLEnv=$dmlphp;//objects seem to be passed by reference! there are no copy constructors!
                //$this->fDMLEnv->a();
                _tIFnot(is_object($this->fDMLEnv));
                show($this->fDMLEnv);
                _yntIFnot( $this->fDMLEnv->ynTestTypeInvariants($type) );
                _yntIFnot( $this->fDMLEnv->ynTestElementInvariants($domainID) );
                _yntIFnot( $this->fDMLEnv->ynIsNode($domainID) );
                $this->fCursorType=$type;
                $this->fDomainID=$domainID;
                __( $fDPointer=new dmlphpL2_DomainPointer($this->fDMLEnv, $this->fCursorType, $this->fDomainID, nil) );
        }endfunc1(yes)/*}}}*/

        func1 (__destruct())/*{{{*/
        {
                __( $this->fDPointer=null );
        }endfunc1(yes)/*}}}*/

        func1 (GetEnvironment(&$env))/*{{{*/
        {
                $env=$this->fDMLEnv;//passed by reference! object
        }endfunc1(yes)/*}}}*/

}//endclass2

#endif //header ends
// vim: fdm=marker
//?>
