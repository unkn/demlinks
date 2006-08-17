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
* Description: interface
*
****************************************************************************/


#include "petrackr.h"
#include "dmentalx.h"

/* PRIVATE DEFINES */
//#define PRIVATE_PARANOIA_CHECKS //some Invariants() alike checks
/* end of PRIVATE DEFINES */

dmentalix::dmentalix(){
#ifdef WASINITED_SAFETY
    setdeinited();
#endif
}

dmentalix::~dmentalix(){
}


/*______________*/
#define INIT(_x_) \
    ret_ifnot( if_##_x_##::init(##_x_##fname, num_cached_records) )

reterrt dmentalix::init(_declall(const char *,fname), const long num_cached_records)
{
    INIT(atom);
    INIT(group);
    INIT(grpatoms_list);
    INIT(grpatomslist_item);
    INIT(eatom);
    INIT(acatom);
    INIT(gcatom);
    INIT(eatoms_list);
    INIT(eatomslist_item);
    INIT(acatoms_list);
    INIT(acatomslist_item);
    INIT(gcatoms_list);
    INIT(gcatomslist_item);

#ifdef WASINITED_SAFETY
    setinited();
#endif
    ret_ok();
}
#undef INIT
/*^^^^^^^^^^^^^^*/


/*______________*/
#define DONE(_x_) \
    ret_ifnot( if_##_x_##::shutdown() )

reterrt dmentalix::shutdown(){
    DONE(atom);
    DONE(grpatoms_list);
    DONE(grpatomslist_item);
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

#ifdef WASINITED_SAFETY
    setdeinited();
#endif
    ret_ok();
}
#undef DONE
/*^^^^^^^^^^^^^^*/

reterrt dmentalix::_who_s_groupID_are_you_atomID(groupID &gid, const atomID me){
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());//files must be open ~ check
#endif
        ret_if( me == _noID_ );//not allowed inexistent atomID

        deref_atomID_type tat;
        ret_ifnot( if_atom::getwithID(me,tat) );//attempt to bring contents of atomID into &tat
        ret_if( tat.at_type == _E_atom );//eatoms cannot be in groups!

        switch (tat.at_type) {
            case _AC_atom:
                deref_acatomID_type aca;
                ret_ifnot( if_acatom::getwithID(tat.at_ID,aca) );//get it
                gid=aca.ptr2group;//we got it
                break;
            case _GC_atom:
                deref_gcatomID_type gca;
                ret_ifnot( if_gcatom::getwithID(tat.at_ID,gca) );//get gc
                gid=gca.ptr2group;
                break;
            default:
                ret_ifalways(unknown atom type detected);//fatal
        }//switch

    ret_ok();
}
/*^^^^^^^^^^^^^^*/

groupID dmentalix::add_empty_group(){
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());//files must be open ~ check
#endif
    //new group -> new list
    deref_grpatoms_listID_type grplistvar;
    grpatoms_listID grplist;
    if_grpatoms_list::compose(grplistvar,_noID_);
//    ret_if_error_after_statement(
        grplist=if_grpatoms_list::addnew(grplistvar);
  //  );//ptr2head=_noID_ -> empty list
    ret_if(_noID_==grplist);


    deref_groupID_type newone;
    if_group::compose(newone,_noID_,grplist);
    //ret_if_error_after_statement(
        groupID newID=if_group::addnew(newone);
    //);
    ret_if(newID==_noID_);
    return newID;
}

groupID dmentalix::add_group_with_headatom(atomID *head){
/*
    *adds a new group that has a chain of atoms in it starting with `head' atom
    *allowing head==_noID_
    *also adds a new empty grpatoms_list(list which holds atomIDs which refer
     only to this group, and list holds atomID which refer only to groups in
     general)
    *also updates each atom from chain to point to fathergroup=US
*/
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());//files must be open ~ check
#endif
//we must check `head' that must not be already allocated to another group
//such as head.ptr2group==_noID_
    groupID tmpgid;
    ret_ifnot( _who_s_groupID_are_you_atomID(tmpgid,*head) );//failure of operation?
    ret_if( _noID_ != tmpgid );/*head is already part of another group, thus
                            we'll refuse to create this group with head*/

