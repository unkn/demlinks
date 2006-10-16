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
* Description: demlinks environment level 1
*
****************************************************************************/

#ifndef DMLENVL1_H__
#define DMLENVL1_H__

#include "dmlenv.h"
#include "dmlenvl0.h"

/****************************/
/*forward*/class TLink;
/****************************/
class TDMLPointer {//is a kGroup pointing to a kSubGroup where kGroup is the pointer identifier and kSubGroup it the pointed element (and not the other way around!) thus the pointer is kGroup and the pointee is kSubGroup
        //can also be a kSubGroup pointing to a kGroup where kSubGroup is the pointer id and kGroup is the pointed element
private:
        TLink*          fLink;
        NodeId_t        fPointerId;
        ENodeType_t     fNodeType;
        DbTxn*          fParentTxn;
        bool            fInited;

        TDMLPointer();//unusable constructor; purposely made private
public:
        TDMLPointer(TLink *m_WorkingOnThisTLink);
        virtual ~TDMLPointer();
//forgetting to use virtual to functions defined in the derived class will make the program use the base functions(these) instead of those that are defined in derrived class (apparently :)
        virtual function
        InitPtr(
                const ENodeType_t a_NodeType,
                const NodeId_t a_NodeId,
                const int a_Flags=kNone,
                DbTxn *a_ParentTxn=NULL
                );

        virtual bool
        IsInited();

        virtual function
        GetPointerId(
                NodeId_t &m_NodeId
                );

        virtual function
        GetEnvironment(
                TLink * &m_TLink
        );

        virtual function
        GetPointee(
                NodeId_t &m_NodeId
                );

        virtual function
        SetPointee(
                const NodeId_t a_NodeId //if empty() then pointer will be set to null
                );

        virtual function
        DeInit();//just unlinks this class instance with the demlinks environment pointer and related stuff (so the dmlenv pointer is not destroyed within the demlinks environment, only the class instance link to it; in other words the instance of this class lives in the C++ enviroment(runtime) and the dmlenv pointer lives in the demlinks environment AND DeInit here destroys the class instance only). All DeInit() funx should do this!


};
/****************************/
/****************************/
class TDMLCursor {
private:
        ENodeType_t fNodeType;
        Dbc *fCursor;
        TLink *fLink;
        DbTxn *fThisTxn;
        Dbt fCurKey;
        NodeId_t fCurKeyStr;
        Db *fDb;
        u_int32_t fFlags;//of the cursor when inited Db->cursor()
        bool fFirstTimeGet;

        TDMLCursor();//unusable constructor; purposely
public:
        TDMLCursor(TLink *m_WorkingOnThisTLink);
        ~TDMLCursor();

        bool
        IsInited();

        function
        InitCurs(
                        const ENodeType_t a_NodeType,
                        const NodeId_t a_NodeId, //key
                        const int a_Flags=kNone,
                        DbTxn *a_ParentTxn=NULL
                        );

        function
        Find(//autopositions if found
                        NodeId_t &m_Node,
                        const int a_Flags=kNone //here, allowing perhaps kCursorWriteLocks
                        );

        function
        Del( //deletes current item
                        const ETDMLFlags_t a_Flags,
                        const NodeId_t a_Node=""
                        );

        function
        Get(
                        NodeId_t &m_Node,
                        const int a_Flags//combination of flags
                        );

        function
        Put(
                        const NodeId_t a_Node,
                        const ETDMLFlags_t a_Flags
                        );

        function
        Put(
                        const NodeId_t a_Node1,
                        const ETDMLFlags_t a_Flags,
                        const NodeId_t a_Node2
                        );

        function
        Count(
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

        //we use two tables for performance ie. if we use only table A->B then searching for all pointees B would be timeconsuming and harddisk stressing not to mention other things; we can safely assume (at this point) that space is not gonna be a problem; not even optimisation is supposed to be one; but understanding the things via the way brain works is the main goal ie. everything should be linked on the computer as it is in the brain(but not at the neuron level *doh*; at a higher level);
        Db *g_DBGroupToSubGroup ;//GroupToSubGroup (primary) forward links table
        Db *g_DBSubGroupFromGroup ;//SubGroupFromGroup (secondary) reverse links table


        TLink(){};/*inaccessible constructor*/

        function
        openDB(
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
                Dbc * const m_Cursor=NULL);

        function
        delFrom(
                Db *a_DBInto,
                DbTxn *a_ParentTxn,
                Dbt *a_Key,
                Dbt *a_Value);

        function
        findAndChange(
                Db *a_DBWhich,
                DbTxn *a_ParentTxn,
                Dbt *a_Key,
                Dbt *a_Value,
                Dbt *a_NewValue);

public:
/****************************PUBLIC**********/
        friend class TDMLCursor;
        friend class TDMLPointer;
        TLink(
                const std::string a_EnvHomePath="dbhome",
                const std::string a_DBFileName="implied arrows.db",
                bool a_PreKill=true//delete dbase each time program runs(aprox.)
                );

        ~TLink();

        /*function
        TLink::ModLink(
                const NodeId_t a_GroupId,
                const NodeId_t a_SubGroupId,
                const NodeId_t a_NewLinkName,
                DbTxn *a_ParentTxn=NULL
                );
*/

        function //uses TDMLCursor::Get()
        IsLinkConsistent( //checks both dbases for this link to be consistent ie. A -> B must have B <- A
                const ENodeType_t a_NodeType,
                const NodeId_t a_NodeId1,
                const NodeId_t a_NodeId2,
                DbTxn *a_ParentTxn=NULL
                );

        function
        IsSemiLink(
                const ENodeType_t a_NodeType,
                const NodeId_t a_NodeId1,
                NodeId_t &m_NodeId2,//if empty then returns first complementary node of a_NodeId1; DB_SET acts like old IsNode() [or IsGroup()]
                DbTxn *a_ParentTxn=NULL
                );

        function
        NewCursorLink( //uses TDMLCursor::Put()
                Dbc * const m_Cursor,
                const u_int32_t a_CursorPutFlags,
                const ENodeType_t a_NodeType,
                const NodeId_t a_NodeId1,
                const NodeId_t a_NodeId2,
                DbTxn *a_ParentTxn=NULL
                );

        //ie. NewLink(kSubGroup,"sub1","grp1") // => grp1 -> sub1
        function
        NewLink(
                const ENodeType_t a_NodeType,
                const NodeId_t a_NodeId1,//may or may not exist
                const NodeId_t a_NodeId2,//same
                DbTxn *a_ParentTxn=NULL
                );

        //links are created, groups are just there as part of links, they don't have to already exist(and if they do they're part of the links only)
        function
        NewLink(
                const NodeId_t a_GroupId,
                const NodeId_t a_SubGroupId,
                DbTxn *a_ParentTxn=NULL
                );

/****************************/
        function
        ShowContents(
                DbTxn *a_ParentTxn=NULL
                );

        function
        KillDB(
                const std::string * const a_PathFN,
                const std::string * const a_FName
                );


/****************************/
        function
        NewTransaction(DbTxn * a_ParentTxn,
                        DbTxn ** m_NewTxn,
                        const u_int32_t a_Flags=TRANSACTION_FLAGS
                        );


        function
        Commit(DbTxn **m_Txn);

        function
        Abort(DbTxn **m_Txn);

};/*class*/
/*******************************/
/****************************/
/****************************/

/****************************/
/****************************/
/****************************/



#endif
