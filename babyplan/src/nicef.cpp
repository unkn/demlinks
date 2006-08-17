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


#include <string.h> //for memcpy
#include <stdlib.h>
#include <process.h>
#include <stdio.h>
#include <conio.h>
#include <fcntl.h>
#include <sys\stat.h>
#include <share.h>
#include <io.h>

#include "nicef.h"

/*
TODO: we could use a cache of one or two records (ie. the last ones r/w)
*/

/* PRIVATE DEFINES */
//#define NOERRORTRACKER //speed w/o safety ; no ret_if() / ret_ifnot()
#undef NOERRORTRACKER
#ifndef NOERRORTRACKER
    #define EXACT_ERRORS_PLUS_A_BIT_OF_PARANOIA //don't set this, unless u want to see if seekto() far, & some cache debugging
#endif
/* end of PRIVATE DEFINES */

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
/*
long nicefi::get_all_unwritten_above_this_recno(const long recno){
//scans the cache and counts all stat_write which are above recno (gt)
//FIXME: make this in a variable
    long cnt=0;
    cache_record *tmp;
    tmp=headCache;
    while (tmp!=NULL){
        if ( (tmp->stat==stat_write)&&(tmp->recno>recno) ){
            ++cnt;
        }//fi
        tmp=tmp->next;
    }//while
    return cnt;
}
*/

long nicefi::getnumrecords(){//how many records are now
#ifdef ISOPEN_SAFETY
    sret_ifnot(isopened());
#endif
    long filesize=filelength(fhandle);
    sret_if( filesize < 0 );
    //cache aware
    long temepe=( ofs2recnum(filesize) -1 );
    return (temepe>highest_recno?temepe:highest_recno);
}

reterrt nicefi::_absolute_writerec(const long recno, const void * from){
//recsize bytes
    sret_ifnot(seekto(recno));
    sret_if(recsize != write(fhandle,from,recsize));
    ret_ok();
}

reterrt nicefi::flushwrites(){
#ifdef ISOPEN_SAFETY
    sret_ifnot(isopened());
#endif
// write all stat_write records from cache, keep them in cache
//    but mark them as read
    cache_record *tmp;
    tmp=headCache;//must parse them from head, FIFO remember!
    while (tmp){
        if (tmp->stat==stat_write){
            //we got something...
            sret_ifnot( _absolute_writerec(tmp->recno,tmp->data) );
            tmp->stat=stat_read;//mark it as read, we won't write it again, instead we'll read it(from cache)
        }//fi
        tmp=tmp->next;//parse them all
        --numCachedRecords;
    }//while
    headCache=NULL;
    tailCache=NULL;
    ret_ok();
}

unsigned char nicefi::absGetRecordFromCache(const long recno, void * into){
//no checks tries to get the recnum `recno' from cache into `into'
    //refusing to check weather into!=NULL
    cache_record *tmp=tailCache;
    while (tmp!=NULL){
        if (tmp->recno==recno){//found it
            memcpy(into,tmp->data,recsize);
            break;//fromwhile
        }//fi
        tmp=tmp->prev;
    }//while
    if (!tmp) return _no_;
    return _yes_;//if all ok, tmp!=NULL
}

reterrt nicefi::AddRecord2Cache(const stat_type typ, const long recno, const void * from){
//check for existenz before adding
//any existing recno is too old to be considered, since this one has the same
//recno.
    //we must parse the cache to find `recno', if found sink it (sunk?)
    cache_record *tmp=tailCache;//from tail up
    while (tmp!=NULL){
        if (tmp->recno==recno){
            //we got it letz kill it
            if (tmp!=headCache){
                if (tmp!=tailCache){
                    sret_if(tmp->prev ==NULL);
                    tmp->prev->next=tmp->next;
                    sret_if(tmp->next==NULL);
                    tmp->next->prev=tmp->prev;
                }//fi
                else{//tmp==tailCache
                    sret_if(tmp->prev ==NULL);
                    tmp->prev->next=NULL;
                    tailCache=tmp->prev;
                }//else
            }//fi
            else{//tmp==headCache
                if (tmp!=tailCache){
                    sret_if(tmp->next ==NULL);
                    tmp->next->prev=NULL;
                    headCache=tmp->next;
                }//fi
                else{//tmp==tailCache
                    headCache=NULL;//just emptied the cache huh?!
                    tailCache=NULL;
                }//else
            }//else

//TESTME:  (mind debugged, seems ok)
            free(tmp->data);delete tmp;
            --numCachedRecords;
            break;//from while
        }
        tmp=tmp->prev;
    }//while
    //so we're ok to add the new record , at tail
    sret_ifnot( absAddRecord2Cache(typ,recno,from) );
    ret_ok();
}