//maybe head is already a part of a chain and it's not head of that chain
    atomID atomiter=*head;//initially
//trying to see if there are prev atoms in chain, if they are we must find
// the real chain head and make sure we point the group.ptr2head to it
    atomID tid;
    do {
        ret_if_error_after_statement(
            tid=get_prev_atomID_in_chain(atomiter);
        );//ret
        //again, we assume that all atoms in chain have no father group,thus
        //we don't make checks ~ they'll slow down the operation(s)
        if (tid != _noID_) {
            atomiter=tid;//set the new head
        }
        else break;//exit do
        //I guess we could have put the groupID while we were scanning to find
        //the head but then again, the user could have given use the proper head in the 1st place.
    } while (1);



    //new group -> new list
    deref_grpatoms_listID_type grplistvar;
    grpatoms_listID grplist;
    if_grpatoms_list::compose(grplistvar,_noID_);
//    ret_if_error_after_statement(
        grplist=if_grpatoms_list::addnew(grplistvar);
  //  );//ptr2head=_noID_ -> empty list
    ret_if(_noID_==grplist);

    deref_groupID_type dg;
    groupID gID;
    if_group::compose(dg,atomiter,grplist);//atomiter may be==head and head may be _noID_
//    ret_if_error_after_statement(
    gID=if_group::addnew(dg); //);//new group
    ret_if(_noID_==gID);//senses the error from above call, there's no use to call ret_if_error_after_statement

    *head=atomiter;//set the new head for returning call, so the user will know the group's head atom

//now we must update each atom's fathergroup to point to this newly created group
    //atomiter is already set to head of the chain, (from above statmnts)
    while (atomiter != _noID_) { //more atoms in list?
        //we first have to proxex head

        ///here we assume that all other atoms following `head' in the chain
        //are the same as `head' from the point of view that they too don't
        //have a `ptr2group' allocated, that their ptr2group==_noID_
        //if they do, they'll be allocated to this group, and consistency of
        //the entire environment is composmised.
        strict_modif_ptr2group(atomiter,gID);//set atomiter.ptr2group=gID
        ret_if_error_after_statement(
            atomiter=get_next_atomID_in_chain(atomiter);
        );//atomiter might be _noID_ even if there's no error
    }//while

    return gID;//return the newly created group's ID
}
/*^^^^^^^^^^^^^^*/

reterrt dmentalix::strict_modif_ptr2group(const atomID whos, const groupID witwat){
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());//files must be open ~ check
#endif
        ret_if( whos == _noID_ );//not allowed inexistent atomID

        deref_atomID_type tat;
        ret_ifnot( if_atom::getwithID(whos,tat) );//attempt to bring contents of atomID into &tat
        ret_if( tat.at_type == _E_atom );//eatoms cannot be part of chains

        switch (tat.at_type) {//we've to proxex each atomtype separately
            case _AC_atom:
                deref_acatomID_type aca;
                ret_ifnot( if_acatom::getwithID(tat.at_ID,aca) );//get it
                aca.ptr2group=witwat;//modify it
                ret_ifnot( if_acatom::writewithID(tat.at_ID,aca) );//write it back
                break;
            case _GC_atom:
                deref_gcatomID_type gca;
                ret_ifnot( if_gcatom::getwithID(tat.at_ID,gca) );//get gc
                gca.ptr2group=witwat;//we don't care if groupID==_noID_
                ret_ifnot( if_gcatom::writewithID(tat.at_ID,gca) );
                break;
            default:
                ret_ifalways(unknown atom type detected);//fatal
        }//switch

    ret_ok();
}
/*^^^^^^^^^^^^^^*/

