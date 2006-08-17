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


//group-clone ATOM
//an atom that is a referer to a group
#include <process.h>
#include <stdio.h>

#include "petrackr.h"
#include "gcatom.h"

long if_gcatom::howmany(){
    return nicefi::getnumrecords();
}

gcatomID if_gcatom::addnew(const deref_gcatomID_type &from){
    long newgcatomID=howmany()+1;
    ret_ifnot( writewithID(newgcatomID,from) );
    return newgcatomID;
}

reterrt if_gcatom::getwithID(const gcatomID whatgcatomID, deref_gcatomID_type &into){
    ret_ifnot(nicefi::readrec(whatgcatomID,&into));
    ret_ok();
}

reterrt if_gcatom::writewithID(const gcatomID whatgcatomID, const deref_gcatomID_type &from){
    ret_ifnot(nicefi::writerec(whatgcatomID,&from));
    ret_ok();
}

if_gcatom::~if_gcatom(){
    if (opened==_yes_) shutdown();
}

if_gcatom::if_gcatom():
    its_recsize(sizeof(deref_gcatomID_type))
{
    opened=_no_;
}

reterrt if_gcatom::init(const char * fname, const long MAXCACHEDRECORDS){
    ret_ifnot(nicefi::open(fname,0,its_recsize,MAXCACHEDRECORDS));
    opened=_yes_;
    ret_ok();
}

reterrt if_gcatom::shutdown(){
    if (opened==_yes_) ret_ifnot(nicefi::close());
    opened=_no_;
    ret_ok();
}

void if_gcatom::compose(
    deref_gcatomID_type &into,
    const groupID ptr2group,
    const atomID prevINchain,
    const atomID nextINchain,
    const acatoms_listID ptr2clonelist_of_atomIDs_which_point_to_US,
    const groupID Irefer2thisGROUP
)
{
    _5in2(ptr2group,
        prevINchain,
        nextINchain,
        ptr2clonelist_of_atomIDs_which_point_to_US,
        Irefer2thisGROUP);
}
