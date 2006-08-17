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
                const ListOfReferrers_st &a_ListOfReferrers_st)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return kNoListID);

        /* the struct contains pointers to Head and Tail Items which items
           must be created(those items) in case they're not kNoItemID */

        /* FIXME: */


        /* back to the list */
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


bool
MListOfReferrers::BeginConsistentBlock()
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return kNoElementalID);

        ERR_IF(!TRecordsStorage::BeginConsistentBlock(),
                        return false);

        return true;
}

bool
MListOfReferrers::EndConsistentBlock()
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return kNoElementalID);

        ERR_IF(!TRecordsStorage::EndConsistentBlock(),
                        return false);

        return true;
}

