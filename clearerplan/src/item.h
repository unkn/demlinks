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


#ifndef ITEM_____H
#define ITEM_____H

#include "common.h"


class MItem : private TRecordsStorage {
private:
        /* sizeof Item_st struct */
        const long fRecSize;
        const long fHeaderSize;

        /* true if Init was called and succeded */
        bool fInited;
public:
        /* constructor */
        MItem();
        /* destructor */
        ~MItem();

        /* open the file + init stuff
         * use this after constructor somewhere */
        bool Init(const char * a_FileName);

        /* use cache from now on... see recstor.h */
        bool InitCache(const RecNum_t a_MaxNumRecordsToBeCached);

        /* is cache enabled ? */
        bool IsCacheEnabled() { return TRecordsStorage::IsCacheEnabled();};


        /* stop using cache, frees some memory and also flushes the writes */
        bool KillCache();

        /* flush writes and close the file + cleanup stuff
         * use this before destructor, but is not necessary since it's called
           from within it if needed */
        bool DeInit();

        /* retrieve the record(contents) of an Item with the specified ID*/
        bool ReadWithID(
                        const ItemID_t a_ItemID,
                        Item_st &a_Into);

        /* write by overwritting prev data, the contents at a spec. ID */
        bool WriteWithID(
                        const ItemID_t a_ItemID,
                        const Item_st &a_From);

        /* who's the last ID in database
         * returns false if an error
         * bewarned that the ID may be 0 which is kNoItemID meaning there are
           no IDs */
        bool GetLastID(ItemID_t &a_ItemID);

        /* scather the data into the struct, used before Write
         * prev and/or next may be kNoItemID */
        bool Compose(
                        Item_st &a_Item_st,
                        const AnyReferrerID_t a_ReferrerID,
                        const ItemID_t a_PrevItem,
                        const ItemID_t a_NextItem);

        /* create a new item, doesn't connect anything
         * return its ID or kNoItemID if failed */
        ItemID_t AddNew(const Item_st &a_Item_st);

private:
        bool IsInited() const { return fInited; };
        void SetInited() { fInited = true; };
        void SetNotInited() { fInited = false; };
};




#endif
