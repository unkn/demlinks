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

/* elemental specific */
ElementalID_t
MDementalLinksCore::AbsoluteAddBasicElement(
                const BasicElement_t a_WhatBasicElement)
{
        LAME_PROGRAMMER_IF(!IsInited(),
                        return kNoElementalID);

        /* create a new empty list of referrers; this list contains referrers to
           elementals only */
        /* FIXME: make this in a separate func */
        ListOfRef2Elemental_st newListOfRef2Elemental_st;
        ERR_IF(!MListOfRef2Elemental::Compose(
                                newListOfRef2Elemental_st,
                                kNoItemID,/* no head, no tail */
                                kNoItemID),
                        return kNoElementalID);

        ListOfRef2Elemental_ID_t newListOfRef2Elemental_ID;
        ERR_IF(!MListOfRef2Elemental::AddNew(newListOfRef2Elemental_st),
                        return kNoElementalID);
        /* WARN: after this line we have a lost list if those below fail */

        /* put the data into the elemental struct */
        Elemental_st newElemental_st;
        ERR_IF(!MElemental::Compose(
                                newElemental_st,
                                a_WhatBasicElement,
                                newListOfRef2Elemental_ID),
                        return kNoElementalID);

        /* add the new elemental */
        ElementalID_t newElementalID;
        ERR_IF(kNoElementalID ==
                        (newElementalID = MElemental::AddNew(newElemental_st)),
                        return kNoElementalID);

        /* return its ID */
        return newElementalID;
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

        ERR_IF(!MElemental::ReadWithID(
                                a_ElementalID,
                                elemental_st),
                        return false);

        a_IntoBasicElement=elemental_st.BasicElementData;

        return true;
}


