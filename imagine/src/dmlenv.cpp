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
//#define SHOWKEYVAL
#define SHOWCONTENTS //if disabled, the consistency check is still performed, just no records are displayed on console
//#define SHOWTXNS
/*************/

/****************************/
//some notions:
//a (consistent) link is formed of two: forward link AND reverse link
//a forward link(FL) is a kGroup -> kSubGroup ie. A->B in primary
//a reverse link(RL) is a kSubGroup <- kGroup ie. B<-A in secondary
//a reverse link is a complementary to a forward link (and a forward link is the complementary of a reverse link)
//PRI   |       SEC
//--------------------
//A->B  |       B<-A
//G->sG |      sG<-G
//FL    |       RL
//a complementary link(CL) is the opposite of a FL/RL ie. complementary link of a forward link is the reverse link; complementary link of a reverse link is a forward link
//a self link(SL) is a FL&RL that points to self ie. A->A | A<-A  => usually regarded as a NULL pointer
//a pointer is that which points to something; is the part on the left ie. A from A->B ; OR B from B<-A
//a pointee is that which is being pointed by a pointer; the part on the right ie. B from A->B; OR A from B<-A
//<- or -> doesn't indicate which is the pointer(usually) it means that the part on the '-' side ie. A from A->B is the kGroup and the part on the '>' or '<' side is the kSubGroup ie. B of B<-A is the kSubGroup and this link denotes a reverse link representation
//in our system if a RL exists then a FL counterpart must also exist (unless a bug is present) also if a FL exists the RL complement must be present also; this is what we call a (consistent) link within our lowest level system of demlinks; if you go lower than that you're here in C++ code
//a node can be any of kGroup(pointer) or kSubGroup(pointee)

/****************************/
/*****************************************************************/
/*****************************************************************/
/********************************************************/
#define _makeFLAG(whatFlag ) \
        bool fl_##whatFlag=whatFlag == (tmpFlags & whatFlag); \
        if (fl_##whatFlag) { \
                tmpFlags -= whatFlag; /*substract this flag*/\
        } /* tmpFlags is a variable that supposedly is already defined */
#define _makeUniqueFLAG(whatFlag ) \
        bool fl_##whatFlag=whatFlag == (tmpFlags & whatFlag); \
        if (fl_##whatFlag) { \
                tmpFlags -= whatFlag; /*substract this flag*/\
                if (0 != tmpFlags) { \
                        _ht(this flag is present but not uniquely present: ie. there are more others); \
                }\
        } /* tmpFlags is a variable that supposedly is already defined */

