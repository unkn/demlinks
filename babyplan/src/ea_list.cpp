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
#include "ea_list.h"

long if_eatoms_list::howmany(){
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());
#endif
    return nicefi::getnumrecords();
}

eatoms_listID if_eatoms_list::addnew(const deref_eatoms_listID_type &from){
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());
#endif
    long neweatoms_listID=howmany()+1;
    ret_ifnot( writewithID(neweatoms_listID,from) );
    return neweatoms_listID;
}

reterrt if_eatoms_list::getwithID(const eatoms_listID whateatoms_listID, deref_eatoms_listID_type &into){
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());
#endif
    ret_ifnot(nicefi::readrec(whateatoms_listID,&into));
    ret_ok();
}

reterrt if_eatoms_list::writewithID(const eatoms_listID whateatoms_listID, const deref_eatoms_listID_type &from){
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());
#endif
    ret_ifnot(nicefi::writerec(whateatoms_listID,&from));
    ret_ok();
}

if_eatoms_list::~if_eatoms_list(){
#ifdef WASINITED_SAFETY //if unset, user must use shutdown() before destruct.
    if (wasinited())
        shutdown();
#endif
}

if_eatoms_list::if_eatoms_list():
    its_recsize(sizeof(deref_eatoms_listID_type))
{
#ifdef WASINITED_SAFETY
    setdeinited();
#endif
}

reterrt if_eatoms_list::init(const char * fname, const long MAXCACHEDRECORDS){
#ifdef WASINITED_SAFETY
    ret_if(wasinited());
#endif
    ret_ifnot(nicefi::open(fname,0,its_recsize,MAXCACHEDRECORDS));
#ifdef WASINITED_SAFETY
    setinited();
#endif
    ret_ok();
}

reterrt if_eatoms_list::shutdown(){
#ifdef WASINITED_SAFETY
    if (wasinited()) {
#endif
        ret_ifnot(nicefi::close());
#ifdef WASINITED_SAFETY
        setdeinited();
    }
#endif
    ret_ok();
}

void if_eatoms_list::compose(
    deref_eatoms_listID_type &into,
    const eatomslist_itemID ptr2head
)
{
    _in2(ptr2head);
}
