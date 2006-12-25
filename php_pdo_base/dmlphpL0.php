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
        }endfunc(yes)/*}}}*/

        func (__destruct(), ddestr)/*{{{*/
        {
                //print_r($this->AllElements);
                $this->AllElements=null;
        }endfunc(yes)/*}}}*/


        func (addChild($parent,$child), dadd)/*{{{*/
        {
                _tIFnot( $ar=UniqAppendToList($child, kChildren[$parent] /*$this->AllElements[$parent][kChildren]*/) );
                foreach ($ar as $val) {
                        retflag($val);
                }
        }endfunc()/*}}}*/

        func (addParent($child,$parent), dadd)/*{{{*/
        {
                _tIFnot( $ar=UniqAppendToList($parent, kParents[$child]/*$this->AllElements[$child][kParents]*/) );
                foreach ($ar as $val) {
                        retflag($val);
                }
        }endfunc()/*}}}*/

        func (GetChildren($parent,&$children), dget)/*{{{*/
        {
                $children=kChildren[$parent];//$this->AllElements[$parent][kChildren];
                if (is_array($children)) {
                        retflag(yes);
                } else {
                        retflag(no);
                }
        }endfunc()/*}}}*/

        func (GetParents($child,&$parents), dget)/*{{{*/
        {
                $parents=kParents[$child];//$this->AllElements[$child][kParents];
                if (is_array($parents)) {
                        retflag(yes);
                } else {
                        retflag(no);
                }
        }endfunc()/*}}}*/

}//endclass

#endif //header ends
// vim: fdm=marker
//?>
