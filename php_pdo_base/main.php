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
#include "debugL0.php"
#include "color.php"
//#include "dmlL1fun.php"
#include "dmlphpL1.php"
#include "debugL1.php"


        beginprogram
        __( $dphp=new dmlphpL1 );
        __( $dphp->AddRel("A","B") );
        __( $dphp->AddRel("A","B") );
/*        __( $dc=new dmlL1 );
        //debug_zval_dump($dc);


        _ynif ($dc->fFirstTime) {
                deb(dinfo,"First time run!");
        } else {
                deb(dinfo,"...using prev. defined table");
        }
 */
        //_yntIFnot( $contents=file_get_contents("/home/emacs/phpnet.php") );
        __( $res=split("[ .,/\\\"\?\<\>&!;|\#\$\*\+\{\}=\(\)'`\n\-]",file_get_contents("debugL1.php")) );
        _yntIF(1===count($res));
        $i=2;
        $cnt=0;
        $prevval="";
//        _yntIFnot( $dc->OpenTransaction() );
        foreach ($res as $val) {
                     $val=trim($val);
                _ynif ($val) {//ie. non-empty
                        //if ($cnt % 15 == 0) {
                       // }

                   //_TRY(

                        _yntIFnot( $ret=$dphp->AddRel($prevval, $val) );
                        $prevval=$val;
                        //_yntIFnot( $ret=$dc->AddName($val) );
                        //_ynif (yes===isvalue(kAdded,$ret)) {
                        _ynif (no===isvalue(kAlready,$ret)) {
                                if ($i<6) {
                                        $i++;
                                } else {
                                        $i=2;
                                }
                                echo setcol($i).$val." ";
                        } else {
                                $i=1;
                                echo setcol($i).$val." ";
                        }
                        //usleep(100000);
                        $cnt++;//echo "cnt=".$cnt.nl;

                        //if ($cnt % 15 == 0) {
                                //_yntIFnot( $dc->CloseTransaction() );
                        //}

                   //, _yntIFnot( $dc->AbortTransaction());$aborted=yes ;break );//_TRY

                } //fi
        }
       echo nocol.nl;
/*        //if ( $cnt % 15 !== 0) { //left it open? if so close it
                _ynifnot($aborted) {
                        _yntIFnot( $dc->CloseTransaction() );
                }
        //}
       echo nocol.nl;



        _yntIFnot( $dc->IsName("if") );

        _yntIFnot( $dc->Show($result) );
        __( $arr=$result->fetchAll() );
        $count=count($arr);
        deb(dnormal, "$count times.");

        _TRY( $dc->DelName("if") );
        __( $dc->IsName("if") );

        _yntIFnot( $dc->Show($result) );
        __( $arr=$result->fetchAll() );
        $count=count($arr);
        deb(dnormal, "$count times.");
        print_r($dc->IsID("1"));

        $dc=null;//ie. dispose()
 */
        //$arc=array();
        echo redcol.nl;
        _yntIFnot( $dphp->GetOfChild_AllParents("if",$arc) );
        echo "Parents of 'if': ".getvalue($arc).nl;

        _yntIFnot( $dphp->GetOfParent_AllChildren("if",$arc) );
        echo "Children of 'if': ".getvalue($arc).nl;

        __( echo isGood($dphp->IsRel("text","if")).nl );
        __( echo getvalue($dphp->DelRel("if","yes")).nl );
        __( echo isGood($dphp->IsRel("if","yes")).nl );

        echo greencol.nl;
        _yntIFnot( $dphp->GetOfParent_AllChildren("if",$arc) );
        echo "Children of 'if' after del child 'yes': ".getvalue($arc).nl;

        __( echo getvalue($dphp->DelRel("text","if")).nl );echo greencol;
        _yntIFnot( $dphp->GetOfChild_AllParents("if",$arc) );
        echo "Parents of 'if' after parent 'text' del: ".getvalue($arc).nl;


        echo purplecol.nl;
        _yntIFnot( $dphp->GetOfChild_AllParents("not",$arc) );
        echo "Parents of 'not': ".getvalue($arc).nl;

        _yntIFnot( $dphp->DelAllChildrenOf("if") );

        __( $dphp->GetOfParent_AllChildren("if",$arc) );
        echo "Children after del all children of 'if': ".getvalue($arc).nl;

        _yntIFnot( $dphp->GetOfChild_AllParents("not",$arc) );
        echo "Parents of 'not', not 'if'; after del: ".getvalue($arc).nl;


        echo greencol.nl;
        __( $dphp->GetOfParent_AllChildren("program",$arc) );
        echo "Children of 'program', before del 'if': ".getvalue($arc).nl;

        _yntIFnot( $dphp->GetOfChild_AllParents("if",$arc) );
        echo "Parents of 'if', before del: ".getvalue($arc).nl;

        _yntIFnot( $dphp->DelAllParents("if") );

        __( $dphp->GetOfChild_AllParents("if",$arc) );
        echo "Parents of 'if', after del: ".getvalue($arc).nl;

        __( $dphp->GetOfParent_AllChildren("program",$arc) );
        echo "Children of 'program', after del 'if': ".getvalue($arc).nl;

        $dphp=null;//ie. dispose()

        echo nocol.nl;


        funcl1 (AnotherFunc,($someparam))
        {
                if (is_string($someparam)) {
                        setretflagl1($someparam);
                } else {
                        setretflagl1("not a string");
                }
        }endfuncl1("done")

        funcl1 (GetName,(&$name, $id))
        {
                print_r($name);
                print_r($id);
                echo nl;
                if ($id==1) {
                        addretflagl1(yes,no,kAdded);
                }
                if ($id==2) {
                        delretflagl1(no);
                        addretflagl1("a");
                }
                if ($id==3) {
                        setretflagl1(no);
                }
                countretflags($numretflags);
                if ($numretflags <= 0) {
                        setretflagl1("other");
                }
        }endfuncl1("ReachedEndNormally")

        AnotherFunc("return1");
        AnotherFunc(1);
        AnotherFunc(2);

        $a="a";
        _yntIFnot( $c=$debugL1->ShowTreeOfParentsForChild( GetName($a,"a") ) );
        echo isValidReturn($c);
        print_r( isValidReturn( GetName($a,"b") ));
        echo isValidReturn( GetName($a,"1") );
        echo isValidReturn( GetName($a,"3") );
        echo isValidReturn( GetName($a,"2") );
        echo isValidReturn( GetName($a,"c") );
        global $debugL1;
        _yntIFnot( $debugL1->ShowTreeOfChildrenForParent(kAllFunctions) );
        _yntIFnot( $debugL1->ShowTreeOfChildrenForParent(kAllReturns) );
        _yntIFnot( $debugL1->ShowTreeOfParentsForChild("a") );
        _yntIFnot( $debugL1->ShowTreeOfParentsForChild(yes) );



        //print_r($AllReturnLists);

        endprogram
// vim: fdm=marker
?>
