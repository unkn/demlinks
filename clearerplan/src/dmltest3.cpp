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
#       include <conio.h>
#       define CACHE_THIS_MANY_ITEMS 2048
#else
#       define kbhit() false
#       define getch() false
#       define CACHE_THIS_MANY_ITEMS kDisableCache
#endif

#define HOW_MANY_ITEMS 777

#define REFRESH_TIME 7 /* ever N items */
#define KEEP_LINES 7

// FIXME: clean-up on Ctrl-C

const char * gMarker="|/-\\|/-\\";

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
        int tmpMarkerPos = 0;

        printf("writting...\n");
        for (element = 1; element <= HOW_MANY_ITEMS; element++){
                basicElement = (element -1) % 256;

                if (element % REFRESH_TIME == 0) {
                        printf("%c",gMarker[tmpMarkerPos]);
                        fflush(stdout);
                        printf("\b");
                        tmpMarkerPos++;
                        if (tmpMarkerPos >= strlen(gMarker))
                                tmpMarkerPos = 0;
                }

                if (element % (HOW_MANY_ITEMS / KEEP_LINES) == 0) {
                        printf(".%d",element);
                        fflush(stdout);
                }

                EXIT_IF(kNoElementalID ==
                        (tmpElementalID =
                         MyDemlinks->AddBasicElement(basicElement)) );

                EXIT_IF(tmpElementalID != (basicElement + 1));
                EXIT_IF((kbhit()) && (getch() == 27));
        }
        printf("\nreading...\n");

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

