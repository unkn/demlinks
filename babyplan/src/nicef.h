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


#ifndef __NICEF_H
#define __NICEF_H

#include "petrackr.h"
/* PRIVATE DEFINES */
#define NOERRORTRACKER //speed w/o safety ; no ret_if() / ret_ifnot()
//#define ISOPEN_SAFETY //alwayscheck if we opened the file prior to operations.
/* end of PRIVATE DEFINES */

/* other defines, not quite private */
#define _FIRST_RECORD_ 1  //starts from 1, don't change!
/* end */

class nicefi {
private:
	int fhandle;
	long headersize;//long is -2GB..+2GB dammit!
	long recsize;
#ifdef ISOPEN_SAFETY
	int _opened;
#endif
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
#ifdef ISOPEN_SAFETY
	int isopened();
#endif
private:
#ifdef ISOPEN_SAFETY
	void _setopened();
	void _setclosed();
#endif
	reterrt seekto(const long recno);//1..
	long recnum2ofs(const long recnum);
	long ofs2recnum(const long ofs);

};

#endif
