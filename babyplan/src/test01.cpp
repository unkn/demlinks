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
* Description: just for testing the capabilities of currently developed stuff
*
****************************************************************************/


#include <process.h>
#include <stdio.h>
#include <conio.h>

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

//lame stuff:
#define part1(_what_) \
if_##_what_## *f##_what_##;\
deref_##_what_##ID_type tmp_##_what_##_type;

#define part3(_w_)\
ab_if(NULL == (f##_w_##=new if_##_w_##));\
f##_w_##->init(#_w_"s.dat");\
part2(##_w_##);

#define part4(_w_)\
_w_##ID tmp##_w_##ID=f##_w_##->new##_w_##(&tmp_##_w_##_type);\
printf("new"#_w_"ID==%ld\n",tmp##_w_##ID);\
part2(##_w_##);

#define part5(_w_)\
f##_w_##->shutdown();\
delete f##_w_##;f##_w_##=NULL;

#define part2(_what_) \
printf("f"#_what_"->howmany"#_what_"s()==%ld\n",f##_what_##->howmany##_what_##s());


part1(group);
part1(atom);
part1(gcatom);
part1(gcatoms_list);
part1(gcatomslist_item);
part1(acatom);
part1(acatoms_list);
part1(acatomslist_item);
part1(eatom);
part1(eatoms_list);
part1(eatomslist_item);


int main(){//clrscr();

	part3(group);
	part3(atom);
	part3(gcatom);
	part3(gcatoms_list);
	part3(gcatomslist_item);
	part3(acatom);
	part3(acatoms_list);
	part3(acatomslist_item);
	part3(eatom);
	part3(eatoms_list);
	part3(eatomslist_item);

	fgroup->composegroup(&tmp_group_type,0,0);
	part4(group);

	fatom->composeatom(&tmp_atom_type,_AC,0);
	part4(atom);

	fgcatom->composegcatom(&tmp_gcatom_type,tmpgroupID,tmpatomID,tmpatomID,0,tmpgroupID);
	part4(gcatom);

	fgcatomslist_item->composegcatomslist_item(&tmp_gcatomslist_item_type,0,0,tmpatomID);
	part4(gcatomslist_item);

	fgcatoms_list->composegcatoms_list(&tmp_gcatoms_list_type,tmpgcatomslist_itemID);
	part4(gcatoms_list);

	facatom->composeacatom(&tmp_acatom_type,tmpgroupID,tmpatomID,tmpatomID,0,tmpgroupID);
	part4(acatom);


	facatomslist_item->composeacatomslist_item(&tmp_acatomslist_item_type,1,1,tmpatomID);
	part4(acatomslist_item);


	facatoms_list->composeacatoms_list(&tmp_acatoms_list_type,tmpacatomslist_itemID);
	part4(acatoms_list);


	featom->composeeatom(&tmp_eatom_type,0,'');
	part4(eatom);


	featomslist_item->composeeatomslist_item(&tmp_eatomslist_item_type,1,1,tmpatomID);
	part4(eatomslist_item);


	featoms_list->composeeatoms_list(&tmp_eatoms_list_type,tmpeatomslist_itemID);
	part4(eatoms_list);


	part5(group);
	part5(atom);
	part5(gcatom);
	part5(gcatoms_list);
	part5(gcatomslist_item);
	part5(acatom);
	part5(acatoms_list);
	part5(acatomslist_item);
	part5(eatom);
	part5(eatoms_list);
	part5(eatomslist_item);

	printf("\nDone...press key\n");
	getch();
	return 0;
}
