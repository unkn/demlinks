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
MDementalLinksCore::Init(
                const char * a_ElementalsFileName,
                const char * a_ListOfRef2Elemental_FileName)
{
        LAME_PROGRAMMER_IF(IsInited(),
                        return false);

        ERR_IF(!MElemental::Init(a_ElementalsFileName),
                        return false);

        ERR_IF(!MListOfRef2Elemental::Init(a_ListOfRef2Elemental_FileName),
                        return false);
        SetInited();
        return true;
}

bool
MDementalLinksCore::InitCache(const RecNum_t a_MaxNumRecordsToBeCached)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);

        ERR_IF(!MElemental::InitCache(a_MaxNumRecordsToBeCached),
                        return false);
        ERR_IF(!MListOfRef2Elemental::InitCache(a_MaxNumRecordsToBeCached),
                        return false);

        /* check one of those since all are supposed to have the same issue :
           they react on kDisableCache and we don't want to clone that
           functionality here */
        if (MElemental::IsCacheEnabled())
                SetCache();
        else
                SetNoCache();

        return true;
}

/* <if something goes wrong, it is chained> */
bool
MDementalLinksCore::DeInit()
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);

        ERR_IF(!MElemental::DeInit(),
                        return false);

        ERR_IF(!MListOfRef2Elemental::DeInit(),
                        return false);

        SetNotInited();
        return true;
}

bool
MDementalLinksCore::KillCache()
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);

        ERR_IF(!MElemental::KillCache(),
                        return false);
        ERR_IF(!MListOfRef2Elemental::KillCache(),
                        return false);
        return true;
}

/* elemental specific
 * ensures consistency: if something fails any in mid data is not to be written
   to disk (such as the list newly created) */
ElementalID_t
MDementalLinksCore::AbsoluteAddBasicElement(
                const BasicElement_t a_WhatBasicElement)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return kNoElementalID);

        /* a new compound is being added, thus if we fail all other data created
           with it is not written to the database; if it were written it'd
           render the dbase inconsistent
         * talk about workarounds... */
        BeginConsistentBlock();

        /* create a new empty list of referrers; this list contains referrers to
           elementals only */
        ListOfRef2Elemental_ID_t newList_ID;

        ERR_IF(kNoListID ==
                        (newList_ID =
                         AbsoluteAddListOfRef2Elemental(
                                 kNoItemID,/* no head, no tail */
                                 kNoItemID)),
                        return kNoElementalID);
        /* WARN: after this line we have a lost list if those below fail */

        /* put the data into the elemental struct */
        Elemental_st newElemental_st;

        ERR_IF(!MElemental::Compose(
                                newElemental_st,
                                a_WhatBasicElement,
                                newList_ID),
                        return kNoElementalID);

        /* add the new elemental */
        ElementalID_t newElementalID;

        ERR_IF(kNoElementalID ==
                        (newElementalID =
                         MElemental::AddNew(newElemental_st)),
                        return kNoElementalID);

        EndConsistentBlock();
        /* by this time the dbase is considered to be consistent */

        /* return its ID */
        return newElementalID;
}


/* returns the ID of the specified BasicElement if exists
 * or creates a new one
 * may hang inside Find */
ElementalID_t
MDementalLinksCore::AddBasicElement(
                const BasicElement_t a_WhatBasicElement)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return kNoElementalID);

        ElementalID_t elemID;

        if (kNoElementalID ==
            (elemID = FindBasicElement(a_WhatBasicElement))) {
                ERR_IF(kNoElementalID ==
                        (elemID =
                         AbsoluteAddBasicElement(a_WhatBasicElement)),
                        return kNoElementalID);
        }

        /* return its ID */
        return elemID;
}


/* searches for the needed BasicElement and returns its ID
 * may hang inside this function */
ElementalID_t
MDementalLinksCore::FindBasicElement(
                const BasicElement_t a_WhatBasicElement)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return kNoElementalID);

        ElementalID_t foundElemID;
        /* first we must try this trick or optimization :
           the user might have created the BasicElements in order so #0 starts
           at ID 1 and so on #255 has ID 256
         * don't trace this since we might have #230 at ID 1 and have only one
           ID per total in the database */
        BasicElement_t tmpBasicElement;
        foundElemID = a_WhatBasicElement + 1;
        if (GetBasicElementWithID(tmpBasicElement,foundElemID))
                if (foundElemID != kNoElementalID)
                        return foundElemID;

        /* prev trick failed thus we perform a brute parsing */
        ElementalID_t lastElemID;
        ERR_IF(!MElemental::GetLastID(lastElemID),
                        return kNoElementalID);

        /* there are no IDs thus empty */
        if (lastElemID < kFirstElementalID)
                return kNoElementalID;

        /* parse all IDs from first to last existing and compare */
        for (foundElemID = kFirstElementalID;
             foundElemID <= lastElemID;
             foundElemID++){
                ERR_IF(!GetBasicElementWithID(tmpBasicElement,foundElemID),
                                return kNoElementalID);
                if (a_WhatBasicElement == tmpBasicElement)
                        return foundElemID;
        }

        return kNoElementalID;
}


bool
MDementalLinksCore::GetBasicElementWithID(
                BasicElement_t &a_IntoBasicElement,
                const ElementalID_t a_ElementalID
                )
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return false);

        Elemental_st elemental_st;

        /* if requested ID is out of range don't do ERR, instead only say it
           didn't work */
        ElementalID_t lastElemID;
        ERR_IF(!MElemental::GetLastID(lastElemID),
                        return false);
        if (lastElemID < a_ElementalID)
                return false;

        /* get it */
        ERR_IF(!MElemental::ReadWithID(
                                a_ElementalID,
                                elemental_st),
                        return false);

        a_IntoBasicElement=elemental_st.BasicElementData;

        return true;
}

/* create a new empty list in the proper place: a list for an elemental */
ListOfRef2Elemental_ID_t
MDementalLinksCore::AbsoluteAddListOfRef2Elemental(
                const ItemID_t a_HeadItem,
                const ItemID_t a_TailItem)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return kNoListID);

        ListOfRef2Elemental_st newList_st;
        ERR_IF(!MListOfRef2Elemental::Compose(
                                newList_st,
                                a_HeadItem,
                                a_TailItem),
                        return kNoListID);

        ListOfRef2Elemental_ID_t newList_ID;
        ERR_IF(kNoListID ==
                (newList_ID = MListOfRef2Elemental::AddNew(newList_st)),
                        return kNoListID);

        return newList_ID;
}

bool
MDementalLinksCore::BeginConsistentBlock()
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return kNoListID);

        ERR_IF(!MElemental::BeginConsistentBlock(),
                        return false);

        ERR_IF(!MListOfRef2Elemental::BeginConsistentBlock(),
                        return false);
        return true;
}


bool
MDementalLinksCore::EndConsistentBlock()
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return kNoListID);

        ERR_IF(!MElemental::EndConsistentBlock(),
                        return false);

        ERR_IF(!MListOfRef2Elemental::EndConsistentBlock(),
                        return false);
        return true;
}

