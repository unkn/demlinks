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


//REMEBER: If something goes wrong, it's chained!
//[it's either the first item or anyother item of a chain,
// it's up to you to make it the last item in the chain. Act now ;;)]

#include <process.h>
#include <stdio.h>

#include "petrackr.h"
#include "eatom.h"


implement(eatom,
void if_eatom::composeeatom(
	deref_eatomID_type *into,
	eatoms_listID ptr2list,
	basic_element data
)
{
	_2in2(ptr2list,data);
})

