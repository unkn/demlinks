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


//group-clone ATOM
//an atom that is a referer to a group
#include <process.h>
#include <stdio.h>

#include "petrackr.h"
#include "gcatom.h"

long if_gcatom::howmany(){ 
    return nicefi::getnumrecords();
}

long if_gcatom::addnew(const deref_gcatomID_type *from){
    long newgcatomID=howmany()+1;
    writewithID(newgcatomID,from);
    return newgcatomID;
}

reterrt if_gcatom::getwithID(const gcatomID whatgcatomID, deref_gcatomID_type *into){
    ret_ifnot(nicefi::readrec(whatgcatomID,into));
    ret_ok();
}

reterrt if_gcatom::writewithID(const gcatomID whatgcatomID, const deref_gcatomID_type *from){
    ret_ifnot(nicefi::writerec(whatgcatomID,from));
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

reterrt if_gcatom::init(const char * fname){
    ret_ifnot(nicefi::open(fname,0,its_recsize));
    opened=_yes_;
    ret_ok();
}

reterrt if_gcatom::shutdown(){
    if (opened==_yes_) ret_ifnot(nicefi::close());
    opened=_no_;
    ret_ok();
}

void if_gcatom::compose(
    deref_gcatomID_type *into,
    const groupID ptr2group,
    const atomID prevINchain,
    const atomID nextINchain,
    const gcatoms_listID ptr2clonelist,
    const groupID Irefer2thisGROUP
)
{
    _5in2(ptr2group,prevINchain,nextINchain,ptr2clonelist,Irefer2thisGROUP);
}
