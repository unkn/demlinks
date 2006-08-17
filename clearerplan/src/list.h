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
* Description: the part of demlinks which interfaces Lists
*
****************************************************************************/
/* a list; currently while talkin'bout a list we actually talk about a list of
   referrers; */


#ifndef LIST_____H
#define LIST_____H

#include "common.h"

/* generic List of referrers */
class MListOfReferrers : private TRecordsStorage {
private:
        /* sizeof struct */
        const long fRecSize;
        const long fHeaderSize;

        /* true if Init was called and succeded */
        bool fInited;
public:
        /* constructor */
        MListOfReferrers();

        /* destructor */
        ~MListOfReferrers();

        bool Init(const char * a_FileName);

        /* use cache from now on... see recstor.h */
        bool InitCache(const RecNum_t a_MaxNumRecordsToBeCached);

        /* is cache enabled ? */
        bool IsCacheEnabled() { return TRecordsStorage::IsCacheEnabled();};

        /* see dmlcore.h */
        bool BeginConsistentBlock();
        bool EndConsistentBlock();
/* FIXME: add funx to handle items addition */

        /* stop using cache, frees some memory and also flushes the writes */
        bool KillCache();

        /* flush writes and close the file + cleanup stuff
         * use this before destructor, but is not necessary since it's called
           from within it if needed */
        bool DeInit();

        /* retrieve the record(contents) with the specified ID*/
        bool ReadWithID(
                        const ListOfReferrers_ID_t a_ListOfReferrers_ID,
                        ListOfReferrers_st &a_Into);

        /* write by overwritting prev data, the contents at a spec. ID */
        bool WriteWithID(
                        const ListOfReferrers_ID_t a_ListOfReferrers_ID,
                        const ListOfReferrers_st &a_From);

        /* who's the last ID
         * returns false if an error
         * bewarned that the ID may be 0 which is kNoItemID meaning there are
           no IDs */
        bool GetLastID(ListOfReferrers_ID_t &a_ListOfReferrers_ID);

        /* scather the data into the struct, used before Write */
        bool Compose(
                        ListOfReferrers_st &a_ListOfReferrers_st,
                        const ItemID_t a_HeadItemID,
                        const ItemID_t a_TailItemID);

        /* create a new one, and since it's empty we just add head and tail
           items found within the struct
         * return its ID */
        ListOfReferrers_ID_t AddNew(
                        const ListOfReferrers_st &a_ListOfReferrers_st);

private:
        bool IsInited() const { return fInited; };
        void SetInited() { fInited = true; };
        void SetNotInited() { fInited = false; };
};




#endif
