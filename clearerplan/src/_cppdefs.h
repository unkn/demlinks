#ifndef _CPPDEFS_H__
#define _CPPDEFS_H__
/* to be included only in .cpp files, first in line but after those with <>
 * (and before those #include with "")
 * there's no use for this in .h files
 * may speed up compilation */

/* the user might set one of the following, but before including this header:
        PARANOIA_DEBUG enables DEBUG mode AND the paranoid checks
        DEBUG enables DEBUG mode WITHOUT paranoid checks
*/


/* internal stuff follows, please don't change (or smth) */

#ifdef PARANOIA_DEBUG
        #define DEBUG
        #define PARANOIA
#else
        #undef PARANOIA
#endif


#ifdef DEBUG

        #ifdef PARANOIA
        /* used within the pnotetrk.h to def some of the PARANOID_IF()
           to really DO check the conditions and EXEC that statements
         * provide extra safety during development, at cost of speed which
           should indeed be irrelevant while testing the code for functionality
         */
        #define PARANOID_CHECKS

        #endif //PARANOIA

        /* enables code that checks to see if somehow the programmer called
         * functions in another order than needed, such as a call to write to
         * an unopened file */
        #define CHECK_FOR_LAME_PROGRAMMERS

#else //no DEBUG

        #ifndef PARANOIA

        /* if undefined, as u can see in pnotetrk.h , all PARANOID or
         * PARANOID_IF() are disabled, ie. like assert() when NDEBUG is set */
        #undef  PARANOID_CHECKS

        #endif //no PARANOIA

        /* undefined, causes the extra code that does the checks(and execution)
         * to be removed */
        #undef  CHECK_FOR_LAME_PROGRAMMERS

#endif //DEBUG


#endif /* file */
