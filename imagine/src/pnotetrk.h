/****************************************************************************
*
*                             dmental links
*    Copyright (C) 2005-2006 AtKaaZ, AtKaaZ at users.sourceforge.net
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
* Description: provides personalized notify tracking
*
****************************************************************************/


#ifndef PNOTETRK_H__
#define PNOTETRK_H__

#include <stdlib.h> //for EXIT_FAILURE
//#include <db_cxx.h>
#include <stdexcept>
#include "notetrk.h"
#include "_gcdefs.h"


extern bool gTrackFRETs;
extern bool gTrackHRETs;
extern bool gTrackFlags;

typedef enum {//avoiding to use the value zero
        kFuncOK=110
        ,kFuncFailed//can't use this if throwing. ie. _tIF(true)
        ,kFuncNoLowLevelInputs
        ,kFuncNoGenericInputs
        ,kFuncNoActions
        ,kFuncAlreadyExists //some element already exists(used in dmlenv.cpp)
        ,kFuncNotFound //some element wasn't found (-//-)
        ,kFuncInexistentNodeNotCreated //because the flag wasn't specified!
        ,kFuncExistentSingleNodeNotOverwritten //the overwrite flag wasn't specified!
        ,kFuncMoreThanOneNodeNotTruncated //because the flag that says truncate wasn't present
        ,kFuncNULLPointer //ie. no pointee found

//last
        ,kMaxFuncErrors
} EFunctionReturnTypes_t;

enum ENotifyTypes {
        kNotify_None=0,
        kNotify_Warn,
        kNotify_Err,
        kNotify_Fail,
        kNotify_Exit,
        kNotify_Info,
        kNotify_Exception,

        kNotify_PossiblyBug,//check it out
        /* this signals that a paranoid check on a condition was true,
         * if so then is considered fatal ;) since the condition is expected
         * always to be false (*doh* paranoid) */
        kNotify_Paranoid,

        /* last in line*/
        kNumNotifyTypes
};


class MNotifyTracker : public TNotify {
private:
        bool fIsOn;//true if inited!
public:
        MNotifyTracker();
       ~MNotifyTracker();

       void SetOn();
       void SetOff();
       bool IsOn()const { return fIsOn; }
       bool IsOff()const { return (!fIsOn); }

        /* implicitly kills all notifications after routing them to stderr */
        void ShowAllNotes();
        void PurgeAllNotifications();
};

extern MNotifyTracker gNotifyTracker;

void ShowAllNotifications();


//the trouble is that using delete var; without parantheses will use the old delete so you should call delete(var); to use this one! the three dots are for casting ie. int *var; void *c=var; delete(c, (int *) ); parantheses to the second argument are mandatory!
#define delete(_smth_ , ...) { \
        if (_smth_) { \
                delete __VA_ARGS__ _smth_;   \
                _smth_ = NULL; \
        } else { \
                WARN(attempted to delete a NULL variable); \
        }\
}

/* displays all notifications prior to executing and after execution of the
 * passed statements */
#define TRAP(a_BunchOfStatements)               \
{                                               \
        gNotifyTracker.ShowAllNotes();         \
        {                                       \
                gTrackFRETs=true;               \
                gTrackHRETs=true;               \
                gTrackFlags=true;\
                a_BunchOfStatements;            \
                gTrackFRETs=false;               \
                gTrackHRETs=false;               \
                gTrackFlags=false;\
        }                                       \
        gNotifyTracker.ShowAllNotes();         \
}


/* define this before including this header file in your sources
 * if defined, turns on the code that does the checks and executes the
   statements if the checks are true (see below) */
#if defined(PARANOID_CHECKS)

/* adds a paranoia msg to the notify-list if the condition is true
 * this usually means that something is going really wrong and this is like
 * a fatal situation (if the condition is true, of course) */
#define PARANOID_IF(a_ConditionalStatement,a_MoreStatementsIfTrue)  \
{                                                               \
        if ((a_ConditionalStatement)) {                         \
                PARANOID(a_ConditionalStatement)                    \
                { a_MoreStatementsIfTrue; }                     \
        }                                                       \
}

