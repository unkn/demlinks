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
* Description: provides FIFO notification tracking capabilities at source level
* ie. error tracking
*
****************************************************************************/


#ifndef NOTETRK__H__
#define NOTETRK__H__


typedef char * PChar_t;

/* the type used to represent __LINE__ macro, provided that it is passed, by the
 * user, to the proper function as a parameter. */
typedef unsigned int Line_t;
typedef PChar_t File_t;//__FILE__
typedef PChar_t Func_t;//__func__

/* this type is used to count the notes ie. one, two, three, ten, eleven...;) */
typedef int Counter_t;
/* who said anything about base 4 ? :> */

/* ie. constants defined in derived class,like WARN, ERR, DEBUG */
typedef int NotifyType_t;

struct NotifyData_st {

/* the type or */
        NotifyType_t Type;

/* the depth level where the notification ocurred */
/* ie. a function called another function which returned an error(depth=1),
then the former function returns an error, of depth=2, to the main program.
*/
        int Depth;

/* name of the source file where the notification(or error) occured */
        File_t      File;//__FILE__

/* function name */
        Func_t      Func;//__func__

/* line number */
        Line_t      Line;//__LINE__

/* eventually the contents of the line which caused the notif, or a msg */
        PChar_t     UserDesc;//description
};

/*
 a simple chained list, you'll see below that a head and a tail is kept as
 pointers to first respectively to last element in the list
*/
struct NotifyItem_st {//FIFO (first in, first out) ie. not stack!

/* this points to the next element in this list */
        NotifyItem_st *Next;

/* the actual data about the notify is kept inhere */
        NotifyData_st Contents;
};

class TNotify {
/* a FIFO <notify tracking> class support */
/* items are added at the tail and removed from the head of the list*/
private:

/* points to first notify that entered the list, the one that will be
 * pop-ed OUT from the list */
        NotifyItem_st *fHead;

/* notify items are pushed IN from the tail */
/* ie. last notify that happened(the most recent one) is pointed by this*/
        NotifyItem_st *fTail;

/*
set while something such as mem alloc failed, in internal functions
*/
        int fInternalFailed;

protected:
        /* keeps count of how many notifications are in the list */
        Counter_t fHowMany;
public:
        TNotify();
        virtual ~TNotify();
        /* adds a new notification to the list with data provided by params */
        bool AddUserNote(
                        const NotifyType_t a_NotifyType,
                        PChar_t a_Desc,
                        File_t a_FileName,
                        Func_t a_Func,
                        const Line_t a_Line);

        /* adds a new notification to list with predefined contents */
        /* mostly used internally */
        bool AddNote(const NotifyItem_st &a_NewNote);


        /* returns pointer to the last notification, or NULL if list is empty */
        /* moves the first notification from the list outside into user space,
         * that's removing the link from list, but not deallocating it
         * the user should deallocate as necessary */
        NotifyItem_st * MoveOutNote();

        /* returns pointer to the last notification, or NULL if list is empty */
        /* last notification is the first one put in the list when the list was
           empty.
         * This is _NOT_ to be deallocated by the caller, like MoveOutNote()*/
        NotifyItem_st * GetLastNote();

        /* drops in vain the last note (this is at head of the list, FIFO re?!!)
         * ie. like never happend */
        void ClearLastNote();

        /* empties the list of notes unconditionally ie. clear all notes */
        void PurgeThemAll();

        bool HasFailedInternally(){
                if (fInternalFailed==true)
                        return true;
                return false;
        };

        /* how many notifications are in list */
        Counter_t GetNumNotes(){ return fHowMany; };

private:
        bool Add2List(NotifyItem_st *a_What);

        /* accessor functions for counting/retrieving the number of notes */
        void SetNoNotes(){ fHowMany=0; };
        void SetLessNotes(){ fHowMany--; };
        void SetMoreNotes(){ fHowMany++; };

        void SetFailedInternally(){ fInternalFailed=true; };
        void SetOkInternally(){ fInternalFailed=false; };

};/* class TNotify */

#endif
