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
* Description:  
*
****************************************************************************/


#ifndef __EATOM_H
#define __EATOM_H

#include "gdefs.h"

#include "nicef.h"

#include "common.h"

/* PRIVATE DEFINES */
//#define WASINITED_SAFETY //always check if was inited before operating
#undef WASINITED_SAFETY
/* end of PRIVATE DEFINES */

class if_eatom:public nicefi {
#ifdef WASINITED_SAFETY
protected:
    int inited;
#endif
private:
    const long its_recsize;
public:                           
    if_eatom();
    ~if_eatom();
    reterrt init(const char *fname);    
    reterrt getwithID(const eatomID whateatomID, deref_eatomID_type *into);
    reterrt writewithID(const eatomID whateatomID, const deref_eatomID_type *from);
    eatomID find_eatom(deref_eatomID_type *into,const basic_element searchme);
    eatomID find_basic_element_and_ret_eatomID(const basic_element what2search);
    eatomID addnew(const deref_eatomID_type *from);
    long howmany();
    reterrt shutdown();
    void compose(
        deref_eatomID_type *into,
        atomID ptrback2atomID_for_faster_search_when_single,
        eatoms_listID ptr2list,
        basic_element basicelementdata
    /*one might realize that we don't need to store basic_element, we could
    interpret the eatomID as being the char, as char(eatomID - 1); however the
    other, ptr2list item must be stored.
      Also we would require that we'd have all eatomIDs preallocated in file,
    since adding a new #200 ID would require that all #1..#199 IDs be present
    since we're using seek and eatomID is the recnum
      The way the interface is rite now, we could add only #20 and #214 for
    example, which is what's intended and considered ideal. However to find
    the basic element #214 in a bunch of 64000 added and uniq basic elements
    would require significant time.
    */
    );
#ifdef WASINITED_SAFETY
private:
    int wasinited() const { if (inited==_yes_) return _yes_; return _no_; }
    void setinited(){ inited=_yes_; };
    void setdeinited(){ inited=_no_; };
#endif
    
};//class



#endif
