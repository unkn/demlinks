<?php

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
* Description: detects if running inside a terminal or as a webpage
*
***************************************************************************}}}*/


include_once("served.php");

static $IsTerminal=false;

function IsTerminal() //well since there's no way to make own variables superglobal (or i haven't searched enough)
{//an evil workaround eh?
        global $IsTerminal;
        return $IsTerminal;
}

if (isset($_SERVER['TERM'])) {
        $IsTerminal=true;
} else if (!Served()) {
        throw new Exception("one of Terminal and Served must exist!");
}

// vim: fdm=marker
?>
