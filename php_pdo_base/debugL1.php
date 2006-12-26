//<?php
#ifndef DEBUGL1_PHP
#define DEBUGL1_PHP

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
* Description: this include file will help with tracking; Level 1;
*              it will use dmlphp*.php to hold data
*
***************************************************************************}}}*/


#include "shortdef.php"
#include "color.php"
#include "dmlphpL1.php"
#include "debug.php"

if (!is_a($debugL1,"dmlphpL1")) {
        echo "Initializing dmlphpL1".nl;
        $debugL1=new dmlphpL1;
}

define(kAllFunctions,"kAllFunctions");

#define addretflagl1(...) \
        _tIFnot( $debugL1->AppendToParent_Children($funcnameALKSD, array(__VA_ARGS__) ) );

#define delretflagl1(...) \
        _tIFnot( $debugL1->DeleteFromParent_Children($funcnameALKSD, array(__VA_ARGS__) ) );

#define setretflagl1(...) \
        _tIFnot( $debugL1->SetOfParent_Children($funcnameALKSD, array(__VA_ARGS__) ) );

#define funcl1(funcname, funcparams,...) \
        function funcname funcparams \
        { \
                $funcnameALKSD=#funcname." (vim ".getfile." +".getline.")"; \
                global $debugL1; \
                _tIFnot( $debugL1->SetRel(kAllFunctions, $funcnameALKSD) );

#define endfuncl1 \
                _tIFnot( $debugL1->GetAllChildren($funcnameALKSD, $tmpASKD) );/*must have at least one return flag*/ \
        }

// vim: fdm=marker

#endif //header
//?>