#define CURSOR_ABORT_HOOK \
        __(fLink->Abort(&fThisTxn)); \
        fThisTxn=NULL;

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
TLink::findAndChange( //affecting only the value of the a_Key, aka the key cannot be changed anyways(u must do del then put for that)
                Db *a_DBWhich,
                DbTxn *a_ParentTxn,
                Dbt *a_Key,
                Dbt *a_Value,
                Dbt *a_NewValue)//we fail(not throw) if newval already exists
{
//------------ validate params
        __tIF(NULL == a_Key);
        __tIF(NULL == a_Value);
        __tIF(NULL == a_NewValue);
        __tIF(NULL == a_DBWhich);

//------------ create a new transaction
        DbTxn *thisTxn;
        __tIFnok(NewTransaction(a_ParentTxn,&thisTxn ));

#define THROW_HOOK \
        ABORT_HOOK

//------------ open new cursor
        /*Note: Cursors may not span transactions; that is, each cursor must be opened and closed within a single transaction.*/
        /*on a more personal note: you cannot Put() if you created other transactions after the cursor transaction, you must have those closed first; they don't have to be children of cursor transaction - same applies;*/
        Dbc *cursor1=NULL;
        _htIF( 0 != a_DBWhich->cursor(thisTxn,&cursor1, 0) );

#undef THROW_HOOK
#define THROW_HOOK \
                __( cursor1->close() );/*done prior to Abort()*/\
                ABORT_HOOK;
#define ERR_HOOK \
        THROW_HOOK


//------------ check and fail if the key/newval pair already exists
        //a_Key/a_NewValue must not already exist!
        _hif( DB_NOTFOUND != cursor1->get( a_Key, a_NewValue, DB_GET_BOTH|DB_RMW) ) {
                _hreterr(kFuncAlreadyExists);//doesn't throw if already exists but instead it reports it
        }_fih
//------------ find current key/data pair, position on it and aquire a write lock -ie. others cannot write to it
        _htIF(0!=cursor1->get( a_Key, a_Value, DB_GET_BOTH|DB_RMW));
//------------ change it to new key/newval
        _htIF(0!=cursor1->put( a_Key/*ignored*/, a_NewValue, DB_CURRENT));

//------------ close cursor
#undef THROW_HOOK
#define THROW_HOOK \
        ABORT_HOOK

        _h(cursor1->close());

//------------ commit transaction
#undef THROW_HOOK
#undef ERR_HOOK
        __tIFnok( Commit(&thisTxn) );

//------------ end
        _OK;
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

//------------ validating params
        __tIF(NULL == a_Key);
        __tIF(NULL == a_Value);
        __tIF(NULL == a_DBInto);


//------------ new transaction
        DbTxn *thisTxn;
        __tIFnok(NewTransaction(a_ParentTxn,&thisTxn ));

#define THROW_HOOK \
        ABORT_HOOK

//------------ new cursor
        Dbc *cursor1=NULL;
        _htIF( 0 != a_DBInto->cursor(thisTxn,&cursor1, 0) );

#undef THROW_HOOK
#define THROW_HOOK \
                __( cursor1->close() );/*done prior to Abort()*/\
                ABORT_HOOK

//------------ aquire a write lock on this specific key/data pair and position the cursor on it
        //position on the item
        _htIF(0 != cursor1->get(a_Key, a_Value, DB_GET_BOTH|DB_RMW) );//apparently fails if not found!

//------------ delete current item
        _htIF( 0 != cursor1->del(0) );

//------------ close cursor
#undef THROW_HOOK
#define THROW_HOOK \
        ABORT_HOOK

        _h(cursor1->close());

//------------ commit transaction
#undef THROW_HOOK
        __tIFnok( Commit(&thisTxn) );

//------------ end
        _OK;
}


/*************************/
function
TLink :: putInto(
                Db *a_DBInto,
                DbTxn *a_ParentTxn,//can be null
                Dbt *a_Key,
                Dbt *a_Value,
                const u_int32_t a_CursorPutFlags,
                Dbc *const m_Cursor//can be NULL, then put() is used thus appended to the list of data of that key(ie. order of insertion)
                )
{
//------------ validate params
        __tIF(NULL == a_DBInto);
        __tIF(NULL == a_Key);
        __tIF(NULL == a_Value);

//------------ ready to start
#ifdef SHOWKEYVAL
        __( cout << "putInto: begin:"<<(NULL!=m_Cursor?"Cursor:":":")<< (char *)a_Key->get_data() << " = " << (char *)a_Value->get_data() <<endl; );
#endif

//------------ open a new cursor
        //since dbase is opened DB_DUP it will allow dup key+data pairs; but we won't!
//no DUPS! fail(not throw!) if already exists
        //the value we're about to put() must not already exist within this key!! this isn't allowed in our system
        Dbc *cursor1 = NULL;
        __tIF( 0 != a_DBInto->cursor(a_ParentTxn, &cursor1, 0));

#undef THROW_HOOK
#define THROW_HOOK \
                __( cursor1->close() );
#define ERR_HOOK \
        THROW_HOOK

//------------ if key/data pair exists we fail since our purpose is fulfilled
        _hif (0 == cursor1->get(a_Key, a_Value, DB_GET_BOTH)) {
                _hreterr(kFuncAlreadyExists);
        }_fih

//------------ close cursor
#undef THROW_HOOK
#undef ERR_HOOK
        __tIF(0 != cursor1->close());

//------------ key/data pair doesn't already exist so it's safe to put it
        //if not found, then put it
        if (NULL == m_Cursor) { //only using a new child transaction if there's no cursor specified
        //------------ new transaction
                DbTxn *thisTxn;
                __tIFnok(NewTransaction(a_ParentTxn, &thisTxn ));
#define THROW_HOOK \
        ABORT_HOOK
        //------------ create the key/data pair
                        _htIF(0 != a_DBInto->put(thisTxn, a_Key, a_Value, 0) );
                        /*The default behavior of the Db::put
                         * function is to enter the new key/data pair, replacing any previously existing key if duplicates are
                         * disallowed, or adding a duplicate data item if duplicates are allowed. (from berkeley db docs)*/
        //------------ commit transaction
#undef THROW_HOOK
                __tIFnok( Commit(&thisTxn) );
        } else {
        //------------ use the passed cursor to create the key/data pair as specified in the params of this func.
                __tIF( m_Cursor->put(a_Key, a_Value, a_CursorPutFlags) );//even if DB_CURRENT and a_Value exist in this same place, it'll return kFuncAlreadyExists above! there's no use to overwrite with the same value!
        }

//------------ after done
#ifdef SHOWKEYVAL
        __( cout << "putInto: done:"<<(NULL!=m_Cursor?"Cursor:":":")<< (char *)a_Key->get_data() << " = " << (char *)a_Value->get_data() <<endl; );
#endif

//------------ end
        _OK;
}
/****************************/


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

