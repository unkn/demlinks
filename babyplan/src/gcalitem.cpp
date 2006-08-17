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
#include "gcalitem.h"


long if_gcatomslist_item::howmany(){
    return nicefi::getnumrecords();
}

gcatomslist_itemID if_gcatomslist_item::addnew(const deref_gcatomslist_itemID_type &from){
    long newgcatomslist_itemID=howmany()+1;
    ret_ifnot( writewithID(newgcatomslist_itemID,from) );
    return newgcatomslist_itemID;
}

reterrt if_gcatomslist_item::getwithID(const gcatomslist_itemID whatgcatomslist_itemID, deref_gcatomslist_itemID_type &into){
    ret_ifnot(nicefi::readrec(whatgcatomslist_itemID,&into));
    ret_ok();
}

reterrt if_gcatomslist_item::writewithID(const gcatomslist_itemID whatgcatomslist_itemID, const deref_gcatomslist_itemID_type &from){
    ret_ifnot(nicefi::writerec(whatgcatomslist_itemID,&from));
    ret_ok();
}

if_gcatomslist_item::~if_gcatomslist_item(){
    if (opened==_yes_) shutdown();
}

if_gcatomslist_item::if_gcatomslist_item():
    its_recsize(sizeof(deref_gcatomslist_itemID_type))
{
    opened=_no_;
}

reterrt if_gcatomslist_item::init(const char * fname, const long MAXCACHEDRECORDS){
    ret_ifnot(nicefi::open(fname,0,its_recsize,MAXCACHEDRECORDS));
    opened=_yes_;
    ret_ok();
}

reterrt if_gcatomslist_item::shutdown(){
    if (opened==_yes_) ret_ifnot(nicefi::close());
    opened=_no_;
    ret_ok();
}

void if_gcatomslist_item::compose(
    deref_gcatomslist_itemID_type &into,
    gcatomslist_itemID prevINlist,
    gcatomslist_itemID nextINlist,
    atomID ptr2atom_that_points_to_US
)
{
    _3in2(prevINlist,nextINlist,ptr2atom_that_points_to_US);
}

