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
* Description: dmlenv level 0 (lowest level)
*
****************************************************************************/

#include <iostream>
#include <sys/stat.h>
#include <sys/types.h>
#include <allegro.h>

#include "dmlenvl1.h"
#include "pnotetrk.h"

#include "dmlenv.hpp"


/*************************/
function
TLink::IsLinkConsistent(
                const ENodeType_t a_NodeType,
                const NodeId_t a_NodeId1,
                const NodeId_t a_NodeId2,
                DbTxn *a_ParentTxn
                )
{
//------------ validating params
        __tIF(a_NodeId1.empty());
        __tIF(a_NodeId2.empty());

//------------ start transaction
        DbTxn *thisTxn;
        __tIFnok(NewTransaction(a_ParentTxn,&thisTxn ));
#define THROW_HOOK \
        ABORT_HOOK

//------------ check if link exists on one of the dbases
        NodeId_t nod;
        __( nod=a_NodeId2 );
        _hfIFnok( IsSemiLink(a_NodeType, a_NodeId1, nod, thisTxn) );

//------------ check if link exists on the other dbase
        ENodeType_t otherNodeType=a_NodeType;
        if (otherNodeType == kGroup) {
                otherNodeType=kSubGroup;
        } else {//assumed kSubGroup; we're in trouble if there will be three types
                otherNodeType = kGroup;
        }

        __( nod=a_NodeId1 );
        _hfIFnok( IsSemiLink(otherNodeType, a_NodeId2, nod, thisTxn) );

//------------ commit transaction
#undef THROW_HOOK
        __tIFnok( Commit(&thisTxn) );

//------------ end
        _OK;
}
/*************************/
/*************************/
function
TLink::IsSemiLink(
                const ENodeType_t a_NodeType,
                const NodeId_t a_NodeId1,//may or may not exist, but must be set
                NodeId_t &m_NodeId2,//can be: not set ie. "" case in which we try to find the first
                DbTxn *a_ParentTxn
                )
{
//------------ validate params
        __tIF(a_NodeId1.empty());

//------------ begin
//------------ open new cursor
        TDMLCursor *meCurs;
        __( meCurs=new TDMLCursor(this) );//done after DBs are inited!!!
#define DONE_CURSOR \
        __( delete(meCurs) );
#define THROW_HOOK \
        DONE_CURSOR
//---------- initialize cursor
        _htIFnok( meCurs->InitCurs(a_NodeType, a_NodeId1, kNone, a_ParentTxn) );
#undef THROW_HOOK
#define THROW_HOOK \
        __( meCurs->DeInit() ); \
        DONE_CURSOR

#define ERR_HOOK \
        THROW_HOOK
        //---------- check for a complementary link of a_NodeId1
        ETDMLFlags_t flags;
                //------- debug
                #ifdef SHOWKEYVAL
                        std::cout<<"\tIsSemiLink:begin:"<<
                        a_NodeId1;
                #endif

        //------- continuing
        __if (m_NodeId2.empty()) {
                flags=kFirstNode;
                #ifdef SHOWKEYVAL
                        cout<<endl;
                #endif
        } __fielse {
                flags=kPinPoint;
                #ifdef SHOWKEYVAL
                        cout<<"->" <<m_NodeId2<<endl;
                #endif
        }__fi

        //------- finally checking
        function err;
        _h( err=meCurs->Get(m_NodeId2, flags) );//return value in m_NodeId2
        if (kFuncNotFound == err) {
#ifdef SHOWKEYVAL
                std::cout<<"\tIsSemiLink: is not!."<<endl;
#endif
                _hreterr(err);
        }
        _htIF(kFuncOK != err); //no other error is handled
//---------- deinit cursor
        _htIFnok( meCurs->DeInit() );

//---------- close cursor
#undef THROW_HOOK
#undef ERR_HOOK

        DONE_CURSOR;
//---------- done
//------------ after done
#ifdef SHOWKEYVAL
        std::cout<<"\tIsSemiLink:true:"<<a_NodeId1<<" - "<<m_NodeId2<<endl;
#endif

//------------ end
        _OK;
}

/*************************/
/*************************/


/*************************/
function
TLink::NewLink(
                const ENodeType_t a_NodeType,
                const NodeId_t a_NodeId1,//may or may not exist
                const NodeId_t a_NodeId2,//same
                DbTxn *a_ParentTxn
                )
{
//---------- validating parameters
        __tIF(a_NodeId1.empty());
        __tIF(a_NodeId2.empty());
//---------- frontend for dual function
        switch (a_NodeType) {
                case kGroup: {
                                //---------- create a forward link
                                     __fIFnok( NewLink(a_NodeId1, a_NodeId2, a_ParentTxn) );
                                     break;
                             }
                case kSubGroup: {
                                //---------- create a reverse link
                                     __fIFnok( NewLink(a_NodeId2, a_NodeId1, a_ParentTxn) );
                                     break;
                             }
                default:
                                //---------- bad usage
                                __t("more than kGroup or kSubGroup specified!");
        }//switch
//---------- done
//---------- end
        _OK;
}
/*************************/
/*************
 * creates a connection between a_GroupId TO/FROM a_SubGroupId; both a FL and a RL is created!
 * if any group doesn't aready exist it is created
 * if connection exists the function fails (doesn't throw)
 * doesn't use a cursor
 * ***************/
