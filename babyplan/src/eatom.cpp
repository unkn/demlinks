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


//REMEBER: If something goes wrong, it's chained!
//[it's either the first item or anyother item of a chain,
// it's up to you to make it the last item in the chain. Act now ;;)]

#include <process.h>
#include <stdio.h>

#include "petrackr.h"
#include "eatom.h"

long if_eatom::find_basic_element(const basic_element what2search){
#ifdef WASINITED_SAFETY
	ret_ifnot(wasinited());
#endif
	//find element here
	etracker->clearlastfunxerr();
	long tmpi=howmany();//getting 0 both if err and if howmany==0 records
	ret_if(etracker->asks_if_last_funx_had_an_error());
	//^^^^^^^^ if howmany() had an error, we exit.

	deref_eatomID_type *tmpinto=new deref_eatomID_type;
	ret_if(tmpinto==NULL);

	for (long i=_FIRST_RECORD_;i<=tmpi;i++){//parse all records
		nicefi::readrec(i,tmpinto);//recnum `i' means eatomID actually.
		if ( tmpinto->basicelementdata==what2search ){
			//found it
			return i;//eatomID
		}
	}
	//well we didn't find shit
	return 0;//eatomID cannot be zero, so...
}

long if_eatom::howmany(){ 
#ifdef WASINITED_SAFETY
	ret_ifnot(wasinited());
#endif
	return nicefi::getnumrecords();
}

long if_eatom::addnew(const deref_eatomID_type *from){
#ifdef WASINITED_SAFETY
	ret_ifnot(wasinited());
#endif
	long neweatomID=howmany()+1;
	writewithID(neweatomID,from);
	return neweatomID;
}

reterrt if_eatom::getwithID(const eatomID whateatomID, deref_eatomID_type *into){
#ifdef WASINITED_SAFETY
	ret_ifnot(wasinited());
#endif
	ret_ifnot(nicefi::readrec(whateatomID,into));
	ret_ok();
}

reterrt if_eatom::writewithID(const eatomID whateatomID, const deref_eatomID_type *from){
#ifdef WASINITED_SAFETY
	ret_ifnot(wasinited());
#endif
	ret_ifnot(nicefi::writerec(whateatomID,from));
	ret_ok();
}                                          
											
if_eatom::~if_eatom(){
#ifdef WASINITED_SAFETY //if unset, user must use shutdown() before destruct.
	if (wasinited())
		shutdown(); 
#endif
}

if_eatom::if_eatom():
	its_recsize(sizeof(deref_eatomID_type))
{
#ifdef WASINITED_SAFETY
	setdeinited();
#endif
}

reterrt if_eatom::init(const char * fname){
#ifdef WASINITED_SAFETY
	ret_if(wasinited());
#endif
	ret_ifnot(nicefi::open(fname,0,its_recsize));
#ifdef WASINITED_SAFETY
	setinited();
#endif
	ret_ok();
}

reterrt if_eatom::shutdown(){
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

void if_eatom::compose(
	deref_eatomID_type *into,
	eatoms_listID ptr2list,
	basic_element basicelementdata
)
{
	_2in2(ptr2list,basicelementdata);
}

