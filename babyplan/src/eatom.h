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
    reterrt init(const char *fname, const long MAXCACHEDRECORDS);
    reterrt getwithID(const eatomID whateatomID, deref_eatomID_type &into);
    reterrt writewithID(const eatomID whateatomID, const deref_eatomID_type &from);
    eatomID find_eatom(deref_eatomID_type &into,const basic_element searchme);
    eatomID find_basic_element_and_ret_eatomID(const basic_element what2search);
    eatomID addnew(const deref_eatomID_type &from);
    long howmany();
    reterrt shutdown();
    void compose(
        deref_eatomID_type &into,
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
