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

#include "config-inc.h"
#ifndef DMLENV_H__
#define DMLENV_H__


// #include DB_HEADER //using Berkeley DB
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

/****************************/
typedef std::string NodeId_t;
/****************************/
typedef enum{
        kNone=0,//first
        kCreateNodeIfNotExists=1 //DB_WRITECURSOR on init, and DB_RMW on get()
        ,kCursorWriteLocks=2 //only used with Get() not with Put()
        ,kCurrentNode=4
        ,kThisNode=4 //alias
        ,kOverwriteNode=4 //alias
        ,kBeforeNode=8
        ,kPrevNode=8//alias
        ,kAfterNode=16 //after current or specified node
        ,kNextNode=16 //first time when used, kNextNode is kFirstNode just like DB_NEXT
        ,kFirstNode=32
        ,kLastNode=64
        ,kPinPoint=128 //ie. DB_GET_BOTH key/data pair -> makes the cursor position on this
//last:
        ,kCursorMax
} ECursorFlags_t;
/****************************/
/*forward*/class TLink;
class TDMLCursor {
private:
        ENodeType_t fNodeType;
        Dbc *fCursor;
        TLink *fLink;
        DbTxn *thisTxn;
        //Dbt fCurVal;
        Dbt fCurKey;
        NodeId_t fCurKeyStr;
        //Dbt fOriginalKey;//lame workaround FIXME: if u can parse with a cursor all data of key 'X' only! w/o getting get() to modify the key_value('X') in the process
        Db *fDb;
        u_int32_t fFlags;//of the cursor when inited Db->cursor()
        //int fOKMaxLen;//strnlen ceil
        bool fFirstTimeGet;

        TDMLCursor();//unusable constructor; purposely
public:
        TDMLCursor(TLink *m_WorkingOnThisTLink);
        ~TDMLCursor();

        bool
        TDMLCursor :: IsInited();

        function
        InitFor(
                        const ENodeType_t a_NodeType,
                        const NodeId_t a_NodeId,
                        DbTxn *a_ParentTxn,
                        const ECursorFlags_t a_Flags
                        );

        function
        Get(
                        NodeId_t &m_Node,
                        const int a_Flags
                        );

        function
        Put(
                        const NodeId_t a_Node,
                        const ECursorFlags_t a_Flags
                        );

        function
        TDMLCursor :: Put(
                        const NodeId_t a_Node1,
                        const ECursorFlags_t a_Flags,
                        const NodeId_t a_Node2
                        );

        function
        TDMLCursor :: Count(
                        db_recno_t &m_Into
                        );

        function
        DeInit();
};
/*************************/
class TLink {
private:
        int fStackLevel;

        std::string fEnvHomePath;
        std::string fDBFileName;//implied arrows

        DbEnv *fDBEnviron ;

    // Initialize our handles

        Db *g_DBGroupToSubGroup ;//GroupToSubGroup (primary)
        Db *g_DBSubGroupFromGroup ;//SubGroupFromGroup (secondary)


        TLink(){};/*inaccessible constructor*/

        function
        OpenDB(
                Db **a_DBpp,
                const std::string * const a_DBName);


        function
        showRecords(
                DbTxn *a_ParentTxn,
                ENodeType_t a_NodeType,
                char *a_Sep="==");

        function
        putInto(
                Db *a_DBInto,
                DbTxn *a_ParentTxn,
                Dbt *a_Key,
                Dbt *a_Value,
                const u_int32_t a_CursorPutFlags=0,//mandatory if m_Cursor is used below
                Dbc * const m_Cursor=NULL
               );

        function
        delFrom(
                Db *a_DBInto,
                DbTxn *a_ParentTxn,
                Dbt *a_Key,
                Dbt *a_Value);

        function
        TLink::findAndChange(
                Db *a_DBWhich,
                DbTxn *a_ParentTxn,
                Dbt *a_Key,
                Dbt *a_Value,
                Dbt *a_NewValue);

public:
/****************************PUBLIC**********/
        friend class TDMLCursor;
        TLink(
                const std::string a_EnvHomePath="dbhome",
                const std::string a_DBFileName="implied arrows.db",
                bool a_PreKill=true//delete dbase each time program runs(aprox.)
                );

        ~TLink();

        function
        TLink::ModLink(
                const NodeId_t a_GroupId,
                const NodeId_t a_SubGroupId,
                const NodeId_t a_NewLinkName,
                DbTxn *a_ParentTxn=NULL
                );


        function
        TLink::IsLinkConsistent( //checks both dbases for this link to be consistent ie. A -> B must have B <- A
                const ENodeType_t a_NodeType,
                const NodeId_t a_NodeId1,
                const NodeId_t a_NodeId2,
                DbTxn *a_ParentTxn=NULL
                );

        function
        TLink::IsGroup(
                const ENodeType_t a_NodeType,
                const NodeId_t a_GroupId,
                DbTxn *a_ParentTxn=NULL
                );

        function
        TLink::IsLink(
                const ENodeType_t a_NodeType,
                const NodeId_t a_NodeId1,
                const NodeId_t a_NodeId2,
                DbTxn *a_ParentTxn=NULL
                );
        function
        TLink::IsLink(
                const NodeId_t a_GroupId,
                const NodeId_t a_SubGroupId,
                DbTxn *a_ParentTxn=NULL
                );

        function
        TLink::NewCursorLink(
                Dbc * const m_Cursor,
                const u_int32_t a_CursorPutFlags,
                const ENodeType_t a_NodeType,
                const NodeId_t a_NodeId1,
                const NodeId_t a_NodeId2,
                DbTxn *a_ParentTxn=NULL
                );

        //ie. NewLink(kSubGroup,"sub1","grp1") // => grp1 -> sub1
        function
        TLink::NewLink(
                const ENodeType_t a_NodeType,
                const NodeId_t a_NodeId1,//may or may not exist
                const NodeId_t a_NodeId2,//same
                DbTxn *a_ParentTxn=NULL
                );

        //links are created, groups are just there as part of links, they don't have to already exist(and if they do they're part of the links only)
        function
        TLink::NewLink(
                const NodeId_t a_GroupId,
                const NodeId_t a_SubGroupId,
                DbTxn *a_ParentTxn=NULL
                );
//FIXME: need to add iterator (aka cursor) which parses a Group's kTo connections(this group pointing to many subgroups) or a SubGroup's kFrom(many groups pointing to this subgroup) connections
        function
        TLink::ShowContents(
                DbTxn *a_ParentTxn=NULL
                );

        function
        KillDB(
                const std::string * const a_PathFN,
                const std::string * const a_FName
                );


        function
        NewTransaction(DbTxn * a_ParentTxn,
                        DbTxn ** a_NewTxn,
                        const u_int32_t a_Flags=TRANSACTION_FLAGS
                        );


/****************************/
        function
        Commit(DbTxn **a_Txn);

        function
        Abort(DbTxn **a_Txn);

/*******************************/
/****************************/
/****************************/

/****************************/
/****************************/
/****************************/
};/*class*/



#endif
