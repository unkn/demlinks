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
* Description:.
*
****************************************************************************/

#ifndef FLAGS_H
#define FLAGS_H

#include "_gcdefs.h"
#include "pnotetrk.h"

enum EFlags_t {
        kF_Hold1Key = 0,
        kF_QuitProgram,
//last:
        kMaxFlags
};


function
InitFlags();

function
SetFlag(EFlags_t which);

function
ClearFlag(EFlags_t which);

int
Flag(EFlags_t which);




#endif
