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
* Description: provides personalized notify tracking
*
****************************************************************************/


#ifndef PNOTETRK_H__
#define PNOTETRK_H__

#include "notetrk.h"

enum ENotifyTypes {
        kNotify_None=0,
        kNotify_Warn,
        kNotify_Err,
        kNotify_Info,

        /* last in line*/
        kNumNotifyTypes 
};


class MNotifyTracker : public TNotify {
public:
        MNotifyTracker();
       ~MNotifyTracker();

        /* implicitly kills all notifications after routing them to stderr */
        void ShowAllNotes();
};

/* global notify-tracker variable, used everywhere 
 * here we hold the list of encountered notifications during program exec.*/
extern MNotifyTracker *gNotifyTracker;

/* displays all notifications prior to executing and after execution of the
 * passed statements */
#define TRAP(a_BunchOfStatements)               \
{                                               \
        gNotifyTracker->ShowAllNotes();         \
        {                                       \
                a_BunchOfStatements;            \
        }                                       \
        gNotifyTracker->ShowAllNotes();         \
}

/* adds an info to the notify-list if the condition is true */
#define INFO_IF(a_ConditionalStatement)                          \
{                                                               \
        if ((a_ConditionalStatement)) {                         \
                ADD_NOTE(kNotify_Info,a_ConditionalStatement)    \
        }                                                       \
}

/* adds a warning to the notify-list if the condition is true */
#define WARN_IF(a_ConditionalStatement)                          \
{                                                               \
        if ((a_ConditionalStatement)) {                         \
                ADD_NOTE(kNotify_Warn,a_ConditionalStatement)    \
        }                                                       \
}

/* adds an error to the notify-list if the condition is true */
#define ERR_IF(a_ConditionalStatement)                          \
{                                                               \
        if ((a_ConditionalStatement)) {                         \
                ADD_NOTE(kNotify_Err,a_ConditionalStatement)    \
        }                                                       \
}

/* adds a notification to the list */
#define ADD_NOTE(a_NotifyType, a_Cause)                         \
{                                                               \
        CheckedAddNote( a_NotifyType,                           \
                        (char *)#a_Cause,                               \
                        (char *)__FILE__,                               \
                        (char *)__func__,                               \
                        __LINE__);                              \
}

/* adds a notification and checks to see if we failed to properly add it
 * if so displays a message and aborts the running program */
void CheckedAddNote(   
                const NotifyType_t a_NotifyType,
                PChar_t a_Desc,
                File_t a_FileName,
                Func_t a_Func,
                const Line_t a_Line);
                
void ShutDownNotifyTracker();
void InitNotifyTracker();
void PurgeAllNotifications();


#endif

