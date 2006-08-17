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
* Description:
*
****************************************************************************/


#ifndef __COMMON_H
#define __COMMON_H
#pragma pack(1) //allign structs at one byte, prior was four

/* NOTE NOTE NOTE : valid IDs are never zero or less */
#define _noID_ 0L //identifies no valid ID; this must be 0L don't change!!!

//GROUP
//a group actually represents a chain of atoms; group=&chain
typedef long groupID;//a ptr to a GROUP; a referer to a GROUP; a placeholder

//generic atom
typedef long atomID;//specs an atom, generally; ie. not a GROUP
typedef long anyatomID;//used for AC/GC/E types

//element ATOM
typedef long eatomslist_itemID;
struct deref_eatomslist_itemID_type{
    eatomslist_itemID prevINlist;
    eatomslist_itemID nextINlist;
    atomID ptr2atom_that_points_to_US;//US=element atom
};

typedef long eatoms_listID;
struct deref_eatoms_listID_type {
    eatomslist_itemID ptr2head;
};

typedef unsigned char basic_element;//#x=chr(x)  thus #0..#255, but left 3 more bytes.
typedef anyatomID eatomID;
struct deref_eatomID_type {//ATOME.DAT    those type of atoms that are elements
    atomID ptrback2atomID_for_faster_search_when_single;
    eatoms_listID ptr2list;
    basic_element basicelementdata;//#0..#255
};



//ATOM CLONE atom

typedef long acatomslist_itemID;//an _item_ ID ~ from an acatoms LIST
struct deref_acatomslist_itemID_type {//AC_LISTs.DAT
    acatomslist_itemID prevINlist;
    acatomslist_itemID nextINlist;
    atomID ptr2atom_that_points_to_US;//US=the <ATOM CLONE> atom
};



typedef long acatoms_listID;//the ID of the list of acatom items
struct deref_acatoms_listID_type {
    acatomslist_itemID ptr2head;//first item in the list
};


typedef anyatomID acatomID;
struct deref_acatomID_type {//AC_ATOMs.DAT  <clone to ATOM> type of atom
    groupID ptr2group;//upwards ptr to father group
    atomID prevINchain;//could be anyatom
    atomID nextINchain;
    acatoms_listID ptr2clonelist;//ptr to a list of atoms that refer to US=atomID
    atomID Irefer2thisATOM;//this Atom_Clone refers to this atomID
};



//GROUP CLONE atom
typedef long gcatomslist_itemID;//an _item_ ID ~ from a gcatoms LIST
struct deref_gcatomslist_itemID_type {//GC_LISTs.DAT
    gcatomslist_itemID prevINlist;
    gcatomslist_itemID nextINlist;
    atomID ptr2atom_that_points_to_US;//US=the <GROUP CLONE> atom
};

typedef long gcatoms_listID;//that which refers to a gcatoms_list
struct deref_gcatoms_listID_type {
    gcatomslist_itemID ptr2head;//ptr to first item in list
};


typedef anyatomID gcatomID;
struct deref_gcatomID_type {//GC_ATOMs.DAT <clone to GROUP> type of ATOM
    groupID ptr2group;//from which group does this GCatom belong
    atomID prevINchain;
    atomID nextINchain;
    gcatoms_listID ptr2clonelist_of_atomIDs_which_point_to_US;
    //hmm... a list of atomIDs which point to US=gcatom
    //ptr to a list of atoms that only refer to GCatoms
    groupID Irefer2thisGROUP;//the group to which WE refer, since we're clone
};




#ifndef __WATCOMC__
#if (sizeof(acatomID)!=sizeof(gcatomID))||(sizeof(gcatomID)!=sizeof(eatomID))\
    ||(sizeof(acatomID)!=sizeof(anyatomID))
#error wtf r u doing dude? a MUST: anyatomID==acatomID==gcatomID==eatomID
#endif
#endif


typedef unsigned char atomtypes;//AC, GC or element [C=clone]
const atomtypes _AC_atom='A';
const atomtypes _GC_atom='G';
const atomtypes _E_atom='E';
struct deref_atomID_type {//generally any ATOM.        ATOMS.DAT
    atomtypes at_type;//AC/GC/E  a specific type of atom
    anyatomID at_ID;//AC/GC/E  ID ~ depending on type; not atomID!!
};


//GROUP

typedef long grpatomslist_itemID;//an _item_ ID ~ from a grpcatoms LIST
struct deref_grpatomslist_itemID_type {
    grpatomslist_itemID prevINlist;
    grpatomslist_itemID nextINlist;
    atomID atomID_that_points_to_US_the_group;//US=theGROUP
};

typedef long grpatoms_listID;//that which refers to a grpatoms_list
struct deref_grpatoms_listID_type {
    grpatomslist_itemID ptr2head_item;//ptr to first item in list
};

struct deref_groupID_type { //GROUPS.DAT
    atomID ptr2atom_head_of_chain;//ptr to that ATOM that has .prev=NULL
    grpatoms_listID ptr2list_of_atomIDs;
// list of atomIDs that point to US(=group) particulary these atomIDs
//are type GC only
};
/***************************************/

#define _in2(_w_)\
    into.##_w_##=##_w_##;

#define _2in2(_1,_2)\
    _in2(_1);_in2(_2);

#define _3in2(_1,_2,_3)\
    _2in2(_1,_2);_in2(_3);

#define _5in2(_1,_2,_3,_4,_5)\
    _3in2(_1,_2,_3);_2in2(_4,_5);







#endif
