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
    reterrt init(const char *fname, const long MAXCACHEDRECORDS);
    reterrt getwithID(const gcatomID whatgcatomID, deref_gcatomID_type &into);
    reterrt writewithID(const gcatomID whatgcatomID, const deref_gcatomID_type &from);
    gcatomID addnew(const deref_gcatomID_type &from);
    long howmany();
    reterrt shutdown();
    void compose(
        deref_gcatomID_type &into,
        const groupID ptr2group,
        const atomID prevINchain,
        const atomID nextINchain,
        const gcatoms_listID ptr2clonelist_of_atomIDs_which_point_to_US,
        //hmm... a list of atomIDs which point to US=gcatom
        const groupID Irefer2thisGROUP
    );
};//class

#endif
