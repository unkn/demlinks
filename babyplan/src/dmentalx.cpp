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


#include <stdlib.h> //NULL macro
#include "petrackr.h"
#include "dmentalx.h"

dmentalix::dmentalix(){
    setdeinited();
}

dmentalix::~dmentalix(){
}


/*..............*/
#define INIT(_x_) \
    ret_ifnot( if_##_x_##::init(##_x_##fname) )
    
reterrt dmentalix::init(_declall(const char *,fname))
{
    INIT(atom);
    INIT(group);
    INIT(eatom);
    INIT(acatom);
    INIT(gcatom);
    INIT(eatoms_list);
    INIT(eatomslist_item);
    INIT(acatoms_list);
    INIT(acatomslist_item);
    INIT(gcatoms_list);
    INIT(gcatomslist_item);

    setinited();
    ret_ok();
}
#undef INIT
/*^^^^^^^^^^^^^^*/


/*..............*/
#define DONE(_x_) \
    ret_ifnot( if_##_x_##::shutdown() )
    
reterrt dmentalix::shutdown(){
    DONE(atom);
    DONE(group);
    DONE(eatom);
    DONE(acatom);
    DONE(gcatom);
    DONE(eatoms_list);
    DONE(eatomslist_item);
    DONE(acatoms_list);
    DONE(acatomslist_item);
    DONE(gcatoms_list);
    DONE(gcatomslist_item);

    setdeinited();
    ret_ok();
}
#undef DONE
/*^^^^^^^^^^^^^^*/

/*..............*/
atomID dmentalix::strict_add_atom_type_E(const basic_element BE){//no check, imperativeADD!
/* adds the eatom, even if it exists! thus adds a new atom too*/
    ret_ifnot(wasinited());//files must be open ~ check

//we create a new atom with type E, with a NULL eatomID because we don't have
//it yet. We need to create the atom first, because we will pass its atomID
//to the funx that created the eatom, then we eatomID; we modify the prev atom
//to the new eatomID.

    deref_atomID_type _atom;
    if_atom::compose(&_atom,_E_atom,NULL);
    atomID father=if_atom::addnew(&_atom);
    ret_if( father==NULL );//atomID or 0 if failure

    eatomID _eatomID=strict_addelemental(father,BE);//append/add a new elemental
    ret_if( _eatomID==NULL );
    //now we gotta modify the father.eatomID=eatomID :)
    _atom.at_ID=_eatomID;
    ret_ifnot( if_atom::writewithID(father,&_atom) );//exchange contents of atomID, with the new contents from &_atom

    return father; //returns atomID, NOT eatomID
}
/*^^^^^^^^^^^^^^*/


/*..............*/
atomID dmentalix::try_add_atom_type_E(const basic_element BE){
/* adds the atom which is an eatom type and hass all the stuff in it like
an empty eatoms_list which points to nothing in eatomslist_item
*/
    ret_ifnot(wasinited());//files must be open ~ check
//try and see if BE already exists
    
    deref_eatomID_type dood;
    eatomID _exist=if_eatom::find_eatom(&dood,BE);
    if (_exist){ //if already exists then we must return the atomID
        atomID &atomIDof_eatom=dood.ptrback2atomID_for_faster_search_when_single;
        ret_ifnot(atomIDof_eatom);//invalid atomID? just checking!
        return atomIDof_eatom;//ok, do it ~ return it, it's ok as in, > 0
    }//fi
//if we're here, the BE doesn't exist, thus we create it
    return strict_add_atom_type_E(BE);
}
/*^^^^^^^^^^^^^^*/

/*..............*/
eatomID dmentalix::strict_addelemental(const atomID whosmy_atomID, const basic_element thenewbe){//no check, appendnew!
/* this funx adds a new eatomID with the spec `thenewbe' basic_element, w/o
    checking if it already exists;
while this funx is used for speed(dramatical increase), it also can be very
 dangerous since it breaks one of the rules of dmental links which is :
 there cannot be any two eatoms that have the same basic_element;
 B_E is uniq!!
*/
    ret_ifnot(wasinited());//files must be open ~ check
    ret_if(whosmy_atomID==NULL);

    //we gotta add a new element here:
    //so we add a new item, a new list, and then a new eatom; except that
    //there are no items yet, items are those clones that refer to US
    //US=the new not yet created eatom
    //so we create an empty eatoms_listID, that has no `head' item
    

    //allocating a new list, unconnected to any atoms
    deref_eatoms_listID_type _eatoms_list;
    eatoms_listID _eatoms_listID;

    //NULL means no items, eatomslist_itemID==NULL thus no head item
    if_eatoms_list::compose(&_eatoms_list,0L);
    _eatoms_listID=if_eatoms_list::addnew(&_eatoms_list);
    ret_ifnot(_eatoms_listID);//failed prev addition, creating a new list
    
    //composing a new eatom, connecting the above list to it; and `thenewbe'.
    deref_eatomID_type _eatom;
    if_eatom::compose(&_eatom,whosmy_atomID,_eatoms_listID,thenewbe);
    
    return if_eatom::addnew(&_eatom);//allocate and return ID or error=0
}
/*^^^^^^^^^^^^^^*/

/*..............
eatomID dmentalix::get_eatomID_of_elemental(const basic_element seekBE){
    ret_ifnot(wasinited());//files must be open ~ check

    return if_eatom::find_basic_element_and_ret_eatomID(seekBE);
}
^^^^^^^^^^^^^^*/

/*..............*/
eatomID dmentalix::try_newelemental(const atomID whosmy_atomID, const basic_element thenewbe){//a new eatom?!
/*
  when adding a new eatom by basic_element, we must check if this basic_elem
doesn't already exist.
  either way we must return eatomID, or 0 if error ( 0==_no_ and eatomID type
is compatible with reterrt type, thus we can properly use errortracker)
*/
    ret_ifnot(wasinited());//files must be open ~ check
    ret_if(whosmy_atomID==NULL);

    eatomID got_eatomID;

    ret_if_error_after_statement(got_eatomID=if_eatom::find_basic_element_and_ret_eatomID(thenewbe));

    if (got_eatomID) {
        return got_eatomID; //return the found eatomID which has `thenewbe'
    }//fi

    return strict_addelemental(whosmy_atomID, thenewbe);//performs an unchecked append
}
/*^^^^^^^^^^^^^^*/

