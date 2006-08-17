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
