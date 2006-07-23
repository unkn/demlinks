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

#include <iostream>
#include <sys/stat.h>
#include <sys/types.h>
#include <allegro.h>

#include "classhit.h"
#include "pnotetrk.h"

using namespace std;

/****************************/
const char * TLErrorStrings[kMaxTLinkErrors]={
                "All OK."
                ,"Already exists"
                ,"Not found"
};
/****************************/
/*****************************************************************/
void
TLShowError(const ETLinkErrors_t a_Err)
{
#define THROW_HOOK ;
        _tIF( a_Err < 0);
        _tIF( a_Err >= kMaxTLinkErrors);
        cout << TLErrorStrings[a_Err]<<endl;
#undef THROW_HOOK
}
/*****************************************************************/
/********************************************************/

#define ABORT_HOOK \
        __(this->Abort(&thisTxn));

#define ENVCLOSE_HOOK \
        if (fDBEnviron != NULL) { \
            /*throws*/__(fDBEnviron->close(0););\
                fDBEnviron=NULL;       \
        }

#define DB1CLOSE_HOOK \
        if (g_DBGroupToSubGroup != NULL) { \
            /*not*/___(g_DBGroupToSubGroup->close(0)); \
                g_DBGroupToSubGroup=NULL;\
        }

#define DB2CLOSE_HOOK \
        if (g_DBSubGroupFromGroup != NULL) { \
            /*not*/___(g_DBSubGroupFromGroup->close(0)); \
                g_DBSubGroupFromGroup=NULL;\
        }

//berkeley db environ+dbases shutdown on exception, used only where aplicable
#define SAFEBDBCLOSE_HOOK \
        DB1CLOSE_HOOK\
        DB2CLOSE_HOOK\
        ENVCLOSE_HOOK


/****************************/
ETLinkErrors_t
TLink::findAndChange(
                Db *a_DBWhich,
                DbTxn *a_ParentTxn,
                Dbt *a_Key,
                Dbt *a_Value,
                Dbt *a_NewValue)//we fail(not throw) if newval already exists
{
#define THROW_HOOK
        _tIF(NULL==a_DBWhich);


        DbTxn *thisTxn;
        _(NewTransaction(a_ParentTxn,&thisTxn, DB_TXN_NOSYNC););
#undef THROW_HOOK
#define THROW_HOOK \
        ABORT_HOOK

        /*Note: Cursors may not span transactions; that is, each cursor must be opened and closed within a single transaction.*/
        Dbc *cursor1=NULL;



        _( a_DBWhich->cursor(thisTxn,&cursor1, 0) );

#undef THROW_HOOK
#define THROW_HOOK \
                cursor1->close();/*done prior to Abort()*/\
                ABORT_HOOK;
#define ERR_HOOK \
        THROW_HOOK


        //a_Key+a_NewValue must not already exist!
        _if( DB_NOTFOUND != cursor1->get( a_Key, a_NewValue, DB_GET_BOTH|DB_RMW)
                ) {
                _reterr kTLAlreadyExists;//doesn't throw if already exists but instead it reports it
        }_fi
        //find current
        _tIF(0!=cursor1->get( a_Key, a_Value, DB_GET_BOTH|DB_RMW));
        //change it to new
        _tIF(0!=cursor1->put( a_Key/*ignored*/, a_NewValue, DB_CURRENT));
        /*the DB_CURRENT flag was specified, a duplicate sort function has been specified, and the data item of the referenced key/data pair does not compare equally to the data parameter;*/

#undef THROW_HOOK
#define THROW_HOOK \
        ABORT_HOOK

        _(cursor1->close());

#undef THROW_HOOK
#define THROW_HOOK

        _( Commit(&thisTxn); );
        return kTLNoError;
#undef THROW_HOOK
#undef ERR_HOOK
}
/*************************/
//performes unsynced delete from one of the databases (unsynced means the other db is left inconsistent ie. A->B in primary, and B->A in secondary, delFrom deletes one but leaves untouched the other so you're left with either A->B or B->A thus inconsistent)
void
TLink::delFrom(
                Db *a_DBInto,
                DbTxn *a_ParentTxn,
                Dbt *a_Key,
                Dbt *a_Value
                )
{
#define THROW_HOOK

        _tIF(NULL==a_DBInto);


        DbTxn *thisTxn;
        _(NewTransaction(a_ParentTxn,&thisTxn, DB_TXN_NOSYNC););

#undef THROW_HOOK
#define THROW_HOOK \
        ABORT_HOOK

        Dbc *cursor1=NULL;
        _(a_DBInto->cursor(thisTxn,&cursor1, 0));

#undef THROW_HOOK
#define THROW_HOOK \
                cursor1->close();/*done prior to Abort()*/\
                ABORT_HOOK

        //position on the item
        _tIF(0!=cursor1->get(a_Key, a_Value, DB_GET_BOTH|DB_RMW) );

        //delete current item
        _tIF( 0 != cursor1->del(0) );

#undef THROW_HOOK
#define THROW_HOOK \
        ABORT_HOOK

        _(cursor1->close());

#undef THROW_HOOK
#define THROW_HOOK

        _( Commit(&thisTxn); );

#undef THROW_HOOK
}

