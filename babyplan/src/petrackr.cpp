/****************************************************************************
*
*                             dmental links
*    Copyright (c) 28 Feb 2005 AtKaaZ, AtKaaZ at users.sourceforge.net
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

void purge_all_errors(){
    if (etracker) etracker->purgemall();
}

