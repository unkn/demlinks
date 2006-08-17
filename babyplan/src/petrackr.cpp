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
* Description: provides personalized FIFO error tracking capab@source level
*
****************************************************************************/

#include <stdlib.h>
#include <stdio.h>
#include "petrackr.h"

uderrtrk *etracker;

uderrtrk::uderrtrk(){
}
uderrtrk::~uderrtrk(){
}

void uderrtrk::usrshowthemall(){
	s_item *tmp=getlasterr();
	while (tmp){
		fprintf(stderr,
			"%stypeTRKd::lvl#%d `%s'\nIn func `%s' from file `%s' at line `%d'\n"
			,str_t_all[tmp->error.errtype]
			,tmp->error.level
			,tmp->error.userdesc
			,tmp->error.funx
			,tmp->error.errfile
			,tmp->error.errline
		);
		clrlasterr();
		tmp=getlasterr();
	}//while
}


void init_error_tracker(){
	etracker=new uderrtrk;
	if (!etracker) {
		fprintf(stderr,"error allocating etracker pointer in file %s at line %d in func %s\n",__FILE__,__LINE__,__func__);
		abort();
	}
}

void deinit_error_tracker(){
	if (etracker) {
		if (etracker->getlasterr()!=NULL){//show all errors before kill
			etracker->usrshowthemall();
		}
		delete etracker;
		etracker=NULL;
	}
}