/*************************/
ETLinkErrors_t
TLink::putInto(
                Db *a_DBInto,
                DbTxn *a_ParentTxn,
                Dbt *a_Key,
                Dbt *a_Value)
{
#define THROW_HOOK

        _tIF(NULL==a_DBInto);

        DbTxn *thisTxn;
        _(NewTransaction(a_ParentTxn,&thisTxn, DB_TXN_NOSYNC););

#undef THROW_HOOK
#define THROW_HOOK \
        ABORT_HOOK;

//no DUPS! fail(not throw!) if already exists
        Dbc *cursor1=NULL;
        _(a_DBInto->cursor(thisTxn,&cursor1, 0));

#undef THROW_HOOK
#define THROW_HOOK \
                cursor1->close();/*done prior to Abort()*/\
                ABORT_HOOK
#define ERR_HOOK \
        THROW_HOOK

        _if (0==cursor1->get(a_Key, a_Value, DB_GET_BOTH|DB_RMW)) {
                _reterr kTLAlreadyExists;
        }_fi

#undef THROW_HOOK
#define THROW_HOOK \
        ABORT_HOOK

        _(cursor1->close());


        //if not found, then put it
        _tIF(0 != a_DBInto->put(thisTxn, a_Key, a_Value, 0) );

#undef THROW_HOOK
#define THROW_HOOK

        _( Commit(&thisTxn); );
        return kTLNoError;

#undef THROW_HOOK
#undef ERR_HOOK
}


