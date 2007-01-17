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
//thus since the functions are non-reentrant we won't have to worry about locks at this level <- actually this isn't quite true
//one function would be executed at a time, except when calling recursively those functions defined with func0re()

#include "shortdef.php"
#include "color.php"


/*
#define onentry_HOOK \
                echo "E";\
                DisallowGlobalReentry($GLOBAL_LOCKvar_for_dmlphpL1);

#define onexit_HOOK \
                echo 'X';\
                AllowGlobalReentry();
*/

#define func0(funcdef) \
        funcL0(funcdef/*, onentry_HOOK*/)

#define endfunc0(...) \
        endfuncL0(__VA_ARGS__/*, onexit_HOOK*/ )

#define endnow0(...) \
        endnowL0(__VA_ARGS__/*, onexit_HOOK*/)

#define func0re(funcdef) \
        funcL0re(funcdef)

#define endfunc0re(...) \
        endfuncL0re(__VA_ARGS__)


func0 (UniqAppendElemToList($elem,&$list))/*{{{*/
{
        _if (TRUE===is_array(&$list) && TRUE===in_array($elem, &$list, TRUE/*strict type check*/)) {
                addretflagL0(kAlready);
        } else { //attempting to append
                $list[]=$elem;//auto numbered index, appending to end
                addretflagL0(kAdded);
        }
}endfunc0(yes)/*}}}*/

func0 (RelaxedArrayCount(&$list, &$count))/*{{{*/
{
        _if (TRUE===is_array(&$list)) {
                $count=count(&$list);
        } else { //attempting to append
                $count=0;
        }
}endfunc0(yes)/*}}}*/

func0 (ArrayCount(&$list, &$count))/*{{{*/
{
        _tIFnot(is_array(&$list)); //catching some bug in the program
        _yntIFnot( $ar=RelaxedArrayCount(&$list, &$count) );
        keepflagsL0($ar);
}endfunc0()/*}}}*/

func0 (DelElemFromList($elem,&$list))/*{{{*/
{
        _if (TRUE===is_array(&$list) && $key=array_search($elem, &$list, TRUE) ) {
                        unset($list[$key]);
                        addretflagL0(kDeleted);
        } else {
                addretflagL0(kAlready);
        }
}endfunc0(yes)/*}}}*/

class dmlphpL0 {
        protected $AllElements;
        //if an element doesn't have a relation whatsoever then it doesn't exist ie. cannot exist and be null

        func0 (__construct())/*{{{*/
        {
                $this->AllElements=array();
                define(dParents,"Parents");
                define(dChildren,"Children");
//when accessing ie. kParentsOf[$elem] you must make sure that $elem is scalar! aka not array! or an error/warning php issues
#define kParentsOf $this->AllElements[dParents]
#define kChildrenOf $this->AllElements[dChildren]
        }endfunc0(ok)/*}}}*/

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

        func0 (TestElementInvariants(&$elem) )
        {
                _if (is_string($elem)){ //we allow empty string as a valid element id && ! empty($elem)) {
                        addretflagL0(yes);
                } else {
                        echo "TestElementInvariantsL0: var that failed test is \" ".retValue(&$elem)."\"".nl;
                        addretflagL0(no);
                }
        }endfunc0()

        func0 (__destruct())/*{{{*/
        {
                $this->AllElements=null;//i wonder if this destroys recursively; common sense tells me yes
        }endfunc0(ok)/*}}}*/

//TODO: we would add a next level to be able to add dup elements into a list; the list will hold transparent(to the level of dup elements) unique elements that point to the real elements, thus we got dup elements build on unique elements...

        protected func0 (addChild($parent,$child))/*{{{*/
        {//let me make smth str8: $parent and $child are ID names similar to pointer value of some pointer, not the actual data but the pointer to the data; these IDs are names/descriptions but they really are pointers; remember that the data is(are) rather irrelevant, the relations(/-ships) within the data are the relevant ones
                _yntIFnot($this->TestElementInvariants($parent));
                _yntIFnot($this->TestElementInvariants($child));
                _yntIFnot( $ar=UniqAppendElemToList($child, kChildrenOf[$parent]/* returnArray(dChildren)[$parent]*/ ) );
                keepflagsL0($ar);
        }endfunc0()/*}}}*/

        protected func0 (addParent($child,$parent))/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($parent));
                _yntIFnot($this->TestElementInvariants($child));
                _yntIFnot( $retlist=UniqAppendElemToList($parent, kParentsOf[$child]) );
                keepflagsL0($retlist);
        }endfunc0()/*}}}*/

        protected func0 (delChild($parent,$child))/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($parent));
                _yntIFnot($this->TestElementInvariants($child));
                _yntIFnot( $ar=DelElemFromList($child, kChildrenOf[$parent] ) );
                keepflagsL0($ar);
        }endfunc0()/*}}}*/

        protected func0 (delParentFromChild($parent,$child))/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($parent));
                _yntIFnot($this->TestElementInvariants($child));
                _yntIFnot( $ar=DelElemFromList($parent, kParentsOf[$child] ) );
                keepflagsL0($ar);
        }endfunc0()/*}}}*/

        func0 (ynIsNode($node))/*{{{*/
        {//let me remind you that a Node(be it parent of child) cannot exist unless it is a part of a relationship, ie. another node is somehow connected to it
                _yntIFnot($this->TestElementInvariants($node));
                _if( TRUE===is_array(kChildrenOf[$node]) || TRUE===is_array(kParentsOf[$node])) {
                        addretflagL0(yes);
                } else {
                        addretflagL0(no);
                }
        }endfunc0()/*}}}*/

        func0 (ynIsPCRel($parent,$child))/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($parent));
                _yntIFnot($this->TestElementInvariants($child));
                _if( TRUE===is_array(kChildrenOf[$parent]) && TRUE===in_array($child, kChildrenOf[$parent])) {
                        addretflagL0(yes);
                } else {
                        addretflagL0(no);
                }
        }endfunc0()/*}}}*/

        func0 (GetOfParent_AllChildren($parent,&$children))/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($parent));
                _ifnot (is_array($parent)) {
                        $children=kChildrenOf[$parent];
                        _if (is_array($children)) {
                                endnow0(yes);
                        }
                }
        }endfunc0(no)/*}}}*/

        func0 (DelAllChildrenOf($parent))/*{{{*/
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
        }endfunc0(yes)/*}}}*/

        func0 (GetOfChild_AllParents($child,&$parents))/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($child));
                _ifnot (is_array($child)) {
                        $parents=kParentsOf[$child];
                        _if (is_array($parents)) {
                                endnow0(yes);
                        }
                }
        }endfunc0(no)/*}}}*/

        func0 (DelAllParents($child))/*{{{*/
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
        }endfunc0(yes)/*}}}*/


}//endclass

#endif //header ends
// vim: fdm=marker
//?>
