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

#include "flags.h"
#include "_gcdefs.h"

//a val of 0 means flag is off, a positive value means is set on
//negative value is undefined, shouldn't happen, bugs around if so.
int Flags[kMaxFlags];

int
Flag(EFlags_t which)
{
        ERR_IF((which<0)||(which>=kMaxFlags),
                        return 0);
        ERR_IF(Flags[which]<0,//negative value handled here
                        return 0);
        return (Flags[which]);
}

EFunctionReturnTypes_t
InitFlags()
{
        for (int i=0;i<kMaxFlags;i++) {
                Flags[i]=0;
        }//for
        return kFuncOK;
}


EFunctionReturnTypes_t
SetFlag(EFlags_t which)
{
        ERR_IF((which<0)||(which>=kMaxFlags),
                        return kFuncFailed);
        Flags[which]++;
        ERR_IF(Flags[which]<=0,
                        return kFuncFailed);
        return kFuncOK;
}


EFunctionReturnTypes_t
ClearFlag(EFlags_t which)
{
        ERR_IF((which<0)||(which>=kMaxFlags),
                        return kFuncFailed);
        Flags[which]--;
        ERR_IF(Flags[which]<0,
                        return kFuncFailed);
        return kFuncOK;
}