/****************************/
//kGroup:a->b =>a->c ~ pri:A-B, sec:B-A => pri: A-C, sec:C-A(new)(B-A deleted)
//kSubGroup:b<-a => b<-c
ETLinkErrors_t
TLink::ModLink(
                const std::string a_GroupId,
                const std::string a_SubGroupId,
                const std::string a_NewLinkName,
                DbTxn *a_ParentTxn
                )
{
#define THROW_HOOK

        _tIF(a_GroupId.empty());
        _tIF(a_SubGroupId.empty());
        _tIF(a_NewLinkName.empty());

        DbTxn *thisTxn;
        _(NewTransaction(a_ParentTxn,&thisTxn, DB_TXN_NOSYNC););

#undef THROW_HOOK
#define THROW_HOOK \
        ABORT_HOOK
#define ERR_HOOK \
        THROW_HOOK

                Dbt value;
                Dbt newValue;
                Dbt key;

                key.set_data((void *)a_GroupId.c_str());
                key.set_size((u_int32_t)a_GroupId.length() + 1);

                value.set_data((void *)a_SubGroupId.c_str());
                value.set_size((u_int32_t)a_SubGroupId.length() + 1);

                newValue.set_data((void *)a_NewLinkName.c_str());
                newValue.set_size((u_int32_t)a_NewLinkName.length() + 1);

#ifdef SHOWKEYVAL
        cout <<"\t"<< "Mod:begin:"<<
                (char *)key.get_data()<<
                "-" <<
                (char *)value.get_data()<<
                "=>" <<
                (char *)newValue.get_data()<<
                endl;
#endif

        //modify in primary
        ETLinkErrors_t err;

        _if ( kTLNoError != (err = findAndChange(g_DBGroupToSubGroup,thisTxn,&key,&value,&newValue))) {
                        _reterr err;//keep error state thru calls
        }_fi//from A->B changed to A->C(only if A->C didn't exist already)

        _(delFrom(g_DBSubGroupFromGroup,thisTxn,&value,&key));//del B<-A from SECondary
        //must not return kTLAlreadyExists otherwise a bug is present somewhere
        _tIF(kTLNoError != putInto(g_DBSubGroupFromGroup,thisTxn,&newValue,&key));//create C<-A in secondary

#ifdef SHOWKEYVAL
        cout <<"\t"<< "Mod:done."<<endl;
#endif

#undef THROW_HOOK
#define THROW_HOOK

        _( Commit(&thisTxn); );
        return kTLNoError;

#undef THROW_HOOK
#undef ERR_HOOK
}

