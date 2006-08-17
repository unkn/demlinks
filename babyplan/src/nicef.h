/****************************************************************************
*
*                             dmental links
*    Copyright (c) 28 Feb 2005 AtKaaZ, AtKaaZ at users.sourceforge.net
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

#include "petrackr.h"
/* PRIVATE DEFINES */
//#define ISOPEN_SAFETY //alwayscheck if we opened the file prior to operations.
/* end of PRIVATE DEFINES */

/* other defines, not quite private */
#define _FIRST_RECORD_ 1  //starts from 1, don't change!
/* end */

enum bull {
    stat_read=1,
    stat_write=2
};

typedef int stat_type;

struct cache_record {
    stat_type stat;
    long recno;
    void * data;//malloc(recsize)
    cache_record *prev;
    cache_record *next;
};

class nicefi {
private:
    int fhandle;
    long headersize;//long is -2GB..+2GB dammit!
    long recsize;
    /* cache stuff: */
    long numCachedRecords;//in records
    cache_record * headCache;//head of a FIFO unsorted double-linked list
    cache_record * tailCache;//tail
    long highest_recno;//used with getnumrecords()
    long max_numcachedrex;// 1024
    //how many records to cache (ie. don't yet writ'em to disk)

#ifdef ISOPEN_SAFETY
    int _opened;
#endif
public:
    nicefi();
    ~nicefi();
    reterrt flushwrites();//flush the cache, ...no shit!? we have cache?!
    reterrt open(const char * fname, const long header_size,const long rec_size,const long maxcachedrecords);
    reterrt close();
    reterrt readrec(const long recno, void * into);//recsize bytes
    reterrt writerec(const long recno, const void * from);//recsize bytes
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
    reterrt _absolute_readrec(const long recno, void * into);
    reterrt _absolute_writerec(const long recno, const void * from);
    reterrt absAddRecord2Cache(const stat_type typ,const long recno, const void * from);
    reterrt AddRecord2Cache(const stat_type typ,const long recno, const void * from);
    unsigned char absGetRecordFromCache(const long recno, void * into);
    reterrt killcache();
    reterrt initCache(const long maxrex);

};

#endif
