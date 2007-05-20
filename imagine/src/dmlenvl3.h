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
* Description: demlinks environment level 3
*
****************************************************************************/

#ifndef DMLENVL3_H__
#define DMLENVL3_H__

#include "dmlenvl2.h"

class MDMLFIFOBufferWithDUPs: private MDMLFIFOBuffer { //contains dups at the next tree level ie. A->B->C where B is irrelevant only A->C is seen ; and A->D->C is the A->C dup, the pointer really points to B or C to keep track of which dup is really pointed; so you actually see A->C and A->C when in reality you have A->B->C and A->D->C where B&D are temporary IDs not normally used anywhere else.
//due to lock issues(cannot create a new ID w/o making a link containing it + some other Id) we cannot seem to keep that newly created id unique, thus we let the human pass us a parameter with the intermediary ID which is supposedly have to be unique[aka inexistent already](or we fail). This affect only Push()
private:
        ENodeType_t fDomainType;
        NodeId_t fDomainId;
        DbTxn *fParentTxn;
        TLink *fWorkingOnThisTLink;

        MDMLFIFOBufferWithDUPs();//constructor
public:
        MDMLFIFOBufferWithDUPs(TLink *m_WorkingOnThisTLink);
        virtual ~MDMLFIFOBufferWithDUPs();

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
        Push(
                        const NodeId_t a_Intermediary_UniqueId, //pointer->intermediary->a_NodeId
                        const int a_PtrFlags, //kCreateNodeIfNotExists | kTruncateIfMoreThanOneNode
                        const NodeId_t a_NodeId
                        );//append node to list ie. insert last; doesn't touch the pointer (MDMLDomainPointer)
};
/****************************/
class MDMLFIFOBufferWithDUPsAndAI: public MDMLFIFOBufferWithDUPs { //AI=auto intermediaries
private:
        MDMLFIFOBufferWithDUPsAndAI();//constructor
public:
        MDMLFIFOBufferWithDUPsAndAI(TLink *m_WorkingOnThisTLink);
        ~MDMLFIFOBufferWithDUPsAndAI();
        function
        Push(
                        const NodeId_t a_NodeId
                        );//append node to list ie. insert last; doesn't touch the pointer (MDMLDomainPointer)
};
/****************************/
/****************************/
/****************************/



#endif
