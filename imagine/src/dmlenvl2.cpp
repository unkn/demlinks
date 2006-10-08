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
* Description: dmlenv level 2
*
****************************************************************************/

#include "dmlenvl2.h"
#include "dmlenv.hpp"
#include "uniqstr.h"

/*******************************/
MDMLFIFOBuffer :: MDMLFIFOBuffer(TLink *m_WorkingOnThisTLink):
                TDMLCursor(m_WorkingOnThisTLink),
                MDMLDomainPointer(m_WorkingOnThisTLink)
{//constructor
}
/*******************************/
MDMLFIFOBuffer :: ~MDMLFIFOBuffer()
{ //destructor
}
/*******************************/
function
MDMLFIFOBuffer :: InitFIFO(
                const ENodeType_t a_PtrNodeType, //use kGroup here
                const NodeId_t a_PtrId, //pointer name
                const ENodeType_t a_DomainType,
                const NodeId_t a_DomainId, //domain in which pointer can be assigned values from.
                const int a_PtrFlags,//pointer flags
                const int a_DomFlags,//domain flags
                DbTxn *a_ParentTxn
        )//the Domain is a synonym for TDMLCursor here
{
#ifdef SHOWKEYVAL
        cout << "MDMLFIFOBuffer :: InitFIFO"<<endl;
#endif
//------ validate params
        __tIF( a_PtrId.empty() );
        __tIF( a_DomainId.empty() );
        __tIF( kNone != a_DomFlags ); //at the time of creation, TDMLCursor didn't support any flags thus this is here to make any necessary checks in case some flags were added
//------ init cursor
        __fIFnok( TDMLCursor :: InitCurs(a_DomainType, a_DomainId, a_DomFlags, a_ParentTxn) );
//------ init pointer
        __fIFnok( MDMLDomainPointer :: InitDomPtr(a_PtrNodeType, a_PtrId, a_DomainType, a_DomainId, a_PtrFlags, a_ParentTxn) );
//------ done
        _OK;
}
/*******************************/
function
MDMLFIFOBuffer :: DeInit()
{
#ifdef SHOWKEYVAL
        cout << "MDMLFIFOBuffer :: DeInit"<<endl;
#endif
//------- deinit pointer
        __fIFnok( MDMLDomainPointer :: DeInit() );
//------- deinit cursor
        __fIFnok( TDMLCursor :: DeInit() );
//------- done
        _OK;
}
/*******************************/
function
MDMLFIFOBuffer :: Push(
                const NodeId_t a_NodeId)
{
#ifdef SHOWKEYVAL
        cout << "MDMLFIFOBuffer :: Push:begin"<<endl;
#endif
//-------- check if not inited
        __tIF( ! TDMLCursor :: IsInited() );//assuming if the TDMLCursor is inited then so is the MDMLDomainPointer
//------- check if empty param
        __tIF( a_NodeId.empty() );
//------- do append
        __fIFnok( TDMLCursor :: Put(a_NodeId, kLastNode) );
//------- done
#ifdef SHOWKEYVAL
        cout << "MDMLFIFOBuffer :: Push:done"<<endl;
#endif
        _OK;
}
/*******************************/
function
MDMLFIFOBuffer :: Pull(
                        NodeId_t &m_NodeId
        //                ,const ETDMLFlags_t a_Flags //current implementation doesn't allow kMoveNode because the pointer points to the last pulled node and thus it must still exist; maybe when we can make Get(kLastNode) work (it's a berkeley db limitation)
)
{
#ifdef SHOWKEYVAL
        cout << "MDMLFIFOBuffer :: Pull:begin"<<endl;
#endif
//---------- setting flags
        //int tmpFlags=a_Flags;
        //int tmpSafeFlags=0;
        //_makeFLAG(kMoveNode);
//---------- validating flags
        //__tIF(0 != tmpFlags);//illegal flags
//---------- get pointee
        NodeId_t pointee;
        function err;
        __( err=( MDMLDomainPointer :: GetPointee(pointee) ) );
        __tIF( (kFuncOK != err) && (kFuncNULLPointer != err) );//unhandled new error;
//------- we position the cursor on the last pulled element
        //------- if pointer was not initialized then we need to get the first item of Domain
        int flags=kNone;
        if (kFuncNULLPointer == err) {
                flags=kFirstNode;
        } else if (kFuncOK == err) {
                //we got the pointee
                __tIFnok( TDMLCursor :: Find(pointee, kNone) ); //position on the current
                flags=kNextNode; //prepare to fetch the next
        }
//---------- attempting to fetch next item in list, if any
        NodeId_t next;
        __( err=( TDMLCursor :: Get(next, flags) ) );
        __tIF( (kFuncOK != err) && (kFuncNotFound != err) );//unhandled new error;
        if (kFuncOK != err) {//prolly no items in list OR no more items after current
                _fret(err);//returning "not found"
        }
//---------- if we're here we have the item, we must point to it
        __tIFnok( MDMLDomainPointer :: SetPointee(next) );//this must not fail
//---------- return the data to the caller only if full success
        m_NodeId=next;
//------- done
#ifdef SHOWKEYVAL
        cout << "MDMLFIFOBuffer :: Pull:done"<<endl;
#endif
        _OK;
}

