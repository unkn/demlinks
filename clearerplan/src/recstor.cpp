/****************************************************************************
*
*                             dmental links
*    Copyright (c) 28 Feb 2005 AtKaaZ, AtKaaZ at users.sourceforge.net
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
* Description:  implements an interface between records of any (but fixed) size
*               stored within a file and record numbers.
*               Records are identified by a number, the record number.
*
****************************************************************************/


#include <string.h> //for memcpy
#include <stdlib.h>
#include <fcntl.h>
#include <sys/stat.h>

#ifdef __WATCOMC__

#       include <io.h>

#       ifndef O_NDELAY
#               define O_NDELAY 0
#       endif

#else /* linux ? */
#       include <unistd.h>

#       ifndef O_BINARY
#               define O_BINARY 0
#       endif


#endif

#include "_gcdefs.h" /* first */

/* personalized notification tracking capabilities */
#include "pnotetrk.h" /* not included in recstor.h ~ not needed there */

#include "recstor.h"


#ifndef __WATCOMC__

#       ifndef filelength
        /* there's no predefined filesize function with clibs , or so... */

/* returns the size in bytes of the already opened file; used internally */
FileSize_t
filelength(FileHandle_t a_FileHandle)
{
        struct stat tmpStat;
        ERR_IF( fstat(a_FileHandle, &tmpStat),
                        return kInvalidFileOffset);
        return tmpStat.st_size;
}

#       endif /* filelength */

#endif /* no watcom compiler, thus we assumed gcc */

/* returns the number of records (counting those from cache too) that "were"
 * written */
RecNum_t
TRecordsStorage::GetNumRecords()
{
        LAME_PROGRAMMER_IF(!IsOpen(),
                        return kInvalidRecNum);

        FileSize_t fileSize = filelength(fFileHandle);
        ERR_IF(fileSize < 0,
                        return kInvalidRecNum);

        RecNum_t retRecNum = Convert_FileOffset_To_RecNum(fileSize);
        PARANOID_IF(!Invariants(retRecNum),
                        return kInvalidRecNum);

        if (IsCacheEnabled()) { /* is cache enabled? */
                /* return the bigger of the two */
                if (retRecNum < fHighestRecNum)
                        retRecNum=fHighestRecNum;

                PARANOID_IF(!Invariants(retRecNum),
                        return kInvalidRecNum);
        }

        return retRecNum;
}

/* unconditionally writes record data, bypassing the cache
 * supposed to be called only internally */
bool
TRecordsStorage::AbsolutelyWriteRecord(
        const RecNum_t a_RecNum,
        const void * a_MemSource)
/* writes fRecSize bytes from a_MemSource, to the specified record (located by
 * a_RecNum) */
{
        ERR_IF( ! FileSeekToRecNum(a_RecNum),
                        return false);

        /* checking if we did indeed wrote as many bytes as we intended */
        ERR_IF(fRecSize != write(fFileHandle, a_MemSource, fRecSize),
                        return false);
        return true;
}

/* writes to file all the records that were supposed to have been written */
bool
TRecordsStorage::FlushWrites()
/* write all kState_Written records FROM cache, keep them in cache but mark them
   as kState_Read */
{
        if (!IsCacheEnabled()) /* is cache DISabled? */
                return true;

        LAME_PROGRAMMER_IF(!IsOpen(),
                        return false)

        /* well we're gonna parse the cache and write all records marked as
         * written, since they haven't been written to disk yet */
        CacheItem *iterator;
        iterator=fCacheHead;
        while (iterator){

                if (iterator->State == kState_Written) {

                        ERR_IF( !AbsolutelyWriteRecord(iterator->RecNum,
                                                iterator->Data),
                                        return false);

                        /* mark it as read, we won't write it again next time,
                         * instead we'll read it(from cache) */
                        iterator->State = kState_Read;
                }

                iterator = iterator->Next;

                --fNumCachedRecords;
        }
        fCacheHead = NULL;
        fCacheTail = NULL;
        return true;
}

/* retrieves record identified by the specified recordnumber, from cache,
 * without performing extra checks */
bool
TRecordsStorage::AbsolutelyGetRecordFromCache(
                const long a_RecNum,
                void * a_MemDest)
