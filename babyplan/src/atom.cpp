/****************************************************************************
*
*                             dmental links
*    Copyright (c) 28 Feb 2005 AtKaaZ, AtKaaZ at users.sourceforge.net
*    Portions Copyright (c) 1983-2002 Sybase, Inc. All Rights Reserved.
*
*  ========================================================================
*
*    This file contains Original Code and/or Modifications of Original
*    Code as defined in and that are subject to the Sybase Open Watcom
*    Public License version 1.0 (the 'License'). You may not use this file
*    except in compliance with the License. BY USING THIS FILE YOU AGREE TO
*    ALL TERMS AND CONDITIONS OF THE LICENSE. A copy of the License is
*    provided with the Original Code and Modifications, and is also
*    available at www.sybase.com/developer/opensource.
*
*    The Original Code and all software distributed under the License are
*    distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
*    EXPRESS OR IMPLIED, AND SYBASE AND ALL CONTRIBUTORS HEREBY DISCLAIM
*    ALL SUCH WARRANTIES, INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF
*    MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR
*    NON-INFRINGEMENT. Please see the License for the specific language
*    governing rights and limitations under the License.
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

atomID if_atom::addnew(const deref_atomID_type *from){
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());
#endif
    long newatomID=howmany()+1;
    writewithID(newatomID,from);
    return newatomID;
}

reterrt if_atom::getwithID(const atomID whatatomID, deref_atomID_type *into){
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());
#endif
    ret_ifnot(nicefi::readrec(whatatomID,into));
    ret_ok();
}

reterrt if_atom::writewithID(const atomID whatatomID, const deref_atomID_type *from){
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());
#endif
    ret_ifnot(nicefi::writerec(whatatomID,from));
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

reterrt if_atom::init(const char * fname){
#ifdef WASINITED_SAFETY
    ret_if(wasinited());
#endif
    ret_ifnot(nicefi::open(fname,0,its_recsize));
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
    deref_atomID_type *into,
    const atomtypes at_type,
    const anyatomID at_ID
)
{
    _2in2(at_type,at_ID);
}