//--------- validate params
        __tIF(a_GroupId.empty());
        __tIF(a_SubGroupId.empty());
        __tIF(a_NewLinkName.empty());

//--------- new encapsulating transactions (the outter transaction)
        DbTxn *thisTxn;
        __tIFnok(NewTransaction(a_ParentTxn,&thisTxn ));

#define THROW_HOOK \
        ABORT_HOOK
#define ERR_HOOK \
        THROW_HOOK

//--------- setting values
                Dbt value;
                Dbt newValue;
                Dbt key;

                _h( key.set_data((void *)a_GroupId.c_str()) );
                _h( key.set_size((u_int32_t)a_GroupId.length() + 1) );

                _h( value.set_data((void *)a_SubGroupId.c_str()) );
                _h( value.set_size((u_int32_t)a_SubGroupId.length() + 1) );

                _h( newValue.set_data((void *)a_NewLinkName.c_str()) );
                _h( newValue.set_size((u_int32_t)a_NewLinkName.length() + 1) );

#ifdef SHOWKEYVAL
        __(
        cout <<"\t"<< "Mod:begin:"<<
                (char *)key.get_data()<<
                "-" <<
                (char *)value.get_data()<<
                "=>" <<
                (char *)newValue.get_data()<<
                endl;
          );
#endif

//--------- modify primary dbase
        //modify in primary
        _hfIFnok( findAndChange(g_DBGroupToSubGroup,thisTxn,&key,&value,&newValue) );//from A->B changed to A->C(only if A->C didn't exist already)

//--------- modify in secondary dbase
        _htIFnok( delFrom(g_DBSubGroupFromGroup,thisTxn,&value,&key) );//del B<-A from SECondary
        //must not return kFuncAlreadyExists otherwise a bug is present somewhere
        _htIFnok( putInto(g_DBSubGroupFromGroup,thisTxn,&newValue,&key) );//create C<-A in secondary

//--------- consistency check
        _htIFnok( IsLinkConsistent(kGroup, a_GroupId, a_NewLinkName, thisTxn) );

//--------- commit transaction
#undef THROW_HOOK
#undef ERR_HOOK
        __tIFnok( Commit(&thisTxn) );

//--------- after done
#ifdef SHOWKEYVAL
        cout <<"\t"<< "Mod:done."<<endl;
#endif


//--------- end
        _OK;

}

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
        _htIFnok( IsLink(a_NodeType, a_NodeId1, a_NodeId2, thisTxn) );