function
TLink::NewLink(//creates a consistent link by passing forward link based params to this function
                const NodeId_t a_GroupId,//may or may not exist
                const NodeId_t a_SubGroupId,//same
                DbTxn *a_ParentTxn
                )
{
//---------- validate params
        __tIF(a_GroupId.empty());
        __tIF(a_SubGroupId.empty());


//---------- new transaction
        DbTxn *thisTxn;
        __tIFnok(NewTransaction(a_ParentTxn,&thisTxn ));

#define THROW_HOOK \
        ABORT_HOOK
#define ERR_HOOK \
        THROW_HOOK

//---------- setting values
        Dbt value;
        Dbt key;

        _h( key.set_data((void *)a_GroupId.c_str()) );
        _h( key.set_size((u_int32_t)a_GroupId.length() + 1) );
        _h( value.set_data((void *)a_SubGroupId.c_str()) );
        _h( value.set_size((u_int32_t)a_SubGroupId.length() + 1) );

#ifdef SHOWKEYVAL
                std::cout<<"\tNewLink(G-sG):begin:"<<
                (char *)key.get_data()<<
                "->" <<
                (char *)value.get_data()<<endl;
#endif

//---------- creating a forward link
        //insert in primary
        _hfIFnok( putInto(g_DBGroupToSubGroup,thisTxn,&key,&value) );
//---------- creating the complementary reverse link to make this link complete(consistent)
        //insert in secondary
        //if we're here, means we successfuly entered the record in primary, so the record must be successfully entered in secondary, ie. cannot return kFuncAlreadyExists; so it must be clean, otherwise we throw (up;)
        _htIFnok( putInto(g_DBSubGroupFromGroup,thisTxn,&value,&key) );
//---------- verifying consistency (optional)
        _htIFnok( IsLinkConsistent(kGroup, a_GroupId, a_SubGroupId, thisTxn) );
//---------- commiting transaction
#undef THROW_HOOK
#undef ERR_HOOK
        __tIFnok( Commit(&thisTxn) );
//---------- after done
#ifdef SHOWKEYVAL
                std::cout<<"\tNewLink(G-sG):done."<<endl;
#endif

//---------- end
        _OK;
}

/*************************/
function
TLink::ShowContents(
                DbTxn *a_ParentTxn)
{
//---------- show table of forward links
        std::cout<<"\t\t\tPRI: "<<std::endl;
        __fIFnok(showRecords(a_ParentTxn,kGroup,"->"));
//---------- show table of reverse links
        std::cout<<"\t\t\tSEC: "<<std::endl;
        __fIFnok(showRecords(a_ParentTxn,kSubGroup,"<-"));
//---------- end
        _OK;
}

/****************************/
function
TLink :: NewTransaction(DbTxn * a_ParentTxn,
                        DbTxn ** a_NewTxn,
                        const u_int32_t a_Flags)
{
        return l0_newTransaction(fDBEnviron, a_ParentTxn, a_NewTxn, &fStackLevel, a_Flags);
}
/****************************/
//destructor
TLink::~TLink()
{
//---------- close forward links table
#define THROW_HOOK \
        DB2CLOSE_HOOK\
        ENVCLOSE_HOOK

        // Close our database handle if it was opened.
        if (g_DBGroupToSubGroup != NULL)//DB1
            _h(g_DBGroupToSubGroup->close(0));

//---------- close reverse links table
#undef THROW_HOOK
#define THROW_HOOK \
        ENVCLOSE_HOOK

        if (g_DBSubGroupFromGroup != NULL)//DB2
            _h(g_DBSubGroupFromGroup->close(0));


#undef THROW_HOOK

//---------- close environment
        // Close our environment if it was opened
        ENVCLOSE_HOOK //throw without THROW_HOOK

}

/****************************/
// open a Berkeley DB database
function
TLink::openDB(
                Db **a_DBpp,
                const std::string * const a_DBName)
{

//---------- validate params
        __tIF(NULL == a_DBpp);
//---------- open new environment
    //int ret;
    u_int32_t openFlags;

        Db *dbp;
        __( dbp=new Db(fDBEnviron, 0););

//---------- open new dbase within this environment
        // Point to the new'd Db
        *a_DBpp = dbp;

        __( (void )dbp->set_flags(DB_DUP) );//allow duplicate keys; order is order of insertion unless cursor puts;

        // Now open the database */
        openFlags = DB_CREATE              |// Allow database creation
                    //DB_READ_UNCOMMITTED    | // Allow uncommitted reads
                    DB_AUTO_COMMIT ;          // Allow autocommit

        __( dbp->open(NULL,       // Txn pointer
                  fDBFileName.c_str(),   // File name
                  a_DBName->c_str(),       // Logical db name
                  DB_BTREE,   // Database type (using btree)
                  openFlags,  // open flags
                  0);         // File mode. Using defaults
         );

//---------- end
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
//---------- remove database of this environment
                        __(fDBEnviron->dbremove(NULL,a_FName->c_str(),NULL,0));
//---------- done
                        _OK;//killed
                } else {
//---------- failed done
                        _F;//failed to kill
                }
//---------- unreachable here
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

//---------- validate params
        __tIF(a_DBFileName.empty());

//---------- setting defaults
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


//---------- recreating environment directory
    if (!fEnvHomePath.empty()) {
        if (! file_exists(fEnvHomePath.c_str(),FA_DIREC,NULL)) {
                __tIF(0 != mkdir(fEnvHomePath.c_str(),0700));
        }
    }
//done
//---------- Create and open the environment
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

//---------- kill the database if exists (ie. no state preservation across program restarts, yet)
        if (a_PreKill) {
                const std::string fn=fEnvHomePath+fDBFileName;
                _hdoIFnok( KillDB(&fn,&fDBFileName) ) {
                        INFO("can't dbremove:dbase file doesn't yet exist, first dry run?");
                }_konfiodh
        }

        // If we had utility threads (for running checkpoints or
        // deadlock detection, for example) we would spawn those here.

//---------- open/create forward links table(=dbase)
        _htIFnok( openDB(&g_DBGroupToSubGroup, &nameDBGroupToGroup) );

#undef THROW_HOOK
#define THROW_HOOK \
        DB1CLOSE_HOOK\
        ENVCLOSE_HOOK

//---------- open/create reverse links table/dbase
        _htIFnok( openDB(&g_DBSubGroupFromGroup, &nameDBGroupFromGroup) );

#undef THROW_HOOK
//---------- end
}

