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
#include "debug.php"
#include "color.php"

func (UniqAppendElemToList($elem,&$list), dadd)/*{{{*/
{
        _if (TRUE===is_array(&$list) && TRUE===in_array($elem, &$list, TRUE/*strict type check*/)) {
                retflag(kAlready);
        } else { //attempting to append
                $list[]=$elem;//auto numbered index, appending to end
                retflag(kAdded);
        }
}endfunc(yes)/*}}}*/

func (ArrayCount(&$list, &$count), dadd)/*{{{*/
{
        _if (TRUE===is_array(&$list)) {
                $count=count(&$list);
        } else { //attempting to append
                $count=0;
        }
}endfunc(yes)/*}}}*/

func (DelElemFromList($elem,&$list), dadd)/*{{{*/
{
        _if (TRUE===is_array(&$list) && $key=array_search($elem, &$list, TRUE) ) {
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
#define kParentsOf $this->AllElements[dParents]
#define kChildrenOf $this->AllElements[dChildren]
        }endfunc(ok)/*}}}*/

        func (__destruct(), ddestr)/*{{{*/
        {
                $this->AllElements=null;
        }endfunc(ok)/*}}}*/


        protected func (addChild($parent,$child), dadd)/*{{{*/
        {
                _tIFnot( $ar=UniqAppendElemToList($child, kChildrenOf[$parent] ) );
                keepflags($ar);
        }endfunc()/*}}}*/

        protected func (addParent($child,$parent), dadd)/*{{{*/
        {
                _tIFnot( $ar=UniqAppendElemToList($parent, kParentsOf[$child]) );
                keepflags($ar);
        }endfunc()/*}}}*/

        protected func (delChild($parent,$child), ddel)/*{{{*/
        {
                _tIFnot( $ar=DelElemFromList($child, kChildrenOf[$parent] ) );
                keepflags($ar);
        }endfunc()/*}}}*/

        protected func (delParentFromChild($parent,$child), ddel)/*{{{*/
        {
                _tIFnot( $ar=DelElemFromList($parent, kParentsOf[$child] ) );
                keepflags($ar);
        }endfunc()/*}}}*/

        func (GetOfParent_AllChildren($parent,&$children), dget)/*{{{*/
        {
                $children=kChildrenOf[$parent];
                if (is_array($children)) {
                        retflag(yes);
                } else {
                        retflag(no);
                }
        }endfunc()/*}}}*/

        func (DelAllChildrenOf($parent), ddel)/*{{{*/
        {
                $children=&kChildrenOf[$parent];// get all children of the $parent
                if (is_array($children)) {
                        foreach ($children as $child) {
                                // del all $parent from these $children
                                _tIFnot( $this->delParentFromChild($parent, $child) );
                        }
                        $children=null;//empty the array of children of the $parent
                }
        }endfunc(yes)/*}}}*/

        func (GetOfChild_AllParents($child,&$parents), dget)/*{{{*/
        {
                $parents=kParentsOf[$child];
                if (is_array($parents)) {
                        retflag(yes);
                } else {
                        retflag(no);
                }
        }endfunc()/*}}}*/

        func (DelAllParents($child), ddel)/*{{{*/
        {
                $parents=&kParentsOf[$child];
                if (is_array($parents)) {
                        foreach ($parents as $parent) {
                                // del all $parent from these $children
                                _tIFnot( $this->delChild($parent, $child) );
                        }
                        $parents=null;//empty the array of children of the $parent
                }
        }endfunc(yes)/*}}}*/


}//endclass

#endif //header ends
// vim: fdm=marker
//?>