atomID dmentalix::get_next_atomID_in_chain(const atomID fromwhere){
//we're talking about chains(atomIDs) not lists(items)
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());//files must be open ~ check
#endif
    ret_if(fromwhere==_noID_);//obv.

    deref_atomID_type tat;
    ret_ifnot( if_atom::getwithID(fromwhere,tat) );
    ret_if( tat.at_type == _E_atom );//eatoms cannot be part of chains

    switch (tat.at_type) {//we've to proxex each atomtype separately
        case _AC_atom:
            deref_acatomID_type aca;
            ret_ifnot( if_acatom::getwithID(tat.at_ID,aca) );//get it
            return aca.nextINchain;//this is atomID
        case _GC_atom:
            deref_gcatomID_type gca;
            ret_ifnot( if_gcatom::getwithID(tat.at_ID,gca) );//get gc
            return gca.nextINchain;
    }//switch

    ret_ifalways(unknown atom type detected);//fatal
}
/*^^^^^^^^^^^^^^*/

atomID dmentalix::get_prev_atomID_in_chain(const atomID fromwhere){
//we're talking about chains(atomIDs) not lists(items)
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());//files must be open ~ check
#endif
    ret_if(fromwhere==_noID_);//obv.

    deref_atomID_type tat;
    ret_ifnot( if_atom::getwithID(fromwhere,tat) );
    ret_if( tat.at_type == _E_atom );//eatoms cannot be part of chains

    switch (tat.at_type) {//we've to proxex each atomtype separately
        case _AC_atom:
            deref_acatomID_type aca;
            ret_ifnot( if_acatom::getwithID(tat.at_ID,aca) );//get it
            return aca.prevINchain;//this is atomID
        case _GC_atom:
            deref_gcatomID_type gca;
            ret_ifnot( if_gcatom::getwithID(tat.at_ID,gca) );//get gc
            return gca.prevINchain;
    }//switch

    ret_ifalways(unknown atom type detected);//fatal
}
/*^^^^^^^^^^^^^^*/

reterrt dmentalix::get_eatomslist_item_withID(const eatomslist_itemID whateatomslist_itemID, deref_eatomslist_itemID_type &into){
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());//files must be open ~ check
#endif

    return ( if_eatomslist_item::getwithID(whateatomslist_itemID,into) );
}

reterrt dmentalix::get_atomID_s_headIDof_eatomslistofclones(const atomID which, eatomslist_itemID &head){
/* get ptr2head item of eatomID
causes error if there's no head in items, usually where there are no clones!
*/
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());//files must be open ~ check
#endif

    deref_atomID_type _ecin;
    ret_ifnot ( if_atom::getwithID(which,_ecin) );

    atomtypes ty=_ecin.at_type;
    ret_if( ty != _E_atom ); //expecting only eatom, not gca or aca or other(invalid)

    deref_eatomID_type _ec;
    ret_ifnot( if_eatom::getwithID(_ecin.at_ID,_ec) );
    eatoms_listID _el=_ec.ptr2list;
    ret_if( _noID_ == _el );//serious issue

    deref_eatoms_listID_type t_el;
    ret_ifnot( if_eatoms_list::getwithID(_el,t_el) );
    //all ok
    head=t_el.ptr2head;//may be _noID_ but no error is returned

    ret_ok();
}


/*______________*/
reterrt dmentalix::get_atomID_s_type_prev_next(const atomID whos_atomID, atomtypes &type, atomID &prev, atomID &next){
//it also returns error if type=_E_atom since eatoms cannot be parts of chain
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());//files must be open ~ check
#endif

    deref_atomID_type _tmpa;
    ret_ifnot( if_atom::getwithID(whos_atomID,_tmpa) );
    ret_if( _tmpa.at_type == _E_atom ); //eatoms can't be chained, use acatoms to them, instead.
    type=_tmpa.at_type;//set return type, since it's not eatom(handled above^)

    switch (_tmpa.at_type) {
        case _GC_atom://get this GCatom in order to see it's prev/next
                deref_gcatomID_type _gca;
                ret_ifnot( if_gcatom::getwithID(_tmpa.at_ID,_gca) );
                prev=_gca.prevINchain;
                next=_gca.nextINchain;
                break;
        case _AC_atom:
                deref_acatomID_type _aca;
                ret_ifnot( if_acatom::getwithID(_tmpa.at_ID,_aca) );
                prev=_aca.prevINchain;
                next=_aca.nextINchain;
                break;
        default:
                ret_ifalways(not dodging atom of unknown type);
    }//switch

    ret_ok();//all went OK
}
/*^^^^^^^^^^^^^^*/