/* always adds a paranoia-notify to the list, with the passed description */
#define PARANOID(a_InfoDescription)                 \
{                                               \
        ADD_NOTE(kNotify_Paranoid,a_InfoDescription)\
}

#else //not defined PARANOID_CHECKS

#define PARANOID_IF(a_blah,a_blahblah) /* nada */
#define PARANOID(a_blah) /* yet again nothing done */

#endif //PARANOID_CHECKS

/* adds an info to the notify-list if the condition is true */
#define INFO_IF(a_ConditionalStatement,a_MoreStatementsIfTrue)  \
{                                                               \
        if ((a_ConditionalStatement)) {                         \
                INFO(a_ConditionalStatement)                    \
                { a_MoreStatementsIfTrue; }                     \
        }                                                       \
}

/* always adds an INFO to the list, with the passed description */
#define INFO(a_InfoDescription)                 \
{                                               \
        ADD_NOTE(kNotify_Info,a_InfoDescription)\
}

#define BUG_IF(a_ConditionalStatement,...)  \
{                                                               \
        if ((a_ConditionalStatement)) {                         \
                BUG(a_ConditionalStatement)                    \
                { __VA_ARGS__; }                     \
        }                                                       \
}

#define BUG(a_BugDescription)                 \
{                                               \
        ADD_NOTE(kNotify_PossiblyBug,a_BugDescription)\
}

/* adds a warning to the notify-list if the condition is true */
#define WARN_IF(a_ConditionalStatement,...)  \
{                                                               \
        if ((a_ConditionalStatement)) {                         \
                WARN(a_ConditionalStatement)                    \
                { __VA_ARGS__; }                     \
        }                                                       \
}

/* always adds a WARN to the list, with the passed description */
#define WARN(a_WarnDescription)                 \
{                                               \
        ADD_NOTE(kNotify_Warn,a_WarnDescription)\
}

/* always adds an EXIT type notification to the list,
   with the passed description, just before doing the actual clean EXIT
 * refusing to do an exit with user supplied exitcode */
#if defined(EXIT)
# error EXIT macro is already defined, this may cause problems, check it out !!!
#endif
#define EXIT(a_ErrorDescription)                        \
{                                                       \
        ADD_NOTE(kNotify_Exit,a_ErrorDescription)       \
        gNotifyTracker.SetOff();                        \
        exit(EXIT_FAILURE);                             \
}

/* adds an EXIT type notification to the notify-list if the condition is true
   AND on doing so it also does display all notifications so far prior to
   EXITing the program in a nice way with exit() so the at_exit functions may
   be called */
#define EXIT_IF(a_ConditionalStatement)                         \
{                                                               \
        if ((a_ConditionalStatement)) {                         \
                EXIT(a_ConditionalStatement)                    \
        }                                                       \
}

#define AEXIT(a_Func)                         \
{                                                               \
        if (( kFuncOK != a_Func )) {                         \
                EXIT(AEXIT(a_Func))                    \
        }                                                       \
}

/***************************************/
/***************************************/
/***************************************/
//does NOT throw
#define _TRY(a_DOCmds,...) \
        try { a_DOCmds; } catch (...) { \
                ADD_NOTE(kNotify_Exception,a_DOCmds); \
                __VA_ARGS__; }

//does NOT throw, shows error if known
#define _sTRY(a_DOCmds,...) { \
        try { a_DOCmds; } \
        catch (DbException &e) { \
                ADD_NOTE(kNotify_Exception,a_DOCmds); \
                        cout << e.what() <<endl; \
                __VA_ARGS__; \
        } catch (exception &e) { \
                ADD_NOTE(kNotify_Exception,a_DOCmds); \
                        cout << e.what() <<endl; \
                __VA_ARGS__; \
        } catch (...) { \
                ADD_NOTE(kNotify_Exception,a_DOCmds); \
                        cout << "unknown exception" <<endl; \
                __VA_ARGS__; \
        } \
}
/***************************************/
//tries a_DOCmds and catches any exceptions thrown, if any does (re)throw after executing __VA_ARGS__
//WARNING: can't use things that use THROW_HOOK within a definition of THROW_HOOK ie. #define THROW_HOOK TRY(This()); because when This() throws it'll run TRY(This()) and so on ... until infinity use __() instead of TRY() which throws without using THROW_HOOK at all
#define TRY(a_DOCmds) _TRY(a_DOCmds,  THROW_HOOK; throw;);

