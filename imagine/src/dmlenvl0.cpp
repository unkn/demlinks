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

#include "dmlenvl0.h"
#include "pnotetrk.h"

#include "dmlenv.hpp"


#undef ABORT_HOOK
#define ABORT_HOOK \
        __( l0_abort(&thisTxn, m_StackVar) );

/****************************/
/*******************************/
function
l0_commit(DbTxn **a_Txn, int *m_StackVar)
{//one cannot&shouldnot call Abort after calling this function: is in the Berkeley DB docs that cannot call abort after a failed(or successful) DbTxn->commit()
//---------- validate params
        __tIF(NULL==a_Txn);
//---------- commit only if valid
        if (NULL != *a_Txn) {
                __((*a_Txn)->commit(0));
//---------- almost done
                if (m_StackVar) {
#ifdef SHOWTXNS
                        //---------- show info on screen
                        for (int i=1;i<*m_StackVar;i++) {
                                std::cout << " ";
                        }
                        __( std::cout << "commitd "<<*a_Txn<<"("<<*m_StackVar<<")"
                                        <<std::endl; );
#endif
//---------- clean up
                        (*m_StackVar)--;
                }//fi
                *a_Txn=NULL;
        } else {
                _FA(*a_Txn is NULL);
        }
//---------- done
        _OK;
}

/*******************************/
function
l0_abort(DbTxn **a_Txn, int *m_StackVar)
{
//---------- validating params
        __tIF(NULL==a_Txn);
//---------- abort only if valid transaction
        if (NULL != *a_Txn) {
                if (m_StackVar) {
#ifdef SHOWTXNS
                        for (int i=1;i<*m_StackVar;i++)
                                std::cout << " ";
                        std::cout << "abortin "<<*a_Txn<<"("<<*m_StackVar<<")"<<std::endl;
#endif
                }//fi

                __((void)(*a_Txn)->abort()); //---------------------

                if (m_StackVar) {
#ifdef SHOWTXNS
                for (int i=1;i < *m_StackVar;i++)
                        std::cout << " ";
                std::cout << "aborted "<<*a_Txn<<"("<<*m_StackVar<<")"<<std::endl;
#endif
                        (*m_StackVar)--;
                }//fi
                        *a_Txn=NULL;
        } else {
                _FA(*a_Txn is NULL);
        }
//---------- done
        _OK;
}
/****************************/
function
l0_newTransaction(
                DbEnv *m_DBEnviron,
                        DbTxn * a_ParentTxn, //can be NULL
                        DbTxn ** a_NewTxn,
                        int *m_StackVar,
                        const u_int32_t a_Flags)
{
        /*"Note: Transactions may only span threads if they do so serially; that is, each transaction must be active in only a single thread of control at a time. This restriction holds for parents of nested transactions as well; not two children may be concurrently active in more than one thread of control at any one time." Berkeley DB docs*/
        /*"Note: A parent transaction may not issue any Berkeley DB operations -- except for DbEnv::txn_begin, DbTxn::abort and
DbTxn::commit -- while it has active child transactions (child transactions that have not yet been committed or aborted)" Berkeley DB docs*/
//---------- validate params
        __tIF(m_DBEnviron==NULL);
        __tIF(a_NewTxn==NULL);
//---------- create a new transaction within this environment
        __(m_DBEnviron->txn_begin(a_ParentTxn, a_NewTxn, a_Flags ););
//---------- set depth level to reflect depth of tree
        if (m_StackVar) {
                (*m_StackVar)++;
        }
//---------- done
//---------- show depth level on console(text)
#ifdef SHOWTXNS
        if (m_StackVar) {
                for (int i=1;i<*m_StackVar;i++)
                        std::cout << " ";

#define THROW_HOOK \
        __(l0_abort(a_NewTxn, m_StackVar));

                _h( std::cout << "through "<<*a_NewTxn<<"("<<*m_StackVar<<")"<<std::endl;);
        }
#undef THROW_HOOK
#endif
//---------- end
        _OK;
}
/****************************/

