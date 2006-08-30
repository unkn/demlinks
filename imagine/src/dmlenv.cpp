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

#include "dmlenv.h"
#include "pnotetrk.h"

using namespace std;

/*************debug vars*/
//show debug statistics such as key+value
#define SHOWKEYVAL
#define SHOWCONTENTS
#define SHOWTXNS
/*************/

/****************************/
/****************************/
/*****************************************************************/
/*****************************************************************/
/********************************************************/

#define CURSOR_ABORT_HOOK \
        __(fLink->Abort(&thisTxn));
#define CURSOR_CLOSE_HOOK \
        if (fCursor) { \
                __tIF(0 != fCursor->close()); \
        }


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
function
TLink::findAndChange(
                Db *a_DBWhich,
                DbTxn *a_ParentTxn,
                Dbt *a_Key,
                Dbt *a_Value,
                Dbt *a_NewValue)//we fail(not throw) if newval already exists
{
        __tIF(NULL == a_Key);
        __tIF(NULL == a_Value);
        __tIF(NULL == a_NewValue);
        __tIF(NULL==a_DBWhich);


        DbTxn *thisTxn;
        __tIFnok(NewTransaction(a_ParentTxn,&thisTxn ));

#define THROW_HOOK \
        ABORT_HOOK

        /*Note: Cursors may not span transactions; that is, each cursor must be opened and closed within a single transaction.*/
        Dbc *cursor1=NULL;



        _htIF( 0 != a_DBWhich->cursor(thisTxn,&cursor1, 0) );

#undef THROW_HOOK
#define THROW_HOOK \
                __( cursor1->close() );/*done prior to Abort()*/\
                ABORT_HOOK;
#define ERR_HOOK \
        THROW_HOOK


        //a_Key+a_NewValue must not already exist!
        _hif( DB_NOTFOUND != cursor1->get( a_Key, a_NewValue, DB_GET_BOTH|DB_RMW) ) {
                _hreterr kFuncAlreadyExists;//doesn't throw if already exists but instead it reports it
        }_fih
        //find current
        _htIF(0!=cursor1->get( a_Key, a_Value, DB_GET_BOTH|DB_RMW));
        //change it to new
        _htIF(0!=cursor1->put( a_Key/*ignored*/, a_NewValue, DB_CURRENT));
        /*the DB_CURRENT flag was specified, a duplicate sort function has been specified, and the data item of the referenced key/data pair does not compare equally to the data parameter;*/

#undef THROW_HOOK
#define THROW_HOOK \
        ABORT_HOOK

        _h(cursor1->close());


        __tIFnok( Commit(&thisTxn) );

        _OK;
#undef THROW_HOOK
#undef ERR_HOOK
}
/*************************/
//performes unsynced delete from one of the databases (unsynced means the other db is left inconsistent ie. A->B in primary, and B->A in secondary, delFrom deletes one but leaves untouched the other so you're left with either A->B or B->A thus inconsistent)
function
TLink::delFrom(
                Db *a_DBInto,
                DbTxn *a_ParentTxn,//can be NULL
                Dbt *a_Key,
                Dbt *a_Value
                )
{

        __tIF(NULL == a_Key);
        __tIF(NULL == a_Value);
        __tIF(NULL==a_DBInto);


        DbTxn *thisTxn;
        __tIFnok(NewTransaction(a_ParentTxn,&thisTxn ));

#define THROW_HOOK \
        ABORT_HOOK

        Dbc *cursor1=NULL;
        _htIF( 0 != a_DBInto->cursor(thisTxn,&cursor1, 0) );

#undef THROW_HOOK
#define THROW_HOOK \
                __( cursor1->close() );/*done prior to Abort()*/\
                ABORT_HOOK

        //position on the item
        _htIF(0 != cursor1->get(a_Key, a_Value, DB_GET_BOTH|DB_RMW) );

        //delete current item
        _htIF( 0 != cursor1->del(0) );

#undef THROW_HOOK
#define THROW_HOOK \
        ABORT_HOOK

        _h(cursor1->close());


        __tIFnok( Commit(&thisTxn) );

        _OK;
#undef THROW_HOOK
}

