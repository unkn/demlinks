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


#ifndef __COMMON_H
#define __COMMON_H
#pragma pack(1) //allign structs at one byte, prior was four

/* NOTE NOTE NOTE : valid IDs are never zero or less */
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
    gcatoms_listID ptr2clonelist;//ptr to a list of atoms that only refer to GCatoms
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
struct deref_groupID_type { //GROUPS.DAT
    atomID ptr2atom_head_of_chain;//ptr to that ATOM that has .prev=NULL
    gcatoms_listID ptr2list_of_gcatoms;
};


#define _in2(_w_)\
    into->##_w_##=##_w_##;

#define _2in2(_1,_2)\
    _in2(_1);_in2(_2);

#define _3in2(_1,_2,_3)\
    _2in2(_1,_2);_in2(_3);

#define _5in2(_1,_2,_3,_4,_5)\
    _3in2(_1,_2,_3);_2in2(_4,_5);







#endif