/****************************/
function
TLink::showRecords(
                DbTxn *a_ParentTxn,
                ENodeType_t a_NodeType,
                char *a_Sep)
{

//---------- validating params
        __tIF(NULL==a_Sep);

//---------- new transaction
        DbTxn *thisTxn;
        __tIFnok(NewTransaction(a_ParentTxn,&thisTxn ));

#define THROW_HOOK ABORT_HOOK

//---------- choosing proper dbase to work on, so we won't have to repeat code and then change one part and forget to change the other since we cannot connect the two parts to somehow depend on eachother ie. can't change one unless you also change the other , otherwise the system should be left in an inconsistent state requiring user intervention and shouldn't compile when like that;
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

//---------- computing the name of the current working-on database
        const char *fn,*dbn;
        _h(db->get_dbname(&fn,&dbn));

        std::cout<<"\t\t\tdbase "<<fn<<" aka "<<dbn<<std::endl;

        int count = 0;
//---------- open new cursor
        Dbc *cursorp = NULL;

        // Get the cursor
        _htIF(0 != db->cursor(thisTxn, &cursorp, 0));
#undef THROW_HOOK
#define THROW_HOOK \
        __tIF(0 != cursorp->close());\
        ABORT_HOOK

//---------- set values
        Dbt key, value;
        _h(key.set_flags(DB_DBT_MALLOC););
        _h(value.set_flags(DB_DBT_MALLOC););

//---------- parsing the database from first(DB_SET) to last, with DB_NEXT;
            /*DB_NEXT
    If the cursor is not yet initialized, DB_NEXT is identical to DB_FIRST. Otherwise, the cursor is moved to the next key/data pair of the database, and that pair is returned. In the presence of duplicate key values, the value of the key may not Change.*/
        while (true) {
                //---------- get a new key/value pair; key may not always change
                _hif( 0 != cursorp->get(&key, &value, DB_NEXT) ) {
                        break;
                }_fih
#ifdef SHOWCONTENTS
                _h(cout <<"\t"<< (char *)key.get_data()<< " " << a_Sep << " " <<(char *)value.get_data()<<endl);
#endif
                //---------- consistency check; each displayed link is checked after being displayed obv.
                std::string keyStr;
                std::string valStr;
                __( keyStr=(char *)key.get_data() );
                __( valStr=(char *)value.get_data() );
                _htIFnok( IsLinkConsistent(a_NodeType, keyStr, valStr, thisTxn) );
                //eocc

                //---------- freeing the memory which became responsability of our program after calling get()

                        void *k=key.get_data();
                        if (k)
                                _h(free(k));
                        void *v=value.get_data();
                        if (v)
                                _h(free(v));
                //---------- count the records (a record is a key/value pair aka pointer/pointee pair)
                count++;
        }//while

//---------- close the cursor
#undef THROW_HOOK
#define THROW_HOOK ABORT_HOOK
        _htIF(0 != cursorp->close());
//---------- commit transaction
#undef THROW_HOOK
        __tIFnok( Commit(&thisTxn) );

//---------- done
        std::cout<<"\t\t\tgot "<<count<<" records."<<std::endl<<std::endl;

//---------- end
        _OK;
}
/****************************/
/*******************************/
function
TLink::Commit(DbTxn **a_Txn)
{//one cannot&shouldnot call Abort after calling this function: is in the Berkeley DB docs that cannot call abort after a failed(or successful) DbTxn->commit()
        return l0_commit(a_Txn, &fStackLevel);
}

/*******************************/
function
TLink::Abort(DbTxn **a_Txn)
{
        return l0_abort(a_Txn, &fStackLevel);
}



