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
* Description: the part of demlinks which interfaces Items (of Lists)
*
****************************************************************************/


#include "_gcdefs.h" /* first */
/* personalized notification tracking capabilities */
#include "pnotetrk.h"


#include "item.h"


/* constructor */
MItem::MItem():
        /* the size of one record
         * the contents of one record are the contents of the entire struct */
        fRecSize(sizeof(Item_st)),

        /* FIXME: no header in file
         * eventually make a user-defined header and checkit on open, write it
           on create */
        fHeaderSize(0)
{
        SetNotInited();
}

/* destructor */
MItem::~MItem()
{
        if (IsInited())
                ERR_IF(!DeInit(),);
}
bool
MItem::Init(const char * a_FileName)
{
        LAME_PROGRAMMER_IF(IsInited(),
                        return false);

        ERR_IF(!TRecordsStorage::Open(a_FileName,fHeaderSize,fRecSize),
                        return false);
        SetInited();
        return true;
}

bool
MItem::DeInit()
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);
        ERR_IF(!TRecordsStorage::Close(),
                        return false);
        SetNotInited();
        return true;
}
bool
MItem::ReadWithID(
        const ItemID_t a_ItemID,
        Item_st &a_Into)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);
        ERR_IF(!TRecordsStorage::ReadRecord(
                                a_ItemID,
                                &a_Into),
                        return false);
        return true;
}

bool
MItem::WriteWithID(
        const ItemID_t a_ItemID,
        const Item_st &a_From)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);

        ERR_IF(!TRecordsStorage::WriteRecord(
                                a_ItemID,
                                &a_From),
                        return false);
        return true;
}


/* LastID may be kNoID if there are no IDs, also it may fail thus return false*/
bool
MItem::GetLastID(
                ItemID_t &a_ItemID)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);

        ERR_IF(kBadRecCount ==
                        (a_ItemID = TRecordsStorage::GetNumRecords()),
                        return false);

        return true;
}

bool
MItem::Compose(
                Item_st &a_Item_st,
                const AnyReferrerID_t a_ReferrerID,
                const ItemID_t a_PrevItem,
                const ItemID_t a_NextItem)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);

        a_Item_st.ReferrerID = a_ReferrerID;
        a_Item_st.PrevItem = a_PrevItem;
        a_Item_st.NextItem = a_NextItem;

        return true;
}

bool
MItem::InitCache(
                const RecNum_t a_MaxNumRecordsToBeCached)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);

        ERR_IF(!TRecordsStorage::InitCache(a_MaxNumRecordsToBeCached),
                        return false);
        return true;
}
bool
MItem::KillCache()
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);

        ERR_IF(!TRecordsStorage::KillCache(),
                        return false);
        return true;
}

/* create a new item, doesn't connect anything
 * return its ID or kNoItemID if failed */
ItemID_t
MItem::AddNew(
                const Item_st &a_Item_st)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return kNoItemID);

        ItemID_t newItemID;
        ERR_IF(!GetLastID(newItemID),
                        return kNoItemID);

        /* can be 0++ */
        newItemID++;

        ERR_IF(!WriteWithID(
                                newItemID,
                                a_Item_st),
                        return kNoItemID);

        return newItemID;
}


