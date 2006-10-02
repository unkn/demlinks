/****************************************************************************
*
*                             dmental links
*    Copyright (C) June 2006 AtKaaZ, AtKaaZ at users.sourceforge.net
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
* Description: demlinks environment level 2
*
****************************************************************************/

#ifndef DMLENVL2_H__
#define DMLENVL2_H__

#include "dmlenvl1.h"

/****************************/
                        /* we need that ":public TDMLPointer {"(below) if we want to use it like in NewPoint() in setup_inputs.cpp currently lines 153 and 317 */
class MDMLDomainPointer:public TDMLPointer { //can be NULL or only values from  either a kGroup or a kSubGroup Node;
private:
        ENodeType_t fDomainType;
        NodeId_t fDomainId;

        MDMLDomainPointer();//unusable constructor; purposely made private

        function
        verify(const NodeId_t a_Pointee);
public:
        MDMLDomainPointer(TLink *m_WorkingOnThisTLink);
        ~MDMLDomainPointer();

        function
        InitDomPtr(
                const ENodeType_t a_PtrNodeType, //this should always be kGroup! otherwise you will poison the pointees which you will force them to point to you as a kSubGroup (in other words the pointee is a kGroup which will point to you as a kSubGroup, and maybe that pointee is in turn a kGroup pointer to something else and it having two kSubGroups will invalidate the pointer because a kGroup pointer is supposed to have only one kSubGroup - the pointee) anyways you could totally reverse the kSubGroup and kGroup in all items and thus this param will be useful; or for any other reason.
                const NodeId_t a_PtrId, //pointer name
                const ENodeType_t a_DomainType,
                const NodeId_t a_DomainId, //domain in which pointer can be assigned values from.
                const int a_PtrFlags=kNone,
                DbTxn *a_ParentTxn=NULL
                );

        function
        GetPointee(
                NodeId_t &m_NodeId
                );

        function
        SetPointee(
                const NodeId_t a_NodeId //if empty then pointer will be set to NULL
                );
};
/****************************/
class MDMLFIFOBuffer: private TDMLCursor, public MDMLDomainPointer { //FIFO contains only unique elements (cannot be dups)
//the pointer will point to the last item pulled, ie. you'll have to (internally done of course) try to position the pointer to next element in list and return that, if no more items then return NULL to signal that; the pointer remains on last item pulled; it is null only when there are no elements in Domain or Pull was never used(so, 2 cases).
//this workaround is accepted because berkeley db (afaik) cannot return with cursor->Get() the DB_KEYLAST element of a given key
//the pointer is NULL only when the Domain has no elements... and maybe when MDMLFIFOBuffer is inited
//ie. Domain is kGroup "A"
//elements are kSubGroups of "A"
//A->B, A->C, A->D
//when pointer points to the last element of Domain, then Pull() will have to return kFuncNotFound to signal that there are no more items left to return
//the Domain obv. contains unique elements!
private:
        MDMLFIFOBuffer();//constructor
public:
        MDMLFIFOBuffer(TLink *m_WorkingOnThisTLink);
        virtual ~MDMLFIFOBuffer();
//FIFO(first in first out) is ie. 1,2,3 get in, 1,2,3 get out in this order
        function
        InitFIFO(
                const ENodeType_t a_PtrNodeType, //use kGroup here
                const NodeId_t a_PtrId, //pointer name
                const ENodeType_t a_DomainType,
                const NodeId_t a_DomainId, //domain in which the pointer can be assigned values from.
                const int a_PtrFlags=kNone,//pointer flags
                const int a_DomFlags=kNone,//domain flags
                DbTxn *a_ParentTxn=NULL
                ); //the Domain is a synonym for TDMLCursor here

        virtual function
        DeInit();

        virtual function
        Pull(
                        NodeId_t &m_NodeId
                        //,const ETDMLFlags_t a_Flags
                        );

        virtual function
        Push(const NodeId_t a_NodeId);//append node to list ie. insert last; doesn't touch the pointer (MDMLDomainPointer)

};//MDMLFIFOBuffer
/****************************/
/****************************/
class MDMLFIFOBufferWithDUPs: private MDMLFIFOBuffer { //contains dups at the next tree level ie. A->B->C where B is irrelevant only A->C is seen ; and A->D->C is the A->C dup, the pointer really points to B or C to keep track of which dup is really pointed; so you actually see A->C and A->C when in reality you have A->B->C and A->D->C where B&D are temporary IDs not normally used anywhere else.
private:
        ENodeType_t fDomainType;
        NodeId_t fDomainId;
        DbTxn *fParentTxn;

        MDMLFIFOBufferWithDUPs();//constructor
public:
        MDMLFIFOBufferWithDUPs(TLink *m_WorkingOnThisTLink);
        ~MDMLFIFOBufferWithDUPs();

        function
        InitFIFOWithDUPs(
                const ENodeType_t a_PtrNodeType, //use kGroup here
                const NodeId_t a_PtrId, //pointer name
                const ENodeType_t a_DomainType,
                const NodeId_t a_DomainId, //domain in which the pointer can be assigned values from.
                const int a_PtrFlags=kNone,//pointer flags
                const int a_DomFlags=kNone,//domain flags
                DbTxn *a_ParentTxn=NULL
                ); //the Domain is a synonym for TDMLCursor here

        function
        DeInit();

        function
        Pull(
                        NodeId_t &m_NodeId
                        );

        function
        Push(const NodeId_t a_NodeId);//append node to list ie. insert last; doesn't touch the pointer (MDMLDomainPointer)
};
/****************************/
/****************************/
/****************************/
/****************************/



#endif
