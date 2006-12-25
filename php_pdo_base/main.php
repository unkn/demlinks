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
//#include "dmlL1fun.php"
#include "dmlphpL1.php"


        beginprogram
        __( $dphp=new dmlphpL1 );
        __( $dphp->SetRel("A","B") );
        __( $dphp->SetRel("A","B") );
/*        __( $dc=new dmlL1 );
        //debug_zval_dump($dc);


        _if ($dc->fFirstTime) {
                deb(dinfo,"First time run!");
        } else {
                deb(dinfo,"...using prev. defined table");
        }
 */
        //_tIFnot( $contents=file_get_contents("/home/emacs/phpnet.php") );
        __( $res=split("[ .,/\\\"\?\<\>&!;|\#\$\*\+\{\}=\(\)'`\n\-]",file_get_contents("debug.php")) );
        _tIF(1===count($res));
        $i=2;
        $cnt=0;
        $prevval="";
//        _tIFnot( $dc->OpenTransaction() );
        foreach ($res as $val) {
                     $val=trim($val);
                _if ($val) {//ie. non-empty
                        //if ($cnt % 15 == 0) {
                       // }

                   //_TRY(

                        _tIFnot( $ret=$dphp->SetRel($prevval, $val) );
                        $prevval=$val;
                        //_tIFnot( $ret=$dc->AddName($val) );
                        //_if (yes===isvalue(kAdded,$ret)) {
                        _if (no===isvalue(kAlready,$ret)) {
                                if ($i<6) {
                                        $i++;
                                } else {
                                        $i=2;
                                }
                                echo setcol($i).$val." ";
                                //_tIFnot( $ret=$dc->AddName($val) );
                                //print_r($ret);
                                //echo isflag(kAlready, $ret);
                        } else {
                                $i=1;
                                echo setcol($i).$val." ";
                        }
                        //usleep(100000);
                        $cnt++;//echo "cnt=".$cnt.nl;

                        //if ($cnt % 15 == 0) {
                                //_tIFnot( $dc->CloseTransaction() );
                        //}

                   //, _tIFnot( $dc->AbortTransaction());$aborted=yes ;break );//_TRY

                } //fi
        }
       echo nocol.nl;
/*        //if ( $cnt % 15 !== 0) { //left it open? if so close it
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
        print_r($dc->IsID("1"));

        $dc=null;//ie. dispose()
 */
        //$arc=array();
        echo redcol.nl;
        _tIFnot( $dphp->GetAllParents("if",$arc) );
        echo "Parents of 'if': ".getvalue($arc).nl;

        _tIFnot( $dphp->GetAllChildren("if",$arc) );
        echo "Children of 'if': ".getvalue($arc).nl;

        __( echo isGood($dphp->IsRel("text","if")).nl );
        __( echo getvalue($dphp->DelRel("if","yes")).nl );
        __( echo isGood($dphp->IsRel("if","yes")).nl );

        echo greencol.nl;
        _tIFnot( $dphp->GetAllChildren("if",$arc) );
        echo "Children of 'if' after del child 'yes': ".getvalue($arc).nl;

        __( echo getvalue($dphp->DelRel("text","if")).nl );echo greencol;
        _tIFnot( $dphp->GetAllParents("if",$arc) );
        echo "Parents of 'if' after parent 'text' del: ".getvalue($arc).nl;


        echo purplecol.nl;
        _tIFnot( $dphp->GetAllParents("not",$arc) );
        echo "Parents of 'not': ".getvalue($arc).nl;

        _tIFnot( $dphp->DelAllChildren("if") );

        __( $dphp->GetAllChildren("if",$arc) );
        echo "Children after del all children of 'if': ".getvalue($arc).nl;

        _tIFnot( $dphp->GetAllParents("not",$arc) );
        echo "Parents of 'not', not 'if'; after del: ".getvalue($arc).nl;


        echo greencol.nl;
        __( $dphp->GetAllChildren("program",$arc) );
        echo "Children of 'program', before del 'if': ".getvalue($arc).nl;

        _tIFnot( $dphp->GetAllParents("if",$arc) );
        echo "Parents of 'if', before del: ".getvalue($arc).nl;

        _tIFnot( $dphp->DelAllParents("if") );

        __( $dphp->GetAllParents("if",$arc) );
        echo "Parents of 'if', after del: ".getvalue($arc).nl;

        __( $dphp->GetAllChildren("program",$arc) );
        echo "Children of 'program', after del 'if': ".getvalue($arc).nl;

        $dphp=null;//ie. dispose()

        echo nocol.nl;

        //print_r($AllReturnLists);

        endprogram
// vim: fdm=marker
?>
