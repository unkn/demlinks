/****************************************************************************
*
*                             dmental links
*       Copyright (c) 28 Feb 2005 AtKaaZ, AtKaaZ at users.sourceforge.net
*
*  ========================================================================
*
*    This program is free software; you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation; either version 2 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program; if not, write to the Free Software
*    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
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

