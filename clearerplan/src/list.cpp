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
* Description: the part of demlinks which interfaces Lists
*
****************************************************************************/
/* a list; currently while talkin'bout a list we actually talk about a list of
   referrers; referrers can be of any of the above three types */


#include "_gcdefs.h" /* first */
/* personalized notification tracking capabilities */
#include "pnotetrk.h"

#include "list.h"

/* constructor */
MListOfReferrers::MListOfReferrers():
        /* the size of one record
         * the contents of one record are the contents of the entire struct */
        fRecSize(sizeof(ListOfReferrers_st)),

        /* FIXME: no header in file
         * eventually make a user-defined header and checkit on open, write it
           on create */
        fHeaderSize(0)
{
        SetNotInited();
}

/* destructor */
MListOfReferrers::~MListOfReferrers()
{
        if (IsInited())
                ERR_IF(!DeInit(),);
}

bool
MListOfReferrers::Init(const char * a_FileName)
{
        LAME_PROGRAMMER_IF(IsInited(),
                        return false);

        ERR_IF(!TRecordsStorage::Open(a_FileName,fHeaderSize,fRecSize),
                        return false);
        SetInited();
        return true;
}

bool
MListOfReferrers::DeInit()
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);
        ERR_IF(!TRecordsStorage::Close(),
                        return false);
        SetNotInited();
        return true;
}

bool
MListOfReferrers::ReadWithID(
        const ListOfReferrers_ID_t a_ListOfReferrers_ID,
        ListOfReferrers_st &a_Into)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);
        ERR_IF(!TRecordsStorage::ReadRecord(
                                a_ListOfReferrers_ID,
                                &a_Into),
                        return false);
        return true;
}

bool
MListOfReferrers::WriteWithID(
        const ListOfReferrers_ID_t a_ListOfReferrers_ID,
        const ListOfReferrers_st &a_From)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);

        ERR_IF(!TRecordsStorage::WriteRecord(
                                a_ListOfReferrers_ID,
                                &a_From),
                        return false);
        return true;
}

/* LastID may be kNoID if there are no IDs, also it may fail thus return false*/
bool
MListOfReferrers::GetLastID(
                ListOfReferrers_ID_t &a_ListOfReferrers_ID)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);
        ERR_IF(kBadRecCount ==
                      (a_ListOfReferrers_ID = TRecordsStorage::GetNumRecords()),                        return false);
        return true;
}

bool
MListOfReferrers::Compose(
                ListOfReferrers_st &a_ListOfReferrers_st,
                const ItemID_t a_HeadItemID,
                const ItemID_t a_TailItemID)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);

        a_ListOfReferrers_st.HeadItemID = a_HeadItemID;
        a_ListOfReferrers_st.TailItemID = a_TailItemID;

        return true;
}

bool
MListOfReferrers::InitCache(
                const RecNum_t a_MaxNumRecordsToBeCached)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);

        ERR_IF(!TRecordsStorage::InitCache(a_MaxNumRecordsToBeCached),
                        return false);
        return true;
}


bool
MListOfReferrers::KillCache()
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);

        ERR_IF(!TRecordsStorage::KillCache(),
                        return false);
        return true;
}


ListOfReferrers_ID_t
MListOfReferrers::AddNew(
                const ListOfReferrers_st a_ListOfReferrers_st)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return kNoListID);

        ListOfReferrers_ID_t newListOfReferrers_ID;
        ERR_IF(!GetLastID(newListOfReferrers_ID),
                        return kNoListID);

        newListOfReferrers_ID++;

        ERR_IF(!WriteWithID(
                                newListOfReferrers_ID,
                                a_ListOfReferrers_st),
                        return kNoListID);

        return newListOfReferrers_ID;
}

