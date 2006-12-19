//<?php
#ifndef DEBUG_PHP
#define DEBUG_PHP

#include "shortdef.php"
#include "color.php"
        /*require_once("shortdef.php");
        require_once("color.php");*/

#define debugon
//#define alldebugon

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

//enabling specific debug levels
dseton(dlowlevel);
dseton(dinfo);
//dseton(dbeg);
//dseton(dend);
dseton(dnormal);
dseton(ddel);
dseton(dcrea);
//dseton(dis);
//dseton(dadd);
//dseton(dget);
dseton(dbegtr);//transaction
dseton(dabtr);
dseton(dendtr);

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


//now the line numbers match with those reported on error(s) when using _c() and _t()

#define getline browncol.__LINE__.nocol
#define getfile browncol.__FILE__.nocol

#define except(_a) throw new Exception(_a);

#define quitmsg nl.redcol."vim ".getfile." +".getline.nl.tab.greencol.$e->getmessage().nocol.nl

#define dropmsg(_a) nl.greencol."vim ".getfile." +".getline.nl.tab.purplecol._a.nocol.nl

#define _c(__a) { try { __a; } catch(PDOException $e) { except(quitmsg); } catch(Exception $e) { except(quitmsg) }}

//FIXME: try to catch "PHP Fatal error: " too!

#define _t(__a) { _ifnot( __a ) { except( dropmsg("failed: empty(return)") ) } }

#define _if(boolfunc) _c( $_this_var_accessible_in_caller_func = boolfunc ); if (evalgood($_this_var_accessible_in_caller_func))

#define _ifnot(boolfunc) _c( $_this_var_accessible_in_caller_func = boolfunc ); if (FALSE==evalgood($_this_var_accessible_in_caller_func))

#define beginprogram try { deb(dbeg,"Program begins...");
#define endprogram deb(dend,"Ending program..."); } catch (Exception $e) { die(quitmsg); }

#ifdef debugon
        #define debshow(level,text) echo nl.bluecol."debLev".greencol.level.nocol." ".text." (vim ".getfile." +".getline.")";
        #define deb(level,text) { \
                global $debugar; \
                $levelar=level; \
                $text=text; \
                if (is_array($levelar)) { \
                        foreach ($levelar as $key) { \
                                if (TRUE == $debugar[$key]) { \
                                        debshow($key,$text);\
                                } \
                        } \
                } else { \
                        if (TRUE == $debugar[level]) { \
                                debshow($levelar,$text); \
                        } \
                }\
        }

#else //to bad we gotta repeat implementation for both
        #define deb(level,text)
#endif


#define func(funcname,...) function funcname { $funcnameRFZAHJ=#funcname; deb(many(dbeg,__VA_ARGS__),$funcnameRFZAHJ.":begin...");

#define endfunc(retval,...) deb(many(dend,__VA_ARGS__),$funcnameRFZAHJ.":done:".showbool(retval)); return retval; }
// DO NOT append ";" to endfunc!!!



#endif //header
//?>
