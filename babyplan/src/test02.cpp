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
* Description: just testing stuff 
*
****************************************************************************/


#include <process.h>
#include <stdio.h>
#include <conio.h>
#include <io.h>

#include "petrackr.h"
#include "dmentalx.h"


dmentalix *test2;

int main(){
	init_error_tracker();
	
	test2=new dmentalix;
	ab_ifnot(test2);

	unlinkall(_fnames);
	test2->init(_fnames);
	

	
	test2->deinit();
	delete test2;

//last in line
	deinit_error_tracker();
	printf("\nDone...press key\n");
	getch();
	return 0;
}

