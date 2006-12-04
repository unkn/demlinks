//<?php
#ifndef COLOR_PHP
#define COLOR_PHP

#define setcol(col) "\x1B[3".#col."m"
define(nocol,setcol(9));
//define(nocol,"\x1B[39m");
define(browncol,setcol(3));
define(redcol,setcol(1));
define(bluecol,setcol(4));
define(greencol,setcol(2));
define(purplecol,setcol(5));

#endif //header
//?>
