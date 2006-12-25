//run with ./2
//<?php this is for vim
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
* Description: main program
*
***************************************************************************}}}*/

#include "shortdef.php"
#include "debug.php"
#include "color.php"
#include "dmlL1fun.php"


        beginprogram
        __( $dc=new dmlL1 );
        //debug_zval_dump($dc);


        _if ($dc->fFirstTime) {
                deb(dinfo,"First time run!");
        } else {
                deb(dinfo,"...using prev. defined table");
        }

        //_tIFnot( $contents=file_get_contents("/home/emacs/phpnet.php") );
        __( $res=split("[ .,/\\\"\?\<\>&!;|\#\$\*\+\{\}=\(\)'`\n\-]",file_get_contents("debug.php")) );
        _tIF(1===count($res));
        $i=2;
        $cnt=0;
        _tIFnot( $dc->OpenTransaction() );
        foreach ($res as $val) {
                     $val=trim($val);
                _if ($val) {//ie. non-empty
                        //if ($cnt % 15 == 0) {
                       // }

                   _TRY(

                        _tIFnot( $ret=$dc->AddName($val) );
                        _if (yes===isvalue(kPhysicallyAdded,$ret)) {
                                if ($i<6) {
                                        $i++;
                                } else {
                                        $i=2;
                                }
                                echo setcol($i).$val." ";
                                //_tIFnot( $ret=$dc->AddName($val) );
                                //print_r($ret);
                                //echo isflag(kAlreadyExists, $ret);
                        } else {
                                $i=1;
                                echo setcol($i).$val." ";
                        }
                        //usleep(100000);
                        $cnt++;//echo "cnt=".$cnt.nl;

                        //if ($cnt % 15 == 0) {
                                //_tIFnot( $dc->CloseTransaction() );
                        //}

                   , _tIFnot( $dc->AbortTransaction());$aborted=yes ;break );//_TRY

                } //fi
        }
        //if ( $cnt % 15 !== 0) { //left it open? if so close it
                _ifnot($aborted) {
                        _tIFnot( $dc->CloseTransaction() );
                }
        //}
        echo nocol.nl;



        _tIFnot( $dc->IsName("if") );

        _tIFnot( $dc->Show($result) );
        __( $arr=$result->fetchAll() );
        $count=count($arr);
        deb(dnormal, "$count times.");

        _TRY( $dc->DelName("if") );
        __( $dc->IsName("if") );

        _tIFnot( $dc->Show($result) );
        __( $arr=$result->fetchAll() );
        $count=count($arr);
        deb(dnormal, "$count times.");

        $dc=null;//ie. dispose()

        echo nl;

        //print_r($AllReturnLists);

        endprogram
// vim: fdm=marker
?>