/*************************/
function
TLink :: putInto(
                Db *a_DBInto,
                DbTxn *a_ParentTxn,
                Dbt *a_Key,
                Dbt *a_Value)
{
        __tIF(NULL == a_DBInto);
        __tIF(NULL == a_Key);
        __tIF(NULL == a_Value);

        DbTxn *thisTxn;
        __tIFnok(NewTransaction(a_ParentTxn,&thisTxn ));

#define THROW_HOOK \
        ABORT_HOOK;

//no DUPS! fail(not throw!) if already exists
        Dbc *cursor1 = NULL;
        _htIF( 0 != a_DBInto->cursor(thisTxn,&cursor1, 0));

#undef THROW_HOOK
#define THROW_HOOK \
                cursor1->close();/*done prior to Abort()*/\
                ABORT_HOOK
#define ERR_HOOK \
        THROW_HOOK

        _hif (0 == cursor1->get(a_Key, a_Value, DB_GET_BOTH|DB_RMW)) {
                _hreterr kFuncAlreadyExists;
        }_fih

#undef THROW_HOOK
#define THROW_HOOK \
        ABORT_HOOK

        _h(cursor1->close());


        //if not found, then put it
        _htIF(0 != a_DBInto->put(thisTxn, a_Key, a_Value, 0) );
#undef THROW_HOOK
#undef ERR_HOOK


        __tIFnok( Commit(&thisTxn) );

        _OK;
}


/****************************/
//ie.a->b =>a->c ~ pri:A-B, sec:B-A => pri: A-C(changed from A-B), sec:C-A(new)(B-A deleted)
//the connection MUST already exist! or fails(not throws)
function
TLink::ModLink(
                const NodeId_t a_GroupId,
                const NodeId_t a_SubGroupId,
                const NodeId_t a_NewLinkName,
                DbTxn *a_ParentTxn
                )
{

        __tIF(a_GroupId.empty());
        __tIF(a_SubGroupId.empty());
        __tIF(a_NewLinkName.empty());

        DbTxn *thisTxn;
        __tIFnok(NewTransaction(a_ParentTxn,&thisTxn ));

#define THROW_HOOK \
        ABORT_HOOK
#define ERR_HOOK \
        THROW_HOOK

                Dbt value;
                Dbt newValue;
                Dbt key;

                _h( key.set_data((void *)a_GroupId.c_str()) );
                key.set_size((u_int32_t)a_GroupId.length() + 1);

                _h( value.set_data((void *)a_SubGroupId.c_str()) );
                value.set_size((u_int32_t)a_SubGroupId.length() + 1);

                _h( newValue.set_data((void *)a_NewLinkName.c_str()) );
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
        _hfIFnok( findAndChange(g_DBGroupToSubGroup,thisTxn,&key,&value,&newValue) );//from A->B changed to A->C(only if A->C didn't exist already)

        _htIFnok( delFrom(g_DBSubGroupFromGroup,thisTxn,&value,&key) );//del B<-A from SECondary
        //must not return kFuncAlreadyExists otherwise a bug is present somewhere
        _htIFnok( putInto(g_DBSubGroupFromGroup,thisTxn,&newValue,&key) );//create C<-A in secondary

#ifdef SHOWKEYVAL
        cout <<"\t"<< "Mod:done."<<endl;
#endif


        __tIFnok( Commit(&thisTxn) );

        _OK;

#undef THROW_HOOK
#undef ERR_HOOK
}