/* only used internally */
{
        if (!IsCacheEnabled())
                return false; /* nothing to get, cached was disabled */

        /* refusing to check weather a_MemDest != NULL, counting the fact that
         * we do call this internally */

        CacheItem *iter=fCacheTail;
        while (iter != NULL){
                if (iter->RecNum == a_RecNum){//is it this one?
                        memcpy(a_MemDest, iter->Data, fRecSize);
                        break;//exit while
                }

                iter = iter->Prev;
        }
        if (!iter) return false;
        return true;
}

/* performs additional checks before adding the record to the cache */
bool
TRecordsStorage::AddRecordToCache(
                const EItemState_t a_State,
                const RecNum_t a_RecNum,
                const void * a_MemSource)
{
/* check for existenz before adding
 * any existing a_RecNum is too old to be considered,
   since this one has the same a_RecNum.*/



        RecNum_t numRec;

        ERR_IF((numRec = GetNumRecords()) == kInvalidRecNum,
                        return false);

        LAME_PROGRAMMER_IF(a_RecNum - numRec > 1,
                        return false);

        CacheItem *iter=fCacheTail;//from tail up
        while (iter != NULL) {
                if (iter->RecNum == a_RecNum){
                //nasty IFs
                        if (iter != fCacheHead) {
                                if (iter != fCacheTail) {
                                        PARANOID_IF(iter->Prev == NULL,
                                                return false);
                                        iter->Prev->Next = iter->Next;

                                        PARANOID_IF(iter->Next == NULL,
                                                return false);
                                        iter->Next->Prev = iter->Prev;
                                }
                                else{//iter==fCacheTail
                                        PARANOID_IF(iter->Prev == NULL,
                                                return false);
                                        iter->Prev->Next = NULL;
                                        fCacheTail = iter->Prev;
                                }//else
                        }//fi
                        else{//iter==fCacheHead
                                if (iter != fCacheTail){
                                        PARANOID_IF(iter->Next == NULL,
                                                return false);
                                        iter->Next->Prev = NULL;
                                        fCacheHead = iter->Next;
                                }
                                else{//iter==fCacheTail
                                        fCacheHead = NULL;
                                        fCacheTail = NULL;
                                }
                        }//else

                        free(iter->Data);
                        delete iter;
                        --fNumCachedRecords;
                        break;//from while
                }//if

                iter = iter->Prev;
        }//while

        //so we're ok to add the new record , at tail
        ERR_IF( !AbsolutelyAddRecordToCache(a_State, a_RecNum, a_MemSource),
                        return false);

        return true;
}


/* adds a record to cache, without performind extra checks
 * supposed to be used only internally */
bool
TRecordsStorage::AbsolutelyAddRecordToCache(
                const EItemState_t a_State,
                const long a_RecNum,
                const void * a_MemSource)
        //absolute add to tail, don't check for existence!
{
        /* however there is one extra check that needs to be done
         * a_RecNum - GetNumRecords() must not be above 1 */
        PARANOID_IF(fNumCachedRecords > fMaxNumCachedRecords,
                        return false);

        CacheItem *iter;
        if (fNumCachedRecords == fMaxNumCachedRecords){
                /* the cache is full: drop out one item, the elder */

                PARANOID_IF(!fCacheHead,
                                return false);
                iter=fCacheHead;

                PARANOID_IF(!iter->Data,
                                return false);//cannot be null
                if (iter->State == kState_Written) {
                        //letz write it before we kill it
                        ERR_IF( !AbsolutelyWriteRecord(iter->RecNum,iter->Data),
                                        return false);
                        /* there's no point in marking as read
                         * since we're gonna kill it (`iter') */
                }
                /* items from head of the list(cache) are older so we drop one
                 * out */

                PARANOID_IF((iter->Next == NULL) && (fCacheTail != fCacheHead),
                                return false);

                fCacheHead = iter->Next;//we got a new head
                if (iter->Next)
                        iter->Next->Prev = NULL;
                if (iter == fCacheTail)
                        fCacheTail = NULL;
                free(iter->Data);
                delete iter;//kill them all ;)
                --fNumCachedRecords;
        }//fi
        iter = new CacheItem;
        ERR_IF(!iter,
                        return false);//not allocated?

        iter->Data = malloc(fRecSize);
        ERR_IF(!iter->Data,
                        return false);//not allocated?
        memcpy(iter->Data, a_MemSource, fRecSize);

        iter->RecNum = a_RecNum;
        iter->State = a_State;

        //making the connections
        iter->Next = NULL;//last item on the list
        iter->Prev = fCacheTail;//iter points to old tail, may be NULL

        /* oh so we already got a tail */
        if (fCacheTail != NULL) {
                /* if this really is the tail then it has no next */
                PARANOID_IF(fCacheTail->Next != NULL,
                                return false);

                fCacheTail->Next = iter;
        }

        /* head is not set yet? */
        if (fCacheHead == NULL)
                fCacheHead=iter;


        fCacheTail = iter;//new tail

        /* keep track of the last written element, since last written means
         * that's how many records we have, and we need to know how many records
         * we have to compute the next record's ID , in those program that make
         * use of our functions */
        if ((a_State == kState_Written) && (fHighestRecNum < a_RecNum)) {
                fHighestRecNum = a_RecNum;
        }

        ++fNumCachedRecords;

        return true;
}

