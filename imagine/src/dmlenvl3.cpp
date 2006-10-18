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
* Description: dmlenv level 3
*
****************************************************************************/

#include "dmlenvl3.h"
//#include "dmlenv.hpp"
#include "uniqstr.h"

/*******************************/
MDMLFIFOBufferWithDUPs :: ~MDMLFIFOBufferWithDUPs()
{//destructor
}
/*******************************/
MDMLFIFOBufferWithDUPs :: MDMLFIFOBufferWithDUPs(
                TLink *m_WorkingOnThisTLink): MDMLFIFOBuffer(m_WorkingOnThisTLink),
                                              fWorkingOnThisTLink(m_WorkingOnThisTLink)
{//constructor
}
/*******************************/
function
MDMLFIFOBufferWithDUPs :: InitFIFOWithDUPs(
                const ENodeType_t a_PtrNodeType, //use kGroup here
                const NodeId_t a_PtrId, //pointer name
                const ENodeType_t a_DomainType,
                const NodeId_t a_DomainId, //domain in which the pointer can be assigned values from.
                const int a_PtrFlags,//pointer flags
                const int a_DomFlags,//domain flags
                DbTxn *a_ParentTxn
                ) //the Domain is a synonym for TDMLCursor here
{
//------ validate params
        __tIF( a_PtrId.empty() );
        __tIF( a_DomainId.empty() );
        __tIF( kNone != a_DomFlags ); //at the time of creation, TDMLCursor didn't support any flags thus this is here to make any necessary checks in case some flags were added

//------ save some for later use
        fDomainType=a_DomainType;
        fDomainId=a_DomainId;
        fParentTxn=a_ParentTxn;

//------ inherit
        __fIFnok( MDMLFIFOBuffer :: InitFIFO( a_PtrNodeType, a_PtrId, fDomainType, fDomainId, a_PtrFlags, a_DomFlags, fParentTxn) );
//------ done
        _OK;
}

/*******************************/
function
MDMLFIFOBufferWithDUPs :: DeInit()
{
//------- inherit
        __fIFnok( MDMLFIFOBuffer :: DeInit() );
//------- done
        _OK;
}
/*******************************/
function
MDMLFIFOBufferWithDUPs :: Push(
                const NodeId_t a_Intermediary_UniqueId,
                const int a_PtrFlags,
                const NodeId_t a_NodeId
                )
{ //the FIFO pointer stays unchanged!
#ifdef SHOWKEYVAL
        cout << "MDMLFIFOBufferWithDUPs :: Push:begin"<<endl;
#endif
//------- begin
        __tIF( a_Intermediary_UniqueId.empty() ); //no NULL ptr assignements
        __tIF( a_NodeId.empty() ); //no NULL ptr assignements
//------- make a pointer with that unique Id, intermediary will point in the same sense as the Domain
        TDMLPointer uniqPtr(fWorkingOnThisTLink);
        __fIFnok( uniqPtr.InitPtr(fDomainType, a_Intermediary_UniqueId, a_PtrFlags /*kCreateNodeIfNotExists | kTruncateIfMoreThanOneNode*/ , fParentTxn) );
#define THROW_HOOK \
        __( uniqPtr.DeInit() );

//------- make this temp pointer point to a_NodeId
        _htIFnok( uniqPtr.SetPointee( a_NodeId ) );
//------- sever the link from this system to the pointer
        _hfIFnok( uniqPtr.DeInit() );
#undef THROW_HOOK

//------- inherit
        __fIFnok( MDMLFIFOBuffer :: Push( a_Intermediary_UniqueId ) );
//------- done
#ifdef SHOWKEYVAL
        cout << "MDMLFIFOBufferWithDUPs :: Push:done"<<endl;
#endif
        _OK;
}
/*******************************/
function
MDMLFIFOBufferWithDUPs :: Pull(
                        NodeId_t &m_NodeId
                )
{
#ifdef SHOWKEYVAL
        cout << "MDMLFIFOBufferWithDUPs :: Pull:begin"<<endl;
#endif
//------- begin
//------- get the unique intermediary ID
        NodeId_t tempNode;
        __fIFnok( MDMLFIFOBuffer :: Pull( tempNode ) );
        TDMLPointer uniqPtr(fWorkingOnThisTLink);
        __fIFnok( uniqPtr.InitPtr(fDomainType, tempNode, kKeepPrevValue , fParentTxn) );
#define THROW_HOOK \
        __( uniqPtr.DeInit() );

//------- get read ID
        _hfIFnok( uniqPtr.GetPointee( m_NodeId ) );

//------- done with temp pointer
        _hfIFnok( uniqPtr.DeInit() );
#undef THROW_HOOK

//------- done
#ifdef SHOWKEYVAL
        cout << "MDMLFIFOBufferWithDUPs :: Pull:done"<<endl;
#endif
        _OK;
}
/*******************************/
MDMLFIFOBufferWithDUPsAndAI :: MDMLFIFOBufferWithDUPsAndAI(TLink *m_WorkingOnThisTLink) :
        MDMLFIFOBufferWithDUPs(m_WorkingOnThisTLink)
{ //constructor
        __( MakeSureUniqueStringIsInited() );
}
/*******************************/
MDMLFIFOBufferWithDUPsAndAI :: ~MDMLFIFOBufferWithDUPsAndAI()
{ //destructor
}
/*******************************/
function
MDMLFIFOBufferWithDUPsAndAI :: Push(
                const NodeId_t a_NodeId
                )
{
//------ get a fresh uniq ID
        NodeId_t randNod;
        __tIFnok( GetUniqueString(randNod) );
//------ derrived push
        __fIFnok( MDMLFIFOBufferWithDUPs :: Push(randNod, kCreateNodeIfNotExists, a_NodeId ) );
//------ done
        _OK;
}
/*******************************/
