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

function adef($what)
{
        global $maxdebuglevel;
        if (TRUE===defined($what)) {
                die("already defined $what".nl);
        }
        define($what,++$maxdebuglevel);
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

function dseton($what)
{
        global $debugar;
        $debugar[$what]=TRUE;
}

#ifdef alldebugon
for ($i=debugstartfrom; $i <= $maxdebuglevel; $i++) {
        dseton($i);
}
#endif


//now the line numbers match with those reported on error(s) when using __() and _tIF()

#define getline browncol.__LINE__.nocol
#define getfile browncol.__FILE__.nocol

#define throw_exception(_a) \
                throw new Exception(_a);

#ifdef IMMEDIATE_REPORTS
        #define except(_a) \
                echo _a; \
                throw_exception(_a);
#else
        #define except(_a) \
                throw_exception(_a);
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

#define __(thiscode) \
                { \
                        _TRY(thiscode, except(quitmsg) ) \
                }

//FIXME: try to catch "PHP Fatal error: " too!

#define _tIFnot(__a) \
                { \
                        _ifnot( isGood(__a) ) { \
                        except( dropmsg("_tIFnot( ".#__a." )") ) \
                        } \
                }

#define _tIF(__a) \
                { \
                        _if( isGood(__a) ) { \
                                except( dropmsg("_tIF( ".#__a." )") ) \
                        } \
                }

//boolfunc is a function that returns something, usually anything that can be tested by isGood() but mostly by convention just yes or no  || ok or bad (same thing)
#define _if(boolfunc) \
                __( $_this_var_accessible_in_caller = boolfunc ); \
                if (yes===isGood($_this_var_accessible_in_caller))

#define _ifnot(boolfunc) \
                __( $_this_var_accessible_in_caller = boolfunc ); \
                if (no===isGood($_this_var_accessible_in_caller))

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
        #define deb(level,text) \
                { dEbS(level,text,"") }
        #define debnl(level,text) \
                { dEbS(level,text,nl) }

#else //to bad we gotta repeat interface(?) for both
        #define deb(level,text)
#endif

function array_append_unique_values(&$towhatarray, $listofvalues)/*{{{*/
//all values are copied, not referenced! because, in my view, references are too subtle in php to be used consistently
{
        _tIFnot(is_array($towhatarray));
        _tIFnot(is_array($listofvalues));
        foreach ( $listofvalues as $val ) {
                //print_r($val);
                if (no!=$val) {
                        //print_r($val);
                        _tIF(isNotGood($val));
                }
                if (! in_array($val, $towhatarray) ) {
                        $towhatarray[]=$val;
                }
        }
}/*}}}*/

#define prependtolist(tolist,...) \
                array_append_unique_values( tolist, array(__VA_ARGS__ /*many elements here*/) );

#define getalist(oflist,...) \
                array_merge(oflist,array(__VA_ARGS__))


//called within func() and endfunc()
#define retflag(/*what flags*/...) \
                prependtolist($TheReturnStateList, __VA_ARGS__);

#define keepflags(var) \
{ \
        $RSL_var=&var; \
        _tIFnot( TRUE===boolIsReturnStateList(&$RSL_var) ); \
        foreach ($RSL_var as $val) { \
                if (kReturnStateList_type!==$val) { \
                        retflag($val); \
                } \
        } \
}

//returns yes or no
//#define isflag(whatflag) \
//                isvalue(whatflag, $AllReturnLists)

#define isvalue(whatflag,inwhatlist) \
                (TRUE===in_array(whatflag, inwhatlist, FALSE/*check types?=no*/)?yes:no)

function getvalue($var)
{
        if (TRUE===is_array($var)) {
                $ret='';
                foreach ($var as $v) {
                        $ret.="'$v' ";
                }
        } else {
                $ret="'$var'";
        }
        return $ret;
}

//supposed to return either yes or no ONLY!, if u need to return something use a &$var as a parameter
#define func(funcname,...) /*{{{*/ \
function &funcname \
        { \
                $funcnameRFZAHJ=#funcname; \
                $otherlevelsRFZAHJ=array(__VA_ARGS__); \
                deb(getalist($otherlevelsRFZAHJ, dbeg), $funcnameRFZAHJ.":begin..."); \
                $TheReturnStateList=array(kReturnStateList_type);//this sets this to array type and also flags this array as being a return type, needed on isGood() to make the diff between our array and system returned arrays

#define endnow(.../*more elements to add here*/) \
                { \
                        __( retflag(__VA_ARGS__) ); \
                        global $AllReturnLists;\
                        $theKey="vim ".getfile." +".getline;\
                        $AllReturnLists[$theKey]=$TheReturnStateList;\
                        debnl(getalist($otherlevelsRFZAHJ, dend) , $funcnameRFZAHJ.":done:isGood(".getvalue($TheReturnStateList).")===".isGood($TheReturnStateList));\
                        return $TheReturnStateList; \
                }/*}}}*/

// DO NOT append ";" to endfunc!!!
#define endfunc(.../* more elements */) /*{{{*/ \
                endnow(__VA_ARGS__) \
        }/*}}}*/

function boolIsReturnStateList($var)
{
        if (TRUE===is_array($var) && yes===isvalue(kReturnStateList_type, $var)) {
                return TRUE;
        } else {
                return FALSE;
        }
}

//never do if (isGood($var)) => always true, instead do _if ($var)  OR if (yes===isGood($var))
function isGood($var,$allowemptystr=no)
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
                        if ( TRUE===boolIsReturnStateList($var) ) {
                                global $AllReturnLists;
                                //print_r($var);
                                _tIF(yes===isvalue(yes,$var) && yes===isvalue(no, $var) );
                                _if (isvalue(yes, $var)) {
                                        return yes;
                                } else { 
                                        _if (isvalue(no, $var)) {
                                                return no;
                                        }
                                        _tIF("invalid kReturnStateList_type! neither yes, nor no");
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
} //func

function isNotGood($var,$allowemptystr=no)
{
        return (yes===isGood($var,$allowemptystr)?no:yes);
}



$maxDifferentReturnItems=0;//don't need to change this

function rdef($what)
{
        global $maxDifferentReturnItems;
        if (TRUE===defined($what)) {
                die("already defined $what".nl);
        }
        define($what,"$what");
        ++$maxDifferentReturnItems;
}

define(kReturnStateList_type,"kReturnStateList_type"/*this array is a ReturnStateList type; this element is a way for isGood() to determine that"*/);
//this is the kind that flags the return array from the function so that isGoods know it's a return value instead of a normal array returned by ie. fetchAll()

//define kConsts aka return types here:
rdef(kAlready);
rdef(kAdded);
rdef(kCreatedDBNodeNames);
rdef(kCreatedDBRelations);
rdef(kEmpty);


// vim: fdm=marker

#endif //header
//?>
