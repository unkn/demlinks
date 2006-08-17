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
#include "gca_list.h"

long if_gcatoms_list::howmany(){
    return nicefi::getnumrecords();
}

gcatoms_listID if_gcatoms_list::addnew(const deref_gcatoms_listID_type &from){
    long newgcatoms_listID=howmany()+1;
    ret_ifnot( writewithID(newgcatoms_listID,from) );
    return newgcatoms_listID;
}

reterrt if_gcatoms_list::getwithID(const gcatoms_listID whatgcatoms_listID, deref_gcatoms_listID_type &into){
    ret_ifnot(nicefi::readrec(whatgcatoms_listID,&into));
    ret_ok();
}

reterrt if_gcatoms_list::writewithID(const gcatoms_listID whatgcatoms_listID, const deref_gcatoms_listID_type &from){
    ret_ifnot(nicefi::writerec(whatgcatoms_listID,&from));
    ret_ok();
}

if_gcatoms_list::~if_gcatoms_list(){
    if (opened==_yes_) shutdown();
}

if_gcatoms_list::if_gcatoms_list():
    its_recsize(sizeof(deref_gcatoms_listID_type))
{
    opened=_no_;
}

reterrt if_gcatoms_list::init(const char * fname, const long MAXCACHEDRECORDS){
    ret_ifnot(nicefi::open(fname,0,its_recsize,MAXCACHEDRECORDS));
    opened=_yes_;
    ret_ok();
}

reterrt if_gcatoms_list::shutdown(){
    if (opened==_yes_) ret_ifnot(nicefi::close());
    opened=_no_;
    ret_ok();
}

void if_gcatoms_list::compose(
    deref_gcatoms_listID_type &into,
    const gcatomslist_itemID ptr2head
)
{
    _in2(ptr2head);
}
