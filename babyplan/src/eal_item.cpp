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
#include "eal_item.h"


long if_eatomslist_item::howmany(){ 
    return nicefi::getnumrecords();
}

eatomslist_itemID if_eatomslist_item::addnew(const deref_eatomslist_itemID_type *from){
    long neweatomslist_itemID=howmany()+1;
    ret_ifnot( writewithID(neweatomslist_itemID,from) );
    return neweatomslist_itemID;
}

reterrt if_eatomslist_item::getwithID(const eatomslist_itemID whateatomslist_itemID, deref_eatomslist_itemID_type *into){
    ret_ifnot(nicefi::readrec(whateatomslist_itemID,into));
    ret_ok();
}

reterrt if_eatomslist_item::writewithID(const eatomslist_itemID whateatomslist_itemID, const deref_eatomslist_itemID_type *from){
    ret_ifnot(nicefi::writerec(whateatomslist_itemID,from));
    ret_ok();
}                                          
                                            
if_eatomslist_item::~if_eatomslist_item(){
    if (opened==_yes_) shutdown();
}

if_eatomslist_item::if_eatomslist_item():
    its_recsize(sizeof(deref_eatomslist_itemID_type))
{
    opened=_no_;
}

reterrt if_eatomslist_item::init(const char * fname){
    ret_ifnot(nicefi::open(fname,0,its_recsize));
    opened=_yes_;
    ret_ok();
}

reterrt if_eatomslist_item::shutdown(){
    if (opened==_yes_) ret_ifnot(nicefi::close());
    opened=_no_;
    ret_ok();
}

void if_eatomslist_item::compose(
    deref_eatomslist_itemID_type *into,
    eatomslist_itemID prevINlist,
    eatomslist_itemID nextINlist,
    atomID ptr2atom_that_points_to_US
)
{
    _3in2(prevINlist,nextINlist,ptr2atom_that_points_to_US);
}

