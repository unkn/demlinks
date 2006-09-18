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
* Description: demlinks environment level 0
*
****************************************************************************/

#ifndef DMLENVL0_H__
#define DMLENVL0_H__

#include "dmlenv.h"

/*******************************/
function
_commit(DbTxn **a_Txn, int *m_StackVar=NULL);
/*******************************/
function
_abort(DbTxn **a_Txn, int *m_StackVar=NULL);
/*******************************/
function
_newTransaction(
                DbEnv *m_DBEnviron,
                        DbTxn * a_ParentTxn, //can be NULL
                        DbTxn ** a_NewTxn,
                        int *m_StackVar=NULL,
                        const u_int32_t a_Flags=TRANSACTION_FLAGS
                        );
/*******************************/
function
_findAndChange( //affecting only the value of the a_Key, aka the key cannot be changed anyways(u must do del then put for that)
                DbEnv *m_DBEnviron,
                Db *a_DBWhich,
                DbTxn *a_ParentTxn,
                Dbt *a_Key,
                Dbt *a_Value,
                Dbt *a_NewValue,
                int *m_StackVar=NULL);
                //we fail(not throw) if newval already exists
/****************************/
function
_delFrom(
                DbEnv *m_DBEnviron,
                Db *a_DBInto,
                DbTxn *a_ParentTxn,//can be NULL
                Dbt *a_Key,
                Dbt *a_Value,
                int *m_StackVar=NULL);
/****************************/
function
_putInto(
                DbEnv *m_DBEnviron,
                Db *a_DBInto,
                DbTxn *a_ParentTxn,//can be null
                Dbt *a_Key,
                Dbt *a_Value,
                int *m_StackVar=NULL,
                const u_int32_t a_CursorPutFlags=0,
                Dbc *const m_Cursor=NULL //can be NULL, then put() is used thus appended to the list of data of that key(ie. order of insertion)
                );
/****************************/
function
_newCursorLink(//creates a consistent link between two nodes in the sense selected by a_NodeType below
                DbEnv *m_DBEnviron,
                Db * m_G2sGDb,
                Db * m_sG2GDb,
                Dbc * const m_Cursor,
                const u_int32_t a_CursorPutFlags,
                const ENodeType_t a_NodeType,
                const NodeId_t a_NodeId1,//may or may not exist
                const NodeId_t a_NodeId2,//same
                int *m_StackVar=NULL,
                DbTxn *a_ParentTxn=NULL //no child transaction created except with putInto(); apparently the cursor may not span transactions RTFM;
                );
/****************************/
/****************************/



#endif
