/****************************************************************************
*
*                             dmental links
*	Copyright (c) 28 Feb 2005 AtKaaZ, AtKaaZ at users.sourceforge.net
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
#include "acatom.h"


long if_acatom::howmany(){ 
	return nicefi::getnumrecords();
}

long if_acatom::addnew(const deref_acatomID_type *from){
	long newacatomID=howmany()+1;
	writewithID(newacatomID,from);
	return newacatomID;
}

reterrt if_acatom::getwithID(const acatomID whatacatomID, deref_acatomID_type *into){
	ret_ifnot(nicefi::readrec(whatacatomID,into));
	ret_ok();
}

reterrt if_acatom::writewithID(const acatomID whatacatomID, const deref_acatomID_type *from){
	ret_ifnot(nicefi::writerec(whatacatomID,from));
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

reterrt if_acatom::init(const char * fname){
	ret_ifnot(nicefi::open(fname,0,its_recsize));
	opened=_yes_;
	ret_ok();
}

reterrt if_acatom::shutdown(){
	if (opened==_yes_) ret_ifnot(nicefi::close());
	opened=_no_;
	ret_ok();
}

void if_acatom::compose(
	deref_acatomID_type *into,
	groupID ptr2group,
	atomID prevINchain,
	atomID nextINchain,
	acatoms_listID ptr2clonelist,
	atomID Irefer2thisATOM
)
{
	_5in2(ptr2group,prevINchain,nextINchain,ptr2clonelist,Irefer2thisATOM);
}

