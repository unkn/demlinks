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
#include "grplitem.h"


long if_grpatomslist_item::howmany(){ 
    return nicefi::getnumrecords();
}

grpatomslist_itemID if_grpatomslist_item::addnew(const deref_grpatomslist_itemID_type &from){
    long newgrpatomslist_itemID=howmany()+1;
    ret_ifnot( writewithID(newgrpatomslist_itemID,from) );
    return newgrpatomslist_itemID;
}

reterrt if_grpatomslist_item::getwithID(const grpatomslist_itemID whatgrpatomslist_itemID, deref_grpatomslist_itemID_type &into){
    ret_ifnot(nicefi::readrec(whatgrpatomslist_itemID,&into));
    ret_ok();
}

reterrt if_grpatomslist_item::writewithID(const grpatomslist_itemID whatgrpatomslist_itemID, const deref_grpatomslist_itemID_type &from){
    ret_ifnot(nicefi::writerec(whatgrpatomslist_itemID,&from));
    ret_ok();
}                                          
                                            
if_grpatomslist_item::~if_grpatomslist_item(){
    if (opened==_yes_) shutdown();
}

if_grpatomslist_item::if_grpatomslist_item():
    its_recsize(sizeof(deref_grpatomslist_itemID_type))
{
    opened=_no_;
}

reterrt if_grpatomslist_item::init(const char * fname, const long MAXCACHEDRECORDS){
    ret_ifnot(nicefi::open(fname,0,its_recsize,MAXCACHEDRECORDS));
    opened=_yes_;
    ret_ok();
}

reterrt if_grpatomslist_item::shutdown(){
    if (opened==_yes_) ret_ifnot(nicefi::close());
    opened=_no_;
    ret_ok();
}

void if_grpatomslist_item::compose(
    deref_grpatomslist_itemID_type &into,
    grpatomslist_itemID prevINlist,
    grpatomslist_itemID nextINlist,
    atomID atomID_that_points_to_US_the_group
)
{
    _3in2(prevINlist,nextINlist,atomID_that_points_to_US_the_group);
}

