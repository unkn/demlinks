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
* Description: the part of demlinks which interfaces Elementals
*
****************************************************************************/


#include "_gcdefs.h" /* first */
/* personalized notification tracking capabilities */
#include "pnotetrk.h"

#include "elem.h"

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


/* LastID may be kNoID if there are no IDs, also it may fail thus return false*/

bool
MElemental::GetLastID(
                ElementalID_t &a_ElementalID)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);

        ERR_IF(kBadRecCount ==
                        (a_ElementalID = TRecordsStorage::GetNumRecords()),
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
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);

        ERR_IF(!TRecordsStorage::InitCache(a_MaxNumRecordsToBeCached),
                        return false);
        return true;
}
bool
MElemental::KillCache()
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);

        ERR_IF(!TRecordsStorage::KillCache(),
                        return false);
        return true;
}

ElementalID_t
MElemental::AddNew(
                const Elemental_st &a_Elemental_st)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return kNoElementalID);

        ElementalID_t newElementalID;
        ERR_IF(!GetLastID(newElementalID),
                        return kNoElementalID);

        /* can be 0++ */
        newElementalID++;

        ERR_IF(!WriteWithID(
                                newElementalID,
                                a_Elemental_st),
                        return kNoElementalID);

        return newElementalID;
}

bool
MElemental::BeginConsistentBlock()
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return kNoElementalID);

        ERR_IF(!TRecordsStorage::BeginConsistentBlock(),
                        return false);

        return true;
}

bool
MElemental::EndConsistentBlock()
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return kNoElementalID);

        ERR_IF(!TRecordsStorage::EndConsistentBlock(),
                        return false);

        return true;
}

