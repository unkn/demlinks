/****************************************************************************
*
*                             dmental links
*    Copyright (C) 2005 AtKaaZ, AtKaaZ at users.sourceforge.net
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
*               These are the global temporary defines.
*               This is included only in .cpp files not in .h files
*
****************************************************************************/
/* to be included only in .cpp files, first in line but after those with <>
 * (and before those #include with "")
 * there's no use for this in .h files; actually there are exceptions where the
   use of ie. ERR_IF in classes that are decl&defined in .h files
 * may speed up compilation */



#ifndef _GCDEFS__H__
#define _GCDEFS__H__

//#include "../config.h" conflicts with defs from allegro.h
#include <demlinks-config.h>

//#define ENABLE_TIMED_INPUT //enable time capab based on gTimer of all input events OBSOLETE
//#define TRACKABLE_RETURNS //_hret _ret _hreterr _fret all are defined
//#define TRACKABLE_FRET //only _fret is tracked! do not define TRACKABLE_RETURNS if you define this; on the other hand if TRACKABLE_RETURNS is defined then _cppdefs.h below will define TRACKABLE_FRET automaticly

//#define CONTINUE_IF_NOTINITED //continue even if notification subsystem is not initialized, this could happen if some classes are instantiated before executing anything from main() 04aug2006

//#define DISABLE_TRANSACTIONS
#define DISABLE_LOCK
//#define DISABLE_LOG

#ifdef DML_PARANOIA_DEBUG
        #define PARANOIA_DEBUG
#endif

#include "_cppdefs.h" /* last */

#endif /* file */
