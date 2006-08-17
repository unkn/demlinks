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
#include "acatom.h"


long if_acatom::howmany(){
    return nicefi::getnumrecords();
}

acatomID if_acatom::addnew(const deref_acatomID_type &from){
    long newacatomID=howmany()+1;
    ret_ifnot( writewithID(newacatomID,from) );
    return newacatomID;
}

reterrt if_acatom::getwithID(const acatomID whatacatomID, deref_acatomID_type &into){
    ret_ifnot(nicefi::readrec(whatacatomID,&into));
    ret_ok();
}

reterrt if_acatom::writewithID(const acatomID whatacatomID, const deref_acatomID_type &from){
    ret_ifnot(nicefi::writerec(whatacatomID,&from));
    ret_ok();
}

if_acatom::~if_acatom(){
    if (opened==_yes_) shutdown();
}

if_acatom::if_acatom():
    its_recsize(sizeof(deref_acatomID_type))
{
    opened=_no_;
}

reterrt if_acatom::init(const char * fname, const long MAXCACHEDRECORDS){
    ret_ifnot(nicefi::open(fname,0,its_recsize,MAXCACHEDRECORDS));
    opened=_yes_;
    ret_ok();
}

reterrt if_acatom::shutdown(){
    if (opened==_yes_) ret_ifnot(nicefi::close());
    opened=_no_;
    ret_ok();
}

void if_acatom::compose(
    deref_acatomID_type &into,
    groupID ptr2group,
    atomID prevINchain,
    atomID nextINchain,
    acatoms_listID ptr2clonelist,
    atomID Irefer2thisATOM
)
{
    _5in2(ptr2group,prevINchain,nextINchain,ptr2clonelist,Irefer2thisATOM);
}

