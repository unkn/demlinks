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
#else
#       define CACHE_THIS_MANY_ITEMS kDisableCache
#endif

#define HOW_MANY_ITEMS 7777

#define KEEP_LINES 7

// FIXME: clean-up on Ctrl-C

MDementalLinksCore *MyDemlinks;

void MyDemlinksCleanUp()
{
/* destructing it, makes sure that writes are flushed, ie. when records are
 * cached */
        printf("Been here.");
        if (MyDemlinks) {
                if (MyDemlinks->IsCacheEnabled()) {
                        printf("Flushing writes.");
                        fflush(stdout);/* not this one */
                        ERR_IF(!MyDemlinks->KillCache(),);
                }
                ERR_IF(!MyDemlinks->DeInit(),);
                delete MyDemlinks;
                MyDemlinks = NULL;
                printf(".. done that.");
        }
        printf("\n");
}

int main()
{
        InitNotifyTracker();


        EXIT_IF(!(MyDemlinks = new MDementalLinksCore));

        atexit(MyDemlinksCleanUp);

        ElementalID_t element;
        unlink("elements.dat");
        unlink("List_R2E.dat");
        EXIT_IF(!MyDemlinks->Init("elements.dat","List_R2E.dat"));

        EXIT_IF(!MyDemlinks->InitCache(CACHE_THIS_MANY_ITEMS));
        printf("Do we have cache? %s\n",
                        MyDemlinks->IsCacheEnabled()==true?"Yes":"No");

        ElementalID_t tmpElementalID;
        BasicElement_t basicElement;

        for (element = 1; element <= HOW_MANY_ITEMS; element++){
                basicElement = (element -1) % 256;

                if (element % (HOW_MANY_ITEMS / KEEP_LINES) == 0)
                        printf("attempting to write element %d\n",element);

                EXIT_IF(kNoElementalID ==
                        (tmpElementalID =
                         MyDemlinks->AddBasicElement(basicElement)) );

                EXIT_IF(tmpElementalID != (basicElement + 1));
        }

        ElementalID_t tmpElemID;
        element = 1;
        while (element <= HOW_MANY_ITEMS){
                basicElement = (element - 1) % 256;

                if (element % (HOW_MANY_ITEMS / KEEP_LINES) == 0)
                        printf("attempting to read element %d\n",element);

                EXIT_IF(kNoElementalID ==
                        (tmpElemID = MyDemlinks->FindBasicElement(
                                        basicElement)));

                EXIT_IF((basicElement + 1) != tmpElemID);
                element++;
        }

        MyDemlinksCleanUp();/* maybe we want to kill it earlier than exit */

        ShutDownNotifyTracker();

        return EXIT_SUCCESS;
}