/****************************/
function
l0_findAndChange( //affecting only the value of the a_Key, aka the key cannot be changed anyways(u must do del then put for that)
                DbEnv *m_DBEnviron,
                Db *a_DBWhich,
                DbTxn *a_ParentTxn,
                Dbt *a_Key,
                Dbt *a_Value,
                Dbt *a_NewValue,
                int *m_StackVar
                )//we fail(not throw) if newval already exists
{
//------------ validate params
        __tIF(NULL == m_DBEnviron);
        __tIF(NULL == a_Key);
        __tIF(NULL == a_Value);
        __tIF(NULL == a_NewValue);
        __tIF(NULL == a_DBWhich);

//------------ create a new transaction
        DbTxn *thisTxn;
        __tIFnok( l0_newTransaction(m_DBEnviron,a_ParentTxn,&thisTxn, m_StackVar));

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
        __tIFnok( l0_commit(&thisTxn, m_StackVar) );

//------------ end
        _OK;
}
/*************************/
//performes unsynced delete from one of the databases (unsynced means the other db is left inconsistent ie. A->B in primary, and B->A in secondary, l0_delFrom deletes one but leaves untouched the other so you're left with either A->B or B->A thus inconsistent)
function
l0_delFrom(
                DbEnv *m_DBEnviron,
                Db *a_DBInto,
                DbTxn *a_ParentTxn,//can be NULL
                Dbt *a_Key,
                Dbt *a_Value,
                int *m_StackVar
                )
{

//------------ validating params
        __tIF(NULL == m_DBEnviron);
        __tIF(NULL == a_Key);
        __tIF(NULL == a_Value);
        __tIF(NULL == a_DBInto);


//------------ new transaction
        DbTxn *thisTxn;
        __tIFnok(l0_newTransaction(m_DBEnviron,a_ParentTxn,&thisTxn, m_StackVar));

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
        __tIFnok( l0_commit(&thisTxn, m_StackVar) );

//------------ end
        _OK;
}


/*************************/
function
l0_putInto(
                DbEnv *m_DBEnviron,
                Db *a_DBInto,
                DbTxn *a_ParentTxn,//can be null
                Dbt *a_Key,
                Dbt *a_Value,
                int *m_StackVar,
                const u_int32_t a_CursorPutFlags,
                Dbc *const m_Cursor//can be NULL, then put() is used thus appended to the list of data of that key(ie. order of insertion)
                )
{
//------------ validate params
        __tIF(NULL == m_DBEnviron);
        __tIF(NULL == a_DBInto);
        __tIF(NULL == a_Key);
        __tIF(NULL == a_Value);

//------------ ready to start
#ifdef SHOWKEYVAL
        __( cout << "l0_putInto: begin:"<<(NULL!=m_Cursor?"Cursor:":":")<< (char *)a_Key->get_data() << " = " << (char *)a_Value->get_data() <<endl; );
#endif

//------------ open a new cursor
#ifdef SHOWKEYVAL
        __( cout << "l0_putInto: newcursor;"<<endl );
#endif
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
#ifdef SHOWKEYVAL
        __( cout << "l0_putInto: get:"<<(NULL!=m_Cursor?"Cursor:":":")<< (char *)a_Key->get_data() << " = " << (char *)a_Value->get_data() <<endl; );
#endif

        _hif (0 == cursor1->get(a_Key, a_Value, DB_GET_BOTH)) {
                _hreterr(kFuncAlreadyExists);
        }_fih

//------------ close cursor
#ifdef SHOWKEYVAL
        __( cout << "l0_putInto: closecursor;"<<endl );
#endif

#undef THROW_HOOK
#undef ERR_HOOK
        __tIF(0 != cursor1->close());

//------------ key/data pair doesn't already exist so it's safe to put it
        //if not found, then put it
        if (NULL == m_Cursor) { //only using a new child transaction if there's no cursor specified
        //------------ new transaction
                DbTxn *thisTxn;
                __tIFnok(l0_newTransaction(m_DBEnviron,a_ParentTxn,&thisTxn, m_StackVar));
#define THROW_HOOK \
        ABORT_HOOK
        //------------ create the key/data pair
                        _htIF(0 != a_DBInto->put(thisTxn, a_Key, a_Value, 0) );
                        /*The default behavior of the Db::put
                         * function is to enter the new key/data pair, replacing any previously existing key if duplicates are
                         * disallowed, or adding a duplicate data item if duplicates are allowed. (from berkeley db docs)*/
        //------------ commit transaction
#undef THROW_HOOK
                __tIFnok( l0_commit(&thisTxn, m_StackVar) );
        } else {
        //------------ use the passed cursor to create the key/data pair as specified in the params of this func.
#ifdef SHOWKEYVAL
        __( cout << "l0_putInto: put:"<<(NULL!=m_Cursor?"Cursor:":":")<< (char *)a_Key->get_data() << " = " << (char *)a_Value->get_data() <<endl; );
#endif
                __tIF( m_Cursor->put(a_Key, a_Value, a_CursorPutFlags) );//even if DB_CURRENT and a_Value exist in this same place, it'll return kFuncAlreadyExists above! there's no use to overwrite with the same value!
        }

//------------ after done
#ifdef SHOWKEYVAL
        __( cout << "l0_putInto: done:"<<(NULL!=m_Cursor?"Cursor:":":")<< (char *)a_Key->get_data() << " = " << (char *)a_Value->get_data() <<endl; );
#endif

//------------ end
        _OK;
}
/****************************/


