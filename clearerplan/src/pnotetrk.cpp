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
* Description: provides personalized FIFO notify tracking capab@source level
*
****************************************************************************/

#include <stdlib.h>
#include <stdio.h>

#include "pnotetrk.h"

/* global uniq variable, this is where we keep all errors
 * this is used in almost all source files */
MNotifyTracker *gNotifyTracker;

/*  the textual descriptions of notification types
 *  ie. "WARN" */
const PChar_t kNotifyDescriptions[kNumNotifyTypes]={
        "NONE",
        "WARN",
        "ERR",
        "INFO",
        "ProgrammingERR", /* most prolly developer's fault */

        /* triggered by a paranoid check which should always be false */
        "FATAL"
};


/* constructor */
MNotifyTracker::MNotifyTracker()
{
}

/* destructor
 * FIXME: it doesn't seem to be called when doing a `return' from main() */
MNotifyTracker::~MNotifyTracker()
{
        /* show them all if we didn't got the chance */
        if (GetLastNote())
                ShowAllNotes();
}

/* adds a notification and checks to see if we failed to properly add it
 * if so displays a message and aborts the running program */
void
CheckedAddNote(
        const NotifyType_t a_NotifyType,
        PChar_t a_Desc,
        File_t a_FileName,
        Func_t a_Func,
        const Line_t a_Line)
{

        bool tmpres=gNotifyTracker->AddUserNote(a_NotifyType,
                                                a_Desc,
                                                a_FileName,
                                                a_Func,
                                                a_Line);

        if ((tmpres==false) && (gNotifyTracker->HasFailedInternally())) {

                fprintf(stderr,
                        "The NotifyTracker has failed internally.\n"
                        "This is perhaps due to lack of memory.\n"
                        "However we're going to try and show you %d"
                        "notifications before aborting...\n",
                        gNotifyTracker->GetNumNotes());

                gNotifyTracker->ShowAllNotes();

                abort();
        }

}

/* show a list of all notifications that happened since last time the list was
 * empty */
void
MNotifyTracker::ShowAllNotes()
{
        NotifyItem_st *tmp=GetLastNote();
        while (tmp){
                fprintf(stderr,
                        "%s#%d: `%s' at line %u in file %s\n\tfunc: %s\n",
                        kNotifyDescriptions[tmp->Contents.Type],
                        tmp->Contents.Depth,
                        tmp->Contents.UserDesc,
                        tmp->Contents.Line,
                        tmp->Contents.File,
                        tmp->Contents.Func);
                ClearLastNote();
                tmp=GetLastNote();
        }
}


void
InitNotifyTracker()
{
        gNotifyTracker=new MNotifyTracker;
        if (!gNotifyTracker) {
                fprintf(stderr,
                        "error allocating memory\n"
                        "in file %s at line %d in func %s\n",
                        __FILE__,
                        __LINE__,
                        __func__);
                abort();
        }
}

void
ShutDownNotifyTracker()
{
        if (gNotifyTracker) {
                if (gNotifyTracker->GetLastNote() != NULL)
                        gNotifyTracker->ShowAllNotes();
                delete gNotifyTracker;
                gNotifyTracker=NULL;
        }
}

void
PurgeAllNotifications()
{
        if (gNotifyTracker)
                gNotifyTracker->PurgeThemAll();
}

void
ShowAllNotifications()
{
        if (gNotifyTracker)
                gNotifyTracker->ShowAllNotes();
}