/*************************/
function
TLink::IsGroup(
                const ENodeType_t a_NodeType,
                const NodeId_t a_GroupId,
                DbTxn *a_ParentTxn
                )
{

        __tIF(a_GroupId.empty());

        DbTxn *thisTxn;
        __tIFnok(NewTransaction(a_ParentTxn,&thisTxn ));

#define THROW_HOOK \
        ABORT_HOOK
#define ERR_HOOK \
        THROW_HOOK

        Dbt value;
        Dbt key;

        _h( key.set_data((void *)a_GroupId.c_str()) );
        key.set_size((u_int32_t)a_GroupId.length() + 1);
        _h( value.set_flags(DB_DBT_MALLOC) );
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
        switch (a_NodeType) {
                case kGroup: {
                                     db=g_DBGroupToSubGroup;
                                     break;
                             }
                case kSubGroup: {
                                     db=g_DBSubGroupFromGroup;
                                     break;
                             }
                default:
                                _ht("more than kGroup or kSubGroup specified!");
        }//switch
        _htIF( 0 != db->cursor(thisTxn,&cursor1, 0) );

#undef ERR_HOOK
#undef THROW_HOOK
#define THROW_HOOK \
                __( cursor1->close() );/*done prior to Abort()*/\
                ABORT_HOOK;
#define FREE_VAL \
        if (value.get_data()) \
                free(value.get_data());
#define ERR_HOOK \
        FREE_VAL \
        THROW_HOOK


        int err;
        _hif( DB_NOTFOUND == (err=cursor1->get( &key, &value, DB_SET)) ) {
#ifdef SHOWKEYVAL
                std::cout<<"\tIsGroup: is not!."<<endl;
#endif
                _hreterr kFuncNotFound;
        }_fih
        //else throws! just in case it doesn't we check for err==0 => ok, below
        _htIF(err!=0);
        FREE_VAL;

#undef THROW_HOOK
#define THROW_HOOK \
        ABORT_HOOK
#undef ERR_HOOK

        _htIF(0 != cursor1->close());

#ifdef SHOWKEYVAL
                std::cout<<"\tIsGroup:true."<<endl;
#endif


        __tIFnok( Commit(&thisTxn) );

        _OK;//found
#undef THROW_HOOK
#undef FREE_VAL
}
/*************************/
function
TLink::IsLink(
                const NodeId_t a_GroupId,//may or may not exist
                const NodeId_t a_SubGroupId,//same
                DbTxn *a_ParentTxn
                )
{
        //FIXME: do one of :
        //1) count a_GroupId kTo records, and a_SubGroupId kFrom records, and run the search on the one with least records (problem: dno if count has the value cached or it really parses each and counts them, which defeats our purpose)
        //OR 2) run two threads one that finds a_GroupId kTo a_SubGroupId (default)  and second that finds a_SubGroupId kFrom a_GroupId and whichever finishes first ends the other (can we really stop the other one??! don't think so; that's why we prefer variant 1) )

        __tIF(a_GroupId.empty());
        __tIF(a_SubGroupId.empty());



        DbTxn *thisTxn;
        __tIFnok(NewTransaction(a_ParentTxn,&thisTxn ));

#undef THROW_HOOK
#define THROW_HOOK \
        ABORT_HOOK
#define ERR_HOOK \
        THROW_HOOK

        Dbt value;
        Dbt key;

        _h( key.set_data((void *)a_GroupId.c_str()) );
        key.set_size((u_int32_t)a_GroupId.length() + 1);
        _h( value.set_data((void *)a_SubGroupId.c_str()) );
        value.set_size((u_int32_t)a_SubGroupId.length() + 1);

#ifdef SHOWKEYVAL
                std::cout<<"\tIsLink:begin:"<<
                (char *)key.get_data()<<
                "->" <<
                (char *)value.get_data()<<endl;
#endif

        Dbc *cursor1=NULL;
        _htIF( 0 != g_DBGroupToSubGroup->cursor(thisTxn,&cursor1, 0) );

#undef ERR_HOOK
#undef THROW_HOOK
#define THROW_HOOK \
                __tIF( 0 != cursor1->close() );/*done prior to Abort()*/\
                ABORT_HOOK;
#define ERR_HOOK \
        THROW_HOOK


        int err;
        _hif( DB_NOTFOUND == (err=cursor1->get( &key, &value, DB_GET_BOTH)) ) {
#ifdef SHOWKEYVAL
                std::cout<<"\tIsLink: is not!."<<endl;
#endif
                _hreterr kFuncNotFound;
        }_fih
        //else throws! just in case it doesn't we check for err==0 => ok, below
        _htIF(err!=0);

#undef THROW_HOOK
#define THROW_HOOK \
        ABORT_HOOK
#undef ERR_HOOK

        _htIF( 0 != cursor1->close());

#ifdef SHOWKEYVAL
                std::cout<<"\tIsLink:true."<<endl;
#endif


        __tIFnok( Commit(&thisTxn) );

        _OK;
#undef THROW_HOOK
}

/*************************/

/*************************/
/*************************/
/*************
 * creates a connection between a_GroupId TO/FROM a_SubGroupId
 * if any group doesn't aready exist it is created
 * if connection exists the function fails (doesnt' throw)
 * ***************/
