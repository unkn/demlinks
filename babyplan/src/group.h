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


#ifndef __GROUP_H
#define __GROUP_H

#include "gdefs.h"

#include "nicef.h"

#include "common.h"


class if_group:public nicefi {
private:
    int opened;
    const long its_recsize;
public:
    if_group();
    ~if_group();
    reterrt init(const char *fname, const long MAXCACHEDRECORDS);
    reterrt getwithID(const groupID whatgroupID, deref_groupID_type &into);
    reterrt writewithID(const groupID whatgroupID, const deref_groupID_type &from);
    long addnew(const deref_groupID_type &from);
    long howmany();
    reterrt shutdown();
    void compose(
        deref_groupID_type &into,
        const atomID ptr2atom_head_of_chain,
        const grpatoms_listID ptr2list_of_atomIDs
        //only groups have a list of groupatoms that list which is full of gcatoms
        //but is not the same as the gcatoms_list since that list is a list of acatoms
        // that point to the  gcatom(s)
    );
};//class



#endif //EOF
