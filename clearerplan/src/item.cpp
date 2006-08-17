/****************************************************************************
*
*                             dmental links
*    Copyright (c) 28 Feb 2005 AtKaaZ, AtKaaZ at users.sourceforge.net
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


