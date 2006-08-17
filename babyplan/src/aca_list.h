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
