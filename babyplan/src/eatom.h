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


#ifndef __EATOM_H
#define __EATOM_H

#include "gdefs.h"

#include "nicef.h"

#include "common.h"

/* PRIVATE DEFINES */
//#define WASINITED_SAFETY //always check if was inited before operating
/* end of PRIVATE DEFINES */

class if_eatom:public nicefi {
private:                       
#ifdef WASINITED_SAFETY
	int inited;                 
#endif
	const long its_recsize;      
public:                           
	if_eatom();                    
	~if_eatom();                    
	reterrt init(const char *fname);    
	reterrt getwithID(const eatomID whateatomID, deref_eatomID_type *into);
	reterrt writewithID(const eatomID whateatomID, const deref_eatomID_type *from);
	long find_basic_element(const basic_element what2search);
	long addnew(const deref_eatomID_type *from); 
	long howmany();
	reterrt shutdown();
	void compose(
		deref_eatomID_type *into,
		eatoms_listID ptr2list,
		basic_element basicelementdata
	);
#ifdef WASINITED_SAFETY
private:
	int wasinited(){ if (inited==_yes_) return _yes_; return _no_; }
	void setinited(){ inited=_yes_; };
	void setdeinited(){ inited=_no_; };
#endif
	
};//class



#endif
