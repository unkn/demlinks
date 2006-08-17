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



//the idea is reflected in the source too, in the way the source is composed.

#include <process.h>
#include <stdio.h>

#include "petrackr.h"
#include "group.h"


long if_group::howmany(){
    return nicefi::getnumrecords();
}

long if_group::addnew(const deref_groupID_type &from){
    long newgroupID=howmany()+1;
    ret_ifnot( writewithID(newgroupID,from) );
    return newgroupID;
}

reterrt if_group::getwithID(const groupID whatgroupID, deref_groupID_type &into){
    ret_ifnot(nicefi::readrec(whatgroupID,&into));
    ret_ok();
}

reterrt if_group::writewithID(const groupID whatgroupID, const deref_groupID_type &from){
    ret_ifnot(nicefi::writerec(whatgroupID,&from));
    ret_ok();
}

if_group::~if_group(){
    if (opened==_yes_) shutdown();
}

if_group::if_group():
    its_recsize(sizeof(deref_groupID_type))
{
    opened=_no_;
}

reterrt if_group::init(const char * fname, const long MAXCACHEDRECORDS){
    ret_ifnot(nicefi::open(fname,0,its_recsize,MAXCACHEDRECORDS));
    opened=_yes_;
    ret_ok();
}

reterrt if_group::shutdown(){
    if (opened==_yes_) ret_ifnot(nicefi::close());
    opened=_no_;
    ret_ok();
}

void if_group::compose(
    deref_groupID_type &into,
    const atomID ptr2atom_head_of_chain,
    const gcatoms_listID ptr2list_of_atomIDs
)
{
    _2in2(ptr2atom_head_of_chain,ptr2list_of_atomIDs);
}