function
TLink::NewLink(
                const NodeId_t a_GroupId,//may or may not exist
                const NodeId_t a_SubGroupId,//same
                DbTxn *a_ParentTxn
                )
{

        __tIF(a_GroupId.empty());
        __tIF(a_SubGroupId.empty());



        DbTxn *thisTxn;
        __tIFnok(NewTransaction(a_ParentTxn,&thisTxn ));

#define THROW_HOOK \
        ABORT_HOOK
#define ERR_HOOK \
        THROW_HOOK

        Dbt value;
        Dbt key;

        _h( key.set_data((void *)a_GroupId.c_str()) );
        _h( key.set_size((u_int32_t)a_GroupId.length() + 1) );
        _h( value.set_data((void *)a_SubGroupId.c_str()) );
        _h( value.set_size((u_int32_t)a_SubGroupId.length() + 1) );

#ifdef SHOWKEYVAL
                std::cout<<"\tNewLink:begin:"<<
                (char *)key.get_data()<<
                "->" <<
                (char *)value.get_data()<<endl;
#endif

        //insert in primary
        _hfIFnok( putInto(g_DBGroupToSubGroup,thisTxn,&key,&value) );
        //insert in secondary
        //if we're here, means we successfuly entered the record in primary, so the record must be successfully entered in secondary, ie. cannot return kFuncAlreadyExists; so it must be clean, otherwise we throw (up;)
        _htIFnok( putInto(g_DBSubGroupFromGroup,thisTxn,&value,&key) );
#ifdef SHOWKEYVAL
                std::cout<<"\tNewLink:done."<<endl;
#endif


        __tIFnok( Commit(&thisTxn) );

        _OK;

#undef THROW_HOOK
#undef ERR_HOOK
}

/*************************/
function
TLink::ShowContents(
                DbTxn *a_ParentTxn)
{
        std::cout<<"\t\t\tPRI: "<<std::endl;
        __fIFnok(showRecords(a_ParentTxn,g_DBGroupToSubGroup,"->"));
        std::cout<<"\t\t\tSEC: "<<std::endl;
        __fIFnok(showRecords(a_ParentTxn,g_DBSubGroupFromGroup,"<-"));

        _OK;
}

/****************************/
function
TLink :: NewTransaction(DbTxn * a_ParentTxn,
                        DbTxn ** a_NewTxn,
                        const u_int32_t a_Flags)
{
        /*"Note: Transactions may only span threads if they do so serially; that is, each transaction must be active in only a single thread of control at a time. This restriction holds for parents of nested transactions as well; not two children may be concurrently active in more than one thread of control at any one time." Berkeley DB docs*/
        /*"Note: A parent transaction may not issue any Berkeley DB operations -- except for DbEnv::txn_begin, DbTxn::abort and DbTxn::commit -- while it has active child transactions (child transactions that have not yet been committed or aborted)" Berkeley DB docs*/

        __tIF(a_NewTxn==NULL);
        __(fDBEnviron->txn_begin(a_ParentTxn, a_NewTxn, a_Flags ););
        fStackLevel++;

#ifdef SHOWTXNS
                for (int i=1;i<fStackLevel;i++)
                        std::cout << " ";

#define THROW_HOOK \
        __(this->Abort(a_NewTxn));

        _h( std::cout << "through "<<*a_NewTxn<<"("<<fStackLevel<<")"<<std::endl;);
#undef THROW_HOOK
#endif
        _OK;
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
            _h(g_DBGroupToSubGroup->close(0));

#undef THROW_HOOK
#define THROW_HOOK \
        ENVCLOSE_HOOK

        if (g_DBSubGroupFromGroup != NULL)//DB2
            _h(g_DBSubGroupFromGroup->close(0));


#undef THROW_HOOK

        // Close our environment if it was opened
        ENVCLOSE_HOOK //throw without THROW_HOOK

}

/****************************/
// Open a Berkeley DB database
function
TLink::OpenDB(
                Db **a_DBpp,
                const std::string * const a_DBName)
{

    //int ret;
    u_int32_t openFlags;

        Db *dbp;
        __( dbp=new Db(fDBEnviron, 0););

        // Point to the new'd Db
        *a_DBpp = dbp;

        __( (void )dbp->set_flags(DB_DUP) );

        // Now open the database */
        openFlags = DB_CREATE              |// Allow database creation
                    //DB_READ_UNCOMMITTED    | // Allow uncommitted reads
                    DB_AUTO_COMMIT ;          // Allow autocommit

        //without the following, insertion is sorted
        //dbp->set_dup_compare(&dup_compare_fcn);
        __( dbp->open(NULL,       // Txn pointer
                  fDBFileName.c_str(),   // File name
                  a_DBName->c_str(),       // Logical db name
                  DB_BTREE,   // Database type (using btree)
                  openFlags,  // Open flags
                  0);         // File mode. Using defaults
         );

        _OK;
}