reterrt nicefi::absAddRecord2Cache(const stat_type typ, const long recno, const void * from){
//absolute add to tail, don't check for existence!
    cache_record *tmp;
    if (numCachedRecords >= max_numcachedrex){//drop one out
        sret_ifnot(headCache);
        tmp=headCache;
#ifdef EXACT_ERRORS_PLUS_A_BIT_OF_PARANOIA
        sret_if( (tmp->stat != stat_write )&&( tmp->stat != stat_read ) );
        sret_ifnot(tmp->data);//cannot be null
#endif
        if (tmp->stat==stat_write) {//letz write it before we kill it
            sret_ifnot( _absolute_writerec(tmp->recno,tmp->data) );
            //there's no point in marking as read since we're gonna kill it (`tmp')
        }//fi
        //the stat_read records are discarded (obv.!)
#ifdef EXACT_ERRORS_PLUS_A_BIT_OF_PARANOIA
        ret_if( (tmp->next == NULL) && (tailCache !=headCache) );
#endif
        headCache=tmp->next;//we got a new head
        if (tmp->next) tmp->next->prev=NULL;//zbish
        if (tmp==tailCache) tailCache=NULL;
        free(tmp->data);delete tmp;//kill them all ;)
        --numCachedRecords;
    }//fi
    tmp=new cache_record;
    sret_ifnot(tmp);//not allocated?

    tmp->data=malloc(recsize);
    sret_ifnot(tmp->data);//not allocated?
    memcpy(tmp->data,from,recsize);

    tmp->recno=recno;
    tmp->stat=typ;
//making the connections
    tmp->next=NULL;//last item on the list
    tmp->prev=tailCache;//tmp points to old tail, may be NULL
    if (tailCache!=NULL) {
        ret_if(tailCache->next != NULL);
        tailCache->next=tmp;
    }
    if (headCache==NULL) headCache=tmp;


    tailCache=tmp;//new tail

    if ( (typ==stat_write)&&(highest_recno<recno) ) {
        highest_recno=recno;
    }
    ++numCachedRecords;//oh, we added one more

    ret_ok();
}

reterrt nicefi::writerec(const long recno, const void * from){
#ifdef ISOPEN_SAFETY
    sret_ifnot(isopened());
#endif
    sret_ifnot( AddRecord2Cache(stat_write,recno,from) );
    ret_ok();
}

reterrt nicefi::initCache(const long maxrex){
    highest_recno=0;
    numCachedRecords=0;
    headCache=NULL;
    tailCache=NULL;
    max_numcachedrex=maxrex;

    ret_ok();
}

reterrt nicefi::killcache(){//dealloc and stuff
    sret_ifnot( flushwrites() );//write all to be written first

#ifdef EXACT_ERRORS_PLUS_A_BIT_OF_PARANOIA
    sret_if( headCache );
    sret_if( tailCache );
    sret_if( numCachedRecords );//miscalculation?
#endif
    while (headCache != NULL){
        cache_record *tmp=headCache;
        headCache=tmp->next;
        free(tmp->data);
        delete tmp;//the value of tmp remains
#ifdef EXACT_ERRORS_PLUS_A_BIT_OF_PARANOIA
        //checking if tailCache really pointed to last item
        if (!headCache) sret_if( tmp != tailCache );//tmp, the value remained
        //checking if counter is rite
        --numCachedRecords;
        sret_if( numCachedRecords < 0 ); //cannot be if calculated rite
#endif
    }//while
#ifdef EXACT_ERRORS_PLUS_A_BIT_OF_PARANOIA
    sret_if(numCachedRecords >0);
#endif
    headCache=NULL;
    tailCache=NULL;
    numCachedRecords=0;
    highest_recno=0;

    ret_ok();
}

reterrt nicefi::_absolute_readrec(const long recno, void * into){
//recsize bytes
    sret_ifnot(seekto(recno));
    sret_if(recsize != read(fhandle,into,recsize));
    ret_ok();
}

reterrt nicefi::readrec(const long recno, void * into){
#ifdef ISOPEN_SAFETY
    sret_ifnot(isopened());
#endif
    if ( ! absGetRecordFromCache(recno,into) )//copy from cache to `into'
    {//failed above ^  thus recno is not cached
        sret_ifnot( _absolute_readrec(recno,into) );//read it from disk
        sret_ifnot( absAddRecord2Cache(stat_read,recno,into) );//add it to cache
    }
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
    sret_ifnot( killcache() );//autoflushes writes, duh**

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
    sret_ifnot(isopened()); //this is used inside read/write too, from seekto()
#endif
    sret_if(fhandle<=0);//if not open;
    sret_if(recno<=0);//cannot be 0 or less
    sret_if(headersize< 0);
    sret_if(recsize<=0);
#ifdef EXACT_ERRORS_PLUS_A_BIT_OF_PARANOIA
    sret_if(recno>getnumrecords()+1);//attempting to seek +2, no can do!
#endif
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



reterrt nicefi::open(const char * fname, const long header_size,const long rec_size,const long maxcachedrecords){
    sret_if(fhandle>0);//if already open
#ifdef ISOPEN_SAFETY
    sret_if(isopened());
#endif

    sret_if(rec_size<=0);
    sret_if(header_size<0);

    sret_ifnot( initCache(maxcachedrecords) );

    /* open the file */
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