/*______________*/
atomID dmentalix::strict_add_atom_type_AC_after_prev(const atomID ptr2what_atomID, const groupID father_groupID, const atomID whosprev_atomID){//add a new CA
/*
    creates a new atom (type AtomClone) which points to `ptr2what' (initially)
    prev/group can be anything
    if prev is _noID_ then this is the first atom in list
    else US.next=prev.next;
        prev.next = US
        and US.prev=prev;
        => insert US after prev and before prev.next
*/
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());//files must be open ~ check
#endif


//the real thing:
    deref_atomID_type _atom;
    if_atom::compose(_atom,_AC_atom,_noID_);
    atomID father=if_atom::addnew(_atom);
    ret_if( father==_noID_ );//atomID or 0 if failure


    deref_acatoms_listID_type _list;
//compose the new empty list
    if_acatoms_list::compose(_list,_noID_);//ptr2head=_noID_=_noID_ no items in list
    acatoms_listID listID=if_acatoms_list::addnew(_list);
    ret_if(listID==_noID_);//return if error ^ somehow list wasn't allocated


    deref_acatomID_type _acatom;
    if_acatom::compose(_acatom,father_groupID,whosprev_atomID,_noID_,listID,ptr2what_atomID);//append/add a new elemental
    acatomID _acatomID=if_acatom::addnew(_acatom);//create the new acatom
    ret_if( _acatomID==_noID_ );//quit if failed to create it

    _atom.at_ID=_acatomID;//err...
    ret_ifnot( if_atom::writewithID(father,_atom) );//exchange contents of atomID, with the new contents from &_atom
//so by this line, we've successfuly added a new atomID which has a new
//acatomID and a new acatoms_listID with no acatomslist_itemID in that list
//and has a father_groupID (points upwards) and it(acatom) may be a part of
//a chain of other atoms, prev and next in a list.
//HOWEVER, we must add into `ptr2what_atomID' 's list the fact that this
//`father' acatom, we created, points to it, we do this on the following
//line:
//add father as an item to the list of thos which point to `ptr2what_atomID'
    ret_ifnot( add_atomID_to_clone_list_of_atom(father,ptr2what_atomID) );
//also DONE:: we have to go to prevatomID we should update that atom's next
//to point to US

    if (whosprev_atomID != _noID_){
        //so prev atom is not _noID_, we have to insert our selves after it

        //we change prev's next to point to us, and remember oldnext=prev.next
        atomID oldprevnext;
        ret_ifnot( strict_modif_next(whosprev_atomID,father,&oldprevnext) );

        //we change us.next to point to prev.next (even if prev.next is _noID_
        ret_ifnot( strict_modif_next(father,oldprevnext,_noID_) );
        //father.prev was changed before(above) to point to prev.
    }//fi prev

    return father; //returns atomID, NOT acatomID
}
/*^^^^^^^^^^^^^^*/

/*______________*/
atomID dmentalix::strict_add_atom_type_GC_after_prev(const groupID ptr2what_groupID, const groupID father_groupID, const atomID whosprev_atomID){//add a new CA
/*
    creates a new atom (type GroupClone) which points to group `ptr2what' (initially)
    prev/group can be anything
    if prev is _noID_ then this is the first atom in list
    else US.next=prev.next;
        prev.next = US
        and US.prev=prev;
        => insert US after prev and before prev.next
*/
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());//files must be open ~ check
#endif


