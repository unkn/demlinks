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

        /* the developer used certain statements under a disconnected situation
         * ie. using flush() without having the file opened, or using a NULL
         * pointer, to put data into */
        kNotify_ProgrammingError,

        /* this signals that a paranoid check on a condition was true,
         * if so then is considered fatal ;) since the condition is expected
         * always to be false (*doh* paranoid) */
        kNotify_Paranoid,

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


/* define this before including this header file in your sources
 * if defined, turns on the code that does the checks and executes the
   statements if the checks are true (see below) */
#if defined(PARANOID_CHECKS)

/* adds a paranoia msg to the notify-list if the condition is true
 * this usually means that something is going really wrong and this is like
 * a fatal situation (if the condition is true, of course) */
#define PARANOID_IF(a_ConditionalStatement,a_MoreStatementsIfTrue)  \
{                                                               \
        if ((a_ConditionalStatement)) {                         \
                PARANOID(a_ConditionalStatement)                    \
                { a_MoreStatementsIfTrue; }                     \
        }                                                       \
}

/* always adds a paranoia-notify to the list, with the passed description */
#define PARANOID(a_InfoDescription)                 \
{                                               \
        ADD_NOTE(kNotify_Paranoid,a_InfoDescription)\
}

#else //not defined PARANOID_CHECKS

#define PARANOID_IF(a_blah,a_blahblah) /* nada */
#define PARANOID(a_blah) /* yet again nothing done */

#endif //PARANOID_CHECKS

/* adds an info to the notify-list if the condition is true */
#define INFO_IF(a_ConditionalStatement,a_MoreStatementsIfTrue)  \
{                                                               \
        if ((a_ConditionalStatement)) {                         \
                INFO(a_ConditionalStatement)                    \
                { a_MoreStatementsIfTrue; }                     \
        }                                                       \
}

/* always adds an INFO to the list, with the passed description */
#define INFO(a_InfoDescription)                 \
{                                               \
        ADD_NOTE(kNotify_Info,a_InfoDescription)\
}


/* adds a warning to the notify-list if the condition is true */
#define WARN_IF(a_ConditionalStatement,a_MoreStatementsIfTrue)  \
{                                                               \
        if ((a_ConditionalStatement)) {                         \
                WARN(a_ConditionalStatement)                    \
                { a_MoreStatementsIfTrue; }                     \
        }                                                       \
}

/* always adds a WARN to the list, with the passed description */
#define WARN(a_WarnDescription)                 \
{                                               \
        ADD_NOTE(kNotify_Warn,a_WarnDescription)\
}


/* adds an error to the notify-list if the condition is true */
#define ERR_IF(a_ConditionalStatement,a_MoreStatementsIfTrue)   \
{                                                               \
        if ((a_ConditionalStatement)) {                         \
                ERR(a_ConditionalStatement)                     \
                { a_MoreStatementsIfTrue; }                     \
        }                                                       \
}

/* always adds an error to the list, with the passed description */
#define ERR(a_ErrorDescription)                 \
{                                               \
        ADD_NOTE(kNotify_Err,a_ErrorDescription)\
}

#if defined(CHECK_FOR_LAME_PROGRAMMERS)
/* adds a programming-error to the notify-list if the condition is true
   and optionally executes more statements if so
 * this should only happen if the programmer misused some statements or forgot
 * something such as openning the file before writing to it */
#define LAME_PROGRAMMER_IF(a_ConditionalStatement,a_MoreStatementsIfTrue)  \
{                                                               \
        if ((a_ConditionalStatement)) {                         \
                LAME_PROGRAMMER(a_ConditionalStatement)                    \
                { a_MoreStatementsIfTrue; }                     \
        }                                                       \
}

/* always adds a programming-error to the list, with the passed description */
#define LAME_PROGRAMMER(a_ErrorDescription)                 \
{                                               \
        ADD_NOTE(kNotify_ProgrammingError,a_ErrorDescription)\
}
#else //not defined:

#define LAME_PROGRAMMER_IF(a_blah,a_blahblah) /* refusing to do anything */
#define LAME_PROGRAMMER(a_blah) /* nothing here */

#endif //CHECK_FOR_LAME_PROGRAMMERS


/* adds a notification to the list, generic use */
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
void ShowAllNotifications();


#endif