/****************************/
/****************************/
function
TLink::KillDB(
                const std::string * const a_PathFN,
                const std::string * const a_FName
                )
{
                if (file_exists(a_PathFN->c_str(),0,NULL)) {
                        __(fDBEnviron->dbremove(NULL,a_FName->c_str(),NULL,0));
                        _OK;//killed
                } else {
                        _F;//failed to kill
                }
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

        __tIF(a_DBFileName.empty());

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
                __tIF(0 != mkdir(fEnvHomePath.c_str(),0700));
        }
    }
//done
        // Create and open the environment
        __(fDBEnviron = new DbEnv(0));
        __tIF(NULL==fDBEnviron);
        // Indicate that we want db to internally perform deadlock
        // detection.  Also indicate that the transaction with
        // the fewest number of write locks will receive the
        // deadlock notification in the event of a deadlock.
        __(fDBEnviron->set_lk_detect(DB_LOCK_MINWRITE););


        __(fDBEnviron->open(fEnvHomePath.c_str(), fDBEnvironFlags, 0););

#undef THROW_HOOK
#define THROW_HOOK ENVCLOSE_HOOK

        if (a_PreKill) {
                const std::string fn=fEnvHomePath+fDBFileName;
                _hdoIFnok( KillDB(&fn,&fDBFileName) ) {
                        INFO("can't dbremove:dbase file doesn't yet exist, first dry run?");
                }_konfiodh
        }

        // If we had utility threads (for running checkpoints or
        // deadlock detection, for example) we would spawn those
        // here. However, for a simple example such as this,
        // that is not required.

        // Open databases
        _htIFnok( OpenDB(&g_DBGroupToSubGroup, &nameDBGroupToGroup) );

#undef THROW_HOOK
#define THROW_HOOK \
        DB1CLOSE_HOOK\
        ENVCLOSE_HOOK

        _htIFnok( OpenDB(&g_DBSubGroupFromGroup, &nameDBGroupFromGroup) );

#undef THROW_HOOK
}

/****************************/
function
TLink::showRecords(
                DbTxn *a_ParentTxn,
                Db *a_DB,
                char *a_Sep)
{

        __tIF(NULL==a_DB);
        __tIF(NULL==a_Sep);

        DbTxn *thisTxn;
        __tIFnok(NewTransaction(a_ParentTxn,&thisTxn ));

#define THROW_HOOK ABORT_HOOK

        const char *fn,*dbn;
        _h(a_DB->get_dbname(&fn,&dbn));

        std::cout<<"\t\t\tdbase "<<fn<<" aka "<<dbn<<std::endl;
        Dbc *cursorp = NULL;
        int count = 0;

        // Get the cursor
        _htIF(0 != a_DB->cursor(thisTxn, &cursorp, 0));
#undef THROW_HOOK
#define THROW_HOOK \
        __tIF(0 != cursorp->close());\
        ABORT_HOOK

        Dbt key, value;
        _h(key.set_flags(DB_DBT_MALLOC););
        _h(value.set_flags(DB_DBT_MALLOC););
            /*DB_NEXT
    If the cursor is not yet initialized, DB_NEXT is identical to DB_FIRST. Otherwise, the cursor is moved to the next key/data pair of the database, and that pair is returned. In the presence of duplicate key values, the value of the key may not Change.*/
        while (true) {
                _hif( 0 != cursorp->get(&key, &value, DB_NEXT) ) {
                        break;
                }_fih
#ifdef SHOWCONTENTS
                _h(cout <<"\t"<< (char *)key.get_data()<< " " << a_Sep << " " <<(char *)value.get_data()<<endl);
#endif
                        void *k=key.get_data();
                        if (k)
                                _h(free(k));
                        void *v=value.get_data();
                        if (v)
                                _h(free(v));
                count++;
        }//while

#undef THROW_HOOK
#define THROW_HOOK ABORT_HOOK
        _htIF(0 != cursorp->close());

        std::cout<<"\t\t\tgot "<<count<<" records."<<std::endl<<std::endl;


        __tIFnok( Commit(&thisTxn) );

        _OK;
#undef THROW_HOOK
}
/****************************/
/*******************************/
function
TLink::Commit(DbTxn **a_Txn)
{//one cannot&shouldnot call Abort after calling this function: is in the Berkeley DB docs that cannot call abort after a failed(or successful) DbTxn->commit()
        __tIF(NULL==a_Txn);
        if (NULL != *a_Txn) {
                __((*a_Txn)->commit(0));
#ifdef SHOWTXNS
                for (int i=1;i<fStackLevel;i++) {
                        std::cout << " ";
                }
                __( std::cout << "commitd "<<*a_Txn<<"("<<fStackLevel<<")"
                                <<std::endl; );
#endif
                fStackLevel--;
                *a_Txn=NULL;
        } else {
                _FA(*a_Txn is NULL);
        }

        _OK;
}

