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


//group-clone ATOM
//an atom that is a referer to a group
#include <process.h>
#include <stdio.h>

#include "petrackr.h"
#include "gcatom.h"

implement(gcatom,
void if_gcatom::composegcatom(
	deref_gcatomID_type *into,
	const groupID ptr2group,
	const atomID prevINchain,
	const atomID nextINchain,
	const gcatoms_listID ptr2clonelist,
	const groupID Irefer2thisGROUP
)
{
	_5in2(ptr2group,prevINchain,nextINchain,ptr2clonelist,Irefer2thisGROUP);
})
