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

#include "dmlcore.h"

#if defined(__WATCOMC__)
#       define CACHE_THIS_MANY_ITEMS 2048
#endif
#       define HOW_MANY_ITEMS 2000

#define KEEP_LINES 7

// FIXME: clean-up on Ctrl-C

MElemental *MyElementals;

void MyElementalsCleanUp()
{
/* destructing it, makes sure that writes are flushed, ie. when records are
 * cached */
        printf("Been here.");
        if (MyElementals) {
                printf(".. done that.");
                ERR_IF(!MyElementals->DeInit(),);
                delete MyElementals;
                MyElementals = NULL;
        }
        printf("\n");
}
int main()
{
        InitNotifyTracker();


        EXIT_IF(!(MyElementals = new MElemental));

        atexit(MyElementalsCleanUp);

        ElementalID_t element;
        unlink("elements.dat");
        EXIT_IF(!MyElementals->Init("elements.dat"));

#ifdef __WATCOMC__
        EXIT_IF(!MyElementals->InitCache(CACHE_THIS_MANY_ITEMS));
#endif
        printf("Do we have cache? %s\n",
                        MyElementals->IsCacheEnabled()==true?"Yes":"No");

        ElementalID_t tmpCount;
        Elemental_st elemental_s;

        for (element = 1; element <= HOW_MANY_ITEMS; element++){
                if (element % (HOW_MANY_ITEMS / KEEP_LINES) == 0)
                        printf("attempting to write element %d\n",element);
                EXIT_IF(!MyElementals->Compose(
                                        elemental_s,
                                        element % 256,
                                        kNoListID));
                EXIT_IF(kNoElementalID ==
                                (tmpCount = MyElementals->AddNew(elemental_s)));
                //EXIT_IF(!MyElementals->WriteWithID(element,elemental_s));
                //EXIT_IF(!MyElementals->GetLastID(tmpCount));
                EXIT_IF(tmpCount != element);
        }

        element = 1;
        while (element <= HOW_MANY_ITEMS){
                if (element % (HOW_MANY_ITEMS / KEEP_LINES) == 0)
                        printf("attempting to read element %d\n",element);
                EXIT_IF(!MyElementals->ReadWithID(element,elemental_s));
                ++element;
        }

        MyElementalsCleanUp();/* maybe we want to kill it earlier than exit */

        ShutDownNotifyTracker();

        return EXIT_SUCCESS;
}
