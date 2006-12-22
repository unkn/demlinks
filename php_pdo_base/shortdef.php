//<?php
#ifndef SHORTDEF_PHP
#define SHORTDEF_PHP

#include "color.php"

        define(nl,"\n");
        define(tab,"\t");
        define(space," ");
        define(emptystr,"");
        define(yes,greencol."yes".nocol);
        define(no,redcol."no".nocol);
        define(ok,yes);//these must be equivalent:  ok~yes and bad~no   where yes="yes" and no="no" or wtw
        define(bad,no);//certain operations interchange ok with yes, that's why!

//#define many(...) array(__VA_ARGS__)

#endif //header
//?>