/*************************/
ETLinkErrors_t
TLink::IsGroup(
                const EIdType_t a_IdType,
                const std::string a_GroupId,
                DbTxn *a_ParentTxn
                )
{
#define THROW_HOOK

        _tIF(a_GroupId.empty());

        DbTxn *thisTxn;
        _(NewTransaction(a_ParentTxn,&thisTxn, DB_TXN_NOSYNC););

#undef THROW_HOOK
#define THROW_HOOK \
        ABORT_HOOK
#define ERR_HOOK \
        THROW_HOOK

        Dbt value;
        Dbt key;

        key.set_data((void *)a_GroupId.c_str());
        key.set_size((u_int32_t)a_GroupId.length() + 1);
        _(value.set_flags(DB_DBT_MALLOC););
        //value.set_data((void *)a_SubGroupId.c_str());
        //value.set_size((u_int32_t)a_SubGroupId.length() + 1);

#ifdef SHOWKEYVAL
                std::cout<<"\tIsGroup:begin:"<<
                (char *)key.get_data()<<endl;
          //      "->" <<
            //    (char *)value.get_data()<<endl;
#endif

        Dbc *cursor1=NULL;
        Db *db;
        switch (a_IdType) {
                case kGroup: {
                                     db=g_DBGroupToSubGroup;
                                     break;
                             }
                case kSubGroup: {
                                     db=g_DBSubGroupFromGroup;
                                     break;
                             }
                default:
                                _t("more than kGroup or kSubGroup specified!");
        }//switch
        _( db->cursor(thisTxn,&cursor1, 0) );

#undef ERR_HOOK
#undef THROW_HOOK
#define THROW_HOOK \
                cursor1->close();/*done prior to Abort()*/\
                ABORT_HOOK;
#define FREE_VAL \
        if (value.get_data()) \
                free(value.get_data());
#define ERR_HOOK \
        FREE_VAL \
        THROW_HOOK


        int err;
        _if( DB_NOTFOUND == (err=cursor1->get( &key, &value, DB_SET)) ) {
#ifdef SHOWKEYVAL
                std::cout<<"\tIsGroup: is not!."<<endl;
#endif
                _reterr kTLNotFound;
        }_fi
        //else throws! just in case it doesn't we check for err==0 => ok, below
        _tIF(err!=0);
        FREE_VAL;

#undef THROW_HOOK
#define THROW_HOOK \
        ABORT_HOOK
#undef ERR_HOOK

        _(cursor1->close());

#ifdef SHOWKEYVAL
                std::cout<<"\tIsGroup:true."<<endl;
#endif

#undef THROW_HOOK
#define THROW_HOOK

        _( Commit(&thisTxn); )

        return kTLNoError;//found
#undef THROW_HOOK
}
/*************************/
ETLinkErrors_t
TLink::IsLink(
                const std::string a_GroupId,//may or may not exist
                const std::string a_SubGroupId,//same
                DbTxn *a_ParentTxn
                )
{
        //FIXME: do one of :
        //1) count a_GroupId kTo records, and a_SubGroupId kFrom records, and run the search on the one with least records (problem: dno if count has the value cached or it really parses each and counts them, which defeats our purpose)
        //OR 2) run two threads one that finds a_GroupId kTo a_SubGroupId (default)  and second that finds a_SubGroupId kFrom a_GroupId and whichever finishes first ends the other (can we really stop the other one??! don't think so; that's why we prefer variant 1) )
#define THROW_HOOK

        _tIF(a_GroupId.empty());
        _tIF(a_SubGroupId.empty());



        DbTxn *thisTxn;
        _(NewTransaction(a_ParentTxn,&thisTxn, DB_TXN_NOSYNC););

#undef THROW_HOOK
#define THROW_HOOK \
        ABORT_HOOK
#define ERR_HOOK \
        THROW_HOOK

        Dbt value;
        Dbt key;

        key.set_data((void *)a_GroupId.c_str());
        key.set_size((u_int32_t)a_GroupId.length() + 1);
        value.set_data((void *)a_SubGroupId.c_str());
        value.set_size((u_int32_t)a_SubGroupId.length() + 1);

#ifdef SHOWKEYVAL
                std::cout<<"\tIsLink:begin:"<<
                (char *)key.get_data()<<
                "->" <<
                (char *)value.get_data()<<endl;
#endif

        Dbc *cursor1=NULL;
        _( g_DBGroupToSubGroup->cursor(thisTxn,&cursor1, 0) );

#undef ERR_HOOK
#undef THROW_HOOK
#define THROW_HOOK \
                cursor1->close();/*done prior to Abort()*/\
                ABORT_HOOK;
#define ERR_HOOK \
        THROW_HOOK


        int err;
        _if( DB_NOTFOUND == (err=cursor1->get( &key, &value, DB_GET_BOTH)) ) {
#ifdef SHOWKEYVAL
                std::cout<<"\tIsLink: is not!."<<endl;
#endif
                _reterr kTLNotFound;
        }_fi
        //else throws! just in case it doesn't we check for err==0 => ok, below
        _tIF(err!=0);

#undef THROW_HOOK
#define THROW_HOOK \
        ABORT_HOOK
#undef ERR_HOOK

        _(cursor1->close());

#ifdef SHOWKEYVAL
                std::cout<<"\tIsLink:true."<<endl;
#endif

#undef THROW_HOOK
#define THROW_HOOK

        _( Commit(&thisTxn); )
        return kTLNoError;

#undef THROW_HOOK
}

/*************************/

/*************************/
/*************************/
/*************
 * creates a connection between a_GroupId TO/FROM a_SubGroupId
 * if any group doesn't aready exist it is created
 * if connection exists an exception is thrown
 * ***************/