//the real thing:
    deref_atomID_type _atom;
    if_atom::compose(_atom,_GC_atom,_noID_);
    atomID father=if_atom::addnew(_atom);//new GCATOM
    ret_if( father==_noID_ );//atomID or 0 if failure


    deref_gcatoms_listID_type _list;
//compose the new empty list, this list if a list of atomIDs which point to this GCatom
    if_gcatoms_list::compose(_list,_noID_);//ptr2head=_noID_=_noID_ no items in list
    gcatoms_listID listID=if_gcatoms_list::addnew(_list);
    ret_if(listID==_noID_);//return if error ^ somehow list wasn't allocated


    deref_gcatomID_type _gcatom;
    if_gcatom::compose(_gcatom,father_groupID,whosprev_atomID,_noID_,listID,ptr2what_groupID);//append/add a new elemental
    gcatomID _gcatomID=if_gcatom::addnew(_gcatom);//create the new gcatom
    ret_if( _gcatomID==_noID_ );//quit if failed to create it

    _atom.at_ID=_gcatomID;//err...
    ret_ifnot( if_atom::writewithID(father,_atom) );//exchange contents of atomID, with the new contents from &_atom
//make group know that it's being refered by smbdy
    ret_ifnot( add_gcatomID_to_clone_list_of_group(father,ptr2what_groupID) );
//also DONE:: we have to go to prevatomID we should update that atom's next
//to point to US

    if (whosprev_atomID != _noID_){
        //so prev atom is not _noID_, we have to insert our selves after it

        //we change prev's next to point to us, and remember oldnext=prev.next
        atomID oldprevnext;
        ret_ifnot( strict_modif_next(whosprev_atomID,father,&oldprevnext) );

        //we change us.next to point to prev.next (even if prev.next is _noID_
        ret_ifnot( strict_modif_next(father,oldprevnext,_noID_) );
        //father.prev was changed before(above) to point to prev.
    }//fi prev

    return father; //returns atomID, NOT acatomID
}
/*^^^^^^^^^^^^^^*/

/*______________*/
reterrt dmentalix::add_gcatomID_to_clone_list_of_group(const gcatomID what2add_gcatomID, const groupID whos_groupID){
/*
add gcatomID `what2add' into group `whos' 's list of atoms which point to whos
because only GCatoms can point to groups!
*/
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());//files must be open ~ check
#endif
    deref_groupID_type _tmpg;
    ret_ifnot( if_group::getwithID(whos_groupID,_tmpg) );//get the group

    ret_ifnot(
        strict_add_one_more_atomID_to_this_grp_clone_list(\
            _tmpg.ptr2list_of_atomIDs,\
            what2add_gcatomID\
        )
    );


    ret_ok();//allOK
}
/*^^^^^^^^^^^^^^*/

/*______________*/
reterrt dmentalix::strict_add_one_more_atomID_to_this_grp_clone_list(const grpatoms_listID whatlist, const atomID what2add){
//a gcatom refering to a group
/* adds to the list w/o checking if item already exist (hopefully) */
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());//files must be open ~ check
#endif
    deref_grpatoms_listID_type _list;
    ret_ifnot( if_grpatoms_list::getwithID(whatlist,_list) );


    deref_grpatomslist_itemID_type newitem;
    if_grpatomslist_item::compose(newitem,
        _noID_/*prev*/,
        _list.ptr2head_item/*next*/,//the old head
        what2add/*atomID_that_points_to_US_the_group*/
    );
    //connected new item to the chain, but put first, instead of last.

    _list.ptr2head_item=if_grpatomslist_item::addnew(newitem);//append
    ret_if(_list.ptr2head_item == _noID_);//catches errors too
    //wrote new item

    ret_ifnot( if_grpatoms_list::writewithID(whatlist,_list) );
    //wrote list

    ret_ok();
}
/*^^^^^^^^^^^^^^*/

