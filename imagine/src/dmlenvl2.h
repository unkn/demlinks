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
class MDMLDomainPointer:private TDMLPointer { //can be NULL or only values from  either a kGroup or a kSubGroup Node;
private:
        MDMLDomainPointer();//unusable constructor; purposely made private
public:
        MDMLDomainPointer(TLink *m_WorkingOnThisTLink);
        ~MDMLDomainPointer();
        function
        InitPtr(
                const NodeId_t a_Domain,
                const int a_Flags=kNone,
                DbTxn *a_ParentTxn=NULL
                );

        bool
        IsInited();

        function
        GetPointee(
                NodeId_t &m_NodeId
                );

        function
        SetPointee(
                const NodeId_t a_NodeId
                );

        function
        DeInit();

};
/****************************/
class MDMLFIFOBuffer: private TDMLCursor, MDMLDomainPointer {
private:
        MDMLFIFOBuffer();//constructor
public:
        MDMLFIFOBuffer(TLink *m_WorkingOnThisTLink);
        ~MDMLFIFOBuffer();
};//MDMLFIFOBuffer
/****************************/
/****************************/

/****************************/
/****************************/
/****************************/



#endif
