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
* Description:..
*               These are in-between rules that route defines :-"
*               This is to be included only in _gcdefs.h
*
****************************************************************************/
/* the user might set one of the following, but before including this header:
        PARANOIA_DEBUG enables DEBUG mode AND the paranoid checks
        DEBUG enables DEBUG mode WITHOUT paranoid checks
*/

#ifndef _CPPDEFS_H__
#define _CPPDEFS_H__



/* internal stuff follows, please don't change (or smth) */

#ifdef PARANOIA_DEBUG
#       define DEBUG
#       define PARANOIA
#else
#       undef PARANOIA
#endif


#ifdef DEBUG

#       ifdef PARANOIA
        /* used within the pnotetrk.h to def some of the PARANOID_IF()
           to really DO check the conditions and EXEC that statements
         * provide extra safety during development, at cost of speed which
           should indeed be irrelevant while testing the code for functionality
         */
#       define PARANOID_CHECKS

#       endif //PARANOIA

        /* enables code that checks to see if somehow the programmer called
         * functions in another order than needed, such as a call to write to
         * an unopened file */
#       define CHECK_FOR_LAME_PROGRAMMERS

#else //no DEBUG

#       ifndef PARANOIA

        /* if undefined, as u can see in pnotetrk.h , all PARANOID or
         * PARANOID_IF() are disabled, ie. like assert() when NDEBUG is set */
#       undef  PARANOID_CHECKS

#       endif //no PARANOIA

        /* undefined, causes the extra code that does the checks(and execution)
         * to be removed */
#       undef  CHECK_FOR_LAME_PROGRAMMERS

#endif //DEBUG


#endif /* file */