/*______________*/
reterrt dmentalix::add_atomID_to_clone_list_of_atom(const atomID what2add_atomID, const atomID whos_atomID){
/*
*adds an atomID(can be only AC) to the list of <those which point to me> of
another atom (this list can only be of AC atoms!), this "another" atom can be
E/GC/AC, it(whos_atomID) is just being refered by an
AC atom(what2add_atomID).
*/
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());//files must be open ~ check
#endif
    deref_atomID_type _tmpa;
    ret_ifnot( if_atom::getwithID(whos_atomID,_tmpa) );//get it

    switch (_tmpa.at_type) {
        case _E_atom:
            deref_eatomID_type _ea;
            ret_ifnot( if_eatom::getwithID(_tmpa.at_ID,_ea) );
            ret_ifnot( strict_add_one_more_atomID_to_this_eatom_s_clone_list(_ea.ptr2list,what2add_atomID) );
            break;
        case _GC_atom:
                deref_gcatomID_type _gca;
                ret_ifnot( if_gcatom::getwithID(_tmpa.at_ID,_gca) );
                //got gcatom
                ret_ifnot( strict_add_one_more_atomID_to_this_gcatom_s_clone_list(_gca.ptr2clonelist_of_atomIDs_which_point_to_US,what2add_atomID) );
                break;
        case _AC_atom:
                deref_acatomID_type _aca;
                ret_ifnot( if_acatom::getwithID(_tmpa.at_ID,_aca) );
                //got ACatom
                ret_ifnot( strict_add_one_more_atomID_to_this_acatom_s_clone_list(_aca.ptr2clonelist,what2add_atomID) );
                break;
        default:
                ret_ifalways(not dodging atom of unknown type);
    }//switch


    ret_ok();//allOK
}
/*^^^^^^^^^^^^^^*/

/*______________*/
reterrt dmentalix::strict_add_one_more_atomID_to_this_gcatom_s_clone_list(const gcatoms_listID whatlist, const atomID what2add){
//an acatom refering to a gcatom
/* adds to the list w/o checking if item already exist (hopefully) */
//only adds to a GCATOMSLIST !!!
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());//files must be open ~ check
#endif
    deref_gcatoms_listID_type _list;
    ret_ifnot( if_gcatoms_list::getwithID(whatlist,_list) );


    deref_gcatomslist_itemID_type newitem;
    newitem.prevINlist=_noID_;
    newitem.nextINlist=_list.ptr2head;//the old head
    newitem.ptr2atom_that_points_to_US=what2add;
    //connected new item to the chain, but put first, instead of last.

    _list.ptr2head=if_gcatomslist_item::addnew(newitem);//append
    ret_if(_list.ptr2head == _noID_);//catches errors too
    //wrote new item

    ret_ifnot( if_gcatoms_list::writewithID(whatlist,_list) );
    //wrote list

    ret_ok();
}
/*^^^^^^^^^^^^^^*/

/*______________*/
reterrt dmentalix::strict_add_one_more_atomID_to_this_eatom_s_clone_list(const eatoms_listID whatlist, const atomID what2add){
//an acatom refering to an eatom
//adds acatom to an eatomlist
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());//files must be open ~ check
#endif
    deref_eatoms_listID_type _list;
    ret_ifnot( if_eatoms_list::getwithID(whatlist,_list) );


    deref_eatomslist_itemID_type newitem;
    newitem.prevINlist=_noID_;
    newitem.nextINlist=_list.ptr2head;//the old head
    newitem.ptr2atom_that_points_to_US=what2add;
    //connected new item to the chain, but put first, instead of last.

    _list.ptr2head=if_eatomslist_item::addnew(newitem);//append
    ret_if(_list.ptr2head == _noID_);//catches errors too
    //wrote new item

    ret_ifnot( if_eatoms_list::writewithID(whatlist,_list) );
    //wrote list


    ret_ok();
}
/*^^^^^^^^^^^^^^*/

