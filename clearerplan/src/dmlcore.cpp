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
/*
 * "Functions should be short and sweet, and do just one thing.  They should
   fit on one or two screenfuls of text (the ISO/ANSI screen size is 80x24,
   as we all know), and do one thing and do that well." quoting Linus here.
 */

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
        SetNotInited();
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
        SetNotInited();
        return true;
}

bool
MElemental::ReadWithID(
        const ElementalID_t a_ElementalID,
        Elemental_st &a_Into)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);
        ERR_IF(!TRecordsStorage::ReadRecord(
                                a_ElementalID,
                                &a_Into),
                        return false);
        return true;
}

bool
MElemental::WriteWithID(
        const ElementalID_t a_ElementalID,
        const Elemental_st &a_From)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);
        ERR_IF(!TRecordsStorage::WriteRecord(
                                a_ElementalID,
                                &a_From),
                        return false);
        return true;
}


bool
MElemental::GetLastID(
                ElementalID_t &a_ElementalID)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);
        ERR_IF(kBadRecCount == (a_ElementalID = TRecordsStorage::GetNumRecords()),
                        return false);
        return true;
}

bool
MElemental::Compose(
                Elemental_st &a_Elemental_st,
                const BasicElement_t a_BasicElementData,
                const ListOfReferrers_ID_t a_ListOfRef2Elemental_ID)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);

        a_Elemental_st.BasicElementData = a_BasicElementData;
        a_Elemental_st.ListOfRef2Elemental_ID = a_ListOfRef2Elemental_ID;

        return true;
}

bool
MElemental::InitCache(
                const RecNum_t a_MaxNumRecordsToBeCached)
{
        ERR_IF(!TRecordsStorage::InitCache(a_MaxNumRecordsToBeCached),
                        return false);
        return true;
}


bool
MElemental::KillCache()
{
        ERR_IF(!TRecordsStorage::KillCache(),
                        return false);
        return true;
}

/* constructor */
MDementalLinksCore::MDementalLinksCore()
{
        SetNotInited();
        SetNoCache();
}

/* destructor */
MDementalLinksCore::~MDementalLinksCore()
{
        if (IsInited())
                ERR_IF(!DeInit(),);
}

bool
MDementalLinksCore::Init(const char * a_ElementalsFileName)
{
        ERR_IF(!MElemental::Init(a_ElementalsFileName),
                        return false);
        return true;
}

bool
MDementalLinksCore::InitCache(const RecNum_t a_MaxNumRecordsToBeCached)
{
        ERR_IF(!MElemental::InitCache(a_MaxNumRecordsToBeCached),
                        return false);
        SetCache();
        return true;
}


bool
MDementalLinksCore::DeInit()
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);
        ERR_IF(!MElemental::DeInit(),
                        return false);
        SetNotInited();
        return true;
}

bool
MDementalLinksCore::KillCache()
{
        ERR_IF(!MElemental::KillCache(),
                        return false);
        return true;
}


ElementalID_t
MElemental::AddNew(
                const Elemental_st a_Elemental_st)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return kNoElementalID);

        ElementalID_t newElementalID;
        ERR_IF(!GetLastID(newElementalID),
                        return kNoElementalID);

        newElementalID++;

        ERR_IF(!WriteWithID(
                                newElementalID,
                                a_Elemental_st),
                        return kNoElementalID);

        return newElementalID;
}


