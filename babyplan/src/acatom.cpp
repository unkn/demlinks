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


#include <process.h>
#include <stdio.h>

#include "petrackr.h"
#include "acatom.h"


implement(acatom,
void if_acatom::composeacatom(
	deref_acatomID_type *into,
	groupID ptr2group,
	atomID prevINchain,
	atomID nextINchain,
	acatoms_listID ptr2clonelist,
	atomID Irefer2thisATOM
)
{
	_5in2(ptr2group,prevINchain,nextINchain,ptr2clonelist,Irefer2thisATOM);
})