/*______________*/
reterrt dmentalix::strict_add_one_more_atomID_to_this_acatom_s_clone_list(const acatoms_listID whatlist, const atomID what2add){
//an acatom refering to another acatom
//adds an acatom to an acatomlist
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());//files must be open ~ check
#endif
    deref_acatoms_listID_type _list;
    ret_ifnot( if_acatoms_list::getwithID(whatlist,_list) );


    deref_acatomslist_itemID_type newitem;
    newitem.prevINlist=_noID_;
    newitem.nextINlist=_list.ptr2head;//the old head
    newitem.ptr2atom_that_points_to_US=what2add;
    //connected new item to the chain, but put first, instead of last.

    _list.ptr2head=if_acatomslist_item::addnew(newitem);//append
    ret_if(_list.ptr2head == _noID_);//catches errors too
    //wrote new item

    ret_ifnot( if_acatoms_list::writewithID(whatlist,_list) );
    //wrote list

    ret_ok();
}
/*^^^^^^^^^^^^^^*/

/*______________*/
reterrt dmentalix::strict_modif_next(const atomID whos_atomID, const atomID newnext, atomID *oldnext){
//oldnext=;; whos_atomID.next=newnext;

#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());//files must be open ~ check
#endif

    deref_atomID_type _tmpa;
    ret_ifnot( if_atom::getwithID(whos_atomID,_tmpa) );//get it
    ret_if( _tmpa.at_type == _E_atom ); //eatoms can't be chained, use acatoms to them, instead.

    switch (_tmpa.at_type) {
        case _GC_atom://get this GCatom in order to see it's prev/next
                deref_gcatomID_type _gca;
                ret_ifnot( if_gcatom::getwithID(_tmpa.at_ID,_gca) );
                //got gcatom
                if (oldnext) *oldnext=_gca.nextINchain;//remember last next
                _gca.nextINchain=newnext;//modify it
                ret_ifnot( if_gcatom::writewithID(_tmpa.at_ID,_gca) );//write it back
                break;
        case _AC_atom:
                deref_acatomID_type _aca;
                ret_ifnot( if_acatom::getwithID(_tmpa.at_ID,_aca) );
                //got ACatom
                if (oldnext) *oldnext=_aca.nextINchain;//remember last next
                _aca.nextINchain=newnext;//modify it
                ret_ifnot( if_acatom::writewithID(_tmpa.at_ID,_aca) );//write it back
                break;
        default:
                ret_ifalways(not dodging atom of unknown type);
    }//switch


    ret_ok();
}
/*^^^^^^^^^^^^^^*/

/*______________*/
atomID dmentalix::strict_add_atom_type_E(const basic_element BE){//no check, imperativeADD!
/* adds the eatom, even if it exists! thus adds a new atom too*/
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());//files must be open ~ check
#endif

//we create a new atom with type E, with a _noID_ eatomID because we don't have
//it yet. We need to create the atom first, because we will pass its atomID
//to the funx that created the eatom, then we eatomID; we modify the prev atom
//to the new eatomID.

    deref_atomID_type _atom;
    if_atom::compose(_atom,_E_atom,_noID_);
    atomID father=if_atom::addnew(_atom);
    ret_if( father==_noID_ );//atomID or 0 if failure

    eatomID _eatomID=strict_addelemental(father,BE);//append/add a new elemental
    ret_if( _eatomID==_noID_ );
    //now we gotta modify the father.eatomID=eatomID :)
    _atom.at_ID=_eatomID;
    ret_ifnot( if_atom::writewithID(father,_atom) );//exchange contents of atomID, with the new contents from &_atom

    return father; //returns atomID, NOT eatomID
}
/*^^^^^^^^^^^^^^*/


