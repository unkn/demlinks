/****************************************************************************
*
*                             dmental links
*    Copyright (c) 28 Feb 2005 AtKaaZ, AtKaaZ at users.sourceforge.net
*    Portions Copyright (c) 1983-2002 Sybase, Inc. All Rights Reserved.
*
*  ========================================================================
*
*    This file contains Original Code and/or Modifications of Original
*    Code as defined in and that are subject to the Sybase Open Watcom
*    Public License version 1.0 (the 'License'). You may not use this file
*    except in compliance with the License. BY USING THIS FILE YOU AGREE TO
*    ALL TERMS AND CONDITIONS OF THE LICENSE. A copy of the License is
*    provided with the Original Code and Modifications, and is also
*    available at www.sybase.com/developer/opensource.
*
*    The Original Code and all software distributed under the License are
*    distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
*    EXPRESS OR IMPLIED, AND SYBASE AND ALL CONTRIBUTORS HEREBY DISCLAIM
*    ALL SUCH WARRANTIES, INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF
*    MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR
*    NON-INFRINGEMENT. Please see the License for the specific language
*    governing rights and limitations under the License.
*
*  ========================================================================
*
* Description: the core of demlinks
*
****************************************************************************/


#include "_gcdefs.h" /* first */
/* personalized notification tracking capabilities */
#include "pnotetrk.h"

#include "dmlcore.h"

/* constructor */
MElemental::MElemental():
        /* the size of one record
         * the contents of one record are the contents of the entire struct */
        fRecSize(sizeof(Elemental_st)),

        /* FIXME: no header in file
         * eventually make a user-defined header and checkit on open, write it
           on create */
        fHeaderSize(0)
{
        SetDeInited();
}

/* destructor */
MElemental::~MElemental()
{
        if (IsInited())
                ERR_IF(!DeInit(),);
}

bool
MElemental::Init(const char * a_FileName)
{
        LAME_PROGRAMMER_IF(IsInited(),
                        return false);
        ERR_IF(!TRecordsStorage::Open(a_FileName,fHeaderSize,fRecSize),
                        return false);
        SetInited();
        return true;
}

bool
MElemental::DeInit()
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);
        ERR_IF(!TRecordsStorage::Close(),
                        return false);
        SetDeInited();
        return true;
}


