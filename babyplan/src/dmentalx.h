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
* Description:  
*
****************************************************************************/


#ifndef __DMENTALX_H
#define __DMENTALX_H

#include "group.h"
#include "atom.h"
#include "gcatom.h"
#include "gca_list.h"
#include "gcalitem.h"
#include "acatom.h"
#include "aca_list.h"
#include "acalitem.h"
#include "eatom.h"
#include "ea_list.h"
#include "eal_item.h"

#define unlinkall(...) _unlinkall(__VA_ARGS__)
#define _unlinkall(_1,_2,_3,_4,_5,_6,_7,_8,_9,_10,_11) \
    unlink(_1);unlink(_2);unlink(_3);unlink(_4);unlink(_5);\
    unlink(_6);unlink(_7);unlink(_8);unlink(_9);unlink(_10);\
    unlink(_11);
   
/*************preserve the order of operands in all these 3 macros***********/
#define _general_declall(_prefix,_append,_1,_2,_3,_4,_5,_6,_7,_8,_9,_10,_11) \
    _prefix _1##_append, _prefix _2##_append, _prefix _3##_append,\
    _prefix _4##_append, _prefix _5##_append, _prefix _6##_append,\
    _prefix _7##_append, _prefix _8##_append, _prefix _9##_append,\
    _prefix _10##_append, _prefix _11##_append

#define _declall(_prefix,_append) \
    _general_declall(_prefix,_append, \
        group,atom,eatom,eatoms_list,eatomslist_item,\
        gcatom,gcatoms_list,gcatomslist_item,\
        acatom,acatoms_list,acatomslist_item)

#define _fnames \
    "group.dat","atom.dat","eatom.dat","eal.dat","ealitems.dat"\
    ,"gcatom.dat","gcal.dat","gcalitms.dat"\
    ,"acatom.dat","acal.dat","acalitms.dat"
/****************************************************************************/
    
       


class dmentalix :
        private if_atom
        ,private if_group
        ,private if_gcatom, private if_gcatoms_list
        ,private if_gcatomslist_item
        ,private if_acatom, private if_acatoms_list
        ,private if_acatomslist_item
        ,private if_eatom
        ,private if_eatoms_list, private if_eatomslist_item
{
private:
    int inited;
public:
    dmentalix::dmentalix();
    dmentalix::~dmentalix();
    reterrt init(_declall(const char *,fname));//open all files
    reterrt shutdown();//close all files
    atomID try_add_atom_type_E(const basic_element BE);//checks existing
    atomID strict_add_atom_type_E(const basic_element BE);//no check, imperativeADD!
    atomID find_atomID_type_E(const basic_element BE);//only ID is returned
    
private:
    eatomID try_newelemental(const atomID whosmy_atomID, const basic_element thenewbe);//a new eatom?!with check
    eatomID strict_addelemental(const atomID whosmy_atomID, const basic_element thenewbe);//no check, appendnew!
//    eatomID get_eatomID_of_elemental(const basic_element seekBE);

//other set
    void setinited(){ inited=_yes_; };//axexor funx
    void setdeinited(){ inited=_no_; };
    int wasinited(){ if (inited==_yes_) return _yes_; return _no_; };
};




#endif

