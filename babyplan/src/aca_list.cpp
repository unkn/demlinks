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
* Description: lists with those atoms that refer to an <atom clone> atom
*
****************************************************************************/


#include <process.h>
#include <stdio.h>

#include "petrackr.h"
#include "aca_list.h"

long if_acatoms_list::howmany(){ 
    return nicefi::getnumrecords();
}

acatoms_listID if_acatoms_list::addnew(const deref_acatoms_listID_type *from){
    long newacatoms_listID=howmany()+1;
    ret_ifnot( writewithID(newacatoms_listID,from) );
    return newacatoms_listID;
}

reterrt if_acatoms_list::getwithID(const acatoms_listID whatacatoms_listID, deref_acatoms_listID_type *into){
    ret_ifnot(nicefi::readrec(whatacatoms_listID,into));
    ret_ok();
}

reterrt if_acatoms_list::writewithID(const acatoms_listID whatacatoms_listID, const deref_acatoms_listID_type *from){
    ret_ifnot(nicefi::writerec(whatacatoms_listID,from));
    ret_ok();
}                                          
                                            
if_acatoms_list::~if_acatoms_list(){
    if (opened==_yes_) shutdown();
}

if_acatoms_list::if_acatoms_list():
    its_recsize(sizeof(deref_acatoms_listID_type))
{
    opened=_no_;
}

reterrt if_acatoms_list::init(const char * fname){
    ret_ifnot(nicefi::open(fname,0,its_recsize));
    opened=_yes_;
    ret_ok();
}

reterrt if_acatoms_list::shutdown(){
    if (opened==_yes_) ret_ifnot(nicefi::close());
    opened=_no_;
    ret_ok();
}

void if_acatoms_list::compose(
    deref_acatoms_listID_type *into,
    const acatomslist_itemID ptr2head
)
{
    _in2(ptr2head);
}