/*******************************/
//constructor
TDMLPointer :: TDMLPointer(TLink *m_WorkingOnThisTLink)
{
//---------- validating params
        fLink=m_WorkingOnThisTLink;
        __tIF(NULL == fLink);
//---------- setting defaults
        fParentTxn=NULL;
        fInited=false;
}
/*******************************/
//destructor
TDMLPointer :: ~TDMLPointer()
{
//---------- consistency checks; signalling errors at the last moment
        __tIF(IsInited());
        __tIF(NULL == fLink);//cannot be
        __tIF(NULL != fParentTxn);
}
/*******************************/
bool
TDMLPointer :: IsInited()
{
        return (fInited);
}
/*******************************/
function
TDMLPointer :: GetEnvironment(
        TLink * &m_TLink
)
{
//-------- return the pointer name (not the pointee name)
        m_TLink=fLink;
//-------- end
        _OK;
}
/*******************************/
function
TDMLPointer :: GetPointerId(
        NodeId_t &m_NodeId
)
{
//-------- return the pointer name (not the pointee name)
        m_NodeId=fPointerId;
//-------- end
        _OK;
}
/*******************************/
function
TDMLPointer :: GetPointee(
        NodeId_t &m_NodeId
)
{
//FIXME: make it use a new temporary TDMLCursor
//---------- was inited?
        __tIF(! this->IsInited());
//------------ begin
//------------ get our pointer name
        NodeId_t pointerId;
        __tIFnok( GetPointerId( pointerId ) );
#ifdef SHOWKEYVAL
        std::cout<<"\tGetPointee:begin:"<<pointerId<<endl;
#endif
//---------- get first item of key
        __( m_NodeId="" ); //if "" then IsSemiLink() will find the first
        function err;
        __if( kFuncOK != (err=fLink->IsSemiLink(fNodeType, pointerId, m_NodeId, fParentTxn)) ) {
                if (kFuncNotFound == err) {
                        _fret(kFuncNULLPointer);
                }
                __t(unhandled error);
        }__fi
//------- done
#ifdef SHOWKEYVAL
        std::cout<<"\tGetPointee:done:"<<pointerId<<" -> "<<m_NodeId<<endl;
#endif
//---------- end
        _OK;
}
/*******************************/
function
TDMLPointer :: SetPointee(
                        const NodeId_t a_NodeId //an empty param means that the caller wants us to set the pointer to NULL
)
{
//---------- was inited?
        __tIF(! this->IsInited());
//------------ begin
//------------ get our pointer name
        NodeId_t pointerId;
        __tIFnok( GetPointerId( pointerId ) );
#ifdef SHOWKEYVAL
        std::cout<<"\tSetPointee:begin:"<<pointerId<<" -> "<<a_NodeId<<endl;
#endif
//------------ open new cursor
        TDMLCursor *meCurs;
        __( meCurs=new TDMLCursor(fLink) );//done after DBs are inited!!!
#define DONE_CURSOR \
        __( delete(meCurs) );
#define THROW_HOOK \
        DONE_CURSOR
//---------- initialize cursor
        _htIFnok( meCurs->InitCurs(fNodeType, pointerId, kNone, fParentTxn) );
#undef THROW_HOOK
#define THROW_HOOK \
        __( meCurs->DeInit() ); \
        DONE_CURSOR

#define ERR_HOOK \
        THROW_HOOK
//---------- pinpoint the previous pointee, if any
        db_recno_t countPointees;
        ETDMLFlags_t flag;
        NodeId_t temp;
        function err;
        _h( err=meCurs->Get(temp, kFirstNode) );
        if (kFuncNotFound == err) { //then pinpoint the firstnode
                flag=kFirstNode;
        } else {
                if (kFuncOK == err) {
                        flag=kCurrentNode;
                        //---------- pointer integrity check, before change
                        _htIFnok( meCurs->Count(countPointees) );
                        //cout << countPointees <<endl;
                        _htIF(countPointees > 1); //cannot have more than one
                        //---------- set it to NULL if param is empty
                        __if ( a_NodeId.empty() ) {
                                _htIFnok( meCurs->Del(flag) );//delete the pointee thus the pointer is now NULL
                        }__fi
                } else {
                        _ht(unhandled return error from Get)
                }
        }
//---------- change pointee now ie. overwrite
        __if (! a_NodeId.empty()) {
                _htIFnok( meCurs->Put(a_NodeId, flag) );//return value in m_NodeId2
        }__fi

//---------- pointer integrity check, after change
        _htIFnok( meCurs->Count(countPointees) );
//        cout << countPointees <<endl;
        _htIF(1 < countPointees); //cannot have more than one
//---------- deinit cursor
        _htIFnok( meCurs->DeInit() );

//---------- close cursor
#undef THROW_HOOK
#undef ERR_HOOK

        DONE_CURSOR;
//---------- done
//------------ after done
#ifdef SHOWKEYVAL
        std::cout<<"\tSetPointee:done:"<<pointerId<<" -> "<<a_NodeId<<endl;
#endif

//---------- end
        _OK;
}
/*******************************/
function
TDMLPointer :: InitPtr(
                const ENodeType_t a_NodeType, //use kGroup here (see InitDomPtr for explanation)
                const NodeId_t a_NodeId,
                const int a_Flags,//combination of flags
                DbTxn *a_ParentTxn//can be NULL, if by default
                )
{
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLPointer::Init:begin"<<endl;
#endif
//---------- checking if called before and before DeInit()
        __tIF(this->IsInited());
//---------- validity of params
        __tIF(a_NodeId.empty());

//---------- saving values for later use
        fPointerId=a_NodeId;//yeah let's hope this makes a copy; it does!
        fNodeType=a_NodeType;
        fParentTxn=a_ParentTxn;
//---------- setting flags
        int tmpFlags=a_Flags;

        _makeFLAG(kOverwriteNode);//if only one node, we overwrite it(internally we just wipe it out by making the pointer be NULL)
        _makeFLAG(kKeepPrevValue);//if only one node, we keep its value
        _makeFLAG(kCreateNodeIfNotExists);//if pointer is already NULL, we don't fail, we keep it NULL however, but this is here to make sure the reader knows the programmer's intentions
        _makeFLAG(kTruncateIfMoreThanOneNode);

//---------- validate flags
        __tIF(0 != tmpFlags);//illegal flags
        __tIF(kNone == a_Flags);//no flags?!
        __tIF( fl_kOverwriteNode && fl_kKeepPrevValue );//mutually exclusive flags

//---------- begin
//------------ get our pointer name
        NodeId_t pointerId;
        __tIFnok( GetPointerId( pointerId ) );
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLPointer::Init:PointerName="<<
                pointerId<<endl;
#endif
        //FIXME:
        //checking if group exists, if not we make it point to itself which mean the pointer is NULL no! read below! seach RECTIF
        //all above only if certain flags are present

//#1 check if kGroup exists aka if there's a forward link with that pointer(key) [pointer=node]
//---------- new cursor; temporary
        TDMLCursor *meCurs;
        __( meCurs=new TDMLCursor(fLink) );//done after DBs are inited!!!
#undef THROW_HOOK
#define DEL_CURSOR \
        __( delete(meCurs) );
#define THROW_HOOK \
        DEL_CURSOR
//---------- initialize cursor
        _htIFnok( meCurs->InitCurs(fNodeType, pointerId, kNone, a_ParentTxn) );
#undef THROW_HOOK
#define THROW_HOOK \
        __( meCurs->DeInit() ); \
        DEL_CURSOR

#define ERR_HOOK \
        THROW_HOOK

        //---------- tmp vars
        NodeId_t nod;
        function err;
//---------- check if pointer has any pointees or simply doesn't yet exists
        _h( err=meCurs->Get(nod, kFirstNode) );//rethrow
        if (kFuncOK != err) {//error!
                //---------- has no pointees
                if (kFuncNotFound == err) {
                        if ( ! fl_kCreateNodeIfNotExists) {//if it doesn't exit we fail unless the flag is present;of course this is what this func is suppose to do by default but by using this each time we make sure the reader understands; besides defaults are the enemy of portability
                                _fret(kFuncInexistentNodeNotCreated);
                        }
                        //---------- at this point we're supposing the pointer has no pointees and we must create one
                        //we'll set it to point to self and thus being NULL(no!read below); if the pointee doesn't exist(at any point while using this pointer we should throw OR consider it NULL ?!) RECTIFICATION: a pointer is NULL only if it doesn't point to anything ie. it has no kSubGroups; pointing to self should be allowed! like ie. if we wanna flag any Node of this system as being _somehow_ we could use a pointer, and if this Node is the pointer itself it should be allowed! ie. parse the list of all pointers, obv. with a pointer that will eventually point to itself!
                        //so if u read above -> u know we already have this pointer=NULL if we're here; so we don't do anything
                } else {
                        _ht(unhandled return error);
                }
                //... by here our pointer is null
        } else {//no error;it has at least one pointee(aka kSubGroup)
                //---------- check if more than one is present!
                _h( err=meCurs->Get(nod, kNextNode) );
                if (kFuncOK == err) {
                        //-------- more than one present!
                        if (! fl_kTruncateIfMoreThanOneNode) {//no flag?
                                _fret(kFuncMoreThanOneNodeNotTruncated);
                        }
                        //------- if we're here we have permission to truncate, so we delete the current and then parse until the end with deleting
                                NodeId_t nod;
                                err=kFuncOK;//allow deletion of current node, it definitely exists! (this is the 2nd node)
                        while (kFuncOK == err) {
                                _htIFnok( meCurs->Del(kCurrentNode) );
                                _h( err=meCurs->Get(nod, kNextNode) );//try next node (3rd,4th...etc)
                                //can u even imagine what will happen on deadlock? if two or more threads are accessing these same records
                        }
                        //------- by the time we're here only one node(pointee) is left
                }
                //---------- by the time we're here there'll be only ONE pointee present!

                //-------- we remove it only if flag is NOT kKeepPrevValue && NOT kOverwriteNode
                if (! fl_kKeepPrevValue) {
                        //do not keep value
                        if (! fl_kOverwriteNode) {//no overwrite flag and no keepval flag
                                _fret(kFuncExistentSingleNodeNotOverwritten);
                        }
                        //------ we're here to not keep and overwrite the previous value, we need to remove it(the prev node)
                        //------ we're here also only if kOverwriteNode is specified (and of course NOT kKeepPrevValue)
                        _htIFnok( meCurs->Del(kFirstNode) );
                }//else we have kKeepPrevValue which also implies kOverwriteNode, but we don't remove it(the prev node)
                //...by here we have one pointee which we keep and obv. overwrite later ie. when we set the pointer to some other pointee
        }
//-------- by here we either have NULL pointer or a pointer that points to the same pointee it used to point to before init(except that maybe other elements present after the pointee were wiped out)

//---------- deinit cursor
#undef THROW_HOOK
#define THROW_HOOK \
        DEL_CURSOR

        _htIFnok( meCurs->DeInit() );

//---------- close cursor
#undef THROW_HOOK

        DEL_CURSOR

//---------- make sure we don't get here before a DeInit(); signal that we inited once
        fInited=true;
//---------- done
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLPointer::Init:end."<<endl;
#endif
//---------- end
        _OK;
}
/*******************************/
function
TDMLPointer :: DeInit()
{
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLPointer::DeInit:begin"<<endl;
#endif
//---------- was inited?
        __tIF(! this->IsInited());
//---------- cleanup
        if (NULL != fParentTxn){
                fParentTxn=NULL;
        }
//---------- signal we deinited
        fInited=false;
//---------- done
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLPointer::DeInit:end."<<endl;
#endif
//---------- end
        _OK;
}
/*******************************/
/*******************************/
/*******************************/
//constructor
TDMLCursor :: TDMLCursor(TLink *m_WorkingOnThisTLink):
        fCursor(NULL)
{
//---------- validity check of params
        fLink=m_WorkingOnThisTLink;
        __tIF(NULL == fLink);
//---------- and setting defaults
        fThisTxn=NULL;
        fDb=NULL;//also used by IsInited() below
}
/*******************************/
//destructor
TDMLCursor :: ~TDMLCursor()
{
//---------- catching late bugs
        __tIF(IsInited());
        __tIF(NULL == fLink);//cannot be
        __tIF(fCursor != NULL);//forgot to call DeInit() ?
        __tIF(NULL != fDb);//DeInit() must be called!
        __tIF(NULL != fThisTxn);
}
/*******************************/
bool
TDMLCursor :: IsInited()
{
        return (fDb != NULL);
}
/*******************************/
//opens the cursor
function
TDMLCursor :: InitCurs(
                const ENodeType_t a_NodeType,
                const NodeId_t a_NodeId,
                const ETDMLFlags_t a_Flags,//no flags supported yet!
                DbTxn *a_ParentTxn//can be NULL, no problem
                )
{
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::Init:begin"<<endl;
#endif
//---------- validating params
#define THROW_HOOK \
        CURSOR_CLOSE_HOOK \
        CURSOR_ABORT_HOOK
        _htIF(NULL != fThisTxn);//cannot call InitCurs() twice, not before DeInit(); however if called twice we need to close the cursor prior to aborting the current transaction!
//---------- check if called before; before DeInit()
        _htIF(this->IsInited());
#undef THROW_HOOK
//---------- keep validating params
        __tIF(a_NodeId.empty());
        __tIF(kNone != a_Flags);//no flags supported yet

//---------- new global(for this instance of TDMLCursor) transaction
        __tIFnok(fLink->NewTransaction(a_ParentTxn,&fThisTxn));

#define THROW_HOOK \
        CURSOR_ABORT_HOOK

//---------- set values for later use
        fCurKeyStr=a_NodeId;//yeah let's hope this makes a copy
        _h( fCurKey.set_data((void *)fCurKeyStr.c_str()) );//points to that, so don't kill fCurKeyStr!!
        _h( fCurKey.set_size((u_int32_t)fCurKeyStr.length() + 1) );
        fFlags=a_Flags;
//---------- show debug
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::Init:SetKey="<<
                (char *)fCurKey.get_data()<<endl;
#endif

//---------- choose global working dbase for this instance of TDMLCursor
        fNodeType=a_NodeType;
        switch (fNodeType) {
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
//---------- handling flags
        //note that DB_WRITECURSOR doesn't work because the dbase is not opened with DB_INITCDB

        if (fFlags != 0) {//no flags supported at this time
                _ht(no flags supported at this time);
        }
//---------- open new berkeley db cursor
        _htIF( 0 != fDb->cursor(fThisTxn,&fCursor, fFlags) );
#undef THROW_HOOK
#define THROW_HOOK \
        CURSOR_CLOSE_HOOK \
        CURSOR_ABORT_HOOK
        _htIF(NULL == fCursor);//feeling paranoid?
/*//---------- WORKAROUND: we need to init the cursor for Count() otherwise it throws EINVAL
        Dbt curVal;
        _h( curVal.set_flags(DB_DBT_MALLOC) );
        int err;
        _h( err=fCursor->get( &fCurKey, &curVal, DB_SET) );//get first, don't change key; note that there may be none!
        if (curVal.get_data())
                __( free(curVal.get_data()) );
        */
//---------- setting such that the next time we use Get it's gonna be the first time so we use DB_SET there
#undef THROW_HOOK
        fFirstTimeGet=true;

//---------- done
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::Init:end."<<endl;
#endif
//---------- end
        _OK;
}
/*******************************/
function
TDMLCursor :: DeInit()
{
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::DeInit:begin"<<endl;
#endif
//---------- was inited?
        __tIF(! this->IsInited());
//---------- trying to catch some inconsistencies; and setting defaults for next use of InitCurs()
#define THROW_HOOK \
        CURSOR_ABORT_HOOK
        _htIF(NULL == fCursor);//called DeInit() before InitCurs() ? or smth happened inbetween
        //---------- close cursor
        _htIF(0 != fCursor->close());
        fCursor=NULL;
        //---------- commit the transaction (started with InitCurs()
        __tIFnok( fLink->Commit(&fThisTxn) );
        fThisTxn=NULL;
#undef THROW_HOOK
        fDb=NULL;
//---------- done
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::DeInit:end."<<endl;
#endif
//---------- end
        _OK;
}
/*******************************/
function
TDMLCursor :: Find(
                NodeId_t &m_Node,
                const int a_Flags
                )
{
//---------- was inited?
        __tIF(! this->IsInited());
//---------- checking validity of params
#define THROW_HOOK \
        CURSOR_CLOSE_HOOK \
        CURSOR_ABORT_HOOK
        _htIF( (a_Flags != kNone) && (a_Flags - kCursorWriteLocks != 0) );//allowing only DB_RMW flag
#undef THROW_HOOK
//---------- try to locate m_Node
        __fIFnok( this->Get(m_Node, kPinPoint | a_Flags) ); //no hooks throw
//---------- done
        _OK;
}
/*******************************/
function
TDMLCursor :: Get(
                NodeId_t &m_Node,
                const int a_Flags
                )
{
//---------- was inited?
        __tIF(! this->IsInited());
//----------
#define THROW_HOOK \
        CURSOR_CLOSE_HOOK \
        CURSOR_ABORT_HOOK

#ifdef SHOWKEYVAL
                __( std::cout<<"\tTDMLCursor::Get:begin:Key="<<
                (char *)fCurKey.get_data()<<endl; );
#endif
//---------- setting flags
        int tmpFlags=a_Flags;

        _makeFLAG(kCursorWriteLocks);
        _makeFLAG(kNextNode);
        _makeFLAG(kPrevNode);
        _makeFLAG(kFirstNode);
        _makeFLAG(kLastNode);
        _makeFLAG(kCurrentNode);
        _makeFLAG(kPinPoint);

//---------- handling flags
        u_int32_t dbFlags=0;

        if (fl_kCursorWriteLocks) {
                dbFlags|=DB_RMW;
        }

        //---------- setting this which is used in 2 places
        Dbt curVal;
        _h( curVal.set_flags(DB_DBT_MALLOC) );

if ( (fl_kNextNode) || (fl_kPrevNode) || (fl_kFirstNode) ){
        if ( (fFirstTimeGet)||(fl_kFirstNode) ) {
                fFirstTimeGet=false;
                dbFlags|=DB_SET;
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::Get:dbFlags|=DB_SET"<<endl;
#endif
        } else {
                if (fl_kNextNode) {
                                dbFlags|=DB_NEXT_DUP;
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::Get:dbFlags|=DB_NEXT_DUP"<<endl;
#endif
                } else {
                        if (fl_kPrevNode) {
                                dbFlags|=DB_PREV;
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::Get:dbFlags|=DB_PREV"<<endl;
#endif
                        }
                }
        }
} else {
                if (fl_kLastNode) {
                        //FIXME: somehow! ie. parse all until not found, return last found;
                        _ht(bdb apparently doesnt support returning the last datum of a given key if this key has more than one datum associated with it ie. A - B and A - D  cannot return A - D with DB_LAST refers to last of dbase not of key and key is ignored)
                                /*
                        dbFlags|=DB_KEYLAST;//Dbc::get: Invalid argument
                                //DB_LAST not working either, gets the last datum of the dbase not of the key
                        #ifdef SHOWKEYVAL
                                std::cout<<"\tTDMLCursor::Get:dbFlags|=DB_KEYLAST"<<endl;
                        #endif
                        */
                } else {
                        if (fl_kCurrentNode) {
                                dbFlags|=DB_CURRENT;
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::Get:dbFlags|=DB_CURRENT"<<endl;
#endif
                        } else {
                                if (fl_kPinPoint) {
                                        dbFlags|=DB_GET_BOTH;
                                        _h( curVal.set_flags(0) );
                                        _h( curVal.set_data((void *)m_Node.c_str()) );
                                        _h( curVal.set_size((u_int32_t)m_Node.length() + 1) );
#ifdef SHOWKEYVAL
                __( std::cout<<"\tTDMLCursor::Get:PinPoint:Val="<<
                        (char *)curVal.get_data()<<endl;
                );
#endif
                                }
                        }
                }
        //}
}
//---------- trapping illegal flags, or unhandled flags
        __tIF(0 != tmpFlags);

//---------- attempting to fetch key/data pair
#define FREE_VAL \
        if ((DB_GET_BOTH != (dbFlags & DB_GET_BOTH)) && (curVal.get_data())) \
                __( free(curVal.get_data()) );
#undef THROW_HOOK
#define THROW_HOOK \
        FREE_VAL \
        CURSOR_CLOSE_HOOK \
        CURSOR_ABORT_HOOK

        int err;
/*        try {
                (err=fCursor->get( &fCurKey, &curVal, dbFlags));
        } catch (DbException &e) {
                cout << e.what()<<endl;
                throw;
        }*/
        //_hif ( DB_NOTFOUND == err) {
        _hif( DB_NOTFOUND == (err=fCursor->get( &fCurKey, &curVal, dbFlags)) ) {
#ifdef SHOWKEYVAL
                __( std::cout<<"\tTDMLCursor::Get:fail:Key="<<
                (char *)fCurKey.get_data()<<endl;
                );
#endif
                FREE_VAL;//maybe?
                _fret(kFuncNotFound);
        }_fih
        _htIF(0 != err);//other unspecified error
        _htIF(NULL == curVal.get_data());//impossible?
//---------- setting the return value
        __( m_Node=(char *)curVal.get_data() );//hopefully this does copy contents not just point!
#ifdef SHOWKEYVAL
                __( std::cout<<"\tTDMLCursor::Get:found:"<<
                (char *)fCurKey.get_data()<<" = "<< m_Node <<endl; //weird thing here, when DB_SET, key=data
                );
#endif

//---------- checking if link is consistent (optional)
//follows: consistency check, if A -> B exist so must B <- A in the other database; otherwise something happened prior to calling this function and we catched it here
        //_htIFnok( fLink->IsLinkConsistent(fNodeType, fCurKeyStr, m_Node, fThisTxn) ); disabled because of looping

//---------- free value returned by get()
        FREE_VAL;//maybe we should call this in DeInit(); watch the HOOK!
//---------- done
#ifdef SHOWKEYVAL
        std::cout<<"\tTDMLCursor::Get:done."<<endl;
#endif
#undef THROW_HOOK


//---------- end
        _OK;
#undef FREE_VAL
}
/*******************************/
function
TDMLCursor :: Del(
                const ETDMLFlags_t a_Flags,
                const NodeId_t a_Node
                )
{

//---------- was inited?
        __tIF(! this->IsInited());

//---------- validating params
#define THROW_HOOK \
        CURSOR_CLOSE_HOOK \
        CURSOR_ABORT_HOOK

//---------- good to go
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::Del:begin:"<< (fNodeType==kGroup?"Group":"SubGroup") <<": <Key="<<
                (char *)fCurKey.get_data()<<"> flags="<<a_Flags<<endl;
#endif
//---------- setting flags and checking for unicity
        int tmpFlags=a_Flags;
        _makeUniqueFLAG(kCurrentNode);
        _makeUniqueFLAG(kFirstNode);
        _makeUniqueFLAG(kLastNode);
        _makeUniqueFLAG(kNextNode);
        _makeUniqueFLAG(kPrevNode);
        _makeUniqueFLAG(kPinPoint);
//---------- validating dbFlags
        _htIF( (a_Node.empty())&&( (!fl_kCurrentNode)&&(!fl_kFirstNode)&&(!fl_kLastNode) ) );//these accept empty, others not!
        __tIF(0 != tmpFlags);//illegal flags

//---------- getting current item key/val pair and lock it for write
        NodeId_t nod=a_Node;
        _hfIFnok(this->Get(nod, a_Flags | kCursorWriteLocks) );
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::Del:found:"<< (fNodeType==kGroup?"Group":"SubGroup") <<": Key="<<
                (char *)fCurKey.get_data()<<" val="<<nod<<endl;
#endif
        Dbt val;
        _h( val.set_flags(0) );
        _h( val.set_data((void *)nod.c_str()) );
        _h( val.set_size((u_int32_t)nod.length() + 1) );
//---------- consistend delete (from both dbases!)
        //---------- ok, deleting current from the other dbase first becouse this is the one that doesn't have a lock yet!
        if (kSubGroup == fNodeType) {
                //we consider the other dbase: primary
                _htIFnok( fLink->delFrom(fLink->g_DBGroupToSubGroup, fThisTxn, &fCurKey, &val) );
        } else { //kGroup, thus the other dbase is secondary
                _htIFnok( fLink->delFrom(fLink->g_DBSubGroupFromGroup, fThisTxn, &val, &fCurKey) );
        }
        //---------- deleting from the cursor's dbase
        _htIF(0 != fCursor->del(0) );

//---------- done
#undef THROW_HOOK

#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::Del:done:"<<
                (char *)fCurKey.get_data()<<"="<<nod<<endl;
#endif
//---------- end
        _OK;
}
/*******************************/
function
TDMLCursor :: Put(
                const NodeId_t a_Node,
                const ETDMLFlags_t a_Flags
                )
{

//---------- was inited?
        __tIF(! this->IsInited());
//---------- validating params
#define THROW_HOOK \
        CURSOR_CLOSE_HOOK \
        CURSOR_ABORT_HOOK

        _htIF(a_Node.empty());

//---------- good to go
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::Put:begin:"<< (fNodeType==kGroup?"Group":"SubGroup") <<"Key="<<
                (char *)fCurKey.get_data()<< " val=" << a_Node << endl;
#endif
//---------- setting flags
        int tmpFlags=a_Flags;
        _makeFLAG(kCurrentNode);
        _makeFLAG(kAfterNode);
        _makeFLAG(kNextNode);
        _makeFLAG(kBeforeNode);
        _makeFLAG(kFirstNode);
        _makeFLAG(kLastNode);

//---------- handling flags
        u_int32_t dbFlags=0;//FIXME:
        if (fl_kCurrentNode) {
                dbFlags|=DB_CURRENT;//overwrite current, FIXME: 2nd db must be updated also
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::Put:dbFlags|=DB_CURRENT"<<endl;
#endif
        } else {
                if ( (fl_kAfterNode) || (fl_kNextNode) ) {
                        dbFlags|=DB_AFTER; //after current
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::Put:dbFlags|=DB_AFTER"<<endl;
#endif
                } else {
                        if (fl_kBeforeNode) {
                                dbFlags|=DB_BEFORE;//before current
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::Put:dbFlags|=DB_BEFORE"<<endl;
#endif
                        } else {
                                if (fl_kFirstNode) {
                                        dbFlags|=DB_KEYFIRST;
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::Put:dbFlags|=DB_FIRST"<<endl;
#endif
                                } else {
                                        if (fl_kLastNode) {
                                                dbFlags|=DB_KEYLAST;
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::Put:dbFlags|=DB_LAST"<<endl;
#endif
                                        }
                                }
                        }
                }
        }
//---------- validating dbFlags
        __tIF(0 != tmpFlags);//illegal flags
        _htIF(0==dbFlags);

//---------- ok, creating the specified link (both FL & RL)
        function err;
        _hif ( kFuncAlreadyExists == (err=fLink->NewCursorLink(fCursor, dbFlags, fNodeType, fCurKeyStr, a_Node, fThisTxn)) ) {
                _fret(err);
        }_fih
        _htIFnok(err);//other unhandled error is bad for us

//---------- checking is link is trully consistent (ie. that both FL and RL exist)
        _htIFnok( fLink->IsLinkConsistent(fNodeType, fCurKeyStr, a_Node, fThisTxn) );
//---------- done
#undef THROW_HOOK

#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::Put:done:"<<
                (char *)fCurKey.get_data()<<" = "<< a_Node <<endl;
#endif
//---------- end
        _OK;
}
/*******************************/
function
TDMLCursor :: Put(
                const NodeId_t a_Node1,
                const ETDMLFlags_t a_Flags,
                const NodeId_t a_Node2
                )
{
//---------- was inited?
        __tIF(! this->IsInited());
//---------- handling flags
        if ( (a_Flags == kBeforeNode) || (a_Flags == kAfterNode) || (a_Flags==kThisNode) ) {//only one of these allowed!
                //---------- pinpoint prior to put
                NodeId_t temp=a_Node2;
                __fIFnok( this->Find(temp, kCursorWriteLocks) );
                __tIF(temp != a_Node2);
        } else __t(useless combination of flags);
//---------- put
        __fIFnok( this->Put(a_Node1, a_Flags) );
//---------- done
        _OK;
}
/*******************************/
function
TDMLCursor :: Count(
                db_recno_t &m_Into
                )
{
//---------- was inited?
        __tIF(! this->IsInited());
//----------
#define THROW_HOOK \
        CURSOR_CLOSE_HOOK \
        CURSOR_ABORT_HOOK

/*        try {
                _htIF( 0 != fCursor->count( &m_Into, 0) );
        } catch (DbException &e) {
                cout<<"TDMLCursor :: Count :#"<<e.get_errno()<<e.what()<<endl;
                cout<<EINVAL<<endl;
        } */
        //NodeId_t nod;
        //_TRY( Get(nod, kCurrentNode) );//this should reinit the cursor as needed for Dbc->count() - doesn't work
        int err=0;
        try {
                err=fCursor->count( &m_Into, 0);
        } catch (DbException &e) {
                //apparently 22=="Invalid argument" and happens when most likely the cursor wasn't initialized ie. key is unknown
                __if (22 != e.get_errno()) {
                        cout<<"TDMLCursor :: Count :#"<<e.get_errno()<<e.what()<<endl;
                        _ht(some other error);//throw;
                }__fi
                m_Into=0;//FIXME: Count() is broken; sometimes the count is more than 0 but we still get here because somehow the Dbc cursor must be initialized by ie. a Get or Put, otherwise it doesn't know the key to return the dups for. (lame!)
        }
        _htIF(0 != err);
//---------- done
        _OK;
#undef THROW_HOOK
}
/*******************************/
        function
        TLink::NewCursorLink( //uses TDMLCursor::Put()
                Dbc * const m_Cursor,
                const u_int32_t a_CursorPutFlags,
                const ENodeType_t a_NodeType,
                const NodeId_t a_NodeId1,
                const NodeId_t a_NodeId2,
                DbTxn *a_ParentTxn
                )
        {
                __fIFnok( l0_newCursorLink(fDBEnviron, g_DBGroupToSubGroup, g_DBSubGroupFromGroup, m_Cursor, a_CursorPutFlags, a_NodeType, a_NodeId1, a_NodeId2, &fStackLevel, a_ParentTxn) );
                _OK;
        }

/*******************************/
        function
        TLink::putInto(
                Db *a_DBInto,
                DbTxn *a_ParentTxn,
                Dbt *a_Key,
                Dbt *a_Value,
                const u_int32_t a_CursorPutFlags,//mandatory if m_Cursor is used below
                Dbc * const m_Cursor)
        {
                __fIFnok( l0_putInto( fDBEnviron, a_DBInto, a_ParentTxn, a_Key, a_Value, &fStackLevel, a_CursorPutFlags, m_Cursor) );
                _OK;
        }

/*******************************/
        function
        TLink::delFrom(
                Db *a_DBInto,
                DbTxn *a_ParentTxn,
                Dbt *a_Key,
                Dbt *a_Value)
        {
                __fIFnok( l0_delFrom( fDBEnviron, a_DBInto, a_ParentTxn, a_Key, a_Value, &fStackLevel) );
                _OK;
        }

/*******************************/
        function
        TLink::findAndChange(
                Db *a_DBWhich,
                DbTxn *a_ParentTxn,
                Dbt *a_Key,
                Dbt *a_Value,
                Dbt *a_NewValue)
        {
                __fIFnok( l0_findAndChange(fDBEnviron, a_DBWhich, a_ParentTxn, a_Key, a_Value, a_NewValue, &fStackLevel) );
                _OK;
        }


/*******************************/
/*******************************/
/*******************************/
/*******************************/
/*******************************/
/*******************************/
/*******************************/
