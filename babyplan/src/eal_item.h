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


#ifndef __EAL_ITEM_H
#define __EAL_ITEM_H

#include "gdefs.h"

#include "nicef.h"

#include "common.h"


class if_eatomslist_item:public nicefi {
private:
    int opened;
    const long its_recsize;
public:
    if_eatomslist_item();
    ~if_eatomslist_item();
    reterrt init(const char *fname, const long MAXCACHEDRECORDS);
    reterrt getwithID(const eatomslist_itemID whateatomslist_itemID, deref_eatomslist_itemID_type &into);
    reterrt writewithID(const eatomslist_itemID whateatomslist_itemID, const deref_eatomslist_itemID_type &from);
    eatomslist_itemID addnew(const deref_eatomslist_itemID_type &from);
    long howmany();
    reterrt shutdown();
    void compose(
        deref_eatomslist_itemID_type &into,
        eatomslist_itemID prevINlist,
        eatomslist_itemID nextINlist,
        atomID ptr2atom_that_points_to_US
    );
};//class




#endif
