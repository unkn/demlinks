//<?php
#ifndef DEBUG_PHP
#define DEBUG_PHP

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
* Description: this include file will help with tracking the main program errors
*
***************************************************************************}}}*/


#include "shortdef.php"
#include "color.php"
        /*require_once("shortdef.php");
        require_once("color.php");*/

#define debugon
//#define alldebugon
#define IMMEDIATE_REPORTS   //echo the exception when it occurs!

$maxdebuglevel=0;//don't need to change this
#ifdef alldebugon
        define(debugstartfrom,1 + $maxdebuglevel);
#endif


#define boolfunc function
#define ynfunc function
#define procedure function

#define ynIsNotGood(_var,...) \
        (yes===ynIsGood(_var,##__VA_ARGS__/*$allowemptystr*/)?no:yes)


procedure adef($what)
{
        global $maxdebuglevel;
        if (TRUE===defined($what)) {
                die("already defined $what".nl);
        }
        define($what,"adef_".++$maxdebuglevel);
}

//defining specific debug levels
adef(dlowlevel);//start from 1
adef(dbeg);
adef(dend);
adef(dinfo);
adef(dnormal);
adef(ddel);//show deletes
adef(dis);//show IsStuff()
adef(dadd);//show additions ie. AddNode()
adef(dcrea);
adef(dget);
adef(dbegtr);//begin
adef(dabtr);//abort
adef(dendtr);//close transaction
adef(dconstr);//contructor
adef(ddestr);//destructor
adef(ddbadd);//database add, physical addition into the database is executed
adef(dset);
adef(dtest);
adef(dtestcrit);//shows the value of the var that failed test

procedure dseton($what)
{
        global $debugar;
        $debugar[$what]=TRUE;
}

//enabling specific debug levels
dseton(dlowlevel);
dseton(dinfo);
//dseton(dset);
//dseton(dbeg);
//dseton(dend);
dseton(dnormal);
//dseton(ddel);
dseton(dcrea);
//dseton(dis);
//dseton(dadd);
//dseton(dget);
//dseton(dbegtr);//transaction
//dseton(dabtr);
//dseton(dendtr);
//dseton(dconstr);
//dseton(ddestr);
//dseton(ddbadd);
dseton(dtestcrit);


#ifdef alldebugon
for ($i=debugstartfrom; $i <= $maxdebuglevel; $i++) {
        dseton($i);
}
#endif


//now the line numbers match with those reported on error(s) when using __() and _yntIF()

#define getline \
        browncol.__LINE__.nocol
#define getfile \
        browncol.__FILE__.nocol

#define throw_exception(_message) \
                throw new Exception(_message);

#ifdef IMMEDIATE_REPORTS
        #define except(_message) \
                echo _message; \
                throw_exception(_message);
#else
        #define except(_message) \
                throw_exception(_message);
#endif

#define quitmsg \
                nl.redcol."vim ".getfile." +".getline.nl.tab.greencol.$e->getmessage().nocol.nl

#define dropmsg(_a) \
                nl.greencol."vim ".getfile." +".getline.nl.tab.purplecol._a.nocol.nl

#define _TRY(thiscode, ... /*exec_this_if_fail*/) \
                {\
                        try { \
                                thiscode; \
                        } catch(PDOException $e) { \
                                __VA_ARGS__; \
                        } catch(Exception $e) { \
                                __VA_ARGS__; \
                        } \
                }

//wrapper call to catch and rethrow any exception
#define __(thiscode) \
                { \
                        _TRY(thiscode, except(quitmsg) ) \
                }

//FIXME: try to catch "PHP Fatal error: " too! this is obv. unlikely to happen

#define _tIFnot(__bool) \
                { \
                        _ifnot( __bool ) { \
                                except( dropmsg("_tIFnot( ".#__bool." )") ) \
                        } \
                }

#define _tIF(__bool) \
                { \
                        _if( __bool ) { \
                                except( dropmsg("_tIF( ".#__bool." )") ) \
                        } \
                }

#define _yntIFnot(__ynbool) \
                { \
                        _ynifnot( __ynbool ) { \
                                except( dropmsg("_yntIFnot( ".#__ynbool." )") ) \
                        } \
                }

#define _yntIF(__ynbool) \
                { \
                        _ynif( __ynbool ) { \
                                except( dropmsg("_yntIF( ".#__ynbool." )") ) \
                        } \
                }

//boolfunc is a function that returns boolean or something that evaluates to boolean, ie. a non empty string
#define _if(boolfunc) \
                __( $_bool_this_var_accessible_in_caller = boolfunc ); \
                if (TRUE===$_bool_this_var_accessible_in_caller)

#define _ifnot(boolfunc) \
                __( $_bool_this_var_accessible_in_caller = boolfunc ); \
                if (FALSE===$_bool_this_var_accessible_in_caller)

//ynboolfunc is a function that returns something, usually anything that can be tested by ynIsGood() but mostly by convention just yes or no  || ok or bad (same thing)
#define _ynifL0(ynboolfunc, _booltest) \
                __( $_bool_this_var_accessible_in_caller = ynboolfunc ); \
                __( $_yn_this_var_accessible_in_caller = yes===ynIsGood($_bool_this_var_accessible_in_caller) ); \
                if (_booltest===$_yn_this_var_accessible_in_caller)

#define _ynif(ynboolfunc) \
                _ynifL0(ynboolfunc,TRUE)

#define _ynifnot(ynboolfunc) \
                _ynifL0(ynboolfunc,FALSE)

#define beginprogram \
                try { \
                        deb(dbeg,"Program begins...");
#define endprogram \
                        deb(dend,"Ending program...");\
                } catch (Exception $e) { \
                        die(quitmsg); \
                }

#ifdef debugon
        #define debshow(level,text,textappend) \
                echo nl.bluecol."debLev".greencol.level.nocol." ".#text." (vim ".getfile." +".getline.")" . textappend;

        #define dEbS(level,text,textappend) { \
                global $debugar; \
                $levelar=level; \
                $text=text; \
                if (TRUE===is_array($levelar)) { \
                        foreach ($levelar as $key) { \
                                if (TRUE === $debugar[$key]) { \
                                        debshow($key,$text,textappend);\
                                } \
                        } \
                } else { \
                        if (TRUE === $debugar[level]) { \
                                debshow($levelar,$text,textappend); \
                        } \
                }\
        }
                #
        #define deb(level,text) \
                { dEbS(level,text,"") }

        #define debnl(level,text) \
                { dEbS(level,text,nl) }

#else //to bad we gotta repeat interface(?) for both
        #define deb(level,text)
#endif

procedure array_append_unique_values(&$towhatarray, $listofvalues)/*{{{*/
//all values are copied, not referenced! because, in my view, references are too subtle in php to be used consistently
{
        _tIFnot(is_array($towhatarray));
        _tIFnot(is_array($listofvalues));
        foreach ( $listofvalues as $val ) {
                //print_r($val);
                if (no !== $val) {
                        //print_r($val);
                        _yntIF(ynIsNotGood($val));
                }

                if (! in_array($val, $towhatarray) ) {
                        $towhatarray[]=$val;
                }
        }
}/*}}}*/

#define appendtolist(tolist,...) \
                array_append_unique_values( tolist, array(__VA_ARGS__ /*many elements here*/) );

#define getalist(oflist,...) \
                array_merge(oflist,array(__VA_ARGS__))


//called within funcL0() and endfuncL0()
#define addretflagL0(/*what flags*/...) \
                appendtolist($TheReturnStateList, __VA_ARGS__);

#define keepflagsL0(var) \
{ \
        $RSL_var=&var; \
        _tIFnot( isReturnStateList(&$RSL_var) ); \
        foreach ($RSL_var as $val) { \
                if (kReturnStateList_type!==$val) { \
                        addretflagL0($val); \
                } \
        } \
}

#define isValue_InList(whatflag,inwhatlist) \
                in_array(whatflag, inwhatlist, FALSE/*check types?=no*/)

function retValue($var)
{
        $ret='';
        if (TRUE===is_array($var)) {
                foreach ($var as $v) {
                        $ret.="'$v' ";
                }
        } else {
                if (!empty($var)) {
                        $ret="'$var'";
                }
        }
        return $ret;
}

//supposed to return either yes or no ONLY!, if u need to return something use a &$var as a parameter
//actually it returns an array where one of yes or no is present, and other flags,if any,to signal status ie. yes,kAlreadyExists
#define funcL0(funcdef,.../*debug levels here*/) /*{{{*/ \
ynfunc &funcdef \
        { \
                $funcnameRFZAHJ=#funcdef; \
                $other_debuglevelsRFZAHJ=array(__VA_ARGS__); \
                deb(getalist($other_debuglevelsRFZAHJ, dbeg), $funcnameRFZAHJ.":begin..."); \
                $TheReturnStateList=array(kReturnStateList_type);//this sets this to array type and also flags this array as being a return type, needed on ynIsGood() to make the diff between our array and system returned arrays

#define endnowL0(.../*more elements to add here*/) \
                { \
                        __( addretflagL0(__VA_ARGS__) ); \
                        global $AllReturnLists;\
                        $theKey="vim ".getfile." +".getline;\
                        $AllReturnLists[$theKey]=$TheReturnStateList;\
                        debnl(getalist($other_debuglevelsRFZAHJ, dend) , $funcnameRFZAHJ.":done:ynIsGood(".retValue($TheReturnStateList).")===".ynIsGood($TheReturnStateList));\
                        return $TheReturnStateList; \
                }/*}}}*/

// DO NOT append ";" to endfuncL0!!!
#define endfuncL0(.../* more elements */) /*{{{*/ \
                endnowL0(__VA_ARGS__) \
        }/*}}}*/

#define isReturnStateList(_flag) \
        (is_array(_flag) && isValue_InList(kReturnStateList_type, _flag))

//never do if (ynIsGood($var)) => always true, instead do _ynif ($var)  OR if (yes===ynIsGood($var))
//careful with _ynif (ynIsGood(x) && ynIsGood(y))  it is always true, try instead _if (ynIsGood(x)===yes && ynIsGood(y)===yes)
ynfunc ynIsGood($var,$allowemptystr=no)
{
        if (TRUE===is_null($var)) {
                return no;
        }
        if (FALSE===isset($var)){
                return no;
        }
//prior all:
        if ((yes===$var) || (no===$var)) {
                return $var;
        }
//eof

        if (TRUE===is_string($var) || TRUE===is_array($var)) {
                if ((yes===$allowemptystr) || ( FALSE===empty($var) )) { //non empty
                        if ( isReturnStateList($var) ) {
                                global $AllReturnLists;
                                //print_r($var);
                                _tIF(isValue_InList(yes,$var) && isValue_InList(no, $var) );//both yes and no present, bug!
                                _if (isValue_InList(yes, $var)) {
                                        return yes;
                                } else {
                                        _if (isValue_InList(no, $var)) {
                                                return no;
                                        }
                                        throw_exception("invalid kReturnStateList_type! neither yes, nor no");
                                }
                        }
                        return yes; //a non kReturnStateList_type
                } else {
                        return no;
                }
        }
        if (TRUE===is_numeric($var)) { //numeric
                return yes;
        }
        if (TRUE===is_bool($var)) {
                return (TRUE===$var?yes:no);
        }
        if (TRUE===is_object($var) ){
                return yes;
        }

        return yes; //any other object
} //funcL0



$maxDifferentReturnItems=0;//don't need to change this

procedure rdef($what)
{
        global $maxDifferentReturnItems;
        if (TRUE===defined($what)) {
                die("already defined $what".nl);
        }
        define($what,"$what");
        ++$maxDifferentReturnItems;
}

define(kReturnStateList_type,"kReturnStateList_type"/*this array is a ReturnStateList type; this element is a way for ynIsGood() to determine that"*/);
//this is the kind that flags the return array from the function so that ynIsGood knows it's a return value instead of a normal array returned by ie. fetchAll()

//define kConsts aka return types here:
rdef(kAlready);
rdef(kAdded);
rdef(kCreatedDBNodeNames);
rdef(kCreatedDBRelations);
rdef(kEmpty);
rdef(kOneElement);//one element detected, expected list; but it's ok even this way, we made that element into an one element list


// vim: fdm=marker

#endif //header
//?>
