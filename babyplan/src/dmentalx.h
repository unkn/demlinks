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
#include "grpalist.h"
#include "grplitem.h"
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

/* PRIVATE DEFINES */
//#define WASINITED_SAFETY //always check if was inited before operating
#undef WASINITED_SAFETY
/* end of PRIVATE DEFINES */

#define cmrw(_fn) chmod(_fn,S_IREAD|S_IWRITE)
#define erasef(_fn) {cmrw(_fn);unlink(_fn);}

#define unlinkall(...) _unlinkall(__VA_ARGS__)
#define _unlinkall(_1,_2,_3,_4,_5,_6,_7,_8,_9,_10,_11,_12,_13) {\
    erasef(_1);erasef(_2);erasef(_3);erasef(_4);erasef(_5);\
    erasef(_6);erasef(_7);erasef(_8);erasef(_9);erasef(_10);\
    erasef(_11);erasef(_12);erasef(_13);}
   
/*************preserve the order of operands in all these 3 macros***********/
#define _general_declall(_prefix,_append,_1,_2,_3,_4,_5,_6,_7,_8,_9,_10,_11,_12,_13) \
    _prefix _1##_append, _prefix _2##_append, _prefix _3##_append,\
    _prefix _4##_append, _prefix _5##_append, _prefix _6##_append,\
    _prefix _7##_append, _prefix _8##_append, _prefix _9##_append,\
    _prefix _10##_append, _prefix _11##_append, _prefix _12##_append, \
    _prefix _13##_append

#define _declall(_prefix,_append) \
    _general_declall(_prefix,_append, \
        group,grpatoms_list,grpatomslist_item,atom,eatom,eatoms_list,\
        eatomslist_item,\
        gcatom,gcatoms_list,gcatomslist_item,\
        acatom,acatoms_list,acatomslist_item)

#define _fnames \
    "group.dat","grpalist.dat","grplitem.dat","atom.dat","eatom.dat"\
    ,"eal.dat","ealitems.dat"\
    ,"gcatom.dat","gcal.dat","gcalitms.dat"\
    ,"acatom.dat","acal.dat","acalitms.dat"
/****************************************************************************/
    
       


class dmentalix :
        private if_atom
        ,private if_group
        ,private if_grpatoms_list, private if_grpatomslist_item
        ,private if_gcatom, private if_gcatoms_list
        ,private if_gcatomslist_item
        ,private if_acatom, private if_acatoms_list
        ,private if_acatomslist_item
        ,private if_eatom
        ,private if_eatoms_list, private if_eatomslist_item
{
#ifdef WASINITED_SAFETY
protected:
    int inited;
#endif
public:
    dmentalix::dmentalix();
    dmentalix::~dmentalix();
    reterrt init(_declall(const char *,fname), const long num_cached_records);//open all files
    reterrt shutdown();//close all files

    atomID try_add_atom_type_E(const basic_element BE);//checks existing
    atomID strict_add_atom_type_E(const basic_element BE);//no check, imperativeADD!
    atomID find_atomID_type_E(const basic_element BE);//only ID is returned

    //returns the next atomID which is next in chain to fromwhere(=atomID) or _noID_ if end of chain(no more to go)
    atomID get_next_atomID_in_chain(const atomID fromwhere);//we're talking about chains(atomIDs) not lists(items)
    atomID get_prev_atomID_in_chain(const atomID fromwhere);//we're talking about chains(atomIDs) not lists(items)
    
    
    atomID strict_add_atom_type_AC_after_prev(const atomID ptr2what_atomID, const groupID father_groupID, const atomID whosprev_atomID);//add a new CA after but connected with `whosprev...'
    atomID strict_add_atom_type_GC_after_prev(const groupID ptr2what_groupID, const groupID father_groupID, const atomID whosprev_atomID);//add a new CA after but connected with `whosprev...'

    //create a new group with head `head' and return group's head in `head'
    groupID add_group_with_headatom(atomID *head);//with some checks, and `head' gets destroyed/modified if `head' wasn't the atomID head of the chain
    groupID add_empty_group();

    reterrt _who_s_groupID_are_you_atomID(groupID &gid, const atomID me);

//lame funx:
    reterrt get_eatomslist_item_withID(const eatomslist_itemID whateatomslist_itemID, deref_eatomslist_itemID_type &into);
    
    reterrt get_atomID_s_type_prev_next(const atomID whos_atomID, atomtypes &type, atomID &prev, atomID &next);//it also returns error if type=_E_atom since eatoms cannot be parts of chain

    reterrt strict_modif_ptr2group(const atomID whos, const groupID witwat);

    reterrt strict_modif_next(const atomID whos_atomID, const atomID newnext, atomID *oldnext);//if non NULL oldnext=.next before changin;; whos_atomID.next=newnext;

    reterrt get_atomID_s_headIDof_eatomslistofclones(const atomID which, eatomslist_itemID &head);
private:
//acatoms refering to any of E/GC/AC
    reterrt strict_add_one_more_atomID_to_this_gcatom_s_clone_list(const gcatoms_listID whatlist, const atomID what2add);
    reterrt strict_add_one_more_atomID_to_this_acatom_s_clone_list(const acatoms_listID whatlist, const atomID what2add);
    reterrt strict_add_one_more_atomID_to_this_eatom_s_clone_list(const eatoms_listID whatlist, const atomID what2add);
//.
//gcatom refering to a group
    reterrt strict_add_one_more_atomID_to_this_grp_clone_list(const grpatoms_listID whatlist, const atomID what2add);

    reterrt add_gcatomID_to_clone_list_of_group(const gcatomID what2add_gcatomID, const groupID whos_groupID);//only gcatoms poit to groups
    reterrt add_atomID_to_clone_list_of_atom(const atomID what2add_atomID, const atomID whos_atomID);

    eatomID try_newelemental(const atomID whosmy_atomID, const basic_element thenewbe);//a new eatom?!with check
    eatomID strict_addelemental(const atomID whosmy_atomID, const basic_element thenewbe);//no check, appendnew!

//other set
#ifdef WASINITED_SAFETY
private:
    int wasinited() const { if (inited==_yes_) return _yes_; return _no_; }
    void setinited(){ inited=_yes_; };
    void setdeinited(){ inited=_no_; };
#endif
};




#endif

