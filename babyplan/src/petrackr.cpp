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
* Description: provides personalized FIFO error tracking capab@source level
*
****************************************************************************/

#include <stdlib.h>
#include <stdio.h>
#include "petrackr.h"

uderrtrk *etracker;

uderrtrk::uderrtrk(){
}
uderrtrk::~uderrtrk(){
}
    
virtual reterrt uderrtrk::pusherr(const s_item *from){
//added functionality
    setlastwas();//last funx, just had an error, we're using this just in case
                //last funx(), returns funcerr()==_no_==0 both for its use
                //and for in case there's an error, so the user checks with 
                // asks_if_last_was_an_error() and if returns _yes_ then the
                //last function really had an error, of course one should
                //use clearlastfunxerr() before calling funx()

    return ( errtrk::pusherr(from) );
}


void uderrtrk::usrshowthemall(){
    s_item *tmp=getlasterr();
    while (tmp){
        fprintf(stderr,
            "%stype:levl#%d: `%s',\nin func `%s' from file `%s' at line `%d'\n"
            ,str_t_all[tmp->error.errtype]
            ,tmp->error.level
            ,tmp->error.userdesc
            ,tmp->error.funx
            ,tmp->error.errfile
            ,tmp->error.errline
        );
        clrlasterr();
        tmp=getlasterr();
    }//while
}


void init_error_tracker(){
    etracker=new uderrtrk;
    if (!etracker) {
        fprintf(stderr,"error allocating etracker pointer in file %s at line %d in func %s\n",__FILE__,__LINE__,__func__);
        abort();
    }
}

void deinit_error_tracker(){
    if (etracker) {
        if (etracker->getlasterr()!=NULL){//show all errors before kill
            etracker->usrshowthemall();
        }
        delete etracker;
        etracker=NULL;
    }
}


