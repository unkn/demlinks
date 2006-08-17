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
