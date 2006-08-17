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
#include "atom.h"

long if_atom::howmany(){
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());
#endif
    return nicefi::getnumrecords();
}

atomID if_atom::addnew(const deref_atomID_type &from){
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());
#endif
    long newatomID=howmany()+1;
    ret_ifnot( writewithID(newatomID,from) );
    return newatomID;
}

reterrt if_atom::getwithID(const atomID whatatomID, deref_atomID_type &into){
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());
#endif
    ret_ifnot(nicefi::readrec(whatatomID,&into));
    ret_ok();
}

reterrt if_atom::writewithID(const atomID whatatomID, const deref_atomID_type &from){
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());
#endif
    ret_ifnot(nicefi::writerec(whatatomID,&from));
    ret_ok();
}

if_atom::~if_atom(){
#ifdef WASINITED_SAFETY //if unset, user must use shutdown() before destruct.
    if (wasinited())
        shutdown();
#endif
}

if_atom::if_atom():
    its_recsize(sizeof(deref_atomID_type))
{
#ifdef WASINITED_SAFETY
    setdeinited();
#endif
}

reterrt if_atom::init(const char * fname,const long MAXCACHEDRECORDS){
#ifdef WASINITED_SAFETY
    ret_if(wasinited());
#endif
    ret_ifnot(nicefi::open(fname,0,its_recsize,MAXCACHEDRECORDS));
#ifdef WASINITED_SAFETY
    setinited();
#endif
    ret_ok();
}

reterrt if_atom::shutdown(){
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

void if_atom::compose(
    deref_atomID_type &into,
    const atomtypes at_type,
    const anyatomID at_ID
)
{
    _2in2(at_type,at_ID);
}

