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


#ifndef __GRPLITEM_H
#define __GRPLITEM_H

#include "gdefs.h"

#include "nicef.h"

#include "common.h"


//used only as part of a groupID
class if_grpatomslist_item:public nicefi {
private:                       
    int opened;                 
    const long its_recsize;      
public:                           
    if_grpatomslist_item();                    
    ~if_grpatomslist_item();                    
    reterrt init(const char *fname, const long MAXCACHEDRECORDS);    
    reterrt getwithID(const grpatomslist_itemID whatgrpatomslist_itemID, deref_grpatomslist_itemID_type &into);
    reterrt writewithID(const grpatomslist_itemID whatgrpatomslist_itemID, const deref_grpatomslist_itemID_type &from);
    grpatomslist_itemID addnew(const deref_grpatomslist_itemID_type &from);
    long howmany();
    reterrt shutdown(); 
    void compose(
        deref_grpatomslist_itemID_type &into,
        grpatomslist_itemID prevINlist,
        grpatomslist_itemID nextINlist,
        atomID atomID_that_points_to_US_the_group
    );
};//class




#endif
