//<?php
#ifndef DMLL0DEF_PHP
#define DMLL0DEF_PHP

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
* Description: demlinks level 0 defines, also used by level 1
*
****************************************************************************/




define(dbasename,"demlinks6.3sql");
define(dNodeNames,"NodeNames");//table name
define(dRelations,"Relations");//table name
define(dNodeName,"NodeName");//table name
define(dParentNodeID,"ParentNodeID");//table name
define(dChildNodeID,"ChildNodeID");//table name
define(dNodeID,"NodeID");//table name

define(paramprefix,":");
define(paramNodeName,paramprefix.dNodeName);
define(paramNodeID,paramprefix.dNodeID);


#endif //header
//?>
