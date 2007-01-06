//<?php
#ifndef REENTRY_PHP
#define REENTRY_PHP

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
* Description: handling reentry into certain portions of the program, like functions
*
***************************************************************************}}}*/
//USAGE: call DeisallowReentry() at the beginning of the function, and AllowReentry on exit from it
//what this does is, it throws if you enter that function again, ie. from another thread or recursing it...




#define DisallowLocalReentry() { \
                        static $_the_nonreentrant_lockvar=0; \
                        ++$_the_nonreentrant_lockvar; \
                        if ($_the_nonreentrant_lockvar > 1) { /*then this part was "called" at least twice */ \
                                throw_exception("attempting to locally enter for the $_the_nonreentrant_lockvar-th time"); \
                        } }


#define DisallowGlobalReentry(global_lockvar) {\
                        global global_lockvar; \
                        $_the_NONreentrant_GLOBAL_LOCKVAR=&global_lockvar; \
                        ++$_the_NONreentrant_GLOBAL_LOCKVAR; \
                        if ($_the_NONreentrant_GLOBAL_LOCKVAR > 1) { \
                                throw_exception("attempting to globally enter for the $_the_NONreentrant_GLOBAL_LOCKVAR-th time"); \
                        } }

#define AllowLocalReentry() { \
                        _tIFnot(isset($_the_nonreentrant_lockvar));\
                        --$_the_nonreentrant_lockvar;\
                        if ($_the_nonreentrant_lockvar < 0) { \
                                throw_exception("LOCAL:attempting to exit too many times, ie. non matching begin for this exit! $_the_nonreentrant_lockvar should be 0"); \
                        }}

#define AllowGlobalReentry() { \
                        _tIFnot(isset($_the_NONreentrant_GLOBAL_LOCKVAR));\
                        --$_the_NONreentrant_GLOBAL_LOCKVAR; \
                        if ($_the_NONreentrant_GLOBAL_LOCKVAR < 0) { \
                                throw_exception("GLOBAL:attempting to exit too many times, ie. non matching begin for this exit! $_the_nonreentrant_lockvar should be 0"); \
                        }}

//reentry functions must be called within the same scope! ie. within the same {} block or same function, such to not loose sight of the static var, or use another's var
#define DisallowReentry(_local, /*global var name here*/...) { \
                $_reentry_typE=_local; \
                if (TRUE===$_reentry_typE) { \
                        DisallowLocalReentry() \
                } else { \
                        DisallowGlobalReentry($preventemptyGLOBALVARifneeded_##__VA_ARGS__) \
                } }


#define AllowReentry() { \
                        if (TRUE===$_reentry_typE) { \
                                AllowLocalReentry() \
                        } else { \
                                AllowGlobalReentry() \
                        }}


// vim: fdm=marker

#endif //header
//?>
