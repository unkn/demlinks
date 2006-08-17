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
* Description: common used stuff between compounds
*
****************************************************************************/


#ifndef COMMON___H
#define COMMON___H

#pragma pack(1) //allign structs at one byte, prior was four
//FIXME: gcc has some other way of doing this, make it so with defining a macro
//and use it after the struct; ifdef __WATCOMC__ define it to nothing, since
//watcom does know about pack(1)

#include "recstor.h"

/* NOTE : valid IDs are never zero or less */
//identifies no valid ID; this must be 0L don't change!!!
#define kNoID long(kInvalidRecNum)
#define kFirstID long(kFirstRecNum)

#define kNoRefID kNoID

#define kNoRef2RefID kNoRefID
#define kNoRef2ChainID kNoRefID
#define kNoRef2ElemID kNoRefID
#define kNoElementalID kNoID
#define kNoItemID kNoID
#define kNoListID kNoID
#define kNoChainID kNoID

#define kFirstElementalID kFirstID

/* an ID is like a handle */

/* counting on the fact that true and false are both positive >= 0 numbers */
typedef unsigned long AnyID_t;

/* Elemental is the base undivisible element of demlinks
 * ie. chars from #0 to #255
 * as said before, elementals cannot be part of a chain, instead use a
   Ref2Elemental and put this in a chain ~ the fundamental idea behind demlinks
 */

typedef AnyID_t ElementalID_t;


/* the double 'r' dilEma: Refer, Referable, Referred, Referring, Referrence */
typedef unsigned char ReferrerTypes_t;
/* this is while the ID is kNoRefID */
const ReferrerTypes_t kRefToNone='0';
/* while the ID is of a valid Ref and not kNoRefID;
 * apply2the following three: */
const ReferrerTypes_t kRefToRef='R';
const ReferrerTypes_t kRefToChain='C';
const ReferrerTypes_t kRefToElemental='E';


/* the internal_type which is the base type of all three types of referrers
   Ref2Ref, Ref2Chain and Ref2Elemental
 * I don't have to tell you how important it is that sizeof all three types must   be equal, also the sizeof all three IDs, if not we couldn't store the ID or
   type within the same ammount of space(prereserved) ie. if Ref2RefID would be
   4 bytes long and Ref2ElementalID would be 1 byte long; */
typedef AnyID_t AnyReferrerID_t;
typedef AnyReferrerID_t Ref2ReferrerID_t;
typedef AnyReferrerID_t Ref2ChainID_t;
typedef AnyReferrerID_t Ref2ElementalID_t;



/* an item of list ; an Item is to be thought of as one the element of a List */typedef AnyID_t ItemID_t;/* the ID of the item, the item from the list */

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



typedef AnyID_t ListOfReferrers_ID_t; /* the ID of the list */

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


/* chain of referrers this is the only type of chain: of referrers */
typedef AnyID_t ChainID_t;

/* CHAIN
 * a chain can contain only referrers, no elementals or other chains within it
 * a chain actually consist of only a head and a tail pointers to first resp.
   last referrers in the chain
 */
struct Chain_st {
        /* points to first referrer in chain
         * the chain can temporarily be empty if this and TailID are kNoRefID */        ReferrerTypes_t HeadReferrerType;
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

typedef ListOfReferrers_ID_t ListOfRef2Elemental_ID_t;

typedef ListOfReferrers_st ListOfRef2Elemental_st;

typedef unsigned char BasicElement_t;/* for starters limited range #0..#255 */


struct Elemental_st {
        /* ie. chars from #0 to #255 OR from #0 to #2GB
         * but here lies only one of those chars */
        BasicElement_t BasicElementData;

        /* list of those referrers which point to us, us the elemental */
        ListOfRef2Elemental_ID_t ListOfRef2Elemental_ID;/* ID of the list */
};




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




#endif
