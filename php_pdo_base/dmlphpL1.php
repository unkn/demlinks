//<?php
//header starts
#ifndef DMLPHPL1_PHP
#define DMLPHPL1_PHP

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
*               this is Level 1, the next higer level
*
***************************************************************************}}}*/


#include "shortdef.php"
#include "debug.php"
#include "color.php"
#include "dmlphpL0.php"

class dmlphpL1 extends dmlphpL0 {

        func (__construct(), dconstr)/*{{{*/
        {
                __( $ar=parent::__construct() );
                keepflags($ar);
        }endfunc()/*}}}*/

        func (__destruct(), ddestr)/*{{{*/
        {
                __( $ar=parent::__destruct() );
                keepflags($ar);
        }endfunc()/*}}}*/

        func (AddRel($parent,$child), dadd)/*{{{*/
        {//a relation will only exist once
                _tIFnot($this->TestElementInvariants($parent));
                _tIFnot($this->TestElementInvariants($child));
                //well, no transaction... too bad
                _tIFnot( $ar=$this->addChild($parent, $child) );
                keepflags($ar);
                _tIFnot( $ar=$this->addParent($child, $parent) );
                keepflags($ar);
        }endfunc()/*}}}*/

        func (DelRel($parent,$child), dset)/*{{{*/
        {
                _tIFnot($this->TestElementInvariants($parent));
                _tIFnot($this->TestElementInvariants($child));
                /*_ifnot ($this->IsRel($parent,$child) ) {
                        endnow(yes, kAlready);
                }*/
                //well, no transaction... too bad
                _tIFnot( $ar=$this->delChild($parent, $child) );
                keepflags($ar);
                _tIFnot( $ar=$this->delParentFromChild($parent, $child) );
                keepflags($ar);
        }endfunc()/*}}}*/

        func (IsRel($parent,$child), dis)/*{{{*/
        {
                _tIFnot($this->TestElementInvariants($parent));
                _tIFnot($this->TestElementInvariants($child));
                _if( TRUE===is_array(kChildrenOf[$parent]) && TRUE===in_array($child, kChildrenOf[$parent])) {
                        retflag(yes);
                } else {
                        retflag(no);
                }
        }endfunc()/*}}}*/

        func (SetOfParent_Children($parent,$children), dset)/*{{{*/
        {//overwrites all children
                _tIFnot($this->TestElementInvariants($parent));

                _tIFnot( $this->DelAllChildrenOf($parent) );
                _tIFnot( $this->AppendToParent_Children($parent, $children) );
        }endfunc(yes)/*}}}*/

        func (AppendToParent_Children($parent,$children), dadd)/*{{{*/
        {//addition
                _tIFnot($this->TestElementInvariants($parent));
                _ifnot( is_array($children) ) {
                        $children=array($children);
                }
                foreach ($children as $child) {
                        _tIFnot( $this->AddRel($parent, $child) );
                }
        }endfunc(yes)/*}}}*/

        func (DeleteFromParent_Children($parent,$children), dadd)/*{{{*/
        {//substraction
                _tIFnot($this->TestElementInvariants($parent));
                _ifnot( is_array($children) ) {
                        $children=array($children);
                }
                foreach ($children as $child) {
                        _tIFnot( $this->DelRel($parent, $child) );
                }
        }endfunc(yes)/*}}}*/

        func (GetCountOfChildren_OfParent(&$count,$parent), dget)/*{{{*/
        {
                _tIFnot($this->TestElementInvariants($parent));
                _tIFnot( ArrayCount(&kChildrenOf[$parent], $count) );
        }endfunc(yes)/*}}}*/

        func (GetCountOfParents_OfChild(&$count,$child), dget)/*{{{*/
        {
                _tIFnot($this->TestElementInvariants($child));
                _tIFnot( ArrayCount(&kParentsOf[$child], $count) );
        }endfunc(yes)/*}}}*/

        func (ShowTreeOfChildrenForParent($parent, $startlevel=0), dshow)/*{{{*/
        {
                _tIFnot($this->TestElementInvariants($parent));
                for ($i=0; $i<$startlevel; $i++) {
                        echo " ";
                }
                if ($startlevel>0) {
                        echo "|>";
                } else {
                        echo "Children of:";
                }
                echo " \"$parent\"".nl;

                _if ( $this->GetOfParent_AllChildren($parent,$children) ) {
                        foreach ($children as $val) {
                                _tIFnot( $this->ShowTreeOfChildrenForParent($val, 1+$startlevel) );
                        }
                }
        }endfunc(yes)/*}}}*/

        func (ShowTreeOfParentsForChild($child, $startlevel=0), dshow)/*{{{*/
        {
                _tIFnot($this->TestElementInvariants($child));
                for ($i=0; $i<$startlevel; $i++) {
                        echo " ";
                }
                if ($startlevel>0) {
                        echo "|<";
                } else {
                        echo "Parents of:";
                }
                echo " \"$child\"".nl;

                _if ( $this->GetOfChild_AllParents($child,$parents) ) {
                        foreach ($parents as $val) {
                                _tIFnot( $this->ShowTreeOfParentsForChild($val, 1+$startlevel) );
                        }
                }
        }endfunc(yes)/*}}}*/



}//endclass

#endif //header ends
// vim: fdm=marker
//?>
