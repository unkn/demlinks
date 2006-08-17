/****************************************************************************
*
*                             dmental links
*       Copyright (c) 28 Feb 2005 AtKaaZ, AtKaaZ at users.sourceforge.net
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


#define unlinkall(...) _gfuncall(unlink,__VA_ARGS__)

#define _clsfuncme(_what,_func) if_##_what##::##_func();

#define _gfuncall(_what_,_a,_b,_c,_d,_e,_f,_g,_h,_i,_j,_k)\
	 	_what_(_a);\
		_what_(_b);\
		_what_(_c);\
		_what_(_d);\
		_what_(_e);\
		_what_(_f);\
		_what_(_g);\
		_what_(_h);\
		_what_(_i);\
		_what_(_j);\
		_what_(_k);/*11*/
#define _gptrall2func(_func,_1,_2,_3,_4,_5,_6,_7,_8,_9,_10,_11)\
	_clsfuncme(_1,_func)\
	_clsfuncme(_2,_func)\
	_clsfuncme(_3,_func)\
	_clsfuncme(_4,_func)\
	_clsfuncme(_5,_func)\
	_clsfuncme(_6,_func)\
	_clsfuncme(_7,_func)\
	_clsfuncme(_8,_func)\
	_clsfuncme(_9,_func)\
	_clsfuncme(_10,_func)\
	_clsfuncme(_11,_func)
		
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
	
#define _funcall(_func) \
	_gfuncall(_func,\
		group,atom,eatom,eatoms_list,eatomslist_item,\
		gcatom,gcatoms_list,gcatomslist_item,\
		acatom,acatoms_list,acatomslist_item \
	)
#define _ptrall2func(_func) \
	_gptrall2func(_func,\
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
	void init(_declall(ccp,f));
	void deinit();
};




#endif

