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
                __( parent::__construct() );
        }endfunc(yes)/*}}}*/

        func (__destruct(), ddestr)/*{{{*/
        {
                __( parent::__destruct() );
        }endfunc(yes)/*}}}*/

        func (SetRel($parent,$child), dset)/*{{{*/
        {
                //well, no transaction... too bad
                _tIFnot( $ar=$this->addChild($parent, $child) );
                _if (yes===isvalue(kAlready, $ar)) {
                        retflag(kAlready);
                }
                _tIFnot( $ar=$this->addParent($child, $parent) );
        }endfunc(yes)/*}}}*/

        func (DelRel($parent,$child), dset)/*{{{*/
        {
                //well, no transaction... too bad
                _tIFnot( $ar=$this->delChild($parent, $child) );
                _if (yes===isvalue(kAlready, $ar)) {
                        retflag(kAlready);
                }
                _tIFnot( $ar=$this->delParent($child, $parent) );
        }endfunc(yes)/*}}}*/

        func (IsRel($parent,$child), dis)/*{{{*/
        {
                _if( TRUE===is_array(kChildren[$parent]/*$this->AllElements[$parent][kChildren]*/) && TRUE===in_array($child, kChildren[$parent]/*$this->AllElements[$parent][kChildren]*/)) {
                        retflag(yes);
                } else {
                        retflag(no);
                }
        }endfunc()/*}}}*/


}//endclass

#endif //header ends
// vim: fdm=marker
//?>
