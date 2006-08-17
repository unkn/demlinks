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
* Description:
*
****************************************************************************/


#ifndef __NICEF_H
#define __NICEF_H

#include "errtrack.h"

//typedef unsigned long ulong;

class nicefi {
private:
	int fhandle;
	long headersize;//long is -2GB..+2GB dammit!
	long recsize;
	int _opened;
public:
	nicefi();
	~nicefi();
	reterrt open(const char * fname, const long header_size,const long rec_size);
	reterrt close();
	reterrt readrec(const long recno, void * into);//recsize bytes
	reterrt writerec(const long recno, const void * from);//recsize bytes
//	ulong getrecnum();//get the record number of current filepos
	long getnumrecords();//how many records are now
	reterrt writeheader(const void * header);
	reterrt readheader(void * header);
	int isopened();
private:
	void _setopened();
	void _setclosed();
	reterrt seekto(const long recno);//1..
	long recnum2ofs(const long recnum);
	long ofs2recnum(const long ofs);

};

#endif
