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


#include "petrackr.h"
#include "dmentalx.h"

dmentalix::dmentalix(){
}

dmentalix::~dmentalix(){
}


/*..............*/
#define INIT(_x_) \
	ret_ifnot( if_##_x_##::init(##_x_##fname) )
	
reterrt dmentalix::init(_declall(const char *,fname))
{
	INIT(atom);
	INIT(group);
	INIT(eatom);
	INIT(acatom);
	INIT(gcatom);
	INIT(eatoms_list);
	INIT(eatomslist_item);
	INIT(acatoms_list);
	INIT(acatomslist_item);
	INIT(gcatoms_list);
	INIT(gcatomslist_item);

	ret_ok();
}
#undef INIT
/*^^^^^^^^^^^^^^*/


/*..............*/
#define DONE(_x_) \
	ret_ifnot( if_##_x_##::shutdown() )
	
reterrt dmentalix::shutdown(){
	DONE(atom);
	DONE(group);
	DONE(eatom);
	DONE(acatom);
	DONE(gcatom);
	DONE(eatoms_list);
	DONE(eatomslist_item);
	DONE(acatoms_list);
	DONE(acatomslist_item);
	DONE(gcatoms_list);
	DONE(gcatomslist_item);

	ret_ok();
}
#undef DONE
/*^^^^^^^^^^^^^^*/

reterrt newelemental(basic_element thenewbe){//a new eatom?!

	ret_ok();
}