/*******************************/
function
TLink::Abort(DbTxn **a_Txn)
{
        __tIF(NULL==a_Txn);
        if (NULL!=*a_Txn) {
#ifdef SHOWTXNS
                for (int i=1;i<fStackLevel;i++)
                        std::cout << " ";
                std::cout << "abortin "<<*a_Txn<<"("<<fStackLevel<<")"<<std::endl;
#endif
                        __((void)(*a_Txn)->abort());
#ifdef SHOWTXNS
                for (int i=1;i<fStackLevel;i++)
                        std::cout << " ";
                std::cout << "aborted "<<*a_Txn<<"("<<fStackLevel<<")"<<std::endl;
#endif
                        fStackLevel--;
                        *a_Txn=NULL;
        } else {
                _FA(*a_Txn is NULL);
        }
        _OK;
}



/*******************************/
//constructor
TDMLCursor :: TDMLCursor(TLink *m_WorkingOnThisTLink):
        fCursor(NULL)
{
        fLink=m_WorkingOnThisTLink;
        __tIF(NULL == fLink);
        thisTxn=NULL;
        fDb=NULL;
}
/*******************************/
//destructor
TDMLCursor :: ~TDMLCursor()
{
        __tIF(NULL == fLink);//cannot be
        __tIF(fCursor != NULL);//forgot to call DeInit() ?
        __tIF(NULL != fDb);//DeInit() must be called!
}
/*******************************/
//opens the cursor
function
TDMLCursor :: InitFor(
                const ENodeType_t a_NodeType,
                const NodeId_t a_NodeId,
                DbTxn *a_ParentTxn,//can be NULL, no problem
                const ECursorFlags_t a_Flags
                )
{
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::Init:begin"<<endl;
#endif
#define THROW_HOOK \
        CURSOR_CLOSE_HOOK \
        CURSOR_ABORT_HOOK
        _htIF(a_NodeId.empty());
        _htIF(NULL != thisTxn);//cannot call InitFor() twice, not before DeInit(); however if called twice we need to close the cursor prior to aborting the current transaction!
#undef THROW_HOOK

        __tIFnok(fLink->NewTransaction(a_ParentTxn,&thisTxn));

#define THROW_HOOK \
        CURSOR_ABORT_HOOK
#define ERR_HOOK \
        THROW_HOOK

        //fOKMaxLen=a_NodeId.length() + 1;
        fCurKeyStr=a_NodeId;//yeah let's hope this makes a copy
        _h( fCurKey.set_data((void *)fCurKeyStr.c_str()) );//points to that, so don't kill fCurKeyStr!!
        _h( fCurKey.set_size((u_int32_t)fCurKeyStr.length() + 1) );
        //_h( fCurKey.set_data((void *)a_NodeId.c_str()) );//points to that, so don't kill fCurKeyStr!!
        //_h( fCurKey.set_size((u_int32_t)a_NodeId.length() + 1) );
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::Init:SetKey="<<
                (char *)fCurKey.get_data()<<endl;
#endif

        switch (a_NodeType) {
                case kGroup: {
                                     fDb=fLink->g_DBGroupToSubGroup;
                                     break;
                             }
                case kSubGroup: {
                                     fDb=fLink->g_DBSubGroupFromGroup;
                                     break;
                             }
                default:
                                _ht("more than kGroup or kSubGroup specified!");
        }//switch
        fFlags=a_Flags;
        if (fFlags & kCursorWriteLocks) {
                fFlags=DB_WRITECURSOR;
        }
        _htIF( 0 != fDb->cursor(thisTxn,&fCursor, fFlags) );
        _htIF(NULL == fCursor);//feeling paranoid?
#undef ERR_HOOK
#undef THROW_HOOK
        fFirstTimeGet=true;

#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::Init:end."<<endl;
#endif
        _OK;
}
/*******************************/
function
TDMLCursor :: DeInit()
{
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::DeInit:begin"<<endl;
#endif
#define THROW_HOOK \
        CURSOR_ABORT_HOOK
        _htIF(NULL == fCursor);//called DeInit() before InitFor() ? or smth happened inbetween
        _htIF(0 != fCursor->close());
        fCursor=NULL;
        __tIFnok( fLink->Commit(&thisTxn) );
#undef THROW_HOOK
        fDb=NULL;
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::DeInit:end."<<endl;
#endif
        _OK;
}
/*******************************/
function
TDMLCursor :: Get(
                NodeId_t &m_Node,
                const ECursorFlags_t a_Flags
                )
{

#define THROW_HOOK \
        CURSOR_CLOSE_HOOK \
        CURSOR_ABORT_HOOK


#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::Get:begin:Key="<<
                (char *)fCurKey.get_data()<<endl;//how is this "J" here when InitFor() clearly sets it to "B"
#endif
        u_int32_t flags=0;//FIXME:
        if (fFlags & DB_WRITECURSOR) {
                flags|=DB_RMW;
        }

        Dbt curVal;
        _h( curVal.set_flags(DB_DBT_MALLOC) );//this hopefully remains between multiple Put(), Get() calls
if (a_Flags & kNextNode){
        if (fFirstTimeGet) {
                fFirstTimeGet=false;
                flags|=DB_SET;
                //cout << "first";
        } else {
                flags|=DB_NEXT_DUP;
                //cout <<"not";
        }
} else {
        if (a_Flags & kFirstNode) {
                flags|=DB_FIRST;
        } else {
                if (a_Flags & kLastNode) {
                        flags|=DB_LAST;
                } else {
                        if (a_Flags & kCurrentNode) {
                                flags|=DB_CURRENT;
                        } else {
                                if (a_Flags & kPinPoint) {
                                        flags|=DB_GET_BOTH;
                                        _h( curVal.set_flags(0) );
                                        _h( curVal.set_data((void *)m_Node.c_str()) );
                                        _h( curVal.set_size((u_int32_t)m_Node.length() + 1) );
                                }
                        }
                }
        }
}

#define FREE_VAL \
        if ((flags & DB_GET_BOTH==0) && (curVal.get_data())) \
                free(curVal.get_data());
#undef THROW_HOOK
#define THROW_HOOK \
        FREE_VAL \
        CURSOR_CLOSE_HOOK \
        CURSOR_ABORT_HOOK

        int err;
        _hif( DB_NOTFOUND == (err=fCursor->get( &fCurKey, &curVal, flags)) ) {
#ifdef SHOWKEYVAL
                __( std::cout<<"\tTDMLCursor::Get:fail:Key="<<
                (char *)fCurKey.get_data()<<endl;
                );
#endif
                FREE_VAL;//maybe?
                _fret kFuncNotFound;
        }_fih
        _htIF(0 != err);//other unspecified error

        _htIF(NULL == curVal.get_data());//impossible?
        m_Node=(char *)curVal.get_data();//hopefully this does copy contents not just point!
        //((char *)curVal.get_data())[0]='j';//poisoning to test the above; FIXME: delthisline
        FREE_VAL;//maybe we should call this in DeInit();
#undef THROW_HOOK

#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::Get:done:"<<
                (char *)fCurKey.get_data()<<" = "<< m_Node <<endl; //weird thing here, when DB_SET, key=data
#endif

        _OK;
#undef FREE_VAL
}
/*******************************/
/*******************************/
/*******************************/
/*******************************/
/*******************************/
/*******************************/
/*******************************/
/*******************************/
/*******************************/
/*******************************/
/*******************************/
/*******************************/
/*******************************/
/*******************************/
