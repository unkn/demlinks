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
* Description: interface between records and storage, also can cache them.
*               RecNum starts from 1
*               record = user data field
*               item = cached record (cached in memory for faster axes)
*
****************************************************************************/


#ifndef RECSTOR__H__
#define RECSTOR__H__
/* file start */


/* the type/size of all represented RecNum numbers
 * also since first RecNum is 1, this type is used to represent the count of
 * records also (ie. how many records are there) */
typedef long RecNum_t; /* must be able to accomodate EFixedRecNumConstants */

/* storage for the largest size in bytes that can be addressed
 * however since filelength() returns a 'long' we also set this to 'long'
 * I wanted unsigned long but some funx return -1,not to talk about the above */
typedef long FileSize_t; /* ranges (on some machines) -2GB..+2GB */

/* in both open watcom C and gcc this is `int' */
typedef int FileHandle_t;

/* the record size range, however only positive numbers are used
 */
typedef FileSize_t RecSize_t;/* -2GB..2GB */


enum EFixedRecNumConstants {

        /* used as return value when something went wrong */
        kInvalidRecNum = 0,

        /* used as the leftmost value of the range ie. in a `for' */
        kFirstRecNum = 1 /* don't change this, there are other limitations! */
};



/* list of item states remember item=cached record*/
typedef enum {
        kState_Read=1,
        kState_Written=2
} EItemState_t;

/* this is an item in the double linked Cache list 
 * items on the cache list are called items ie. item=cached record
 * items hold records but they are not the records ie. record=user data field 
 * an item may also be referd to as 'cache item', and a record may also be 
 * called 'cached item' or 'cached record' note the extra 'd' */
struct CacheItem {

        /* the state of the cached record such as Read or Written */
        EItemState_t State;

        /* represents the record number of the cached record
         * (not of the cache item) */
        RecNum_t RecNum;

        /* the data stored with the record */
        void * Data;//malloc(fRecSize)

      /* the Cache items are linked in chain, one can go to prev or next item */
        CacheItem *Prev;
        CacheItem *Next;
};

/* a class to handle storage/retrieval of fixed size records into a file
 * identifies them by record number
 * limitation is that record numbers must be consecutive ie. cannot have record
 * number 30 without having all records from 1 to 29 already */
class TRecordsStorage {
private:
        /* the handle of the opened file */
        FileHandle_t             fFileHandle;

        /* the size of the header from the file
         * ie. we must skip this many bytes to get to the first record */
        FileSize_t      fHeaderSize;

        /* the record size in bytes, each record has this fixed size */
        RecSize_t       fRecSize;

        /* cache stuff */
        RecNum_t        fNumCachedRecords;//in records
        CacheItem     * fCacheHead;//head of a FIFO unsorted double-linked list
        CacheItem     * fCacheTail;//tail
        RecNum_t        fHighestRecNum;//used with getnumrecords()

        //how many records to cache (ie. don't yet writ'em to disk)
        RecNum_t         fMaxNumCachedRecords;// 1024

public:
        TRecordsStorage();
       ~TRecordsStorage();
        bool FlushWrites();

        /* opens the specified file for as long as we use this class, until we
         * issue a Close() of course */
        bool Open(const char * a_FileName,
                        const FileSize_t a_HeaderSize,
                        const RecSize_t a_RecSize,
                        const RecNum_t a_MaxNumRecordsToBeCached);

        /* closes the opened file, either we're finished with it or we attempt
         * to use open another file after this */
        bool Close();

        /* reads a record from storage(file) into memory */
        bool ReadRecord(
                const RecNum_t a_RecNum, 
                void * a_MemDest);

        /* writes a record from memory to storage(file) */
        bool WriteRecord(
                const RecNum_t a_RecNum,
                const void * a_MemSource);

        RecNum_t GetNumRecords();//how many records are now

        bool WriteHeader(const void * header);
        bool ReadHeader(void * header);

        bool IsOpen(){ return (fFileHandle > 0); };

private:

#if defined(PARANOID_CHECKS) /* only to be defined inside the .cpp file */
        /* returns false if one of the many things that should always be true
         * ...well is not. Ie. RecNum >= kFirstRecNum */
        bool Invariants(const RecNum_t a_RecNum);
#endif

        /* uses lseek() to set the file pointer just before the specified
         * record; this is done before read or write on the spec. record */
        bool FileSeekToRecNum(const RecNum_t a_RecNum);

        FileSize_t Convert_RecNum_To_FileOffset(const RecNum_t a_RecNum);
        RecNum_t Convert_FileOffset_To_RecNum(const FileSize_t a_FileOffset);

        /* unconditionally writes record data, bypassing the cache */
        bool AbsolutelyWriteRecord(
                        const RecNum_t a_RecNum,
                        const void * a_MemSource);

        bool AbsolutelyReadRecord(
                        const RecNum_t a_RecNum,
                        void * a_MemDest);

        bool AbsolutelyAddRecordToCache(
                        const EItemState_t a_State,
                        const RecNum_t a_RecNum,
                        const void * a_MemSource);

        bool AddRecordToCache(
                        const EItemState_t a_State,
                        const RecNum_t a_RecNum,
                        const void * a_MemSource);

        bool AbsolutelyGetRecordFromCache(
                        const long a_RecNum,
                        void * a_MemDest);

        bool KillCache();

        bool InitCache(
                        const RecNum_t a_MaxRecordsToBeCached);

        /* to avoid duplicating some assignement statements in two places */
        void FlatenCacheVariables();
}; /* class */

#endif /* file */
