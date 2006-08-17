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
* Description: These items all hold atomIDs which refer to acatoms.
*
****************************************************************************/


#ifndef __ACALITEM_H
#define __ACALITEM_H

#include "gdefs.h"

#include "nicef.h"

#include "common.h"


class if_acatomslist_item:public nicefi {
private:
    int opened;
    const long its_recsize;
public:
    if_acatomslist_item();
    ~if_acatomslist_item();
    reterrt init(const char *fname, const long MAXCACHEDRECORDS);
    reterrt getwithID(const acatomslist_itemID whatacatomslist_itemID, deref_acatomslist_itemID_type &into);
    reterrt writewithID(const acatomslist_itemID whatacatomslist_itemID, const deref_acatomslist_itemID_type &from);
    acatomslist_itemID addnew(const deref_acatomslist_itemID_type &from);
    long howmany();
    reterrt shutdown();
    void compose(
        deref_acatomslist_itemID_type &into,
        acatomslist_itemID prevINlist,
        acatomslist_itemID nextINlist,
        atomID ptr2atom_that_points_to_US
    );
};//class




#endif
