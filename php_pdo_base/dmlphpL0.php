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


#include "shortdef.php"
#include "color.php"

func (UniqAppendElemToList($elem,&$list), dadd)/*{{{*/
{
        _ynif (TRUE===is_array(&$list) && TRUE===in_array($elem, &$list, TRUE/*strict type check*/)) {
                retflag(kAlready);
        } else { //attempting to append
                $list[]=$elem;//auto numbered index, appending to end
                retflag(kAdded);
        }
}endfunc(yes)/*}}}*/

func (ArrayCount(&$list, &$count), dadd)/*{{{*/
{
        _ynif (TRUE===is_array(&$list)) {
                $count=count(&$list);
        } else { //attempting to append
                $count=0;
        }
}endfunc(yes)/*}}}*/

func (DelElemFromList($elem,&$list), dadd)/*{{{*/
{
        _ynif (TRUE===is_array(&$list) && $key=array_search($elem, &$list, TRUE) ) {
                        unset($list[$key]);
                        retflag(kDeleted);
        } else {
                retflag(kAlready);
        }
}endfunc(yes)/*}}}*/

class dmlphpL0 {
        protected $AllElements;
        //if an element doesn't have a relation whatsoever then it doesn't exist ie. cannot exist and be null

        func (__construct(), dconstr)/*{{{*/
        {
                $this->AllElements=array();
                define(dParents,"Parents");
                define(dChildren,"Children");
//when accessing ie. kParentsOf[$elem] you must make sure that $elem is scalar! aka not array! or an error/warning php issues
#define kParentsOf $this->AllElements[dParents]
#define kChildrenOf $this->AllElements[dChildren]
        }endfunc(ok)/*}}}*/

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

        func (TestElementInvariants(&$elem) ,dtest)
        {
                if (is_string(&$elem) ) {
                        retflag(yes);
                } else {
                        debnl(dtestcrit, "TestElementInvariants: var that failed test is \" ".getvalue(&$elem)."\"");
                        retflag(no);
                }
        }endfunc()

        func (__destruct(), ddestr)/*{{{*/
        {
                $this->AllElements=null;//i wonder if this destroys recursively; common sense tells me yes
        }endfunc(ok)/*}}}*/

//TODO: we would add a next level to be able to add dup elements into a list; the list will hold transparent(to the level of dup elements) unique elements that point to the real elements, thus we got dup elements build on unique elements...

        protected func (addChild($parent,$child), dadd)/*{{{*/
        {//let me make smth str8: $parent and $child are ID names similar to pointer value of some pointer, not the actual data but the pointer to the data; these IDs are names/descriptions but they really are pointers; remember that the data is(are) rather irrelevant, the relations(/-ships) within the data are the relevant ones
                _yntIFnot($this->TestElementInvariants($parent));
                _yntIFnot($this->TestElementInvariants($child));
                _yntIFnot( $ar=UniqAppendElemToList($child, kChildrenOf[$parent]/* returnArray(dChildren)[$parent]*/ ) );
                keepflags($ar);
        }endfunc()/*}}}*/

        protected func (addParent($child,$parent), dadd)/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($parent));
                _yntIFnot($this->TestElementInvariants($child));
                _yntIFnot( $ar=UniqAppendElemToList($parent, kParentsOf[$child]) );
                keepflags($ar);
        }endfunc()/*}}}*/

        protected func (delChild($parent,$child), ddel)/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($parent));
                _yntIFnot($this->TestElementInvariants($child));
                _yntIFnot( $ar=DelElemFromList($child, kChildrenOf[$parent] ) );
                keepflags($ar);
        }endfunc()/*}}}*/

        protected func (delParentFromChild($parent,$child), ddel)/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($parent));
                _yntIFnot($this->TestElementInvariants($child));
                _yntIFnot( $ar=DelElemFromList($parent, kParentsOf[$child] ) );
                keepflags($ar);
        }endfunc()/*}}}*/

        func (GetOfParent_AllChildren($parent,&$children), dget)/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($parent));
                if (!is_array($parent)) {
                        $children=kChildrenOf[$parent];
                        if (is_array($children)) {
                                endnow(yes);
                        }
                }
        }endfunc(no)/*}}}*/

        func (DelAllChildrenOf($parent), ddel)/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($parent));
                $children=&kChildrenOf[$parent];// get all children of the $parent
                if (is_array($children)) {
                        foreach ($children as $child) {
                                // del all $parent from these $children
                                _yntIFnot( $this->delParentFromChild($parent, $child) );
                        }
                        $children=null;//empty the array of children of the $parent
                }
        }endfunc(yes)/*}}}*/

        func (GetOfChild_AllParents($child,&$parents), dget)/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($child));
                if (!is_array($child)) {
                        $parents=kParentsOf[$child];
                        if (is_array($parents)) {
                                endnow(yes);
                        }
                }
        }endfunc(no)/*}}}*/

        func (DelAllParents($child), ddel)/*{{{*/
        {
                _yntIFnot($this->TestElementInvariants($child));
                $parents=&kParentsOf[$child];
                if (is_array($parents)) {
                        foreach ($parents as $parent) {
                                // del all $parent from these $children
                                _yntIFnot( $this->delChild($parent, $child) );
                        }
                        $parents=null;//empty the array of children of the $parent
                }
        }endfunc(yes)/*}}}*/


}//endclass

#endif //header ends
// vim: fdm=marker
//?>