/****************************/
//ie.a->b =>a->c ~ pri:A-B, sec:B-A => pri: A-C(changed from A-B), sec:C-A(new)(B-A deleted)
//the connection MUST already exist! or fails(not throws)
/*
function
ModLink(
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
        __tIFnok(l0_newTransaction(m_DBEnviron,a_ParentTxn,&thisTxn, m_StackVar));

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
        _htIFnok( l0_delFrom(g_DBSubGroupFromGroup,thisTxn,&value,&key) );//del B<-A from SECondary
        //must not return kFuncAlreadyExists otherwise a bug is present somewhere
        _htIFnok( l0_putInto(g_DBSubGroupFromGroup,thisTxn,&newValue,&key) );//create C<-A in secondary

//--------- consistency check
        _htIFnok( IsLinkConsistent(kGroup, a_GroupId, a_NewLinkName, thisTxn) );

//--------- commit transaction
#undef THROW_HOOK
#undef ERR_HOOK
        __tIFnok( l0_commit(&thisTxn, m_StackVar) );

//--------- after done
#ifdef SHOWKEYVAL
        cout <<"\t"<< "Mod:done."<<endl;
#endif


//--------- end
        _OK;

}
*/
/*************************/
function
l0_newCursorLink(//creates a consistent link between two nodes in the sense selected by a_NodeType below
                DbEnv *m_DBEnviron,
                Db * m_G2sGDb,
                Db * m_sG2GDb,
                Dbc * const m_Cursor,
                const u_int32_t a_CursorPutFlags,
                const ENodeType_t a_NodeType,
                const NodeId_t a_NodeId1,//may or may not exist
                const NodeId_t a_NodeId2,//same
                int *m_StackVar,
                DbTxn *a_ParentTxn //no child transaction created except with l0_putInto(); apparently the cursor may not span transactions RTFM;
                )
{

//---------- validating params
        __tIF(NULL == m_G2sGDb);
        __tIF(NULL == m_sG2GDb);
        __tIF(NULL == m_DBEnviron);
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
                if (a_CursorPutFlags - DB_CURRENT == 0) {//must read current value for later to l0_delFrom()
                                __tIF(0 != m_Cursor->get(&key, &curvalue, DB_CURRENT | DB_RMW) );//flagged for later deletion
                }

                //---------- overwrite old value; insert/overwrite forward link
                        //insert or overwrite in primary
                        __fIFnok( l0_putInto(m_DBEnviron, m_G2sGDb, a_ParentTxn, &key,&value, m_StackVar, a_CursorPutFlags, m_Cursor) );

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
                        __tIFnok( l0_delFrom(m_DBEnviron, m_sG2GDb, a_ParentTxn, &curvalue,&key, m_StackVar) );//order of insertion(appended)
                        if (curvalue.get_data()) {
                                        __( free(curvalue.get_data()) );
                        }
                }//if
                //---------- insert reverse link in secondary
                        //just insert in secondary
                        //if we're here, means we successfuly entered the record in primary, so the record must be successfully entered in secondary, ie. cannot return kFuncAlreadyExists; so it must be clean, otherwise we throw (up;)
                        __tIFnok( l0_putInto(m_DBEnviron, m_sG2GDb, a_ParentTxn, &value,&key, m_StackVar) );//appended at end of list
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
                                if (a_CursorPutFlags - DB_CURRENT == 0) {//must read current value for later to l0_delFrom()
                                        __tIF(0 != m_Cursor->get(&key, &curvalue, DB_CURRENT | DB_RMW) );//flagged for later deletion
                                }
                                //---------- ins/overwrite the reverse link with the new pointee
                                //insert or overwrite in secondary
                                //if we're here, means we successfuly entered the record in primary, so the record must be successfully entered in secondary, ie. cannot return kFuncAlreadyExists; so it must be clean, otherwise we throw (up;)
                                __fIFnok( l0_putInto(m_DBEnviron, m_sG2GDb, a_ParentTxn, &key,&value, m_StackVar, a_CursorPutFlags,m_Cursor) );
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
                                       __tIFnok( l0_delFrom(m_DBEnviron, m_G2sGDb, a_ParentTxn, &curvalue,&key, m_StackVar) );//order of insertion(appended)
                                       if (curvalue.get_data()) {
                                               __( free(curvalue.get_data()) );
                                       }
                                }
                        //---------- create new forward link
                        //just insert in primary
                        __tIFnok( l0_putInto(m_DBEnviron, m_G2sGDb, a_ParentTxn, &value,&key, m_StackVar) );//order of insertion(appended)
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
/*******************************/
/*******************************/