/***************************************/
//wrapper, expects: _(DOcmds,UNDOcmds), id DOcmds fail, then UNDOcmds are executed to hopefully undo wtw DOcmds have done so far; the _ before means don't throw
#define _h(a_DOcmds) TRY(a_DOcmds)
//throw w/o THROW_HOOK, to use within #define THROW_HOOK
#define __(a_DOcmds) _TRY(a_DOcmds, throw)

//attempts to show DbException and exception and maybe others, before rethrowing
#define __s(a_DOcmds) _sTRY(a_DOcmds, throw)

//no throw, no hook, just a catch
#define ___(a_DOcmds) _TRY(a_DOcmds)
/***************************************/
//if evaluation throws an exception it is rethrown WITHOUT HOOK! no hook tIF
//throw if(true)=_tIF(true)
#define __tIF(a_DOifcmd) { \
        bool __bool_ifexpr;\
        _TRY( __bool_ifexpr= (a_DOifcmd),  throw ) \
        if (__bool_ifexpr) { \
                __t(__tIF( a_DOifcmd) );/*not circular*/\
        }       \
} //endblock

/***************************************/
//__tIFnok(callee()) rethrows the exception if the callee threw one; if none, then checks to see that the callee()==kFuncOK if NOT then throws!
//an attempt to standardize function call handling
//a call wrapper; execution stops on throw or kFuncOK != a_Func; no hook (no THROW_HOOK)
#define __tIFnok(a_Func) { \
        EFunctionReturnTypes_t __EFunctionReturnTypes_t__FuncReturn;\
        _TRY( __EFunctionReturnTypes_t__FuncReturn = (a_Func),  throw ) \
        if (kFuncOK != __EFunctionReturnTypes_t__FuncReturn) { \
                __t(__tIFnok( a_Func ));/*doesn't recurse here!*/\
        }       \
} //endblock
#define __fIFnok(a_Func) { \
        EFunctionReturnTypes_t __EFunctionReturnTypes_t__FuncReturn;\
        _TRY( __EFunctionReturnTypes_t__FuncReturn = (a_Func),  throw ) \
        if (kFuncOK != __EFunctionReturnTypes_t__FuncReturn) { \
                _fret(__EFunctionReturnTypes_t__FuncReturn, a_Func);/*returns same error*/\
        }       \
} //endblock

//show if not ok
#define __sIFnok(a_Func) { \
        EFunctionReturnTypes_t __EFunctionReturnTypes_t__FuncReturn;\
        _TRY( __EFunctionReturnTypes_t__FuncReturn = (a_Func),  throw ) \
        if (kFuncOK != __EFunctionReturnTypes_t__FuncReturn) { \
                FAIL(a_Func);/*returns same error*/\
        }       \
} //endblock
//with THROW_HOOK
#define _htIFnok(a_Func) { \
        EFunctionReturnTypes_t __EFunctionReturnTypes_t__FuncReturn;\
        _TRY( __EFunctionReturnTypes_t__FuncReturn = (a_Func), THROW_HOOK ; throw ) \
        if (kFuncOK != __EFunctionReturnTypes_t__FuncReturn) { \
                _ht(_htIFnok( a_Func ));/*doesn't recurse here!*/\
        }       \
} //endblock

//with THROW_HOOK, fails if the callee fails(with the same error) and same THROW_HOOK(not ERR_HOOK!), throws(with THROW_HOOK) if callee throws!
#define _hfIFnok(a_Func) { \
        EFunctionReturnTypes_t __EFunctionReturnTypes_t__FuncReturn;\
        _TRY( __EFunctionReturnTypes_t__FuncReturn = (a_Func), THROW_HOOK ; throw ) \
        if (kFuncOK != __EFunctionReturnTypes_t__FuncReturn) { \
                _hret(__EFunctionReturnTypes_t__FuncReturn);/*returns same error*/\
        }       \
} //endblock

