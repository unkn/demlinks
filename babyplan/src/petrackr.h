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
#define ret_if_error_after_statement(_state)\
    etracker->clearlastfunxerr();\
    _state;\
    ret_if(etracker->asks_if_last_funx_had_an_error());

#define ret_if(_i) gret_if(etracker,"TRUE:",_i,"")
#define ret_ifnot(_i) gret_ifnot(etracker,"FALSE:",_i,"")
#define ab_if(_a) gab_if(etracker,"TRUE:",_a,"")
#define ab_ifnot(_a) gab_ifnot(etracker,"FALSE:",_a,"")
#define ret_ok() gret_ok(etracker)

void deinit_error_tracker();
void init_error_tracker();



#endif

