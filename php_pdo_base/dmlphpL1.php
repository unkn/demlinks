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
                __( $ar=parent::__construct() );
                keepflags($ar);
        }endfunc()/*}}}*/

        func (__destruct(), ddestr)/*{{{*/
        {
                __( $ar=parent::__destruct() );
                keepflags($ar);
        }endfunc()/*}}}*/

        func (SetRel($parent,$child), dset)/*{{{*/
        {
                //well, no transaction... too bad
                _tIFnot( $ar=$this->addChild($parent, $child) );
                keepflags($ar);
                _tIFnot( $ar=$this->addParent($child, $parent) );
                keepflags($ar);
        }endfunc()/*}}}*/

        func (DelRel($parent,$child), dset)/*{{{*/
        {
                //well, no transaction... too bad
                _tIFnot( $ar=$this->delChild($parent, $child) );
                keepflags($ar);
                _tIFnot( $ar=$this->delParent($child, $parent) );
                keepflags($ar);
        }endfunc()/*}}}*/

        func (IsRel($parent,$child), dis)/*{{{*/
        {
                _if( TRUE===is_array(kChildren[$parent]) && TRUE===in_array($child, kChildren[$parent])) {
                        retflag(yes);
                } else {
                        retflag(no);
                }
        }endfunc()/*}}}*/


}//endclass

#endif //header ends
// vim: fdm=marker
//?>
