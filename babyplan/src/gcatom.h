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
* Description: a GCatom is an atom which points(refers) only to a group
*
****************************************************************************/


#ifndef __GCATOM_H
#define __GCATOM_H

#include "gdefs.h"

#include "nicef.h"

#include "common.h"

class if_gcatom:public nicefi {//atoms which point(refer) to group(s) only!
private:                       
    int opened;                 
    const long its_recsize;      
public:                           
    if_gcatom();                    
    ~if_gcatom();                    
    reterrt init(const char *fname);    
    reterrt getwithID(const gcatomID whatgcatomID, deref_gcatomID_type *into);
    reterrt writewithID(const gcatomID whatgcatomID, const deref_gcatomID_type *from);
    gcatomID addnew(const deref_gcatomID_type *from);
    long howmany();
    reterrt shutdown(); 
    void compose(
        deref_gcatomID_type *into,
        const groupID ptr2group,
        const atomID prevINchain,
        const atomID nextINchain,
        const gcatoms_listID ptr2clonelist_of_atomIDs_which_point_to_US,
        //hmm... a list of atomIDs which point to US=gcatom
        const groupID Irefer2thisGROUP
    );
};//class

#endif
