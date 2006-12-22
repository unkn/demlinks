//<?php
#ifndef SHORTDEF_PHP
#define SHORTDEF_PHP

/****************************************************************************
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
* Description: common defines to be used in php sources
*
****************************************************************************/




#include "color.php"

        define(nl,"\n");
        define(tab,"\t");
        define(space," ");
        define(emptystr,"");
        define(yes,greencol."yes".nocol);
        define(no,redcol."no".nocol);
        define(ok,yes);//these must be equivalent:  ok~yes and bad~no   where yes="yes" and no="no" or wtw
        define(bad,no);//certain operations interchange ok with yes, that's why!

#endif //header
//?>