ETLinkErrors_t
TLink::NewLink(
                const std::string a_GroupId,//may or may not exist
                const std::string a_SubGroupId,//same
                DbTxn *a_ParentTxn
                )
{
#define THROW_HOOK

        _tIF(a_GroupId.empty());
        _tIF(a_SubGroupId.empty());



        DbTxn *thisTxn;
        _(NewTransaction(a_ParentTxn,&thisTxn, DB_TXN_NOSYNC););

#undef THROW_HOOK
#define THROW_HOOK \
        ABORT_HOOK
#define ERR_HOOK \
        THROW_HOOK

        Dbt value;
        Dbt key;

        key.set_data((void *)a_GroupId.c_str());
        key.set_size((u_int32_t)a_GroupId.length() + 1);
        value.set_data((void *)a_SubGroupId.c_str());
        value.set_size((u_int32_t)a_SubGroupId.length() + 1);

#ifdef SHOWKEYVAL
                std::cout<<"\tNewLink:begin:"<<
                (char *)key.get_data()<<
                "->" <<
                (char *)value.get_data()<<endl;
#endif

        ETLinkErrors_t err;
        //insert in primary
        _if( (err=putInto(g_DBGroupToSubGroup,thisTxn,&key,&value)) ) {
                        _reterr err;//this we need
                        //no throw!
        }_fi
        //insert in secondary
        //if we're here, means we successfuly entered the record in primary, so the record must be successfully entered in secondary, ie. cannot return kTLAlreadyExists; so it must be clean, otherwise we throw (up;)
        _tIF(kTLNoError != putInto(g_DBSubGroupFromGroup,thisTxn,&value,&key));
#ifdef SHOWKEYVAL
                std::cout<<"\tNewLink:done."<<endl;
#endif

#undef THROW_HOOK
#define THROW_HOOK

        _( Commit(&thisTxn); )
        return kTLNoError;

#undef THROW_HOOK
#undef ERR_HOOK
}

/*************************/
void
TLink::ShowContents(
                DbTxn *a_ParentTxn)
{
#define THROW_HOOK
        std::cout<<"\t\t\tPRI: "<<std::endl;
        _(showRecords(a_ParentTxn,g_DBGroupToSubGroup,"->"));
        std::cout<<"\t\t\tSEC: "<<std::endl;
        _(showRecords(a_ParentTxn,g_DBSubGroupFromGroup,"<-"));
#undef THROW_HOOK
}

/****************************/
void
TLink::NewTransaction(DbTxn * a_ParentTxn,
                        DbTxn ** a_NewTxn,
                        u_int32_t a_Flags)
{
        /*"Note: Transactions may only span threads if they do so serially; that is, each transaction must be active in only a single thread of control at a time. This restriction holds for parents of nested transactions as well; not two children may be concurrently active in more than one thread of control at any one time." Berkeley DB docs*/
        /*"Note: A parent transaction may not issue any Berkeley DB operations -- except for DbEnv::txn_begin, DbTxn::abort and DbTxn::commit -- while it has active child transactions (child transactions that have not yet been committed or aborted)" Berkeley DB docs*/
#define THROW_HOOK

        _tIF(a_NewTxn==NULL);
        _(fDBEnviron->txn_begin(a_ParentTxn, a_NewTxn, a_Flags ););
        fStackLevel++;

#ifdef SHOWTXNS
                for (int i=1;i<fStackLevel;i++)
                        std::cout << " ";

#undef THROW_HOOK
#define THROW_HOOK \
        __(this->Abort(a_NewTxn));

        _( std::cout << "through "<<*a_NewTxn<<"("<<fStackLevel<<")"<<std::endl;);
#endif

#undef THROW_HOOK
}
/****************************/
//destructor
TLink::~TLink()
{
#define THROW_HOOK \
        DB2CLOSE_HOOK\
        ENVCLOSE_HOOK

        // Close our database handle if it was opened.
        if (g_DBGroupToSubGroup != NULL)//DB1
            _(g_DBGroupToSubGroup->close(0));

#undef THROW_HOOK
#define THROW_HOOK \
        ENVCLOSE_HOOK

        if (g_DBSubGroupFromGroup != NULL)//DB2
            _(g_DBSubGroupFromGroup->close(0));


#undef THROW_HOOK

        // Close our environment if it was opened
        ENVCLOSE_HOOK //throw without THROW_HOOK

}

