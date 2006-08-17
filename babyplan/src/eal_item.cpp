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


#include <process.h>
#include <stdio.h>

#include "petrackr.h"
#include "eal_item.h"


long if_eatomslist_item::howmany(){
    return nicefi::getnumrecords();
}

eatomslist_itemID if_eatomslist_item::addnew(const deref_eatomslist_itemID_type &from){
    long neweatomslist_itemID=howmany()+1;
    ret_ifnot( writewithID(neweatomslist_itemID,from) );
    return neweatomslist_itemID;
}

reterrt if_eatomslist_item::getwithID(const eatomslist_itemID whateatomslist_itemID, deref_eatomslist_itemID_type &into){
    ret_ifnot(nicefi::readrec(whateatomslist_itemID,&into));
    ret_ok();
}

reterrt if_eatomslist_item::writewithID(const eatomslist_itemID whateatomslist_itemID, const deref_eatomslist_itemID_type &from){
    ret_ifnot(nicefi::writerec(whateatomslist_itemID,&from));
    ret_ok();
}

if_eatomslist_item::~if_eatomslist_item(){
    if (opened==_yes_) shutdown();
}

if_eatomslist_item::if_eatomslist_item():
    its_recsize(sizeof(deref_eatomslist_itemID_type))
{
    opened=_no_;
}

reterrt if_eatomslist_item::init(const char * fname, const long MAXCACHEDRECORDS){
    ret_ifnot(nicefi::open(fname,0,its_recsize,MAXCACHEDRECORDS));
    opened=_yes_;
    ret_ok();
}

reterrt if_eatomslist_item::shutdown(){
    if (opened==_yes_) ret_ifnot(nicefi::close());
    opened=_no_;
    ret_ok();
}

void if_eatomslist_item::compose(
    deref_eatomslist_itemID_type &into,
    eatomslist_itemID prevINlist,
    eatomslist_itemID nextINlist,
    atomID ptr2atom_that_points_to_US
)
{
    _3in2(prevINlist,nextINlist,ptr2atom_that_points_to_US);
}

