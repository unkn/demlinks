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
