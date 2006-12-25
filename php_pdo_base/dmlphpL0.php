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

func (UniqAppendToList($elem,&$list), dadd)/*{{{*/
{
        _if (TRUE===is_array(&$list) && TRUE===in_array($elem, &$list, TRUE/*strict type check*/)) {
                retflag(kAlready);
        } else { //attempting to append
                $list[]=$elem;//auto numbered index, appending to end
                retflag(kAdded);
        }
}endfunc(yes)/*}}}*/

func (DelFromList($elem,&$list), dadd)/*{{{*/
{
        _tIFnot(is_array(&$list) );
        _if (TRUE===is_array(&$list)) {
                //&& TRUE===in_array($elem, &$list, TRUE/*strict type check*/)) {
                _if ($key=array_search($elem, &$list, TRUE)) {//can return either null or FALSE
                        unset($list[$key]);
                        retflag(yes, kDeleted);
                } else {
                        retflag(yes, kAlready);
                }
        } else {
                retflag(no);//maybe we could throw, to catch bugs outside, in caller
        }
}endfunc()/*}}}*/

class dmlphpL0 {
        protected $AllElements;
        //if an element doesn't have a relation whatsoever then it doesn't exist ie. cannot exist and be null

        func (__construct(), dconstr)/*{{{*/
        {
                $this->AllElements=array();
                define(dParents,"Parents");
                define(dChildren,"Children");
#define kParents $this->AllElements[dParents]
#define kChildren $this->AllElements[dChildren]
        }endfunc(ok)/*}}}*/

        func (__destruct(), ddestr)/*{{{*/
        {
                $this->AllElements=null;
        }endfunc(ok)/*}}}*/


        protected func (addChild($parent,$child), dadd)/*{{{*/
        {
                _tIFnot( $ar=UniqAppendToList($child, kChildren[$parent] ) );
                keepflags($ar);
        }endfunc()/*}}}*/

        protected func (addParent($child,$parent), dadd)/*{{{*/
        {
                _tIFnot( $ar=UniqAppendToList($parent, kParents[$child]) );
                keepflags($ar);
        }endfunc()/*}}}*/

        protected func (delChild($parent,$child), ddel)/*{{{*/
        {
                _tIFnot( $ar=DelFromList($child, kChildren[$parent] ) );
                keepflags($ar);
        }endfunc()/*}}}*/

        protected func (delParent($child,$parent), ddel)/*{{{*/
        {
                _tIFnot( $ar=DelFromList($parent, kParents[$child] ) );
                keepflags($ar);
        }endfunc()/*}}}*/

        func (GetAllChildren($parent,&$children), dget)/*{{{*/
        {
                $children=kChildren[$parent];
                if (is_array($children)) {
                        retflag(yes);
                } else {
                        retflag(no);
                }
        }endfunc()/*}}}*/

        func (DelAllChildren($parent), ddel)/*{{{*/
        {
                $children=&kChildren[$parent];// get all children of the $parent
                if (is_array($children)) {
                        foreach ($children as $child) {
                                // del all $parent from these $children
                                _tIFnot( $this->delParent($child, $parent) );
                        }
                        $children=null;//empty the array of children of the $parent
                }
        }endfunc(yes)/*}}}*/

        func (GetAllParents($child,&$parents), dget)/*{{{*/
        {
                $parents=kParents[$child];
                if (is_array($parents)) {
                        retflag(yes);
                } else {
                        retflag(no);
                }
        }endfunc()/*}}}*/

        func (DelAllParents($child), ddel)/*{{{*/
        {
                $parents=&kParents[$child];
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