/* writes data of a record into specified place; in other words
 * write the specified record (identified by the specified record number)
 */
bool
TRecordsStorage::WriteRecord(
                const RecNum_t a_RecNum,
                const void * a_MemSource)
{
        LAME_PROGRAMMER_IF(!IsOpen(),
                        return false);

        LAME_PROGRAMMER_IF(!a_MemSource,
                        return false);

        PARANOID_IF(!Invariants(a_RecNum),
                        return false);

        if (IsCacheEnabled()) { /* is cache enabled? */
                ERR_IF(!AddRecordToCache(kState_Written, a_RecNum, a_MemSource),
                                return false);
        } else { /* no cache */
                ERR_IF(!AbsolutelyWriteRecord(a_RecNum, a_MemSource),
                                return false);
        }

        return true;
}

bool
TRecordsStorage::InitCache(
                const RecNum_t a_MaxNumRecordsToBeCached)
{
        /* situation with a_MaxRecordsToBeCached <= 0 is handled in every cache
           function ~ not to worry ~ oh and this means cache is disabled */

        /* there should be at least two records cached
         * because there may be some flaws in the implementation and stupid
         * errors could be trigered if there's just one cached record (or 0) */
        LAME_PROGRAMMER_IF(a_MaxNumRecordsToBeCached == 1,
                        return false);

        FlatenCacheVariables();

        fMaxNumCachedRecords = a_MaxNumRecordsToBeCached;

        return true;
}

bool
TRecordsStorage::KillCache()
{
        if (!IsCacheEnabled()) /* is cache DISabled? */
                return true; /* we silently ignore it */

        ERR_IF( !FlushWrites(),
                        return false);//write all to be written first

        PARANOID_IF( fCacheHead ,
                        return false);
        PARANOID_IF( fCacheTail ,
                        return false);
        PARANOID_IF( fNumCachedRecords,//miscalculation?
                        return false);

        while (fCacheHead != NULL){
                CacheItem *iter = fCacheHead;
                fCacheHead = iter->Next;
                free(iter->Data);
                delete iter;//the value of iter remains
                PARANOID_IF( !iter,//should remain!
                                return false);
                //checking if fCacheTail really pointed to last item
                if (!fCacheHead)
                        PARANOID_IF( iter != fCacheTail,//iter's value remained!
                                        return false);
                --fNumCachedRecords;
                //checking if counter was right
                PARANOID_IF( fNumCachedRecords < 0,
                                return false);
        }//while

        PARANOID_IF(fNumCachedRecords > 0,
                        return false);

        FlatenCacheVariables();
        return true;
}

/* called in two places , before init and after kill ~cache */
void
TRecordsStorage::FlatenCacheVariables()
{
    fCacheHead=NULL;
    fCacheTail=NULL;
    fNumCachedRecords=0;
    fHighestRecNum=0;
}

/* reads the record from file directly into mem */
bool
TRecordsStorage::AbsolutelyReadRecord(
                const RecNum_t a_RecNum,
                void * a_MemDest)
{
        ERR_IF( !FileSeekToRecNum(a_RecNum),
                        return false);
        ERR_IF(fRecSize != read(fFileHandle, a_MemDest, fRecSize),
                        return false);
        return true;
}

