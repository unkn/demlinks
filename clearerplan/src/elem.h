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
* Description: the part of demlinks which interfaces Elementals
*
****************************************************************************/


#ifndef ELEM_____H
#define ELEM_____H

#include "common.h"


class MElemental : private TRecordsStorage {
private:
        /* sizeof Elemental_st struct */
        const long fRecSize;
        const long fHeaderSize;

        /* true if Init was called and succeded */
        bool fInited;
public:
        /* constructor */
        MElemental();
        /* destructor */
        ~MElemental();

        /* open the file + init stuff
         * use this after constructor somewhere */
        bool Init(const char * a_FileName);

        /* use cache from now on... see recstor.h */
        bool InitCache(const RecNum_t a_MaxNumRecordsToBeCached);

        /* is cache enabled ? */
        bool IsCacheEnabled() { return TRecordsStorage::IsCacheEnabled();};

        /* see dmlcore.h */
        bool BeginConsistentBlock();
        bool EndConsistentBlock();


        /* stop using cache, frees some memory and also flushes the writes */
        bool KillCache();

        /* flush writes and close the file + cleanup stuff
         * use this before destructor, but is not necessary since it's called
           from within it if needed */
        bool DeInit();

        /* retrieve the record(contents) of an Elemental with the specified ID*/
        bool ReadWithID(
                        const ElementalID_t a_ElementalID,
                        Elemental_st &a_Into);

        /* write by overwritting prev data, the contents at a spec. ID */
        bool WriteWithID(
                        const ElementalID_t a_ElementalID,
                        const Elemental_st &a_From);

        /* who's the last ID in database
         * returns false if an error
         * bewarned that the ID may be 0 which is kNoItemID meaning there are
           no IDs */
        bool GetLastID(ElementalID_t &a_ElementalID);

        /* scather the data into the struct, used before Write */
        bool Compose(
                        Elemental_st &a_Elemental_st,
                        const BasicElement_t a_BasicElementData,
                        const ListOfReferrers_ID_t a_ListOfRef2Elemental_ID);

        /* create a new elemental
         * ( also creates its list of refferers *doh* )
         * return its ID or kNoElementalID if failed */
        ElementalID_t AddNew(const Elemental_st &a_Elemental_st);

private:
        bool IsInited() const { return fInited; };
        void SetInited() { fInited = true; };
        void SetNotInited() { fInited = false; };
};



#endif
