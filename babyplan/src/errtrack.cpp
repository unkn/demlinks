/****************************************************************************
*
*                             dmental links
*    Copyright (c) 28 Feb 2005 AtKaaZ, AtKaaZ at users.sourceforge.net
*    Portions Copyright (c) 1983-2002 Sybase, Inc. All Rights Reserved.
*
*  ========================================================================
*
*    This file contains Original Code and/or Modifications of Original
*    Code as defined in and that are subject to the Sybase Open Watcom
*    Public License version 1.0 (the 'License'). You may not use this file
*    except in compliance with the License. BY USING THIS FILE YOU AGREE TO
*    ALL TERMS AND CONDITIONS OF THE LICENSE. A copy of the License is
*    provided with the Original Code and Modifications, and is also
*    available at www.sybase.com/developer/opensource.
*
*    The Original Code and all software distributed under the License are
*    distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
*    EXPRESS OR IMPLIED, AND SYBASE AND ALL CONTRIBUTORS HEREBY DISCLAIM
*    ALL SUCH WARRANTIES, INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF
*    MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR
*    NON-INFRINGEMENT. Please see the License for the specific language
*    governing rights and limitations under the License.
*
*  ========================================================================
*
* Description: provides FIFO error tracking capabilities at source level
*
****************************************************************************/



#include <stdlib.h>
//#include <stdio.h>

#include "errtrack.h"

#define _yes_ +1;//don't modif, needs to be compat w/ `if's
#define _no_ 0;//same thing here, read above /^

ccp str_t_all[t_last_t]={
"NONE",
"WARN",
"ERR",
"INFO",
"CaRET",
"ABORT",
};


errtrk::errtrk(){
    head=NULL;
    tail=NULL;
    howmany=0;
    internalfails=_no_;//if _yes_ some alloc failed internally, so no more errors added
}
errtrk::~errtrk(){
    if (head) destroy();
}
/*
void errtrk::usrshowthemall(){
    s_item *tmp=getlasterr();
    while (tmp){
        fprintf(stderr,
            "%stypeTRKd:: `%s'\nIn func `%s' from file `%s' at line `%d'\n",
            str_t_all[tmp->error.errtype]
            ,tmp->error.userdesc
            ,tmp->error.funx
            ,tmp->error.errfile
            ,tmp->error.errline
        );
        clrlasterr();
        tmp=getlasterr();
    }//while
}*/

void errtrk::destroy(){//killem from head to tail
    s_item *tmp;
    while (head) {
        tmp=head;
        head=head->below;
        delete tmp;//dealloc
    }
    head=NULL;tail=NULL;//unnecessary
    howmany=0;internalfails=_no_;
}

void errtrk::clrlasterr(){
    if (head) { less(); head=head->below; if (!head) tail=NULL;}
}


s_item * errtrk::getlasterr(){//w/o poping it
    if (!head) return NULL;
    return head;
}

s_item * errtrk::rpoperr(){//lost if not taken
    //less();
    s_item *tmp;
    popfromlist(tmp);
    return tmp;
}

reterrt errtrk::poperr(s_item *&into){//into may be NULL if nada to pop-out
    //less();
    popfromlist(into);
    return funcok();//useless, unless somewhere noted
}


void errtrk::popfromlist(s_item *&whats){
    whats=head;
    if (head) {//go next
        head=head->below;//non NULL head only!
        if (!head) tail=NULL;
        less();
    }
}

void errtrk::push2list(s_item *whats){//adds to TOP, ie. last item
    more();
    whats->below=NULL;//no more items below this one we're adding, *duh*
    if (tail) tail->below=whats; //if tail != NULL
    else head=whats;//head too points to the ONLY item
    
    tail=whats;//if tail was NULL! OR wtw was tail...
}

reterrt errtrk::pusherr(const s_item *from){
    more();
    s_item *tmp=new s_item;
    if (!tmp) {
        internalfails=_yes_;
            goto ret;
    }
    *tmp=*from;//copy data
    tmp->error.level=howmany;
    less();//to accomodate the prev more();
    push2list(tmp);//give tmp for usage;  uses more()
ret:
    return funcerr();//funcret_whenERR;
}



reterrt errtrk::pushuerr(const errtype_t et, const errcode_t ec, ccp desc,ccp fil, ccp func, const errline_t line){
    more();
    s_item *tmp=new s_item;
    if (!tmp) {
        internalfails=_yes_;
            goto ret;
    }
    tmp->error.level=howmany;//from 1..
    tmp->error.errtype=et;
    tmp->error.errcode=ec;
    tmp->error.errfile=fil;
    tmp->error.funx=func;
    tmp->error.errline=line;
    tmp->error.userdesc=desc;//ptr exchange, no alloc!
    less();
////    push2list(tmp);
    pusherr(tmp);//so user must only inherit/override pusherr() for adding features
ret://last in line
    return funcerr();//funcret_whenERR;//signal: das wars an error
}


