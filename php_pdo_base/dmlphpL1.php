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
#include "debugL0.php"
#include "color.php"
#include "dmlphpL0.php"

class dmlphpL1 extends dmlphpL0 {

        funcL0 (__construct(), dconstr)/*{{{*/
        {
                __( $ar=parent::__construct() );
                keepflagsL0($ar);
        }endfuncL0()/*}}}*/

        funcL0 (__destruct(), ddestr)/*{{{*/
        {
                __( $ar=parent::__destruct() );
                keepflagsL0($ar);
        }endfuncL0()/*}}}*/

        funcL0 (AddRel($parent,$child), dadd)/*{{{*/
        {//a relation will only exist once
                _yntIFnot($this->TestElementInvariants($parent));
                _yntIFnot($this->TestElementInvariants($child));
                //well, no transaction... too bad
                _yntIFnot( $ar=$this->addChild($parent, $child) );
                keepflagsL0($ar);
                _yntIFnot( $ar=$this->addParent($child, $parent) );
                keepflagsL0($ar);
        }endfuncL0()/*}}}*/

        funcL0 (DelRel($parent,$child), dset)/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($parent));
                _yntIFnot($this->TestElementInvariants($child));
                //well, no transaction... too bad
                _yntIFnot( $ar=$this->delChild($parent, $child) );
                keepflagsL0($ar);
                _yntIFnot( $ar=$this->delParentFromChild($parent, $child) );
                keepflagsL0($ar);
        }endfuncL0()/*}}}*/

        funcL0 (IsRel($parent,$child), dis)/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($parent));
                _yntIFnot($this->TestElementInvariants($child));
                _if( TRUE===is_array(kChildrenOf[$parent]) && TRUE===in_array($child, kChildrenOf[$parent])) {
                        addretflagL0(yes);
                } else {
                        addretflagL0(no);
                }
        }endfuncL0()/*}}}*/

        funcL0 (SetOfParent_Children($parent,$children), dset)/*{{{*/
        {//overwrites all children
                _yntIFnot($this->TestElementInvariants($parent));

                _yntIFnot( $this->DelAllChildrenOf($parent) );
                _yntIFnot( $this->AppendToParent_Children($parent, $children) );
        }endfuncL0(yes)/*}}}*/

        funcL0 (AppendToParent_Children($parent,$children), dadd)/*{{{*/
        {//addition
                _yntIFnot($this->TestElementInvariants($parent));
                _ifnot( is_array($children) ) {
                        $children=array($children);
                        addretflagL0(kOneElement);
                }
                foreach ($children as $child) {
                        _yntIFnot( $this->AddRel($parent, $child) );
                }
        }endfuncL0(yes)/*}}}*/

        funcL0 (DeleteFromParent_Children($parent,$children), dadd)/*{{{*/
        {//substraction
                _yntIFnot($this->TestElementInvariants($parent));
                _ifnot( is_array($children) ) {
                        $children=array($children);
                }
                foreach ($children as $child) {
                        _yntIFnot( $this->DelRel($parent, $child) );
                }
        }endfuncL0(yes)/*}}}*/

        funcL0 (GetCountOfChildren_OfParent(&$count,$parent), dget)/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($parent));
                _yntIFnot( RelaxedArrayCount(&kChildrenOf[$parent], $count) );
        }endfuncL0(yes)/*}}}*/

        funcL0 (GetCountOfParents_OfChild(&$count,$child), dget)/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($child));
                _yntIFnot( RelaxedArrayCount(&kParentsOf[$child], $count) );
        }endfuncL0(yes)/*}}}*/

        funcL0 (ShowTreeOfChildrenForParent($parent, $startlevel=0), dshow)/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($parent));
                for ($i=0; $i<$startlevel; $i++) {
                        echo " ";
                }
                if ($startlevel>0) {
                        echo "|>";
                } else {
                        echo "Children of:";
                }
                echo " \"$parent\"".nl;

                _ynif ( $this->GetOfParent_AllChildren($parent,$children) ) {
                        foreach ($children as $val) {
                                _yntIFnot( $this->ShowTreeOfChildrenForParent($val, 1+$startlevel) );
                        }
                }
        }endfuncL0(yes)/*}}}*/

        funcL0 (ShowTreeOfParentsForChild($child, $startlevel=0), dshow)/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($child));
                for ($i=0; $i<$startlevel; $i++) {
                        echo " ";
                }
                if ($startlevel>0) {
                        echo "|<";
                } else {
                        echo "Parents of:";
                }
                echo " \"$child\"".nl;

                _ynif ( $this->GetOfChild_AllParents($child,$parents) ) {
                        foreach ($parents as $val) {
                                _yntIFnot( $this->ShowTreeOfParentsForChild($val, 1+$startlevel) );
                        }
                }
        }endfuncL0(yes)/*}}}*/



}//endclass

#endif //header ends
// vim: fdm=marker
//?>
