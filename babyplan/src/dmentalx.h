/****************************************************************************
*
*                             dmental links
*	Copyright (c) 28 Feb 2005 AtKaaZ, AtKaaZ at users.sourceforge.net
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


#define unlinkall(...) _gfuncall(,unlink,__VA_ARGS__)

#define _clsfuncme(_what,_func) if_##_what##::##_func()

#define _gfuncall(_comp,_what_,_a,_b,_c,_d,_e,_f,_g,_h,_i,_j,_k)\
	 	_comp(_what_(_a));\
		_comp(_what_(_b));\
		_comp(_what_(_c));\
		_comp(_what_(_d));\
		_comp(_what_(_e));\
		_comp(_what_(_f));\
		_comp(_what_(_g));\
		_comp(_what_(_h));\
		_comp(_what_(_i));\
		_comp(_what_(_j));\
		_comp(_what_(_k));/*11*/

#define _gptrall2func(_comp,_func,_1,_2,_3,_4,_5,_6,_7,_8,_9,_10,_11)\
	_comp(_clsfuncme(_1,_func));\
	_comp(_clsfuncme(_2,_func));\
	_comp(_clsfuncme(_3,_func));\
	_comp(_clsfuncme(_4,_func));\
	_comp(_clsfuncme(_5,_func));\
	_comp(_clsfuncme(_6,_func));\
	_comp(_clsfuncme(_7,_func));\
	_comp(_clsfuncme(_8,_func));\
	_comp(_clsfuncme(_9,_func));\
	_comp(_clsfuncme(_10,_func));\
	_comp(_clsfuncme(_11,_func));
		
#define _gdeclall(_prefix,_append,_1,_2,_3,_4,_5,_6,_7,_8,_9,_10,_11) \
	_prefix _1##_append, _prefix _2##_append, _prefix _3##_append,\
	_prefix _4##_append, _prefix _5##_append, _prefix _6##_append,\
	_prefix _7##_append, _prefix _8##_append, _prefix _9##_append,\
	_prefix _10##_append, _prefix _11##_append

#define _declall(_prefix,_append) \
	_gdeclall(_prefix,_append, \
		group,atom,eatom,eatoms_list,eatomslist_item,\
		gcatom,gcatoms_list,gcatomslist_item,\
		acatom,acatoms_list,acatomslist_item \
	)
	
#define _funcall(_comp,_func) \
	_gfuncall(_comp,_func,\
		group,atom,eatom,eatoms_list,eatomslist_item,\
		gcatom,gcatoms_list,gcatomslist_item,\
		acatom,acatoms_list,acatomslist_item \
	)
#define _ptrall2func(_comp,_func) \
	_gptrall2func(_comp,_func,\
		group,atom,eatom,eatoms_list,eatomslist_item,\
		gcatom,gcatoms_list,gcatomslist_item,\
		acatom,acatoms_list,acatomslist_item \
	)
	
#define _fnames \
	"group.dat"\
	,"atom.dat"\
	,"eatom.dat"\
	,"eal.dat"\
	,"ealitems.dat"\
	,"gcatom.dat"\
	,"gcal.dat"\
	,"gcalitms.dat"\
	,"acatom.dat"\
	,"acal.dat"\
	,"acalitms.dat"
	
/*#define _all_in_order() group,atom,eatom,eatoms_list,eatomslist_item,\
		gcatom,gcatoms_list,gcatomslist_item,\
		acatom,acatoms_list,acatomslist_item*/
       
#define _init(_a_) if_##_a_##::init(_a_##f)






typedef const char *ccp;

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
public:
	dmentalix::dmentalix();
	dmentalix::~dmentalix();
	reterrt init(_declall(ccp,f));//open all files
	reterrt newelemental(basic_element thenewbe);//a new eatom?!
	reterrt shutdown();//close all files
};




#endif

