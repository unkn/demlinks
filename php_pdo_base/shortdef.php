//<?php
#ifndef SHORTDEF_PHP
#define SHORTDEF_PHP

#include "color.php"

        define(nl,"\n");
        define(tab,"\t");
        define(space," ");
        define(emptystr,"");
        define(yes,TRUE);
        define(no,FALSE);

#define many(...) array(__VA_ARGS__)

function showbool($bo)
{
        return ($bo==TRUE?greencol."OK".nocol:redcol."FAIL".nocol);
}

function evalgood($var,$allowemptystr=no)
{
        if (is_string($var) or is_array($var)) {
                if ((yes==$allowemptystr) || (! empty($var) )) { //non empty
                        return TRUE;
                } else {
                        return FALSE;
                }
        }
        if (is_numeric($var)) { //numeric
                return TRUE;
        }
        if ( is_bool($var)) {
                return $var;
        }
        return TRUE; //any other object
} //func


#endif //header
//?>
