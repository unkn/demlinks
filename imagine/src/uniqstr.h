/****************************************************************************
*
*                             dmental links
*    Copyright (C) 2005 AtKaaZ, AtKaaZ at users.sourceforge.net
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
* Description: provides centralized access to a uniquely generated string
*
****************************************************************************/

#ifndef UNIQSTR_H
#define UNIQSTR_H

#include "pnotetrk.h"
#include "dmlenv.h"

#define _MAX_UNIQCHARS 30
#define _UNIQLEADING_CHAR '0'
#define _UNIQENDING_CHAR '9'


function
GetUniqueString(NodeId_t &m_Ret); //on each call a different string is returned

function
UnconditionallyInitUniqueString(); //any time you call this, the string gets inited to almost "zero", so take care!

function
MakeSureUniqueStringIsInited(); //may call this any number of times: only once the string is inited in this program run.



#endif

