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


#ifndef __ATOM_H
#define __ATOM_H

#include "gdefs.h"

#include "nicef.h"

#include "common.h"

/* PRIVATE DEFINES */
//#define WASINITED_SAFETY //always check if was inited before operating
/* end of PRIVATE DEFINES */

class if_atom:public nicefi {
#ifdef WASINITED_SAFETY
protected:
    int inited;
#endif
private:
    const long its_recsize;
public:
    if_atom();
    ~if_atom();
    reterrt init(const char *fname,const long MAXCACHEDRECORDS);
    reterrt getwithID(const atomID whatatomID, deref_atomID_type &into);
    reterrt writewithID(const atomID whatatomID, const deref_atomID_type &from);
    atomID addnew(const deref_atomID_type &from);
    long howmany();
    reterrt shutdown();
    void compose(
        deref_atomID_type &into,
        const atomtypes at_type,
        const anyatomID at_ID
    );
#ifdef WASINITED_SAFETY
private:
    int wasinited() const { if (inited==_yes_) return _yes_; return _no_; }
    void setinited(){ inited=_yes_; };
    void setdeinited(){ inited=_no_; };
#endif
};//class





#endif
