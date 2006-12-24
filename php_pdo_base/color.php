//<?php
#ifndef COLOR_PHP
#define COLOR_PHP

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
* Description: linux console colors
*
***************************************************************************}}}*/

#define setcol(col) "\x1B[3".#col."m"
define(nocol,setcol(9));
//define(nocol,"\x1B[39m");
define(browncol,setcol(3));
define(redcol,setcol(1));
define(bluecol,setcol(4));
define(greencol,setcol(2));
define(purplecol,setcol(5));

// vim: fdm=marker
#endif //header
//?>
