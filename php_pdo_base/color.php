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

//include_once("term.php");
#include "term.php"
#include "served.php"

#define setcol(col) "\x1B[3".#col."m"

function defcol($str, $termcol, $sitecol)
{
        if (IsTerminal()) {
                define($str,setcol($termcol));//_setcol($num));
        } else if (Served()){
                if ($sitecol==="none") {
                        define($str,'</font>');
                } else {
                        define($str,'<font color="#'.$sitecol.'">');//disabled, while checking onclick-ed item's ID we get none, the id of the <font> tag! '<font color="#'.$sitecol.'">');
                }
        }
}

defcol('nocol',9,"FFFFFF");
defcol('browncol',3,"A52A2A");
defcol('redcol',1,"FF0000");
defcol('bluecol',4,"0000FF");
defcol('greencol',2,"00FF00");
defcol('purplecol',5,"800080");


// vim: fdm=marker
#endif //header
//?>
