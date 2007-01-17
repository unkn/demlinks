//<?php
#ifndef DEBUGL1_PHP
#define DEBUGL1_PHP

/*LICENSE*GNU*GPL************************************************************{{{
*
*                             dmental links
*    Copyright (C) 2006 AtKaaZ, AtKaaZ at users.sourceforge.net
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
* Description: this include file will help with tracking; Level 1;
*              it will use dmlphp*.php to hold data
*
***************************************************************************}}}*/


#include "shortdef.php"
#include "color.php"
#include "dmlphpL1.php"
#include "debugL0.php"

if (!is_a($debugL1,"dmlphpL1")) {
        echo "Initializing dmlphpL1".nl;
        $debugL1=new dmlphpL1;
}

define(kAllFunctions,"kAllFunctions");
define(kAllReturns,"kAllReturns");
define(kSetActedOnce,"kSetActedOnce");//to flag that setretflagL1() was executed once in the current function, thus executing it twice in the same serial_commands :-" is prone to detecting a bug

#define addretflagL1(...) \
        _yntIFnot( $debugL1->AppendToParent_Children($TheReturnOfThisTime_forThisFunction, array(__VA_ARGS__) ) );

#define delretflagL1(...) \
        _yntIFnot( $debugL1->DeleteFromParent_Children($TheReturnOfThisTime_forThisFunction, array(__VA_ARGS__) ) );

#define setretflagL1(...) \
        _yntIF( $debugL1->ynIsPCRel($TheReturnOfThisTime_forThisFunction, kSetActedOnce ) );/*this Relation can only exist after this call, not before, otherwise this set was called twice, and prolly a bug is present in caller*/ \
        _yntIFnot( $debugL1->SetOfParent_Children($TheReturnOfThisTime_forThisFunction, array(kSetActedOnce,##__VA_ARGS__) ) );

#define countretflagsL1(_into) \
        _yntIFnot( $debugL1->GetCountOfChildren_OfParent(_into, $TheReturnOfThisTime_forThisFunction) );

#define funcL1(funcname, funcparams) /*{{{*/ \
        funcL1_part1of2(funcname, funcparams) \
        DisallowReentry(TRUE); /*reentring disallowed locally, ie. non-recursive!*/\
        funcL1_part2of2(funcname, funcparams)

#define funcL1re(funcname, funcparams) \
        funcL1_part1of2(funcname, funcparams) \
        funcL1_part2of2(funcname, funcparams)

#define funcL1_part1of2(funcname, funcparams) \
        function funcname funcparams \
        {

#define funcL1_part2of2(funcname, funcparams) \
                $funcnameALKSD=#funcname." (vim ".getfile." +".getline.")"; \
                $allReturnsForThisFunction="AllReturnsForFunction: ".$funcnameALKSD; \
                global $debugL1; \
                _yntIFnot( $debugL1->EnsurePCRel(kAllFunctions, $funcnameALKSD) ); \
                _yntIFnot( $debugL1->EnsurePCRel(kAllReturns, $allReturnsForThisFunction) ); \
                _yntIFnot( $debugL1->EnsurePCRel($funcnameALKSD, $allReturnsForThisFunction) ); \
                _yntIFnot( $debugL1->GetCountOfChildren_OfParent($TheReturnOfThisTime_forThisFunction, $allReturnsForThisFunction) ); \
                $TheReturnOfThisTime_forThisFunction++; \
                $TheReturnOfThisTime_forThisFunction=#funcname.$TheReturnOfThisTime_forThisFunction; \
                _yntIFnot( $debugL1->EnsurePCRel($allReturnsForThisFunction, $TheReturnOfThisTime_forThisFunction) ); \

#define endnowL1(...) \
                addretflagL1(__VA_ARGS__); \
                _yntIFnot( $debugL1->GetOfParent_AllChildren($TheReturnOfThisTime_forThisFunction, $tmpASKD) );/*must have at least one return flag*/ \
                AllowReentry();\
                return $TheReturnOfThisTime_forThisFunction;

#define endfuncL1(...) \
                endnowL1(__VA_ARGS__); \
        }
/*}}}*/

boolfunc isValidReturnL1($val)/*{{{*/
{
        global $debugL1;
        //kAllReturns -> $allReturnsForThisFunction -> $TheReturnOfThisTime_forThisFunction(aka $val)
        //find parent $X for the child $val, where $X has the parent kAllReturns
        //in other words: kAllReturns -> $X -> $val    ... find $X, if any
        //but, what we do wanna know is whether $val is a child of kAllReturns, thus it would be a valid return from a function
        _if (yes===ynIsGood($debugL1->TestElementInvariants($val)) && yes===ynIsGood($debugL1->GetOfChild_AllParents($val, $parents))) {
                foreach ($parents as $p) {
                        _ynif ($debugL1->GetOfChild_AllParents($p, $parentsofP) ) {
                                foreach ($parentsofP as $pp) {
                                        if ($pp === kAllReturns) {
                                                _yntIFnot( $debugL1->GetCountOfChildren_OfParent($count, $val) );
                                                _tIFnot($count > 0);//bug in the program
                                                return TRUE;
                                        }
                                }
                        }
                }
        }
        return FALSE;
}/*}}}*/

boolfunc isFlagInList_L1($flag, $list)
{
        global $debugL1;
        _ynif( $debugL1->ynIsPCRel($list, $flag) ) {
                return TRUE;
        }
        return FALSE;
}

#define isFlagL1(_flag) \
        isFlagInList_L1(_flag, $TheReturnOfThisTime_forThisFunction)

boolfunc isL1NoReturn($ar_return)
{
        return (! isL1YesReturn( $ar_return ) );
}

boolfunc isL1YesReturn($ar_return)
{
        _tIFnot( isValidReturnL1($ar_return) );//prolly put this(ie. _arif()) in a wrong place(where the return is not as expected)
        //echo retValue($ar_return).nl;
        $yes=isFlagInList_L1(yes, $ar_return);
        $no=isFlagInList_L1(no, $ar_return);
        _tIF( $yes && $no); //can't have both present!//both yes and no flags present in return, this is catching a late bug
        if ($yes) {
                return TRUE;
        } else {
                if ($no) {
                        return FALSE;
                } else {//neither yes nor no is present
                        throw_exception("neither ".yes." nor ".no." is present in this return:".retValue($ar_return));
                }
        }
        throw_exception("can't reach this");
}

#define _arifL0(arreturn, _booltest) \
                __( $_arreturn_this_var_accessible_in_caller = arreturn ); \
                _tIFnot( isValidReturnL1($_arreturn_this_var_accessible_in_caller) ); \
                __( $_arbool_this_var_accessible_in_caller = isL1YesReturn($_arreturn_this_var_accessible_in_caller) ); \
                if (_booltest===$_arbool_this_var_accessible_in_caller)

#define _arif(arreturn) \
                _arifL0(arreturn,TRUE)

#define _arifnot(arreturn) \
                _arifL0(arreturn,FALSE)


#define _artIFnot(__return) \
                { \
                        $_artifnot_var_temp=__return; \
                        _arifnot( $_artifnot_var_temp ) { \
                                except( dropmsg("_artIFnot( ".$_artifnot_var_temp." )") ) \
                        } \
                }

#define _artIF(__return) \
                { \
                        $_artif_var_temp=__return; \
                        _if( $_artif_var_temp ) { \
                                except( dropmsg("_artIF( ".$_artif_var_temp." )") ) \
                        } \
                }


// vim: fdm=marker

#endif //header
//?>
