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
* Description: provides FIFO error tracking capabilities at source level
*
****************************************************************************/


#ifndef __ERRTRACK_H
#define __ERRTRACK_H


//the (l)user must define smth like the following 5 #def lines:
//where `errtrk *etracker;'
//#define ret_if(_i) gret_if(etracker,"TRUE:",_i,"")
//#define ret_ifnot(_i) gret_ifnot(etracker,"FALSE:",_i,"")
//#define ab_if(_a) gab_if(etracker,"TRUE:",_a,"")
//#define ab_ifnot(_a) gab_ifnot(etracker,"FALSE:",_a,"")
//#define ret_ok() gret_ok(etracker)

#define gret_ok(etracker) return etracker->funcok();
#define gab_if(etracker,prefix,_a,sufix) { if ((_a)) {_ab_me(etracker,prefix,_a,sufix)} }
#define gab_ifnot(etracker,prefix,_a,sufix) {if (!(_a)) {_ab_me(etracker,prefix,_a,sufix)} }

#define _ab_me(zt,prefix,_a,sufix) {\
    zt->pushuerr(t_caused_abort,3,prefix#_a##sufix,__FILE__,__func__,__LINE__);\
    zt->usrshowthemall();\
    fprintf(stderr,"Read the above errors in reverse order of appearence!\n");\
    abort();\
    }

#define _do_me(zt,prefix,_i,sufix) \
        return zt->pushuerr(t_caused_a_return,\
                2,\
                prefix#_i##sufix,\
                __FILE__,\
                __func__,\
                __LINE__\
            )
#define gret_ifalways(zt,prefix,_i,sufix) {\
        _do_me(zt,prefix,_i,sufix);\
    }
#define gret_if(zt,prefix,_i,sufix) {\
    if ((_i)) {\
        _do_me(zt,prefix,_i,sufix);\
    }}
#define gret_ifnot(zt,prefix,_i,sufix) {\
        if (!(_i)) {\
        _do_me(zt,prefix,_i,sufix);\
    }}


#define funcret_whenERR 0 //don't change this, it must be used with `if`s
#define funcret_whenOK +1 //same, read above /^

typedef int reterrt;
typedef const char *ccp;
typedef int errcode_t;
typedef int errtype_t;
typedef unsigned int errline_t;

enum t_all {
t_none=0
,t_warn //=1
,t_err
,t_info
,t_caused_a_return  //from function
,t_caused_abort

//last:
,t_last_t
};



extern ccp str_t_all[t_last_t];


struct s_errdata {
    errtype_t errtype;//ie. WARN, ERR, DEBUG, INFO
    errcode_t errcode;//_errc_something
    int level;
    ccp    errfile;//__FILE__
    ccp    funx;//__func__
    errline_t errline;//__LINE__
    ccp userdesc;//description eventually __LINE__ and stuff
};

struct s_item {//FIFO, oops accidentaly done a LIFO first time :-"
    s_item *below;//NULL is none
    s_errdata error;
};

class errtrk {//attempting a FIFO error tracking
private:
    s_item *head;//from head out
    s_item *tail;//damn FIFOs ;)  at tail in
    int internalfails;
protected:
    int howmany;//how many s_items, just for info
public:
    errtrk();
    virtual ~errtrk();
    reterrt pushuerr(const errtype_t et, const errcode_t ec, ccp desc, ccp fil, ccp func, const errline_t line);
    virtual reterrt pusherr(const s_item *from);//the func's making a copy of `from`.
    reterrt poperr(s_item *&into);//into may be NULL if nada to pop-out
    s_item * getlasterr();//w/o poping it
    reterrt funcok(){ return funcret_whenOK; };
    reterrt funcerr(){ return funcret_whenERR; };
    s_item * rpoperr();//pops like poperr() but returns it with funx
    void clrlasterr();
    void purgemall();//clear all errors
//    void usrshowthemall();
private:
    void push2list(s_item *whats);//modified whats->data
    void popfromlist(s_item *&whats);
    void destroy();
    void less(){ howmany--; };
    void more(){ howmany++; };
};

#endif
