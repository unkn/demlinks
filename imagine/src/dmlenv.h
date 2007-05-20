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
* Description: implied arrows 12June2006
*
****************************************************************************/

#ifndef DMLENV_H__
#define DMLENV_H__

#include <demlinks-config.h>
#include DML_DB_HEADER //using Berkeley DB
#include "pnotetrk.h"

/*************debug vars*/
//see dmlenv.cpp file (this header's cpp file)
/*************/

/*******************MACROS**************/
#define TRANSACTION_FLAGS DB_TXN_NOSYNC
//*************CONSTANTS **********
#define MAX_GROUPNAME_LEN 65530

typedef enum {
        kSubGroup = 1,
        kGroup
} ENodeType_t; //a Node is either a kGroup or a kSubGroup; however it can be both depending on p.o.v.

typedef enum {
        kLink=1, //A->B & B<-A
        kForwardLink, //A->B
        kReverseLink, //B<-A
        kSelfLink, //A->A & A<-A
        kComplementaryLink, //if FL then CL=RL; else if RL then CL=FL; else fail;
} ELinkType_t;

/****************************/
typedef std::string NodeId_t;
/****************************/
typedef enum{
        kNone=0,//first
        kCreateNodeIfNotExists=1 //DB_WRITECURSOR on init, and DB_RMW on get(); also used with TDMLPointer
        ,kCursorWriteLocks=2 //only used with Get() not with Put()
        ,kKeepPrevValue=kCursorWriteLocks //used with TDMLPointer
        ,kCurrentNode=4
        ,kThisNode=kCurrentNode //alias
        ,kOverwriteNode=kCurrentNode //alias; TDMLPointer: if just one node exists then it get overwritten; if more nodes exit we fail;
        ,kMoveNode=kCurrentNode //used with MDMLFIFOBuffer
        ,kBeforeNode=8
        ,kPrevNode=kBeforeNode //alias
        ,kAfterNode=16 //after current or specified node
        ,kNextNode=kAfterNode //first time when used, kNextNode is kFirstNode just like DB_NEXT
        ,kFirstNode=32
        ,kLastNode=64
        ,kTruncateIfMoreThanOneNode=kLastNode //TDMLPointer;if more than one, remove the others first; otherwise fail
        ,kPinPoint=128 //ie. DB_GET_BOTH key/data pair -> makes the cursor position on this
//last:
        ,kCursorMax
} ETDMLFlags_t;
/****************************/
/****************************/

/****************************/
/****************************/
/****************************/



#endif
