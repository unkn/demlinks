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

#define addretflag0(...) \
        addretflagL0(__VA_ARGS__)

#define keepflags0(...) \
        keepflagsL0(__VA_ARGS__)

func0 (UniqAppendElemToList($elem,&$list))/*{{{*/
{
        _if (TRUE===isNElist(&$list) && TRUE===in_array($elem, &$list, TRUE/*strict type check*/)) {
                addretflag0(kAlready);
        } else { //attempting to append
                _tIF(isset($list) && !is_array(&$list));//can't be set and non-array
                $list[]=$elem;//auto numbered index, appending to end
                addretflag0(kAdded);
        }
}endfunc0(yes)/*}}}*/

func0 (RelaxedArrayCount(&$list, &$count))/*{{{*/
{
        _if (TRUE===isNElist(&$list)) {
                $count=count(&$list);
        } else { //attempting to append
                $count=0;
        }
}endfunc0(yes)/*}}}*/

func0 (ArrayCount(&$list, &$count))/*{{{*/
{
        _tIFnot(is_array(&$list)); //catching some bug in the program
        _yntIFnot( $ar=RelaxedArrayCount(&$list, &$count) );
        keepflags0($ar);
}endfunc0()/*}}}*/

func0 (DelElemFromList($elem,&$list))/*{{{*/
{
        _if (TRUE===isNElist(&$list) && $key=array_search($elem, &$list, TRUE) ) {
                        unset($list[$key]);
                        addretflag0(kDeleted);
        } else {
                addretflag0(kAlready);
        }
}endfunc0(yes)/*}}}*/

function isNElist(&$list) //returns true if it's a list and it's non-empty
{
        return (isset($list) && is_array($list) && !empty($list));
}

class dmlphpL0 {
        protected $AllElements;
        //if an element doesn't have a relation whatsoever then it doesn't exist ie. cannot exist and be null

        func0 (__construct())/*{{{*/
        {
                $this->AllElements=array();
                define('dParents',"Parents");
                define('dChildren',"Children");
//when accessing ie. dParentsOf($elem) you must make sure that $elem is scalar! aka not array! or an error/warning php issues
//ie. dParents, 'A'
#define dGetAll(_PorC,_ofnode) $this->AllElements[_PorC][_ofnode]
//#define dGetAll(_PorC,_ofnode) ( isset(dlowlevGetAll(_PorC,_ofnode)) ? dlowlevGetAll(_PorC,_ofnode) : null )
#define dParentsOf(_who) dGetAll(dParents,_who)
#define dChildrenOf(_who) dGetAll(dChildren,_who)
        }endfunc0(ok)/*}}}*/

        private func0 (E_NOTICE_hide($PorC, $node))//ie. ,dParents, 'A'
        {
                #define _ez dGetAll($PorC,$node)
                if (!isset(_ez)) {
                        _ez=array();
                        addretflag0(kWasUnset);
                }
                _tIFnot(is_array(_ez));//unlikely
                if (empty(_ez)) {
                        addretflag0(kEmpty);
                }
                #undef _ez
        }endfunc0(ok)
        /*protected func0 (GetList_OfPorC_OfNode(&$list, $PorC, $node))//ie. ,dParents, 'A'
        {
                #define _ez dGetAll($PorC,$node)
                if (!isset(_ez)) {
                        _ez=array();
                }
                _tIFnot(is_array(_ez));//unlikely
                $list=&_ez;//can be empty
                #undef _ez
                if (empty($list)) {
                        addretflag0(kEmpty);
                }
        }endfunc0(ok)

        protected func0( Get_ParentsList_OfNode(&$list, $who))//list of parents of element node $who
        {
                _yntIFnot( $ar=$this->GetList_OfPorC_OfNode($list,dParents, $who) );
                keepflags0($ar);
        }endfunc0()

        protected func0( Get_ChildrenList_OfNode(&$list, $who))//list of children of element node $who
        {
                _yntIFnot( $ar=$this->GetList_OfPorC_OfNode($list,dChildren, $who) );
                keepflags0($ar);
        }endfunc0()
         */

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

        func0 (ynTestElementInvariants(&$elem) )
        {
                _if (is_string($elem) && !empty($elem)){ //we DON'T allow empty string as a valid element id && ! empty($elem)) {
                        addretflag0(yes);
                } else {
                        show("php, ynTestElementInvariants: var that failed test is \" ".retValue(&$elem)."\"");
                        addretflag0(no);
                }
        }endfunc0()

        func0 (__destruct())/*{{{*/
        {
                $this->AllElements=null;//i wonder if this destroys recursively; common sense tells me yes
        }endfunc0(ok)/*}}}*/

//TODO: we would add a next level to be able to add dup elements into a list; the list will hold transparent(to the level of dup elements) unique elements that point to the real elements, thus we got dup elements build on unique elements...

        protected func0 (addChild($parent,$child))/*{{{*/
        {//let me make smth str8: $parent and $child are ID names similar to pointer value of some pointer, not the actual data but the pointer to the data; these IDs are names/descriptions but they really are pointers; remember that the data is(are) rather irrelevant, the relations(/-ships) within the data are the relevant ones
                _yntIFnot($this->ynTestElementInvariants($parent));
                _yntIFnot($this->ynTestElementInvariants($child));
                _yntIFnot($this->E_NOTICE_hide(dChildren,$parent));
                _yntIFnot( $ar=UniqAppendElemToList($child, dChildrenOf($parent)/* returnArray(dChildren)[$parent]*/ ) );
                keepflags0($ar);
        }endfunc0()/*}}}*/

