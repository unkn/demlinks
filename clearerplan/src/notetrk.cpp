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
* Description: provides FIFO <note tracking> capabilities at source level
* consider note ~ notification
****************************************************************************/



#include <stdlib.h> //for the NULL macro

#include "notetrk.h"


/* constructor */
TNotify::TNotify():
        fHead(NULL),
        fTail(NULL)
{
        SetOkInternally();
        SetNoNotes();
}

/* destructor */
TNotify::~TNotify()
{
        /* destroy the list, if not done so */
        if (fHead)
                PurgeThemAll();
}

/* empties the entire list of notifications */
void
TNotify::PurgeThemAll()
{
        /* we go forward until we hit the last element */
        while (fHead) {
                NotifyItem_st *tmp=fHead;
                fHead=fHead->Next;
                /* FIXME: not deallocating pointers contained inside */
                delete tmp;
        }
        fTail=NULL;//fHead is NULL already
        SetNoNotes();
        SetOkInternally();
}

/* one less note in the list, particulary the one that enterd the list first */
void
TNotify::ClearLastNote()
{
        if (fHead) {
                SetLessNotes();
                NotifyItem_st *tmp=fHead;
                fHead=fHead->Next;

                /* FIXME: not deallocating PChar_t types from within */
                delete tmp;
                if (!fHead)
                        fTail=NULL;
        }
}


NotifyItem_st *
TNotify::GetLastNote()
{
/* may return NULL */
        return fHead;
}


/* moves out the item from list, w/o deallocating it
 * it's a job left for the caller
 */
NotifyItem_st *
TNotify::MoveOutNote()
{
/* return NULL if list is empty, otherwise returns a pointer to the item */
        NotifyItem_st *tmp=fHead;
        if (fHead) {
                fHead=fHead->Next;
                if (!fHead)
                        fTail=NULL;
                SetLessNotes();
        }
        return tmp;
}

bool
TNotify::Add2List(NotifyItem_st *a_What)
{
        /* even if we fail something we still have to count this note */
        SetMoreNotes();

        if (!a_What) {/* oops NULL ptr passed to us */
                SetFailedInternally();
                return false;
        }
/* this one we add is at the tail so it points to no more items afterwards */
        a_What->Next=NULL;
        if (fTail)
                fTail->Next=a_What;
        else /* fTail is NULL then so must fHead be NULL */
                fHead=a_What; /* so we only got one item */

        fTail=a_What; /* always pointing to last item in list */
        return true;
}

bool
TNotify::AddNote(const NotifyItem_st &a_NewNote)
{
/* parameter must already be allocated */

        NotifyItem_st *tmp=new NotifyItem_st;
        if (!tmp) /* awwww how sweet, we failed to get some private space */
                goto ifailed;

        *tmp=a_NewNote;//copy data
        /* FIXME: oops the areas where PChar_t values point to, aren't copied */

/* except that the Depth must be corrected by us 'cause we compute the Depth */
        tmp->Contents.Depth=GetNumNotes();

        /* this function does SetMoreNotes() */
        return Add2List(tmp);/* most always returns true */

ifailed:
        /* we still have to count the number of encountered notes */
        SetMoreNotes();

        /* we did fail internally, let's signal that */
        SetFailedInternally();
        return false;
}



bool
TNotify::AddUserNote(
        const NotifyType_t a_NotifyType,
        PChar_t a_Desc,
        File_t a_FileName,
        Func_t a_Func,
        const Line_t a_Line)
{
        /* static or smth */
        NotifyItem_st tmp;

        /* making a copy for us */
        tmp.Contents.Depth=GetNumNotes();
        tmp.Contents.Type=a_NotifyType;
        tmp.Contents.Line=a_Line;

        /* FIXME: just exchanging pointer values here
         * so the user must either provide "strings" on the function call
           (this is what's intended, since we're using __FILE__ and so macros)
         * or we must alloc these, with the risk of not having enough mem
           (but we may fail the alloc anyways, when adding the note item to the
           list)
         */
        tmp.Contents.File=a_FileName;
        tmp.Contents.Func=a_Func;
        tmp.Contents.UserDesc=a_Desc;

        return AddNote(tmp);/* may fail internally */
}


