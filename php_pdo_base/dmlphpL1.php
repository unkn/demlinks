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

#define func1(funcdef, debuglevels) \
        func0(funcdef, debuglevels)

#define endfunc1(...) \
        endfunc0(__VA_ARGS__)

#define endnow1(...) \
        endnow0(__VA_ARGS__)

#define func1re(funcdef, debuglevels) \
        func0re(funcdef, debuglevels)

#define endfunc1re(...) \
        endfunc0re(__VA_ARGS__)

class dmlphpL1 extends dmlphpL0 {

        func1 (__construct(), dconstr)/*{{{*/
        {
                __( $ar=parent::__construct() );
                keepflagsL0($ar);
        }endfunc1()/*}}}*/

        func1 (__destruct(), ddestr)/*{{{*/
        {
                __( $ar=parent::__destruct() );
                keepflagsL0($ar);
        }endfunc1()/*}}}*/

                //PC=parent, child (the parameters are in this order)
        func1 (EnsurePCRel($parent,$child), densure)/*{{{*/
        {//a relation will only exist once
                _yntIFnot($this->TestElementInvariants($parent));
                _yntIFnot($this->TestElementInvariants($child));
                //well, no transaction... too bad
                _yntIFnot( $ar=$this->addChild($parent, $child) );
                keepflagsL0($ar);
                _yntIFnot( $ar=$this->addParent($child, $parent) );
                keepflagsL0($ar);
        }endfunc1()/*}}}*/

        func1 (DelPCRel($parent,$child), dset)/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($parent));
                _yntIFnot($this->TestElementInvariants($child));
                //well, no transaction... too bad
                _yntIFnot( $ar=$this->delChild($parent, $child) );
                keepflagsL0($ar);
                _yntIFnot( $ar=$this->delParentFromChild($parent, $child) );
                keepflagsL0($ar);
        }endfunc1()/*}}}*/

        func1 (SetOfParent_Children($parent,$children), dset)/*{{{*/
        {//overwrites all children
                _yntIFnot($this->TestElementInvariants($parent));

                _yntIFnot( $this->DelAllChildrenOf($parent) );
                _yntIFnot( $ar=$this->AppendToParent_Children($parent, $children) );
                keepflagsL0($ar);
        }endfunc1()/*}}}*/

        func1 (AppendToParent_Children($parent,$children), dadd)/*{{{*/
        {//addition
                _yntIFnot($this->TestElementInvariants($parent));
                _ifnot( is_array($children) ) {
                        $children=array($children);
                        addretflagL0(kOneElement);
                }
                foreach ($children as $child) {
                        _yntIFnot( $ar=$this->EnsurePCRel($parent, $child) );
                        keepflagsL0($ar);
                        /*_if (isValue_InList(kAlready,$ar) ) {
                                addretflagL0();
                }*/
                }
        }endfunc1(yes)/*}}}*/

        func1 (DeleteFromParent_Children($parent,$children), dadd)/*{{{*/
        {//substraction
                _yntIFnot($this->TestElementInvariants($parent));
                _ifnot( is_array($children) ) {
                        $children=array($children);
                }
                foreach ($children as $child) {
                        _yntIFnot( $this->DelPCRel($parent, $child) );
                }
        }endfunc1(yes)/*}}}*/

        func1 (GetCountOfChildren_OfParent(&$count,$parent), dget)/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($parent));
                _yntIFnot( RelaxedArrayCount(&kChildrenOf[$parent], $count) );
        }endfunc1(yes)/*}}}*/

        func1 (GetCountOfParents_OfChild(&$count,$child), dget)/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($child));
                _yntIFnot( RelaxedArrayCount(&kParentsOf[$child], $count) );
        }endfunc1(yes)/*}}}*/

        func1re (ShowTreeOfChildrenForParent($parent, $startlevel=0), dshow)/*{{{*/
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
        }endfunc1re(yes)/*}}}*/

        func1re (ShowTreeOfParentsForChild($child, $startlevel=0), dshow)/*{{{*/
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
        }endfunc1re(yes)/*}}}*/



}//endclass

#endif //header ends
// vim: fdm=marker
//?>
