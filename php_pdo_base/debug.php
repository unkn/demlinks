//<?php
#ifndef DEBUG_PHP
#define DEBUG_PHP

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
        if (defined($what)) {
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

//enabling specific debug levels
dseton(dlowlevel);
dseton(dinfo);
//dseton(dbeg);
//dseton(dend);
dseton(dnormal);
//dseton(ddel);
dseton(dcrea);
//dseton(dis);
//dseton(dadd);
//dseton(dget);
//dseton(dbegtr);//transaction
dseton(dabtr);
//dseton(dendtr);
dseton(dconstr);
dseton(ddestr);
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

#define throw_exception(_a) throw new Exception(_a);

#ifdef IMMEDIATE_REPORTS
        #define except(_a) echo _a; throw_exception(_a);
#else
        #define except(_a) throw_exception(_a);
#endif

#define quitmsg nl.redcol."vim ".getfile." +".getline.nl.tab.greencol.$e->getmessage().nocol.nl

#define dropmsg(_a) nl.greencol."vim ".getfile." +".getline.nl.tab.purplecol._a.nocol.nl

#define _TRY(thiscode, ... /*exec_this_if_fail*/) { try { thiscode; } catch(PDOException $e) { __VA_ARGS__; } catch(Exception $e) { __VA_ARGS__; }}

#define __(thiscode) { _TRY(thiscode, except(quitmsg) ) }

//FIXME: try to catch "PHP Fatal error: " too!

#define _tIFnot(__a) { _ifnot( isGood(__a) ) { except( dropmsg("_tIFnot( ".#__a." )") ) } }

#define _tIF(__a) { _if( isGood(__a) ) { except( dropmsg("_tIF( ".#__a." )") ) } }

//boolfunc is a function that returns something, usually anything that can be tested by isGood() but mostly by convention just yes or no  || ok or bad (same thing)
#define _if(boolfunc) __( $_this_var_accessible_in_caller = boolfunc ); if (yes===isGood($_this_var_accessible_in_caller))

#define _ifnot(boolfunc) __( $_this_var_accessible_in_caller = boolfunc ); if (no===isGood($_this_var_accessible_in_caller))

#define beginprogram try { deb(dbeg,"Program begins...");
#define endprogram deb(dend,"Ending program..."); } catch (Exception $e) { die(quitmsg); }

#ifdef debugon
        #define debshow(level,text,textappend) echo nl.bluecol."debLev".greencol.level.nocol." ".#text." (vim ".getfile." +".getline.")" . textappend;
        #define dEbS(level,text,textappend) { \
                global $debugar; \
                $levelar=level; \
                $text=text; \
                if (is_array($levelar)) { \
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
        #define deb(level,text) { dEbS(level,text,"") }
        #define debnl(level,text) { dEbS(level,text,nl) }

#else //to bad we gotta repeat interface(?) for both
        #define deb(level,text)
#endif


//supposed to return either yes or no ONLY!, if u need to return something use a &$var as a parameter
#define func(funcname,...) function funcname { $funcnameRFZAHJ=#funcname; $otherlevelsRFZAHJ=array(__VA_ARGS__); deb(array_merge((array)dbeg, $otherlevelsRFZAHJ), $funcnameRFZAHJ.":begin...");

#define endfunc(retval) $statusRFZAHJ=retval; debnl(array_merge((array)dend, $otherlevelsRFZAHJ) , $funcnameRFZAHJ.":done:isGood(".$statusRFZAHJ.")===".isGood($statusRFZAHJ)); return isGood($statusRFZAHJ); }
// DO NOT append ";" to endfunc!!!

//never do if (isGood($var)) => always true, instead do _if ($var)  OR if (yes===isGood($var))
function isGood($var,$allowemptystr=no)
{
        if (is_null($var)) {
                return no;
        }
        if (FALSE===isset($var)){
                return no;
        }
//prior all:
        if ((yes===$var) or (no===$var)) {
                return $var;
        }
//eof

        if (is_string($var) || is_array($var)) {
                if ((yes===$allowemptystr) || ( FALSE===empty($var) )) { //non empty
                        return yes;
                } else {
                        return no;
                }
        }
        if (is_numeric($var)) { //numeric
                return yes;
        }
        if ( is_bool($var)) {
                return (TRUE===$var?yes:no);
        }
        if ( is_object($var) ){
                return yes;
        }

        return yes; //any other object
} //func

function isNotGood($var,$allowemptystr=no)
{
        return (!isGood($var,$allowemptystr));
}

#endif //header
//?>