#define function \
        EFunctionReturnTypes_t

        //TRY( __bool_ifexpr= (a_DOifcmd) );
/***************************************/
//no hook OK ret!
#ifdef TRACKABLE_RETURNS
        #define _ret(...) { \
                INFO(_ret(__VA_ARGS__));\
                return __VA_ARGS__; \
        }
#else
        #define _ret(...) { \
                return __VA_ARGS__; \
        }
#endif
/***************************************/
//no hook FAIL ret!
#ifdef TRACKABLE_FRET
        #define _fret(retnum,...) { \
                INFO(_fret(__VA_ARGS__ , retnum));\
                return retnum; \
        }
#else
        #define _fret(retnum,...) { \
                if (gTrackFRETs) { INFO(_fret(__VA_ARGS__ , retnum)); } \
                return retnum; \
        }
#endif
/***************************************/
//fail from a function
//doesn't throw, only exits from function!
#define _FA(a_Desc) { \
        FAIL(a_Desc, \
                _F) \
}

#define _F { \
        _fret(kFuncFailed, kFuncFailed); \
}

#define _OK {\
        _ret(kFuncOK); \
}
/***************************************/
//if evaluation throws an exception it is rethrown after executing __VA_ARGS__
//throw if(true)=_tIF(true)
#define _htIF(a_DOifcmd) { \
        bool __bool_ifexpr;\
        _TRY( __bool_ifexpr= (a_DOifcmd),  THROW_HOOK; throw ) \
        if (__bool_ifexpr) { \
                _ht(_htIF(a_DOifcmd));/*not circular, just text here*/\
        }       \
} //endblock

        //TRY( __bool_ifexpr= (a_DOifcmd) );