/*______________*/
atomID dmentalix::try_add_atom_type_E(const basic_element BE){
/* adds the atom which is an eatom type and hass all the stuff in it like
an empty eatoms_list which points to nothing in eatomslist_item
*/
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());//files must be open ~ check
#endif
//try and see if BE already exists

    deref_eatomID_type dood;
    eatomID _exist=if_eatom::find_eatom(dood,BE);
    if (_exist){ //if already exists then we must return the atomID
#ifdef PRIVATE_PARANOIA_CHECKS
        atomID &atomIDof_eatom=dood.ptrback2atomID_for_faster_search_when_single;
        ret_ifnot(atomIDof_eatom>=0);//invalid atomID? just checking!
        //atomID must be valid, or entire **** was compromised, prior to calling this func
        return atomIDof_eatom;//ok, do it ~ return it, it's ok as in, > 0
#else
        return dood.ptrback2atomID_for_faster_search_when_single;
#endif
    }//fi
//if we're here, the BE doesn't exist, thus we create it
    return strict_add_atom_type_E(BE);
}
/*^^^^^^^^^^^^^^*/

/*______________*/
eatomID dmentalix::strict_addelemental(const atomID whosmy_atomID, const basic_element thenewbe){//no check, appendnew!
/* this funx adds a new eatomID with the spec `thenewbe' basic_element, w/o
    checking if it already exists;
while this funx is used for speed(dramatical increase), it also can be very
 dangerous since it breaks one of the rules of dmental links which is :
 there cannot be any two eatoms that have the same basic_element;
 B_E is uniq!!
*/
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());//files must be open ~ check
#endif

    ret_if(whosmy_atomID==_noID_);

    //we gotta add a new element here:
    //so we add a new item, a new list, and then a new eatom; except that
    //there are no items yet, items are those clones that refer to US
    //US=the new not yet created eatom
    //so we create an empty eatoms_listID, that has no `head' item


    //allocating a new list, unconnected to any atoms
    deref_eatoms_listID_type _eatoms_list;
    eatoms_listID _eatoms_listID;

    //_noID_ means no items, eatomslist_itemID==_noID_ thus no head item
    if_eatoms_list::compose(_eatoms_list,_noID_);
    _eatoms_listID=if_eatoms_list::addnew(_eatoms_list);
    ret_ifnot(_eatoms_listID);//failed prev addition, creating a new list

    //composing a new eatom, connecting the above list to it; and `thenewbe'.
    deref_eatomID_type _eatom;
    if_eatom::compose(_eatom,whosmy_atomID,_eatoms_listID,thenewbe);

    return if_eatom::addnew(_eatom);//allocate and return ID or error=0
}
/*^^^^^^^^^^^^^^*/


/*______________*/
eatomID dmentalix::try_newelemental(const atomID whosmy_atomID, const basic_element thenewbe){//a new eatom?!
/*
  when adding a new eatom by basic_element, we must check if this basic_elem
doesn't already exist.
  either way we must return eatomID, or 0 if error ( 0==_no_ and eatomID type
is compatible with reterrt type, thus we can properly use errortracker)
*/
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());//files must be open ~ check
#endif

    ret_if(whosmy_atomID==_noID_);

    eatomID got_eatomID;

    ret_if_error_after_statement(got_eatomID=if_eatom::find_basic_element_and_ret_eatomID(thenewbe));

    if (got_eatomID) {
        return got_eatomID; //return the found eatomID which has `thenewbe'
    }//fi

    return strict_addelemental(whosmy_atomID, thenewbe);//performs an unchecked append
}
/*^^^^^^^^^^^^^^*/

atomID dmentalix::find_atomID_type_E(const basic_element BE){//only ID is returned
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());//files must be open ~ check
#endif

    eatomID got_eatomID;
    deref_eatomID_type eatom;
    ret_if_error_after_statement(got_eatomID=if_eatom::find_eatom(eatom,BE));

    if (!got_eatomID) {
        return 0;
    }

    //so we got it...
#ifdef PRIVATE_PARANOIA_CHECKS
    //some paranoia checks:
    atomID &tmp_atomID=eatom.ptrback2atomID_for_faster_search_when_single;
    ret_ifnot(tmp_atomID>=0);
    return tmp_atomID;
#else
    return eatom.ptrback2atomID_for_faster_search_when_single;
#endif
}
/*^^^^^^^^^^^^^^*/

