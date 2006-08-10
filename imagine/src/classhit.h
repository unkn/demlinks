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

#ifndef CLASSHIT_H__
#define CLASSHIT_H__

#include <db_cxx.h> //using Berkeley DB
#include "pnotetrk.h"

/*************debug vars*/
//show debug statistics such as key+value
#define SHOWKEYVAL
//#define SHOWCONTENTS
//#define SHOWTXNS
/*************/

/*******************MACROS**************/

//*************CONSTANTS **********
#define MAX_GROUPNAME_LEN 65530

typedef enum {
        kSubGroup = 1,
        kGroup
} EIdType_t;

/****************************/
/****************************/
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
                Db *a_DB,
                char *a_Sep="==");

        function
        putInto(
                Db *a_DBInto,
                DbTxn *a_ParentTxn,
                Dbt *a_Key,
                Dbt *a_Value);

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
        TLink(
                const std::string a_EnvHomePath="dbhome",
                const std::string a_DBFileName="implied arrows.db",
                bool a_PreKill=true//delete dbase each time program runs(aprox.)
                );

        ~TLink();

        function
        TLink::ModLink(
                const std::string a_GroupId,
                const std::string a_SubGroupId,
                const std::string a_NewLinkName,
                DbTxn *a_ParentTxn=NULL
                );


        function
        TLink::IsGroup(
                const EIdType_t a_IdType,
                const std::string a_GroupId,
                DbTxn *a_ParentTxn=NULL
                );

        function
        TLink::IsLink(
                const std::string a_GroupId,
                const std::string a_SubGroupId,
                DbTxn *a_ParentTxn=NULL
                );

        //links are created, groups are just there as part of links, they don't have to already exist(and if they do they're part of the links only)
        function
        TLink::NewLink(
                const std::string a_GroupId,
                const std::string a_SubGroupId,
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
                        const u_int32_t a_Flags=DB_TXN_NOSYNC
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
