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
#include <conio.h>
#include <fcntl.h>
#include <sys\stat.h>
#include <share.h>
#include <io.h>

#include "nicef.h"

/* PRIVATE MACROS */
#ifdef NOERRTRACKER
	#define sret_if(_a_) _a_
	#define sret_ifnot(_a) _a_
#else
	#define sret_if(_a_) ret_if(_a_)
	#define sret_ifnot(_a_) ret_ifnot(_a_)
#endif
/* end of PRIVATE MACROS */


#ifdef ISOPEN_SAFETY
/* private constants */
#define _yes_ +1
#define _no_ 0
/* end */
int nicefi::isopened(){
	return ((_opened)&&(fhandle>0));
}
void nicefi::_setopened(){
	_opened=_yes_;
}
void nicefi::_setclosed(){
	 _opened=_no_;
}
#endif


long nicefi::getnumrecords(){//how many records are now
#ifdef ISOPEN_SAFETY
	sret_ifnot(isopened());
#endif
	long filesize=filelength(fhandle);
	sret_if( filesize < 0 );
	return ( ofs2recnum(filesize) -1 );
}

reterrt nicefi::writerec(const long recno, const void * from){//recsize bytes
#ifdef ISOPEN_SAFETY
	sret_ifnot(isopened());
#endif
	seekto(recno);
	sret_if(recsize != write(fhandle,from,recsize));
	ret_ok();
}

reterrt nicefi::readrec(const long recno, void *  into){
//recsize bytes
#ifdef ISOPEN_SAFETY
	sret_ifnot(isopened());
#endif
	seekto(recno);
	sret_if(recsize != read(fhandle,into,recsize));
	ret_ok();
}


nicefi::nicefi(){
	fhandle=-1;
	recsize=-1;
	headersize=-1;
#ifdef ISOPEN_SAFETY
	_setclosed();
#endif
}

nicefi::~nicefi(){
	if (fhandle>0) close();
	fhandle=-1;
}

reterrt nicefi::close(){
#ifdef ISOPEN_SAFETY
	sret_ifnot(isopened());
#endif
	sret_if( fhandle <=0 );
	sret_if( (0 != ::close(fhandle)) );
	fhandle=-1;
	ret_ok();
}

long nicefi::ofs2recnum(const long ofs){
//recnum can't be 0, it goes from 1..
//ofs goes from 0..
#ifdef ISOPEN_SAFETY
	sret_ifnot(isopened());
#endif
	sret_if(ofs < 0);
	long ofsminusheader= (ofs - headersize);//just tmp
	//this shouldn't be != 0 , if it is, the passed ofs is wrong, '
	//  and perhaps the error is above: to the caller!
	sret_if( ( ofsminusheader % recsize ) != 0);
	return ( ( ofsminusheader / recsize ) +1 );//surely reminder is 0 !
}

long nicefi::recnum2ofs(const long recnum){
//recnum goes from 1..
//ofs goes from 0..
#ifdef ISOPEN_SAFETY
	sret_ifnot(isopened());
#endif
	sret_if(recnum < 0);
	return (headersize+((recnum-1)*recsize));
}

reterrt nicefi::seekto(const long recno){
#ifdef ISOPEN_SAFETY
	sret_ifnot(isopened());
#endif
	sret_if(fhandle<=0);//if not open;
	sret_if(recno<=0);//cannot be 0 or less
	sret_if(headersize< 0);
	sret_if(recsize<=0);
	long exactofs=recnum2ofs(recno);
	sret_if( exactofs != lseek(fhandle,exactofs,SEEK_SET) );

	ret_ok();
}


reterrt nicefi::writeheader(const void * header){
#ifdef ISOPEN_SAFETY
	sret_ifnot(isopened());
#endif
	sret_if(header==NULL);
	sret_if(fhandle<=0);
	sret_if(headersize<=0);
	sret_if(0L != lseek(fhandle,0L,SEEK_SET));
	sret_if(headersize != write(fhandle,header,headersize));

	ret_ok();
}

reterrt nicefi::readheader(void *  header){
#ifdef ISOPEN_SAFETY
	sret_ifnot(isopened());
#endif
	sret_if(header==NULL);
	sret_if(fhandle<=0);
	sret_if(headersize<=0);
	sret_if(0L != lseek(fhandle,0L,SEEK_SET));
	sret_if(headersize != read(fhandle,header,headersize));

	ret_ok();
}



reterrt nicefi::open(const char * fname, const long header_size,const long rec_size){
	sret_if(fhandle>0);//if already open
#ifdef ISOPEN_SAFETY
	sret_if(isopened());
#endif

	sret_if(rec_size<=0);
	sret_if(header_size<0);

	fhandle = ::sopen(fname, O_RDWR | O_CREAT | O_BINARY /*| O_DENYWRITE*/, SH_DENYWR, S_IREAD | S_IWRITE);
	sret_if(fhandle<=0);//if open failed
#ifdef ISOPEN_SAFETY
	_setopened();
#endif
	recsize=rec_size;
	headersize=header_size;
/*
	if (putheader!=NULL) {
		//read and seek after the header
		sret_if(headersize != ::read(fhandle,putheader,headersize));
	}
	else {
		//just seek after the header
		sret_if(headersize!=lseek(fhandle,headersize,SEEK_SET));
	}
	*/
	ret_ok();
}