/*******************************/
/*******************************/
MDMLDomainPointer :: MDMLDomainPointer (TLink *m_WorkingOnThisTLink):
        TDMLPointer(m_WorkingOnThisTLink)
{ //constructor
}
/*******************************/
MDMLDomainPointer :: ~MDMLDomainPointer()
{ //destructor
}
/*******************************/
/*******************************/
function
MDMLDomainPointer :: InitDomPtr(
                const ENodeType_t a_PtrNodeType,
                const NodeId_t a_PtrId, //pointer name
                const ENodeType_t a_DomainType,
                const NodeId_t a_DomainId, //domain in which pointer can be assigned values from.
                const int a_PtrFlags,
                DbTxn *a_ParentTxn
                )
{
//-------- check if already inited
        __tIF( IsInited() );
//-------- validate params
        __tIF(a_PtrId.empty());
        __tIF(a_DomainId.empty());
//-------- save these
        fDomainId=a_DomainId;
        fDomainType=a_DomainType;
//-------- init pointer
        __tIFnok( TDMLPointer::InitPtr(a_PtrNodeType, a_PtrId, a_PtrFlags, a_ParentTxn) );
//-------- end
        _OK;
}
/*******************************/
/*******************************/
function
MDMLDomainPointer :: GetPointee(
        NodeId_t &m_NodeId
        )
{
//-------- check if not inited
        __tIF( ! IsInited() );
//-------- get the pointee
        __fIFnok( TDMLPointer :: GetPointee(m_NodeId) ); //if NULL then it fails here, aka returns kFuncNULLPointer
//-------- verify that it is from domain
        __fIFnok( this->verify(m_NodeId) );
//-------- end
        _OK;
}
/*******************************/
function
MDMLDomainPointer :: SetPointee(
        const NodeId_t a_NodeId //empty is allowed => sets pointer to NULL
        )
{
//-------- check if not inited
        __tIF( ! IsInited() );
//-------- verify that it is from domain
        __if (! a_NodeId.empty() ) {
                __fIFnok( this->verify(a_NodeId) );
        }__fi
//-------- get the pointee
        __fIFnok( TDMLPointer :: SetPointee(a_NodeId) );
//-------- end
        _OK;
}
/*******************************/
function
MDMLDomainPointer :: verify(const NodeId_t a_Pointee)
{
//-------- validate param
        __tIF(a_Pointee.empty());
//-------- get environment
        TLink *link=NULL;
        __tIFnok( GetEnvironment(link) );
        __tIF(NULL == link); //some g++ ie. 3.4 have bugs that prevent assigning a pointer passed thru reference as a param to a function (above)
//-------- make sure it is from domain
        __fIFnok( link->IsLinkConsistent(fDomainType, fDomainId, a_Pointee) );
//--------- done
        _OK;
}
/*******************************/
/*******************************/
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