/***************************************/
//unconditional log prior to throw => log+throw; the line, func and file where this is called is taken and shown in the error message(not in the throw message)
#define _ht(a_ThrowMessage) {\
        THROW_HOOK;/*prior to throw*/\
        ADD_NOTE(kNotify_Exception,#a_ThrowMessage); \
        throw std::logic_error(#a_ThrowMessage); \
}

//the 'no hook' version of _t()
#define __t(a_ThrowMessage) {\
        ADD_NOTE(kNotify_Exception,#a_ThrowMessage); \
        throw std::logic_error(#a_ThrowMessage); \
}

//wrapper for 'if' that catches the evaluation if it throws exceptions(and rethrows!); don't forget the '}_fi' endblock which puts '}}' at end, because we don't want __bool_ifexpr that we declared temporary to be accessed outside _if()
//or use _if(expr) { do_this(); } else { do_that; }_fi
#define _hif(a_IFExpr) {    \
        bool __bool_ifexpr;     \
        TRY( __bool_ifexpr= (a_IFExpr) );\
        if ( __bool_ifexpr ) {//added beginning of block here to prevent forgotten semicolons left after the _hif() statement to close this path

#define _fihelse } else {
#define _fih }}
/***************************************/
//no hook! but keeps the throw
#define __if(a_IFExpr) {    \
        bool __bool_ifexpr;     \
        _TRY( __bool_ifexpr= (a_IFExpr), throw);\
        if ( __bool_ifexpr ) {

#define __fielse } else {
#define __fi }}
/***************************************/
/* failed this:#define __while(a_IFExpr) {    \
        bool __bool_whileexpr;     \
        _TRY( __bool_whileexpr= (a_IFExpr), throw);\
        while ( __bool_whileexpr ) {

#define __elihw \
        } _TRY( __bool_whileexpr= (a_IFExpr), throw);*eval again before while test/\
        }
*/
/***************************************/
//do block if NOT kFuncOK; throw only when callee threw; do nothing on kFuncOK!
#define _hdoIFnok(a_Func) { \
        EFunctionReturnTypes_t __EFunctionReturnTypes_t__FuncReturn;\
        _TRY( __EFunctionReturnTypes_t__FuncReturn = (a_Func),  THROW_HOOK; throw ) \
        if (kFuncOK != __EFunctionReturnTypes_t__FuncReturn) {

#define _konfiodhelse }
#define _konfiodh }} //endblock
/***************************************/
/***************************************/
//do block if kFuncOK, otherwise throw!
#define __doIFok(a_Func) { \
        EFunctionReturnTypes_t __EFunctionReturnTypes_t__FuncReturn;\
        _TRY( __EFunctionReturnTypes_t__FuncReturn = (a_Func),  throw ) \
        if (kFuncOK == __EFunctionReturnTypes_t__FuncReturn) {

#define __kofiod } \
        else {\
                __t(__doIFok( a_Func ) where a_Func!=kFuncOK);/*doesn't recurse here!*/\
        } \
} //endblock
/***************************************/
//careful with if (true) _reterr 0; will fail because lacking { and }
// we also have if (true) _reterr; w/o params so we cannot include { and } inside this macro;
// do this if (true) { _reterr 1; } but if u use _if u can ommit { and } (it has it's own)
/***************************************/
//uses ERR_HOOK hook no the same as _() which uses THROW_HOOK
#ifdef TRACKABLE_HRET
        #define _hreterr(...) { \
                INFO(_hreterr(__VA_ARGS__));\
                ERR_HOOK;\
                return __VA_ARGS__; \
        }
#else
        #define _hreterr(...) { \
                if (gTrackHRETs && gTrackFRETs) { INFO(_hreterr(__VA_ARGS__)); }\
                ERR_HOOK;\
                return __VA_ARGS__; \
        }
#endif
/***************************************/
//uses the same hook and the MACROS that (re)throw exceptions
#ifdef TRACKABLE_HRET
        #define _hret(...) { \
                INFO(_hret(__VA_ARGS__));\
                THROW_HOOK;\
                return __VA_ARGS__; \
        }
#else
        #define _hret(...) { \
                if (gTrackHRETs) { INFO(_hret(__VA_ARGS__)); }\
                THROW_HOOK;\
                return __VA_ARGS__; \
        }
#endif
/***************************************/
#ifdef TRACKABLE_RETURNS
        #define _hokret(...) { \
                INFO(_hokret(__VA_ARGS__));\
                OK_HOOK;\
                return __VA_ARGS__; \
        }
#else
        #define _hokret(...) { \
                OK_HOOK;\
                return __VA_ARGS__; \
        }
#endif
/***************************************/

// adds an error to the notify-list if the condition is true
//a_MoreStatementsIfTrue
#define ERR_IF(a_ConditionalStatement,...)   \
{                                                               \
        if ((a_ConditionalStatement)) {                         \
                ERR(a_ConditionalStatement)                     \
                { __VA_ARGS__; }                     \
        }                                                       \
}

// always adds an error to the list, with the passed description
#define ERR(a_ErrorDescription,...)                 \
{                                               \
        ADD_NOTE(kNotify_Err,a_ErrorDescription)\
        { __VA_ARGS__; }                     \
}


#define FAIL(a_ErrorDescription,...)                 \
{                                               \
        ADD_NOTE(kNotify_Fail,a_ErrorDescription)\
        { __VA_ARGS__; }                     \
}

/* adds a notification to the list, generic use */
#define ADD_NOTE(a_NotifyType, a_Cause)                         \
{                                                               \
        CheckedAddNote( a_NotifyType,                           \
                        (char *)#a_Cause,                               \
                        (char *)__FILE__,                               \
                        (char *)__func__,                               \
                        __LINE__);                              \
}

/* adds a notification and checks to see if we failed to properly add it
 * if so displays a message and the notifications that exist so far and then
 * aborts the running program */
void CheckedAddNote(
        const NotifyType_t a_NotifyType,
        PChar_t a_Desc,
        File_t a_FileName,
        Func_t a_Func,
        const Line_t a_Line);



#endif

