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


#ifndef __EA_LIST_H
#define __EA_LIST_H

#include "gdefs.h"

#include "nicef.h"

#include "common.h"

/* PRIVATE DEFINES */
//#define WASINITED_SAFETY //always check if was inited before operating
/* end of PRIVATE DEFINES */

class if_eatoms_list:public nicefi {
#ifdef WASINITED_SAFETY
protected:
    int inited;
#endif
private:                       
    const long its_recsize;      
public:                           
    if_eatoms_list();                    
    ~if_eatoms_list();                    
    reterrt init(const char *fname);    
    reterrt getwithID(const eatoms_listID whateatoms_listID, deref_eatoms_listID_type *into);
    reterrt writewithID(const eatoms_listID whateatoms_listID, const deref_eatoms_listID_type *from);
    long addnew(const deref_eatoms_listID_type *from); 
    long howmany();
    reterrt shutdown(); 
    void compose(
        deref_eatoms_listID_type *into,
        const eatomslist_itemID ptr2head
    );
#ifdef WASINITED_SAFETY
private:
    int wasinited() const { if (inited==_yes_) return _yes_; return _no_; }
    void setinited(){ inited=_yes_; };
    void setdeinited(){ inited=_no_; };
#endif
};//class


#endif