/****************************/
// Open a Berkeley DB database
void
TLink::OpenDB(
                Db **a_DBpp,
                const std::string * const a_DBName)
{
#define THROW_HOOK

    //int ret;
    u_int32_t openFlags;

        Db *dbp;
        _( dbp=new Db(fDBEnviron, 0););

        // Point to the new'd Db
        *a_DBpp = dbp;

        _( (void )dbp->set_flags(DB_DUP) );

        // Now open the database */
        openFlags = DB_CREATE              |// Allow database creation
                    //DB_READ_UNCOMMITTED    | // Allow uncommitted reads
                    DB_AUTO_COMMIT ;          // Allow autocommit

        //without the following, insertion is sorted
        //dbp->set_dup_compare(&dup_compare_fcn);
        _( dbp->open(NULL,       // Txn pointer
                  fDBFileName.c_str(),   // File name
                  a_DBName->c_str(),       // Logical db name
                  DB_BTREE,   // Database type (using btree)
                  openFlags,  // Open flags
                  0);         // File mode. Using defaults
         );

#undef THROW_HOOK
}

/****************************/
/****************************/
void
TLink::KillDB(
                const std::string * const a_PathFN,
                const std::string * const a_FName
                )
{
#define THROW_HOOK
                if (file_exists(a_PathFN->c_str(),0,NULL)) {
                        _(fDBEnviron->dbremove(NULL,a_FName->c_str(),NULL,0));
                } else INFO("can't dbremove:dbase file doesn't yet exist, first dry run?");
#undef THROW_HOOK
}


/****************************/
//constructor
TLink::TLink(
                const std::string a_EnvHomePath,
                const std::string a_DBFileName,
                bool a_PreKill):
        fStackLevel(0),//1 is the first txn
        fEnvHomePath(a_EnvHomePath),
        fDBFileName(a_DBFileName),
        fDBEnviron(NULL),
        g_DBGroupToSubGroup(NULL),
        g_DBSubGroupFromGroup(NULL)

{
#define THROW_HOOK

        _tIF(a_DBFileName.empty());

        const std::string nameDBGroupToGroup("GroupToSubGroup");
        const std::string nameDBGroupFromGroup("SubGroupFromGroup");
    // Env open flags
    u_int32_t fDBEnvironFlags =
      DB_CREATE     |  // Create the environment if it does not exist
      DB_RECOVER    |  // Run normal recovery.
      DB_INIT_LOCK  |  // Initialize the locking subsystem
      DB_INIT_LOG   |  // Initialize the logging subsystem
      DB_INIT_TXN   |  // Initialize the transactional subsystem. This
                       // also turns on logging.
      DB_INIT_MPOOL |  // Initialize the memory pool (in-memory cache)
      DB_THREAD;       // Cause the environment to be free-threaded, that is, concurrently usable by multiple threads in the address space.


//recreating environment directory
    if (!fEnvHomePath.empty()) {
        if (! file_exists(fEnvHomePath.c_str(),FA_DIREC,NULL)) {
                _tIF(0 != mkdir(fEnvHomePath.c_str(),0700));
        }
    }
//done
        // Create and open the environment
        _(fDBEnviron = new DbEnv(0));
        _tIF(NULL==fDBEnviron);
        // Indicate that we want db to internally perform deadlock
        // detection.  Also indicate that the transaction with
        // the fewest number of write locks will receive the
        // deadlock notification in the event of a deadlock.
        _(fDBEnviron->set_lk_detect(DB_LOCK_MINWRITE););


        _(fDBEnviron->open(fEnvHomePath.c_str(), fDBEnvironFlags, 0););

#undef THROW_HOOK
#define THROW_HOOK ENVCLOSE_HOOK

        if (a_PreKill) {
                const std::string fn=fEnvHomePath+fDBFileName;
                _(KillDB(&fn,&fDBFileName););
        }

        // If we had utility threads (for running checkpoints or
        // deadlock detection, for example) we would spawn those
        // here. However, for a simple example such as this,
        // that is not required.

        // Open databases
        _(OpenDB(&g_DBGroupToSubGroup, &nameDBGroupToGroup););

#undef THROW_HOOK
#define THROW_HOOK \
        DB1CLOSE_HOOK\
        ENVCLOSE_HOOK

        _(OpenDB(&g_DBSubGroupFromGroup, &nameDBGroupFromGroup););
#undef THROW_HOOK
}