bool
TRecordsStorage::ReadRecord(
                const RecNum_t a_RecNum,
                void * a_MemDest)
{
        LAME_PROGRAMMER_IF(!IsOpen(),
                        return false);

        LAME_PROGRAMMER_IF(!a_MemDest,
                        return false);

        PARANOID_IF(!Invariants(a_RecNum),
                        return false);

        if ( ! AbsolutelyGetRecordFromCache(a_RecNum, a_MemDest) ) {
                /* failed above ^ thus record with number a_RecNum is not cached
                   OR cache is DISabled
                 * read it from file */
                ERR_IF( ! AbsolutelyReadRecord(a_RecNum, a_MemDest),
                                return false);

                if (IsCacheEnabled()) { /* cache is ON */
                        /* add it to cache */
                        ERR_IF( ! AbsolutelyAddRecordToCache(
                                                kState_Read,
                                                a_RecNum,
                                                a_MemDest), /* here is Source */
                                        return false);
                }
        }

        return true;
}


TRecordsStorage::TRecordsStorage():
        fFileHandle(-1),
        fRecSize(0),
        fHeaderSize(-1)
{
}

TRecordsStorage::~TRecordsStorage()
{
        if (IsOpen())
                ERR_IF(!Close(),);
}

bool
TRecordsStorage::Close()
{
        LAME_PROGRAMMER_IF(!IsOpen(),
                        return false);

        ERR_IF( !KillCache(),
                        return false);//autoflushes writes, if cache is enabled!

        ERR_IF( 0 != ::close(fFileHandle),
                        return false);
        fFileHandle = -1;// u never know, or is it just me? ;)
        return true;
}

RecNum_t
TRecordsStorage::Convert_FileOffset_To_RecNum(
                const FileSize_t a_FileOffset)
/* counting on the fact that this function is used only internally and thus the
   a_FileOffset always represents the offset in the file, just before the record
   begins */
/* RecNum can't be 0, it goes from 1..
 * offset goes from 0..
 * ie.  if offset == fRecSize then RecNum=2  it's past RecNum 1
        if offset == 0 then RecNum=1*/
{
        LAME_PROGRAMMER_IF(!IsOpen(),
                        return kInvalidRecNum);

        ERR_IF(a_FileOffset < 0,
                        return kInvalidRecNum);

        FileSize_t ofsMinusHeader= (a_FileOffset - fHeaderSize);

        /* the header data might not be written, thus filesize would be less
           than headersize, FIXME: make a bool var weather headerdata was or not
           written
         * the programmer/user must call WriteHeader() if fHeaderSize > 0
           or else we "can't quite seek" after it ~ actually we could :P */
        LAME_PROGRAMMER_IF(ofsMinusHeader < 0,
                        return kInvalidRecNum);

        //this shouldn't be != 0 , if it is, the passed ofs is wrong, '
        //  and perhaps the error is above: to the caller!
        PARANOID_IF( ( ofsMinusHeader % fRecSize ) != 0,
                        return kInvalidRecNum);

        RecNum_t retRecNum=( ( ofsMinusHeader / fRecSize ) +1 );

        PARANOID_IF(!Invariants(retRecNum),
                        return kInvalidRecNum);

        return retRecNum;
}

FileSize_t
TRecordsStorage::Convert_RecNum_To_FileOffset(
                const RecNum_t a_RecNum)
{
/* recnum goes from 1..
 * ofs goes from 0..
 */
        LAME_PROGRAMMER_IF(!IsOpen(),
                        return kInvalidFileOffset);

        PARANOID_IF(!Invariants(a_RecNum),
                        return kInvalidFileOffset);

        FileSize_t retFileOffset = (fHeaderSize + ((a_RecNum - 1) * fRecSize));
        PARANOID_IF(retFileOffset <= 0,
                        return kInvalidFileOffset);

        return retFileOffset;
}

