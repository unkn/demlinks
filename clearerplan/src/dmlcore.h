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

#include "common.h"

#include "elem.h"
//#include "ref.h"
//#include "item.h"
//#include "list.h"
#include "list_r2e.h"
//#include "chain.h"


/* NOTE: within internal program two things may be needed: the Type and the ID,
   in order to be able to identify the compound(chain, ref, elemental)
 * if the compound is a Ref, then the Ref type must also be kept 'in mind' */


/* Demental Links Core class.
 * the connections between the compound are made in this class
 * this is what u should use */
class MDementalLinksCore :
        private MElemental,
        private MListOfRef2Elemental
{
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
        bool Init(
                        const char * a_ElementalsFileName,
                        const char * a_ListOfRef2Elemental_FileName);

        /* set all compounds to use cache from now on... see recstor.h */
        bool InitCache(const RecNum_t a_MaxNumRecordsToBeCached);

        /* is cache enabled (for all compounds) */
        bool IsCacheEnabled() { return fCache;};

        /* marks the beginning of a block which is considered incomplete until
           u do call EndConsistendBlock() below
         * a block is a bunch of writes
         * while being incomplete, a flush will not write data to disk
         * to be used only when writing compounds(chain,elemental,ref) and NOT
           lists, items
         * if the End statement is not encountered everything after Begin is
           discarded ; Begin and End concept is necessary ! (think about it) */
        bool BeginConsistentBlock();

        /* marks the end of a block which is considered CONSISTENT and thus a
           flush will write this block to the disk (data marked within beginning
           and end of this block) */
        bool EndConsistentBlock();

        /* stop using cache, frees some memory and also flushes the writes */
        bool KillCache();

        /* flush writes and close the file + cleanup stuff
         * use this before destructor, but is not necessary since it's called
           from within it if needed */
        bool DeInit();

        /* adds a new Elemental to the database with the spec BasicElement
         * doesn't check for existence of BasicElement
         * only use this if u know what you're doing */
        ElementalID_t AbsoluteAddBasicElement(
                        const BasicElement_t a_WhatBasicElement);

        /* same as above but it doesn't add a new one if already exists, instead
           it returns its ID */
        ElementalID_t AddBasicElement(
                        const BasicElement_t a_WhatBasicElement);

        /* searches for the needed BasicElement and returns its ID */
        ElementalID_t FindBasicElement(
                        const BasicElement_t a_WhatBasicElement);

        /* returns the data at specified ID */
        bool GetBasicElementWithID(
                        BasicElement_t &a_IntoBasicElement,
                        const ElementalID_t a_ElementalID);

        /* create a new empty list in the proper place:
           a list for an elemental */
        ListOfRef2Elemental_ID_t AbsoluteAddListOfRef2Elemental(
                                const ItemID_t a_HeadItem,
                                const ItemID_t a_TailItem);

private:
        bool IsInited() const { return fInited; };
        void SetInited() { fInited = true; };
        void SetNotInited() { fInited = false; };
        void SetCache() { fCache = true; };
        void SetNoCache() { fCache = false; };
};



#endif /* DMLCore.h dmental links core header */