/****************************/
void
TLink::showRecords(
                DbTxn *a_ParentTxn,
                Db *a_DB,
                char *a_Sep)
{
#define THROW_HOOK

        _tIF(NULL==a_DB);
        _tIF(NULL==a_Sep);

        DbTxn *thisTxn;
        _(NewTransaction(a_ParentTxn,&thisTxn, DB_TXN_NOSYNC););

#undef THROW_HOOK
#define THROW_HOOK ABORT_HOOK

        const char *fn,*dbn;
        _(a_DB->get_dbname(&fn,&dbn));

        std::cout<<"\t\t\tdbase "<<fn<<" aka "<<dbn<<std::endl;
        Dbc *cursorp = NULL;
        int count = 0;

        // Get the cursor
        _(a_DB->cursor(thisTxn, &cursorp, 0));
#undef THROW_HOOK
#define THROW_HOOK \
        ___(cursorp->close());\
        ABORT_HOOK

        Dbt key, value;
        _(key.set_flags(DB_DBT_MALLOC););
        _(value.set_flags(DB_DBT_MALLOC););
            /*DB_NEXT
    If the cursor is not yet initialized, DB_NEXT is identical to DB_FIRST. Otherwise, the cursor is moved to the next key/data pair of the database, and that pair is returned. In the presence of duplicate key values, the value of the key may not Change.*/
        _(
        while (cursorp->get(&key, &value, DB_NEXT) == 0) {
#ifdef SHOWCONTENTS
                _(cout <<"\t"<< (char *)key.get_data()<< " " << a_Sep << " " <<(char *)value.get_data()<<endl);
#endif
                        void *k=key.get_data();
                        if (k)
                                _(free(k));
                        void *v=value.get_data();
                        if (v)
                                _(free(v));
                count++;
        });

#undef THROW_HOOK
#define THROW_HOOK ABORT_HOOK
        _(cursorp->close());

        std::cout<<"\t\t\tgot "<<count<<" records."<<std::endl<<std::endl;

#undef THROW_HOOK
#define THROW_HOOK

        _( Commit(&thisTxn) );

#undef THROW_HOOK
}
/****************************/
/*******************************/
void
TLink::Commit(DbTxn **a_Txn)
{
#define THROW_HOOK
        _tIF(NULL==a_Txn);
        if (NULL!=*a_Txn) {
                        _((*a_Txn)->commit(0));
#ifdef SHOWTXNS
                for (int i=1;i<fStackLevel;i++)
                        std::cout << " ";
                _(std::cout << "commitd "<<*a_Txn<<"("<<fStackLevel<<")"<<std::endl;);
#endif
        fStackLevel--;
                        *a_Txn=NULL;
        } else {
                WARN_IF(NULL==*a_Txn);
        }
#undef THROW_HOOK
}

/*******************************/
void
TLink::Abort(DbTxn **a_Txn)
{
#define THROW_HOOK
        _tIF(NULL==a_Txn);
        if (NULL!=*a_Txn) {
                        _((void)(*a_Txn)->abort());
#ifdef SHOWTXNS
                for (int i=1;i<fStackLevel;i++)
                        std::cout << " ";
                std::cout << "aborted "<<*a_Txn<<"("<<fStackLevel<<")"<<std::endl;
#endif
                        fStackLevel--;
                        *a_Txn=NULL;
        } else {
                WARN_IF(NULL==*a_Txn);
        }
#undef THROW_HOOK
}