#if defined(PARANOID_CHECKS)
/* things that must always be true */
bool
TRecordsStorage::Invariants(
                const RecNum_t a_RecNum)
{
        /* for a better understanding of error messages */
        PARANOID_IF(a_RecNum == kInvalidRecNum,
                        return false);

        /* safety */
        PARANOID_IF(a_RecNum < kFirstRecNum,
                        return false);

        PARANOID_IF(fHeaderSize < 0,
                        return false);

        PARANOID_IF(fRecSize <= 0,
                        return false);

        return true;
}
#endif /* PARANOID_CHECKS */

bool
TRecordsStorage::FileSeekToRecNum(
                const RecNum_t a_RecNum)
{
        LAME_PROGRAMMER_IF(!IsOpen(),
                        return false);

        PARANOID_IF( ! Invariants(a_RecNum) ,
                        return false);

        /* attempting to seek +2 beyond the last record, no can do!
         * I mean seeking +1 beyond last is done before appending, but we cannot
           allow gaps, we need all RecNum to be consecutive, so as stated before
           can't jump from RecNum 28 to RecNum 30, without having a RecNum 29
        */
        RecNum_t tmpNumRecordsNow = GetNumRecords();
        PARANOID_IF(tmpNumRecordsNow == kInvalidRecNum,
                        return false);
        PARANOID_IF(a_RecNum >= 2+tmpNumRecordsNow,
                        return false);

        FileSize_t exactOffset=Convert_RecNum_To_FileOffset(a_RecNum);

        PARANOID_IF(exactOffset < fHeaderSize,
                        return false);

        ERR_IF( exactOffset != lseek(fFileHandle, exactOffset, SEEK_SET),
                        return false);

        return true;
}

/* write the data of the header from memory to disk,
 * the fHeaderSize is the size of the written block and it was initialized on
 * Open(...) */
bool
TRecordsStorage::WriteHeader(
                const void * a_MemSource)
{ /* this doesn't use cache... *duh* */

        LAME_PROGRAMMER_IF(!IsOpen(),
                        return false);

        LAME_PROGRAMMER_IF( fHeaderSize == 0,
                        return false);

        LAME_PROGRAMMER_IF( a_MemSource == NULL,
                        return false);

        PARANOID_IF(!Invariants(kFirstRecNum),
                        return false);

        ERR_IF(0L != lseek(fFileHandle, 0L, SEEK_SET),
                        return false);
        ERR_IF(fHeaderSize != write(fFileHandle, a_MemSource, fHeaderSize),
                        return false);

        return true;
}

/* reads the file header from file into the specified memory location
 * it must be preallocated */
bool
TRecordsStorage::ReadHeader(
                void * a_MemDest)
{
        LAME_PROGRAMMER_IF(!IsOpen(),
                        return false);

        LAME_PROGRAMMER_IF(a_MemDest == NULL,
                        return false);

        LAME_PROGRAMMER_IF(fHeaderSize == 0,
                        return false);

        PARANOID_IF(!Invariants(kFirstRecNum),
                        return false);

        ERR_IF(0L != lseek(fFileHandle, 0L, SEEK_SET),
                        return false);
        ERR_IF(fHeaderSize != read(fFileHandle, a_MemDest, fHeaderSize),
                        return false);

        return true;
}



bool
TRecordsStorage::Open(
                const char * a_FileName,
                const FileSize_t a_HeaderSize,
                const RecSize_t a_RecSize,
                const RecNum_t a_MaxNumRecordsToBeCached)
{
        LAME_PROGRAMMER_IF(IsOpen(),
                        return false);

        fRecSize=a_RecSize;
        fHeaderSize=a_HeaderSize;

        PARANOID_IF(!Invariants(kFirstRecNum),
                        return false);


        /* cache disabling is handled inside, since some variables need to be
           set anyways */
        ERR_IF( !InitCache(a_MaxNumRecordsToBeCached),
                        return false);

        //FIXME: some exclusive bullshit to be done here...
        /* open the file */
        fFileHandle = ::open(
                        a_FileName,
                        O_RDWR | O_CREAT | O_BINARY | O_NDELAY/*| O_DENYWRITE*/,
                        /*SH_DENYWR, this worked with ::sopen() */
                        S_IREAD | S_IWRITE);

        ERR_IF(fFileHandle <= 0,
                        return false);//if open failed
        //TODO: make ERR know about strerr(errno) / perror()


        return true;
}

