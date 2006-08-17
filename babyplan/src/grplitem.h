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
