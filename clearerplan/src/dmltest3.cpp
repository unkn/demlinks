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
* Description: testing stuff #3
*
****************************************************************************/


#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h> //unlink()

#include "_gcdefs.h"

#include "pnotetrk.h"

#include "recstor.h"

#if defined(__WATCOMC__)
#       define CACHE_THIS_MANY_ITEMS 2048
#else /* linux ? */
#       define CACHE_THIS_MANY_ITEMS kDisableCache
#endif
#define HOW_MANY_ITEMS 20000
#define KEEP_LINES 7
TRecordsStorage *DataBase;

void DataBaseCleanUp()
{
/* destructing it, makes sure that writes are flushed, ie. when records are
 * cached */
        printf("Been here.");
        if (DataBase) {
                printf(".. done that.");
                delete DataBase;
                DataBase = NULL;
        }
        printf("\n");
}
int main()
{
        InitNotifyTracker();

        
        EXIT_IF(!(DataBase = new TRecordsStorage));

        atexit(DataBaseCleanUp);

        long item;
        char headerBuf [200];
        memset(headerBuf,0,sizeof(headerBuf));
        unlink("test.fil");
        EXIT_IF(!DataBase->Open("test.fil",
                                sizeof(headerBuf),
                                sizeof(item),
                                CACHE_THIS_MANY_ITEMS));

        EXIT_IF(!DataBase->WriteHeader(headerBuf));

        EXIT_IF(!DataBase->ReadHeader(headerBuf));

        RecCount_t tmpCount;
        
        for (item = 1; item <= HOW_MANY_ITEMS; item++){
                if (item % (HOW_MANY_ITEMS / KEEP_LINES) == 0)
                        printf("attempting to write item %d\n",item);
                EXIT_IF(!DataBase->WriteRecord(item,&item));
                EXIT_IF((tmpCount = DataBase->GetNumRecords()) == kBadRecCount);
                EXIT_IF(tmpCount != item);
        }
        item = 1;
        while (item <= HOW_MANY_ITEMS){
                if (item % (HOW_MANY_ITEMS / KEEP_LINES) == 0)
                        printf("attempting to read item %d\n",item);
                EXIT_IF(!DataBase->ReadRecord(item,&item));
                ++item;
        }

        DataBaseCleanUp();/* maybe we want to kill it earlier than exit */
        
        ShutDownNotifyTracker();

        return EXIT_SUCCESS; //exit(EXIT_SUCCESS);
}
