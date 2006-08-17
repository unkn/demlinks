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
* Description: lists with those atoms that refer to an <atom clone> atom
*
****************************************************************************/


#include <process.h>
#include <stdio.h>

#include "petrackr.h"
#include "aca_list.h"

long if_acatoms_list::howmany(){
    return nicefi::getnumrecords();
}

acatoms_listID if_acatoms_list::addnew(const deref_acatoms_listID_type &from){
    long newacatoms_listID=howmany()+1;
    ret_ifnot( writewithID(newacatoms_listID,from) );
    return newacatoms_listID;
}

reterrt if_acatoms_list::getwithID(const acatoms_listID whatacatoms_listID, deref_acatoms_listID_type &into){
    ret_ifnot(nicefi::readrec(whatacatoms_listID,&into));
    ret_ok();
}

reterrt if_acatoms_list::writewithID(const acatoms_listID whatacatoms_listID, const deref_acatoms_listID_type &from){
    ret_ifnot(nicefi::writerec(whatacatoms_listID,&from));
    ret_ok();
}

if_acatoms_list::~if_acatoms_list(){
    if (opened==_yes_) shutdown();
}

if_acatoms_list::if_acatoms_list():
    its_recsize(sizeof(deref_acatoms_listID_type))
{
    opened=_no_;
}

reterrt if_acatoms_list::init(const char * fname, const long MAXCACHEDRECORDS){
    ret_ifnot(nicefi::open(fname,0,its_recsize,MAXCACHEDRECORDS));
    opened=_yes_;
    ret_ok();
}

reterrt if_acatoms_list::shutdown(){
    if (opened==_yes_) ret_ifnot(nicefi::close());
    opened=_no_;
    ret_ok();
}

void if_acatoms_list::compose(
    deref_acatoms_listID_type &into,
    const acatomslist_itemID ptr2head
)
{
    _in2(ptr2head);
}
