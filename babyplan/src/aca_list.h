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
* Description: this list points to head item of a list of atomIDs. Those
* atomIDs are atoms that point to acatoms, particulary they can only be
* acatoms since only acatoms can refer to other atoms.
* But this list represents the list of those atomIDs which refer to US.
* US being the acatoms.
*
****************************************************************************/


#ifndef __ACA_LIST_H
#define __ACA_LIST_H

#include "gdefs.h"

#include "nicef.h"

#include "common.h"


class if_acatoms_list:public nicefi {
private:                       
    int opened;                 
    const long its_recsize;      
public:                           
    if_acatoms_list();                    
    ~if_acatoms_list();                    
    reterrt init(const char *fname, const long MAXCACHEDRECORDS);
    reterrt getwithID(const acatoms_listID whatacatoms_listID, deref_acatoms_listID_type &into);
    reterrt writewithID(const acatoms_listID whatacatoms_listID, const deref_acatoms_listID_type &from);
    acatoms_listID addnew(const deref_acatoms_listID_type &from);
    long howmany();
    reterrt shutdown(); 
    void compose(
        deref_acatoms_listID_type &into,
        const acatomslist_itemID ptr2head
    );
};//class


#endif
