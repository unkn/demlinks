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
void TNotify::PurgeThemAll(){
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
void TNotify::ClearLastNote(){
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


NotifyItem_st * TNotify::GetLastNote(){
/* may return NULL */
        return fHead;
}


/* moves out the item from list, w/o deallocating it 
 * it's a job left for the caller
 */ 
NotifyItem_st * TNotify::MoveOutNote(){
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

Bool_t TNotify::Add2List(NotifyItem_st *a_What){
        /* even if we fail something we still have to count this note */
        SetMoreNotes();
        
        if (!a_What) {/* oops NULL ptr passed to us */
                SetFailedInternally();
                return kFalse; 
        }
/* this one we add is at the tail so it points to no more items afterwards */
        a_What->Next=NULL;
        if (fTail) 
                fTail->Next=a_What;
        else /* fTail is NULL then so must fHead be NULL */
                fHead=a_What; /* so we only got one item */
    
        fTail=a_What; /* always pointing to last item in list */
        return kTrue;
}

Bool_t TNotify::AddNote(const NotifyItem_st &a_NewNote){
/* parameter must already be allocated */

        NotifyItem_st *tmp=new NotifyItem_st;
        if (!tmp) /* awwww how sweet, we failed to get some private space */
                goto ifailed;

        *tmp=a_NewNote;//copy data
        /* FIXME: oops the areas where PChar_t values point to, aren't copied */

/* except that the Depth must be corrected by us 'cause we compute the Depth */
        tmp->Contents.Depth=GetNotes();
        
        /* this function does SetMoreNotes() */
        return Add2List(tmp);/* most always returns kTrue */

ifailed:
        /* we still have to count the number of encountered notes */
        SetMoreNotes();

        /* we did fail internally, let's signal that */
        SetFailedInternally();
        return kFalse;
}



Bool_t TNotify::AddUserNote(
                const NotifyType_t a_NotifyType, 
                const PChar_t a_Desc,
                const PChar_t a_FileName, 
                const PChar_t a_Func, 
                const Line_t a_Line)
{
        /* static or smth */
        NotifyItem_st tmp;

        /* making a copy for us */
        tmp.Contents.Depth=GetNotes();
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


