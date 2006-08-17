/****************************************************************************
*
*                             dmental links
*       Copyright (c) 28 Feb 2005 AtKaaZ, AtKaaZ at users.sourceforge.net
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
* Description:
*
****************************************************************************/


#ifndef __GCALITEM_H
#define __GCALITEM_H

#include "gdefs.h"

#include "nicef.h"

#include "common.h"


interface(gcatomslist_item,
	void composegcatomslist_item(
		deref_gcatomslist_itemID_type *into,
		gcatomslist_itemID prevINlist,
		gcatomslist_itemID nextINlist,
		atomID ptr2atom_that_points_to_US
);)




#endif
