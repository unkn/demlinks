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


#ifndef DMLCORE__H
#define DMLCORE__H

#pragma pack(1) //allign structs at one byte, prior was four
//FIXME: gcc has some other way of doing this, make it so with defining a macro
//and use it after the struct; ifdef __WATCOMC__ define it to nothing, since
//watcom does know about pack(1)

#include "recstor.h"

/* NOTE : valid IDs are never zero or less */
#define kNoID 0L //identifies no valid ID; this must be 0L don't change!!!
#define kNoRefID kNoID
#define kNoItemID kNoID
#define kNoChainID kNoID
#define kNoListID kNoID

#define kNoElementalID kNoID
#define kNoRef2RefID kNoRefID
#define kNoRef2ChainID kNoRefID
#define kNoRef2ElemID kNoRefID

/* an ID is like a handle */

/* counting on the fact that true and false are both positive >= 0 numbers */
typedef unsigned long AnyID_t;

/* chain of referrers this is the only type of chain: of referrers */
typedef AnyID_t ChainID_t;

/* the double 'r' dilEma: Refer, Referable, Referred, Referring, Referrence */
typedef unsigned char ReferrerTypes_t;
/* this is while the ID is kNoRefID */
const ReferrerTypes_t kRefToNone='0';
/* while the ID is of a valid Ref and not kNoRefID;
 * apply2the following three: */
const ReferrerTypes_t kRefToRef='R';
const ReferrerTypes_t kRefToChain='C';
const ReferrerTypes_t kRefToElemental='E';

/* NOTE: within internal program two things may be needed: the Type and the ID,
   in order to be able to identify the compound(chain, ref, elemental)
 * if the compound is a Ref, then the Ref type must also be kept 'in mind' */

/* a list; currently while talkin'bout a list we actually talk about a list of
   referrers; referrers can be of any of the above three types */
typedef AnyID_t ListOfReferrers_ID_t; /* the ID of the list */

/* Elemental is the base undivisible element of demlinks
 * holds the human language ;) ie. chars from #0 to #255
 * as said before, elementals cannot be part of a chain, instead use a
   Ref2Elemental and put this in a chain ~ the fundamental idea behind demlinks
 */

typedef AnyID_t ElementalID_t;
typedef unsigned char BasicElement_t;/* for starters limited range #0..#255 */
struct Elemental_st {
        /* ie. chars from #0 to #255 OR from #0 to #2GB
         * but here lies only one of those chars */
        BasicElement_t BasicElementData;

        /* list of those referrers which point to us, us the elemental */
        ListOfReferrers_ID_t ListOfRef2Elemental_ID;/* ID of the list */
};


/* an item of list ; an Item is to be thought of as one the element of a List */
typedef AnyID_t ItemID_t;/* the ID of the item, the item from the list */

/* a LIST of referrers */
struct ListOfReferrers_st {
        /* pointer to head item of the list
         * can be kNoItemID which means there're no items associated with
           this list
         */
        ItemID_t HeadItemID;
        /* pointer to tail item of the list
         * can be kNoItemID which means there're no items associated with
           this list
         */
        ItemID_t TailItemID;
};

/* the internal_type which is the base type of all three types of referrers
   Ref2Ref, Ref2Chain and Ref2Elemental
 * I don't have to tell you how important it is that sizeof all three types must
   be equal, also the sizeof all three IDs, if not we couldn't store the ID or
   type within the same ammount of space(prereserved) ie. if Ref2RefID would be
   4 bytes long and Ref2ElementalID would be 1 byte long; */
typedef AnyID_t AnyReferrerID_t;
typedef AnyReferrerID_t Ref2ReferrerID_t;
typedef AnyReferrerID_t Ref2ChainID_t;
typedef AnyReferrerID_t Ref2ElementalID_t;

/* this is a referrer, which point(refers) to an Elemental */
struct Ref2Elemental_st {
        /* the Elemental this referrece refers to */
        ElementalID_t ElementalID;

        /* pointer to father chain, the chain of which we're part of */
        ChainID_t FatherChainID;

        /* prev and next referrers in chain */
        ReferrerTypes_t PrevType;
        AnyReferrerID_t PrevID;

        ReferrerTypes_t NextType;
        AnyReferrerID_t NextID;

        /* a list of those referrers which refer to us, us being a ref tho. */
        ListOfReferrers_ID_t ListOfRef2Ref_ID;
};

/* this is a referrer, which point(refers) to a Chain */
struct Ref2Chain_st {
        /* the Chain this referrece refers to */
        ChainID_t ChainID;

