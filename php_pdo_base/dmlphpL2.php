//<?php
//header starts
#ifndef DMLPHPL2_PHP
#define DMLPHPL2_PHP

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
*               this is Level 2, the next higer level
*
***************************************************************************}}}*/


#include "shortdef.php"
#include "debugL0.php"
#include "color.php"
#include "dmlphpL1.php"

class dmlphpL2 extends dmlphpL1 {

        funcL0 (__construct())/*{{{*/
        {
                __( $ar=parent::__construct() );
                keepflagsL0($ar);
        }endfuncL0()/*}}}*/

        funcL0 (__destruct())/*{{{*/
        {
                __( $ar=parent::__destruct() );
                keepflagsL0($ar);
        }endfuncL0()/*}}}*/

                //PC=parent, child (the parameters are in this order)


}//endclass

#endif //header ends
// vim: fdm=marker
//?>