        protected func0 (addParent($child,$parent))/*{{{*/
        {
                _yntIFnot($this->ynTestElementInvariants($parent));
                _yntIFnot($this->ynTestElementInvariants($child));
                _yntIFnot($this->E_NOTICE_hide(dParents,$child));
                _yntIFnot( $retlist=UniqAppendElemToList($parent, dParentsOf($child)) );
                keepflags0($retlist);
        }endfunc0()/*}}}*/

        protected func0 (delChild($parent,$child))/*{{{*/
        {
                _yntIFnot($this->ynTestElementInvariants($parent));
                _yntIFnot($this->ynTestElementInvariants($child));
                _yntIFnot($this->E_NOTICE_hide(dChildren,$parent));
                _yntIFnot( $ar=DelElemFromList($child, dChildrenOf($parent) ) );
                keepflags0($ar);
        }endfunc0()/*}}}*/

        protected func0 (delParentFromChild($parent,$child))/*{{{*/
        {
                _yntIFnot($this->ynTestElementInvariants($parent));
                _yntIFnot($this->ynTestElementInvariants($child));
                _yntIFnot($this->E_NOTICE_hide(dParents,$child));
                _yntIFnot( $ar=DelElemFromList($parent, dParentsOf($child) ) );
                keepflags0($ar);
        }endfunc0()/*}}}*/

        func0 (ynIsNode($node))/*{{{*/
        {//let me remind you that a Node(be it parent of child) cannot exist unless it is a part of a relationship, ie. another node is somehow connected to it
                _yntIFnot($this->ynTestElementInvariants($node));
                _yntIFnot($this->E_NOTICE_hide(dParents,$node));
                _yntIFnot($this->E_NOTICE_hide(dChildren,$node));
                _if( TRUE===isNElist(dChildrenOf($node)) || TRUE===isNElist(dParentsOf($node))) {
                        addretflag0(yes);
                } else {
                        addretflag0(no);
                }
        }endfunc0()/*}}}*/

        func0 (ynIsPCRel($parent,$child))/*{{{*/
        {
                _yntIFnot($this->ynTestElementInvariants($parent));
                _yntIFnot($this->ynTestElementInvariants($child));
                _yntIFnot($this->E_NOTICE_hide(dChildren,$parent));
                _if( TRUE===isNElist(dChildrenOf($parent)) && TRUE===in_array($child, dChildrenOf($parent))) {
                        addretflag0(yes);
                } else {
                        addretflag0(no);
                }
        }endfunc0()/*}}}*/

        func0 (GetOfParent_AllChildren($parent,&$children))/*{{{*/
        {
                _yntIFnot($this->ynTestElementInvariants($parent));
                _yntIFnot($this->E_NOTICE_hide(dChildren,$parent));
                        $children=dChildrenOf($parent);//copy?
                        _if (isNElist($children)) {
                                endnow0(yes);
                        }
        }endfunc0(no)/*}}}*/

        func0 (DelAllChildrenOf($parent))/*{{{*/
        {
                _yntIFnot($this->ynTestElementInvariants($parent));
                _yntIFnot($this->E_NOTICE_hide(dChildren,$parent));
                $children=&dChildrenOf($parent);//ref!
                _if (isNElist($children)) {
                        foreach ($children as $child) {
                                // del all $parent from these $children
                                _yntIFnot( $this->delParentFromChild($parent, $child) );
                        }
                        $children=null;//empty the array of children of the $parent
                }
        }endfunc0(yes)/*}}}*/

        func0 (GetOfChild_AllParents($child,&$parents))/*{{{*/
        {
                _yntIFnot($this->ynTestElementInvariants($child));
                _yntIFnot($this->E_NOTICE_hide(dParents,$child));
                $parents=dParentsOf($child);//copy?
                _if (isNElist($parents)) {
                        endnow0(yes);
                }
        }endfunc0(no)/*}}}*/

        func0 (DelAllParents($child))/*{{{*/
        {
                _yntIFnot($this->ynTestElementInvariants($child));
                _yntIFnot($this->E_NOTICE_hide(dParents,$child));
                $parents=&dParentsOf($child);//ref!
                _if (isNElist($parents)) {
                        foreach ($parents as $parent) {
                                // del all $parent from these $children
                                _yntIFnot( $this->delChild($parent, $child) );
                        }
                        $parents=null;//empty the array of children of the $parent
                }
        }endfunc0(yes)/*}}}*/

        func0 (GetCountOfChildren_OfParent(&$count,$parent))/*{{{*/
        {
                _yntIFnot($this->ynTestElementInvariants($parent));
                _yntIFnot($this->E_NOTICE_hide(dChildren,$parent));
                _yntIFnot( RelaxedArrayCount(dChildrenOf($parent), $count) );
        }endfunc0(yes)/*}}}*/

        func0 (GetCountOfParents_OfChild(&$count,$child))/*{{{*/
        {
                _yntIFnot($this->ynTestElementInvariants($child));
                _yntIFnot($this->E_NOTICE_hide(dParents,$child));
                _yntIFnot( RelaxedArrayCount(dParentsOf($child), $count) );
        }endfunc0(yes)/*}}}*/


}//endclass

#endif //header ends
// vim: fdm=marker
//?>