        /* pointer to father chain, the chain of which we're part of */
        ChainID_t FatherChainID;

        /* prev and next referrers in chain */
        ReferrerTypes_t PrevType;
        AnyReferrerID_t PrevID;

        ReferrerTypes_t NextType;
        AnyReferrerID_t NextID;

        /* a list of those referrers which refer to us, us being a ref tho. */
        ListOfReferrers_ID_t ListOfRef2Ref_ID;
};

/* this is a referrer, which point(refers) to another referrer
 * cannot refer to itself */
struct Ref2Referrer_st {
        /* the referrer this referrer refers to */
        ReferrerTypes_t ReferrerType;
        AnyReferrerID_t ReferrerID;

        /* pointer to father chain, the chain of which we're part of */
        ChainID_t FatherChainID;

        /* prev and next referrers in chain */
        ReferrerTypes_t PrevType;
        AnyReferrerID_t PrevID;

        ReferrerTypes_t NextType;
        AnyReferrerID_t NextID;

        /* a list of those referrers which refer to us, us being a ref tho. */
        ListOfReferrers_ID_t ListOfRef2Ref_ID;
};

/* an ITEM from a list
 * an ITEM actually holds pointers to a referrer of any of the three types
 * (it holds the ID of the referrer)
 * it also holds pointers to prev and next items from this list
 * an item can be only in one list */
struct Item_st {
        /* this ID could be of any of the three Referrer types
         * however the types are not stored, are deduced from those structs
           which use the lists */
        AnyReferrerID_t ReferrerID;
        /* prev and next items in list, kNoItemID means no more items */
        ItemID_t PrevItem;
        ItemID_t NextItem;
};


/* CHAIN
 * a chain can contain only referrers, no elementals or other chains within it
 * a chain actually consist of only a head and a tail pointers to first resp.
   last referrers in the chain
 */
struct Chain_st {
        /* points to first referrer in chain
         * the chain can temporarily be empty if this and TailID are kNoRefID */
        ReferrerTypes_t HeadReferrerType;
        AnyReferrerID_t HeadReferrerID;

        /* points to last referrer in chain */
        ReferrerTypes_t TailReferrerType;
        AnyReferrerID_t TailReferrerID;

        /* list of those referrers which refer to a chain, latter being us.
         * could be kNoListID */
        ListOfReferrers_ID_t ListOfRef2Chain_ID;
        /* automaticly assuming type of items in list to be kRef2Chain ;
         * actually when we say 'item' (above) we mean the ID of the Referrer
           stored within that item ie. item.ReferrerID */
};

/* classes */

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

        /* who's the last ID in database */
        bool GetLastID(ElementalID_t &a_ElementalID);

        /* scather the data into the struct, used before Write */
        bool Compose(
                        Elemental_st &a_Elemental_st,
                        const BasicElement_t a_BasicElementData,
                        const ListOfReferrers_ID_t a_ListOfRef2Elemental_ID);

        /* append a new elemental to the list/database of existing ones
         * return its ID or kNoElementalID if failed */
        ElementalID_t AddNew(const Elemental_st a_Elemental_st);

private:
        bool IsInited() const { return fInited; };
        void SetInited() { fInited = true; };
        void SetNotInited() { fInited = false; };
};

class MDementalLinksCore : private MElemental {
private:
        /* true if Init was called and succeded */
        bool fInited;

        /* true if all compounds have Cache enabled (there cannot be any other
           way: either all have cache or none has cache) */
        bool fCache;
public:
        /* constructor */
        MDementalLinksCore();
        /* destructor */
        ~MDementalLinksCore();

        /* open the files + init stuff
         * use this after constructor somewhere */
        bool Init(const char * a_ElementalsFileName);

        /* set all compounds to use cache from now on... see recstor.h */
        bool InitCache(const RecNum_t a_MaxNumRecordsToBeCached);

        /* is cache enabled (for all compounds) */
        bool IsCacheEnabled() { return fCache;};

        /* stop using cache, frees some memory and also flushes the writes */
        bool KillCache();

        /* flush writes and close the file + cleanup stuff
         * use this before destructor, but is not necessary since it's called
           from within it if needed */
        bool DeInit();


private:
        bool IsInited() const { return fInited; };
        void SetInited() { fInited = true; };
        void SetNotInited() { fInited = false; };
        void SetCache() { fCache = true; };
        void SetNoCache() { fCache = false; };
};

#endif /* DMLCore.h dmental links core header */
