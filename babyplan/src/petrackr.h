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
* Description: provides personalized error tracking
*
****************************************************************************/


#ifndef __PETRACKR_H
#define __PETRACKR_H

#include "gdefs.h" //for _yes_ and _no_ things
#include "errtrack.h" //is this necessary? we shall see

class uderrtrk:public errtrk {
private:
        int lastwaserr;
    void setlastwas(){ lastwaserr=_yes_; };
    void unsetlastwas(){ lastwaserr=_no_; };
public:
    uderrtrk();
    ~uderrtrk();
    void usrshowthemall();
    virtual reterrt pusherr(const s_item *from);//see .cpp file for dox
    int asks_if_last_funx_had_an_error(){if (lastwaserr==_yes_) return _yes_; return _no_; };
    void clearlastfunxerr(){ unsetlastwas(); };
};

extern uderrtrk *etracker;

#define ret_ifalways(_i) gret_ifalways(etracker,"DEADEND:",_i,"")
#define ret_if(_i) gret_if(etracker,"TRUE:",_i,"")
#define ret_ifnot(_i) gret_ifnot(etracker,"FALSE:",_i,"")
#define ab_if(_a) gab_if(etracker,"TRUE:",_a,"")
#define ab_ifnot(_a) gab_ifnot(etracker,"FALSE:",_a,"")
#define ret_ok() gret_ok(etracker)

#define ret_if_error_after_statement(_state)\
    etracker->clearlastfunxerr();\
    _state;\
    gret_if(etracker,"",etracker->asks_if_last_funx_had_an_error()," last funx is: `"#_state##"'");

#define ab_if_error_after_statement(_state)\
    etracker->clearlastfunxerr();\
    _state;\
    gab_if(etracker,"",etracker->asks_if_last_funx_had_an_error()," last funx is: `"#_state##"'");

void deinit_error_tracker();
void init_error_tracker();
void purge_all_errors();


#endif