//------------ check if link exists on the other dbase
        ENodeType_t otherNodeType=a_NodeType;
        if (otherNodeType == kGroup) {
                otherNodeType=kSubGroup;
        } else {//assumed kSubGroup; we're in trouble if there will be three types
                otherNodeType = kGroup;
        }
        _htIFnok( IsLink(otherNodeType, a_NodeId2, a_NodeId1, thisTxn) );

//------------ commit transaction
#undef THROW_HOOK
        __tIFnok( Commit(&thisTxn) );

//------------ end
        _OK;
}
/*************************/
function
TLink::IsGroup(
                const ENodeType_t a_NodeType,
                const NodeId_t a_GroupId,
                DbTxn *a_ParentTxn
                )
{

//------------ validate param
        __tIF(a_GroupId.empty());

//------------ new transaction
        DbTxn *thisTxn;
        __tIFnok(NewTransaction(a_ParentTxn,&thisTxn ));

#define THROW_HOOK \
        ABORT_HOOK
#define ERR_HOOK \
        THROW_HOOK

//------------ set values
        Dbt value;
        Dbt key;

        _h( key.set_data((void *)a_GroupId.c_str()) );
        _h( key.set_size((u_int32_t)a_GroupId.length() + 1) );
        _h( value.set_flags(DB_DBT_MALLOC) );

#ifdef SHOWKEYVAL
        _h(
                std::cout<<"\tIsGroup:begin:"<<
                (char *)key.get_data()<<endl;
          );
#endif

//------------ select proper database
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

//------------ open new cursor
        Dbc *cursor1=NULL;
        _htIF( 0 != db->cursor(thisTxn,&cursor1, 0) );

#undef ERR_HOOK
#undef THROW_HOOK
#define THROW_HOOK \
                __( cursor1->close() );/*done prior to Abort()*/\
                ABORT_HOOK;
#define FREE_VAL \
        if (value.get_data()) \
                __( free(value.get_data()) );
#define ERR_HOOK \
        FREE_VAL \
        THROW_HOOK


//------------ try to get first item of key with DB_SET and return it in value ie. a->b a->c returnc "b"
        int err;
        _hif( DB_NOTFOUND == (err=cursor1->get( &key, &value, DB_SET)) ) {
#ifdef SHOWKEYVAL
                std::cout<<"\tIsGroup: is not!."<<endl;
#endif
                _hreterr(kFuncNotFound);
        }_fih
        //else throws! just in case it doesn't we check for err==0 => ok, below
        _htIF(err!=0);
        FREE_VAL;

//------------ close cursor
#undef THROW_HOOK
#define THROW_HOOK \
        ABORT_HOOK
#undef ERR_HOOK

        _htIF(0 != cursor1->close());

//------------ commit transaction
#undef THROW_HOOK
#undef FREE_VAL
        __tIFnok( Commit(&thisTxn) );

//------------ after done
#ifdef SHOWKEYVAL
                std::cout<<"\tIsGroup:true."<<endl;
#endif


//------------ end
        _OK;//found
}
/*************************/
function
TLink::IsLink(
                const ENodeType_t a_NodeType,
                const NodeId_t a_NodeId1,//may or may not exist
                const NodeId_t a_NodeId2,//same
                DbTxn *a_ParentTxn
                )
{
//------------ validate params
        __tIF(a_NodeId1.empty());
        __tIF(a_NodeId2.empty());

//------------ create new transaction for us
        DbTxn *thisTxn;
        __tIFnok(NewTransaction(a_ParentTxn,&thisTxn ));

#undef THROW_HOOK
#define THROW_HOOK \
        ABORT_HOOK
#define ERR_HOOK \
        THROW_HOOK

//------------ choose proper dbase
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

//------------ set values for later calls
        Dbt value;
        Dbt key;

        _h( key.set_data((void *)a_NodeId1.c_str()) );
        _h( key.set_size((u_int32_t)a_NodeId1.length() + 1) );
        _h( value.set_data((void *)a_NodeId2.c_str()) );
        _h( value.set_size((u_int32_t)a_NodeId2.length() + 1) );

#ifdef SHOWKEYVAL
                std::cout<<"\tIsLink:begin:"<<
                (char *)key.get_data()<<
                "->" <<
                (char *)value.get_data()<<endl;
#endif

//------------ open new cursor
        Dbc *cursor1=NULL;
        _htIF( 0 != db->cursor(thisTxn,&cursor1, 0) );//for duplicate key values(as it is our case) we must use a cursor instead of Db::get()

#undef ERR_HOOK
#undef THROW_HOOK
#define THROW_HOOK \
                __tIF( 0 != cursor1->close() );/*done prior to Abort()*/\
                ABORT_HOOK;
#define ERR_HOOK \
        THROW_HOOK


//------------ try to get the key/data pari (both)
        int err;
        _hif( DB_NOTFOUND == (err=cursor1->get( &key, &value, DB_GET_BOTH)) ) {
#ifdef SHOWKEYVAL
                std::cout<<"\tIsLink: is not!."<<endl;
#endif
                _hreterr(kFuncNotFound);
        }_fih
        //else throws! just in case it doesn't we check for err==0 => ok, below
        _htIF(err!=0);

//------------ close the cursor
#undef THROW_HOOK
#define THROW_HOOK \
        ABORT_HOOK
#undef ERR_HOOK

        _htIF( 0 != cursor1->close());

//------------ commit transaction
#undef THROW_HOOK
        __tIFnok( Commit(&thisTxn) );

//------------ after done
#ifdef SHOWKEYVAL
        std::cout<<"\tIsLink:true."<<endl;
#endif


//------------ end
        _OK;
}

