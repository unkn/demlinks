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

reterrt if_group::init(const char * fname){
    ret_ifnot(nicefi::open(fname,0,its_recsize));
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

