//<?php
//header starts
#ifndef DMLPHPL0_PHP
#define DMLPHPL0_PHP

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
* Description: demlinks applied in coding(php) ie. using demlinks to code
*               this is Level 0 aka lowest level
*
***************************************************************************}}}*/

//using arrays to hold demlinks
//this means they die at end of program AND they can only be used within this program, and I suppose php cannot spawn threads from this program, thus usage would be serial, non parallel; however we'd implement some defines that will make sure each function is non-reentrant, otherwise throws(why? because reentrying would be unexpected behaviour)

#include "shortdef.php"
#include "color.php"

funcL0 (UniqAppendElemToList($elem,&$list), dadd)/*{{{*/
{
        _if (TRUE===is_array(&$list) && TRUE===in_array($elem, &$list, TRUE/*strict type check*/)) {
                addretflagL0(kAlready);
        } else { //attempting to append
                $list[]=$elem;//auto numbered index, appending to end
                addretflagL0(kAdded);
        }
}endfuncL0(yes)/*}}}*/

funcL0 (RelaxedArrayCount(&$list, &$count), dadd)/*{{{*/
{
        _if (TRUE===is_array(&$list)) {
                $count=count(&$list);
        } else { //attempting to append
                $count=0;
        }
}endfuncL0(yes)/*}}}*/

funcL0 (ArrayCount(&$list, &$count), dadd)/*{{{*/
{
        _tIFnot(is_array(&$list)); //catching some bug in the program
        _yntIFnot( $ar=RelaxedArrayCount(&$list, &$count) );
        keepflagsL0($ar);
}endfuncL0()/*}}}*/

funcL0 (DelElemFromList($elem,&$list), dadd)/*{{{*/
{
        _if (TRUE===is_array(&$list) && $key=array_search($elem, &$list, TRUE) ) {
                        unset($list[$key]);
                        addretflagL0(kDeleted);
        } else {
                addretflagL0(kAlready);
        }
}endfuncL0(yes)/*}}}*/

class dmlphpL0 {
        protected $AllElements;
        //if an element doesn't have a relation whatsoever then it doesn't exist ie. cannot exist and be null

        funcL0 (__construct(), dconstr)/*{{{*/
        {
                $this->AllElements=array();
                define(dParents,"Parents");
                define(dChildren,"Children");
//when accessing ie. kParentsOf[$elem] you must make sure that $elem is scalar! aka not array! or an error/warning php issues
#define kParentsOf $this->AllElements[dParents]
#define kChildrenOf $this->AllElements[dChildren]
        }endfuncL0(ok)/*}}}*/

/*yeah doesn't work        function &returnArray($type)
        {
                if (dParents === $type) {
                        return $this->AllElements[dParents];
                } else {
                        if (dChildren === $type) {
                                return $this->AllElements[dChildren];
                        }
                }
                _yntIF("must choose one of dParents, dChildren; you chose: !".$type."!");
        }*/

        funcL0 (TestElementInvariants(&$elem) ,dtest)
        {
                _if (is_string(&$elem) ) {
                        addretflagL0(yes);
                } else {
                        debnl(dtestcrit, "TestElementInvariants: var that failed test is \" ".retValue(&$elem)."\"");
                        addretflagL0(no);
                }
        }endfuncL0()

        funcL0 (__destruct(), ddestr)/*{{{*/
        {
                $this->AllElements=null;//i wonder if this destroys recursively; common sense tells me yes
        }endfuncL0(ok)/*}}}*/

//TODO: we would add a next level to be able to add dup elements into a list; the list will hold transparent(to the level of dup elements) unique elements that point to the real elements, thus we got dup elements build on unique elements...

        protected funcL0 (addChild($parent,$child), dadd)/*{{{*/
        {//let me make smth str8: $parent and $child are ID names similar to pointer value of some pointer, not the actual data but the pointer to the data; these IDs are names/descriptions but they really are pointers; remember that the data is(are) rather irrelevant, the relations(/-ships) within the data are the relevant ones
                _yntIFnot($this->TestElementInvariants($parent));
                _yntIFnot($this->TestElementInvariants($child));
                _yntIFnot( $ar=UniqAppendElemToList($child, kChildrenOf[$parent]/* returnArray(dChildren)[$parent]*/ ) );
                keepflagsL0($ar);
        }endfuncL0()/*}}}*/

        protected funcL0 (addParent($child,$parent), dadd)/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($parent));
                _yntIFnot($this->TestElementInvariants($child));
                _yntIFnot( $retlist=UniqAppendElemToList($parent, kParentsOf[$child]) );
                keepflagsL0($retlist);
        }endfuncL0()/*}}}*/

        protected funcL0 (delChild($parent,$child), ddel)/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($parent));
                _yntIFnot($this->TestElementInvariants($child));
                _yntIFnot( $ar=DelElemFromList($child, kChildrenOf[$parent] ) );
                keepflagsL0($ar);
        }endfuncL0()/*}}}*/

        protected funcL0 (delParentFromChild($parent,$child), ddel)/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($parent));
                _yntIFnot($this->TestElementInvariants($child));
                _yntIFnot( $ar=DelElemFromList($parent, kParentsOf[$child] ) );
                keepflagsL0($ar);
        }endfuncL0()/*}}}*/

        funcL0 (ynIsNode($node), dis)/*{{{*/
        {//let me remind you that a Node(be it parent of child) cannot exist unless it is a part of a relationship, ie. another node is somehow connected to it
                _yntIFnot($this->TestElementInvariants($node));
                _if( TRUE===is_array(kChildrenOf[$node]) || TRUE===is_array(kParentsOf[$node])) {
                        addretflagL0(yes);
                } else {
                        addretflagL0(no);
                }
        }endfuncL0()/*}}}*/

        funcL0 (ynIsPCRel($parent,$child), dis)/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($parent));
                _yntIFnot($this->TestElementInvariants($child));
                _if( TRUE===is_array(kChildrenOf[$parent]) && TRUE===in_array($child, kChildrenOf[$parent])) {
                        addretflagL0(yes);
                } else {
                        addretflagL0(no);
                }
        }endfuncL0()/*}}}*/

        funcL0 (GetOfParent_AllChildren($parent,&$children), dget)/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($parent));
                _ifnot (is_array($parent)) {
                        $children=kChildrenOf[$parent];
                        _if (is_array($children)) {
                                endnowL0(yes);
                        }
                }
        }endfuncL0(no)/*}}}*/

        funcL0 (DelAllChildrenOf($parent), ddel)/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($parent));
                $children=&kChildrenOf[$parent];// get all children of the $parent
                _if (is_array($children)) {
                        foreach ($children as $child) {
                                // del all $parent from these $children
                                _yntIFnot( $this->delParentFromChild($parent, $child) );
                        }
                        $children=null;//empty the array of children of the $parent
                }
        }endfuncL0(yes)/*}}}*/

        funcL0 (GetOfChild_AllParents($child,&$parents), dget)/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($child));
                _ifnot (is_array($child)) {
                        $parents=kParentsOf[$child];
                        _if (is_array($parents)) {
                                endnowL0(yes);
                        }
                }
        }endfuncL0(no)/*}}}*/

        funcL0 (DelAllParents($child), ddel)/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($child));
                $parents=&kParentsOf[$child];
                _if (is_array($parents)) {
                        foreach ($parents as $parent) {
                                // del all $parent from these $children
                                _yntIFnot( $this->delChild($parent, $child) );
                        }
                        $parents=null;//empty the array of children of the $parent
                }
        }endfuncL0(yes)/*}}}*/


}//endclass

#endif //header ends
// vim: fdm=marker
//?>