/*************************/
function
TLink::NewCursorLink(//creates a consistent link between two nodes in the sense selected by a_NodeType below
                Dbc * const m_Cursor,
                const u_int32_t a_CursorPutFlags,
                const ENodeType_t a_NodeType,
                const NodeId_t a_NodeId1,//may or may not exist
                const NodeId_t a_NodeId2,//same
                DbTxn *a_ParentTxn //no child transaction created except with putInto(); apparently the cursor may not span transactions RTFM;
                )
{

//---------- validating params
        __tIF(a_NodeId1.empty());
        __tIF(a_NodeId2.empty());



//---------- setting values for later calls
        Dbt value;
        Dbt key;

        __( key.set_data((void *)a_NodeId1.c_str()) );
        __( key.set_size((u_int32_t)a_NodeId1.length() + 1) );
        __( value.set_data((void *)a_NodeId2.c_str()) );
        __( value.set_size((u_int32_t)a_NodeId2.length() + 1) );

        Dbt curvalue; //curvalue is used to read current value or key when DB_CURRENT is flagged
        __(curvalue.set_flags(DB_DBT_MALLOC););

//---------- handles both G-sG(FL) and sG-G(RL) links
        switch (a_NodeType) {
                case kGroup: {//forward link
                //---------- we create a link using a forward link as params to this function; we must create a forward link from a kGroup to a kSubGroup this also implies a reverse link from kSubGroup to kGroup  => resulting in a (consistent)link
#ifdef SHOWKEYVAL
                __(
                std::cout<<"\tNewCursorLink(G-sG =forward link):begin:"<<
                (char *)key.get_data()<<
                "->" <<
                (char *)value.get_data()<<endl;
                );
#endif
                //---------- if DB_CURRENT save old value(sG) of forward link for later
                if (a_CursorPutFlags - DB_CURRENT == 0) {//must read current value for later to delFrom()
                                __tIF(0 != m_Cursor->get(&key, &curvalue, DB_CURRENT | DB_RMW) );//flagged for later deletion
                }

                //---------- overwrite old value; insert/overwrite forward link
                        //insert or overwrite in primary
                        __fIFnok( putInto(g_DBGroupToSubGroup, a_ParentTxn, &key,&value, a_CursorPutFlags, m_Cursor) );

                //---------- if DB_CURRENT delete key/oldvalue from the other database
                // if DB_CURRENT then we must pre-delete the old value to make dbase consistent within it's own system (our defined system)
                if (a_CursorPutFlags - DB_CURRENT == 0) {
#ifdef SHOWKEYVAL
                        __(
                        std::cout<<"\tNewCursorLink:del(sG-G =reverse link):"<<
                        (char *)curvalue.get_data()<<
                        "->" <<
                        (char *)key.get_data()<<
                        endl;
                        );
#endif
                        //---------- delete saved_old_value reverse link from secondary
                        __tIFnok( delFrom(g_DBSubGroupFromGroup, a_ParentTxn, &curvalue,&key) );//order of insertion(appended)
                        if (curvalue.get_data()) {
                                        __( free(curvalue.get_data()) );
                        }
                }//if
                //---------- insert reverse link in secondary
                        //just insert in secondary
                        //if we're here, means we successfuly entered the record in primary, so the record must be successfully entered in secondary, ie. cannot return kFuncAlreadyExists; so it must be clean, otherwise we throw (up;)
                        __tIFnok( putInto(g_DBSubGroupFromGroup, a_ParentTxn, &value,&key) );//appended at end of list
                                break;
                }//case
                case kSubGroup: {//reverse link case
                //---------- we create a link by using a reverse link as input params to this function
#ifdef SHOWKEYVAL
                __(
                std::cout<<"\tNewCursorLink(sG-G =reverse link):begin:"<<
                (char *)key.get_data()<<
                "->" <<
                (char *)value.get_data()<<endl;
                );
#endif


                //---------- taking care of the reverse link first
#ifdef SHOWKEYVAL
                std::cout<<"\tNewCursorLink(sG-G =RL):part1:secondary db"<<endl;
#endif
                //FIXed: must delete or replace the other connection if DB_CURRENT overwritten something just in one dbase ie. previous B <- J becomes B <- F (in secondary dbase) but remains J -> B and is added F -> B in primary; solution is to replace J -> B to F -> B or if not possible, delete then add; yes DB_CURRENT ignores the key so we can't change the key only the data, thus we will delete J -> B and insert a new F -> B key/data pair with put() w/o cursor
                                //if (a_CursorPutFlags & DB_CURRENT) {
                                //---------- if DB_CURRENT save old pointee
                                if (a_CursorPutFlags - DB_CURRENT == 0) {//must read current value for later to delFrom()
                                        __tIF(0 != m_Cursor->get(&key, &curvalue, DB_CURRENT | DB_RMW) );//flagged for later deletion
                                }
                                //---------- ins/overwrite the reverse link with the new pointee
                                //insert or overwrite in secondary
                                //if we're here, means we successfuly entered the record in primary, so the record must be successfully entered in secondary, ie. cannot return kFuncAlreadyExists; so it must be clean, otherwise we throw (up;)
                                __fIFnok( putInto(g_DBSubGroupFromGroup, a_ParentTxn, &key,&value, a_CursorPutFlags,m_Cursor) );
                //---------- taking care of the forward link
#ifdef SHOWKEYVAL
                std::cout<<"\tNewCursorLink(sG-G =RL):part2:primary db"<<endl;
#endif
                                //---------- if DB_CURRENT then detele old pointee as part of a forward link
                                //if DB_CURRENT then we must pre-delete the old value to make dbase consistent within it's own system (our defined system)
                //DB_XXX flags are not binary ie. DB_CURRENT=7 and DB_KEYFIRST=15 if the latter is present so it the former
                                //if (a_CursorPutFlags & DB_CURRENT) {
                                if (a_CursorPutFlags - DB_CURRENT == 0) {
#ifdef SHOWKEYVAL
                __(
                std::cout<<"\tNewCursorLink:del(G-sG =FL):"<<
                (char *)curvalue.get_data()<<
                "->" <<
                (char *)key.get_data()<<
                endl;
                );
#endif
                                        //---------- del forwardlink pointing to old saved pointee
                                       __tIFnok( delFrom(g_DBGroupToSubGroup, a_ParentTxn, &curvalue,&key) );//order of insertion(appended)
                                       if (curvalue.get_data()) {
                                               __( free(curvalue.get_data()) );
                                       }
                                }
                        //---------- create new forward link
                        //just insert in primary
                        __tIFnok( putInto(g_DBGroupToSubGroup, a_ParentTxn, &value,&key) );//order of insertion(appended)
                                break;
                             }
                default:
                        //---------- bad usage of this function
                                __t("more than kGroup or kSubGroup specified!");
        }//switch
//---------- done
#ifdef SHOWKEYVAL
                std::cout<<"\tNewCursorLink:done."<<endl;
#endif


//---------- end

        _OK;

}
/*************************/
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
 * creates a connection between a_GroupId TO/FROM a_SubGroupId
 * if any group doesn't aready exist it is created
 * if connection exists the function fails (doesnt' throw)
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
        /*"Note: Transactions may only span threads if they do so serially; that is, each transaction must be active in only a single thread of control at a time. This restriction holds for parents of nested transactions as well; not two children may be concurrently active in more than one thread of control at any one time." Berkeley DB docs*/
        /*"Note: A parent transaction may not issue any Berkeley DB operations -- except for DbEnv::txn_begin, DbTxn::abort and DbTxn::commit -- while it has active child transactions (child transactions that have not yet been committed or aborted)" Berkeley DB docs*/
//---------- validate params
        __tIF(a_NewTxn==NULL);
//---------- create a new transaction within this environment
        __(fDBEnviron->txn_begin(a_ParentTxn, a_NewTxn, a_Flags ););
//---------- set depth level to reflect depth of tree
        fStackLevel++;
//---------- done
//---------- show depth level on console(text)
#ifdef SHOWTXNS
                for (int i=1;i<fStackLevel;i++)
                        std::cout << " ";

#define THROW_HOOK \
        __(this->Abort(a_NewTxn));

        _h( std::cout << "through "<<*a_NewTxn<<"("<<fStackLevel<<")"<<std::endl;);
#undef THROW_HOOK
#endif
//---------- end
        _OK;
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
// Open a Berkeley DB database
function
TLink::OpenDB(
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
                  openFlags,  // Open flags
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
        _htIFnok( OpenDB(&g_DBGroupToSubGroup, &nameDBGroupToGroup) );

#undef THROW_HOOK
#define THROW_HOOK \
        DB1CLOSE_HOOK\
        ENVCLOSE_HOOK

//---------- open/create reverse links table/dbase
        _htIFnok( OpenDB(&g_DBSubGroupFromGroup, &nameDBGroupFromGroup) );

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
//---------- validate params
        __tIF(NULL==a_Txn);
//---------- commit only if valid
        if (NULL != *a_Txn) {
                __((*a_Txn)->commit(0));
//---------- almost done
#ifdef SHOWTXNS
                //---------- show info on screen
                for (int i=1;i<fStackLevel;i++) {
                        std::cout << " ";
                }
                __( std::cout << "commitd "<<*a_Txn<<"("<<fStackLevel<<")"
                                <<std::endl; );
#endif
//---------- clean up
                fStackLevel--;
                *a_Txn=NULL;
        } else {
                _FA(*a_Txn is NULL);
        }
//---------- done
        _OK;
}

/*******************************/
function
TLink::Abort(DbTxn **a_Txn)
{
//---------- validating params
        __tIF(NULL==a_Txn);
//---------- abort only if valid transaction
        if (NULL != *a_Txn) {
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
//---------- done
        _OK;
}



/*******************************/
//constructor
TDMLPointer :: TDMLPointer(TLink *m_WorkingOnThisTLink):
        fInited(false)
{
//---------- validating params
        fLink=m_WorkingOnThisTLink;
        __tIF(NULL == fLink);
//---------- setting defaults
        fParentTxn=NULL;
}
/*******************************/
//destructor
TDMLPointer :: ~TDMLPointer()
{
//---------- consistency checks; signalling errors at the last moment
        __tIF(IsInited());
        __tIF(NULL == fLink);//cannot be
        __tIF(NULL != fParentTxn);
        __tIF(fInited);
}
/*******************************/
bool
TDMLPointer :: IsInited()
{
        return (fInited);
}
/*******************************/
function
TDMLPointer :: Init(
                const NodeId_t a_GroupId,
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
        __tIF(a_GroupId.empty());

//---------- saving values for later use
        fGroupStr=a_GroupId;//yeah let's hope this makes a copy; it does
        //__( fGroupKey.set_data((void *)fGroupStr.c_str()) );//points to that, so don't kill fGroupStr!!
        //__( fGroupKey.set_size((u_int32_t)fGroupStr.length() + 1) );
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
#ifdef SHOWKEYVAL
                std::cout<<"\tTDMLCursor::Init:PointerName="<<
                fGroupStr<<endl;
#endif
        //FIXME:
        //checking if group exists, if not we make it point to itself which mean the pointer is NULL no! read below! seach RECTIF
        //all above only if certain flags are present

//---------- new transaction
        DbTxn *thisTxn;
        __tIFnok(fLink->NewTransaction(a_ParentTxn,&thisTxn));
#define PTR_ABORT_HOOK \
        __(fLink->Abort(&thisTxn)); \
        thisTxn=NULL;
#define THROW_HOOK \
        PTR_ABORT_HOOK

        //#1 check if kGroup exists aka if there's a forward link with that pointer(key) [pointer=node]
//---------- new cursor; temporary
        TDMLCursor *meCurs;
        _h( meCurs=new TDMLCursor(fLink) );//done after DBs are inited!!!
#undef THROW_HOOK
#define THROW_HOOK \
        __( delete meCurs ); \
        PTR_ABORT_HOOK
//---------- initialize cursor
        _htIFnok( meCurs->InitFor(kGroup,a_GroupId, kNone, thisTxn) );
#undef THROW_HOOK
#define THROW_HOOK \
        __( meCurs->DeInit() ); \
        __( delete meCurs ); \
        PTR_ABORT_HOOK

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
        _htIFnok( meCurs->DeInit() );

//---------- close cursor
#undef THROW_HOOK
#define THROW_HOOK \
        PTR_ABORT_HOOK
        _h( delete meCurs );


//---------- commit transaction
#undef THROW_HOOK
        __tIFnok( fLink->Commit(&thisTxn) );

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
TDMLCursor :: InitFor(
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
        _htIF(NULL != fThisTxn);//cannot call InitFor() twice, not before DeInit(); however if called twice we need to close the cursor prior to aborting the current transaction!
//---------- check if called before; before DeInit()
        _htIF(this->IsInited());
#undef THROW_HOOK
//---------- keep validating params
        __tIF(a_NodeId.empty());
        __tIF(kNone != a_Flags);//no flags supported yet

//---------- new global(for this instance of TDMLCursor) transaction
        __tIFnok(fLink->NewTransaction(a_ParentTxn,&fThisTxn));

#define THROW_HOOK \
        CURSOR_CLOSE_HOOK \
        CURSOR_ABORT_HOOK
#define ERR_HOOK \
        THROW_HOOK

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
        _htIF(NULL == fCursor);//feeling paranoid?
#undef ERR_HOOK
#undef THROW_HOOK
//---------- setting such that the next time we use Get it's gonna be the first time so we use DB_SET there
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
//---------- trying to catch some inconsistencies; and setting defaults for next use of InitFor()
#define THROW_HOOK \
        CURSOR_ABORT_HOOK
        _htIF(NULL == fCursor);//called DeInit() before InitFor() ? or smth happened inbetween
        //---------- close cursor
        _htIF(0 != fCursor->close());
        fCursor=NULL;
        //---------- commit the transaction (started with InitFor()
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
        _htIFnok( fLink->IsLinkConsistent(fNodeType, fCurKeyStr, m_Node, fThisTxn) );

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
#define THROW_HOOK \
        CURSOR_CLOSE_HOOK \
        CURSOR_ABORT_HOOK
        _htIF( 0 != fCursor->count( &m_Into, 0) );
//---------- done
        _OK;
#undef THROW_HOOK
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
