//<?php
//header starts
#ifndef DMLPHPL1_PHP
#define DMLPHPL1_PHP

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
* Description: demlinks applied in coding(php) ie. using demlinks idea(s) to code in php
*               this is Level 1, the next higer level
*
***************************************************************************}}}*/


#include "term.php"
#include "served.php"
#include "shortdef.php"
#include "debugL0.php"
#include "color.php"
#include "dmlphpL0.php"

#define func1(funcdef) \
        func0(funcdef)

#define endfunc1(...) \
        endfunc0(__VA_ARGS__)

#define endnow1(...) \
        endnow0(__VA_ARGS__)

#define func1re(funcdef) \
        func0re(funcdef)

#define endfunc1re(...) \
        endfunc0re(__VA_ARGS__)

#define addretflag1(...) \
        addretflagL0(__VA_ARGS__)

#define keepflags1(...) \
        keepflagsL0(__VA_ARGS__)

class dmlphpL1 extends dmlphpL0 {

        func1 (__construct())/*{{{*/
        {
                __( $ar=parent::__construct() );
                keepflags1($ar);
        }endfunc1()/*}}}*/

        func1 (__destruct())/*{{{*/
        {
                __( $ar=parent::__destruct() );
                keepflags1($ar);
        }endfunc1()/*}}}*/

                //PC=parent, child (the parameters are in this order)
        func1 (EnsurePCRel($parent,$child))/*{{{*/
        {//a relation will only exist once
                _yntIFnot($this->ynTestElementInvariants($parent));
                _yntIFnot($this->ynTestElementInvariants($child));
                //well, no transaction... too bad
                _yntIFnot( $ar=$this->addChild($parent, $child) );
                keepflags1($ar);
                _yntIFnot( $ar=$this->addParent($child, $parent) );
                keepflags1($ar);
        }endfunc1()/*}}}*/

        func1 (DelPCRel($parent,$child))/*{{{*/
        {
                _yntIFnot($this->ynTestElementInvariants($parent));
                _yntIFnot($this->ynTestElementInvariants($child));
                //well, no transaction... too bad
                _yntIFnot( $ar=$this->delChild($parent, $child) );
                keepflags1($ar);
                _yntIFnot( $ar=$this->delParentFromChild($parent, $child) );
                keepflags1($ar);
        }endfunc1()/*}}}*/

        func1 (SetOfParent_Children($parent,$children))/*{{{*/
        {//overwrites all children
                _yntIFnot($this->ynTestElementInvariants($parent));

                _yntIFnot( $this->DelAllChildrenOf($parent) );
                _yntIFnot( $ar=$this->AppendToParent_Children($parent, $children) );
                keepflags1($ar);
        }endfunc1()/*}}}*/

        func1 (AppendToParent_Children($parent,$children))/*{{{*/
        {//addition
                _yntIFnot($this->ynTestElementInvariants($parent));
                _ifnot( is_array($children) ) {
                        $children=array($children);
                        addretflag1(kOneElement);
                }
                foreach ($children as $child) {
                        _yntIFnot( $ar=$this->EnsurePCRel($parent, $child) );
                        keepflags1($ar);
                        /*_if (isValue_InList(kAlready,$ar) ) {
                                addretflag1();
                }*/
                }
        }endfunc1(yes)/*}}}*/

        func1 (DeleteFromParent_Children($parent,$children))/*{{{*/
        {//substraction
                _yntIFnot($this->ynTestElementInvariants($parent));
                _ifnot( is_array($children) ) {
                        $children=array($children);
                }
                foreach ($children as $child) {
                        _yntIFnot( $this->DelPCRel($parent, $child) );
                }
        }endfunc1(yes)/*}}}*/

        func1re (ShowTreeOfChildrenForParent($parent, $startlevel=0))/*{{{*/
        {
                _yntIFnot($this->ynTestElementInvariants($parent));
                for ($i=0; $i<$startlevel; $i++) {
                        echo space;
                }
                if ($startlevel>0) {
                        echo "|>";
                } else {
                        echo "Children of:";
                }
                echo " \"$parent\"".nl;

                _ynif ( $this->GetOfParent_AllChildren($parent,$children) ) {
                        foreach ($children as $val) {
                                _yntIFnot( $this->ShowTreeOfChildrenForParent($val, 1+$startlevel) );
                        }
                }
        }endfunc1re(yes)/*}}}*/

        func1 (ShowTreeOfParents_WithID_ForChild($treemenuid,$child))/*{{{*/
        {
                _tIF(empty($treemenuid));
                //$treemenuid=preg_replace('/([\'\"])/',preg_quote('\\').'\1',$treemenuid);
                $treemenuid=rawurlencode($treemenuid);
                _yntIFnot($this->ynTestElementInvariants($child));

                //root
                if (IsTerminal()) {
                        echo "Parents of:";
                } else if (Served()){
                        //$treemenuid='TreeMenu for Parents of '.$child;
                        //echo '<a href="javascript:ddtreemenu.flatten(\''.$treemenuid.'\', \'expand\')">Expand All</a> | <a href="javascript:ddtreemenu.flatten(\''.$treemenuid.'\', \'contract\')">Contract All</a>'.rnl;

                        echo '<ul id="'.$treemenuid.'" class="treeview">'.rnl;
                }
                _yntIFnot( $ar=$this->parseTree($child) );
                keepflags1($ar);

                if (Served()) {
                        echo "</ul>".rnl;
                        echo '<script type="text/javascript">'.rnl;
                        echo '//ddtreemenu.createTree(treeid, enablepersist, opt_persist_in_days (default is 1))'.rnl;
                        echo 'ddtreemenu.createTree("'.$treemenuid.'", true)'.rnl;
                        echo 'Drag.init(document.getElementById("'.$treemenuid.'"));'.rnl;
                        echo '</script>'.rnl;
                }
        }endfunc1()/*}}}*/

        private func1re (parseTree($child, $startlevel=0))/*{{{*/
        {
                _yntIFnot($this->ynTestElementInvariants($child));
                        for ($i=0; $i<$startlevel; $i++) {
                                if (IsTerminal()) {
                                        echo rspace;
                                } else {
                                        echo rtab;
                                }
                        }
                if (Served()) {
                        $uec=rawurlencode($child);
                }

                if ($startlevel>0) {//non-root
                        if (IsTerminal()) {
                                echo "|<";
                        }
                }

                _ynif ( $this->GetOfChild_AllParents($child,$parents) ) { //folder
                        if (IsTerminal()) {
                                echo " \"$child\"".nl;
                        } else if (Served()) {
                                echo '<li id="'.$uec.'" style="background-image: url(closed.png);" class="'.($startlevel==0?'root':'node').'">'.$child.rnl;
                                echo rtab.'<ul id="'.$uec.'" style="display: none;" rel="closed">'.rnl;
                        }
                        foreach ($parents as $val) {
                                _yntIFnot( $this->parseTree($val, 1+$startlevel) );
                        }
                        if (Served()) {
                                echo rtab."</ul>".rnl;
                                echo "</li>".rnl;
                        }
                } else {//not a folder  ==leaf!
                        if (IsTerminal()) {
                                echo " \"$child\"".nl;
                        } else if (Served()){
                                echo '<li id="'.$uec.'" class="leaf">'.$child.'</li>'.rnl;
                        }
                }

        }endfunc1re(yes)/*}}}*/



}//endclass

define('kNull','NullPtr');
define('nil',emptystr);

class dmlphpL1_Pointer {//random access pointer; can point to any ID, this makes connection between the php code(ie. a php var) and the demlinks environment which is held in php arrays; so it keeps a live link between php var and demlinks environment dmlphp; WTW!
        protected $fPointee;//by convention empty() means NULL pointer
        protected $fDMLEnv;
        protected $fType;//kParent or kChild

        func1 (__construct($dmlenv,$type,$pointee/*=nil*/))/*{{{*/
        {
                $this->fDMLEnv=&$dmlenv;
                _tIFnot(is_object($this->fDMLEnv));
                _yntIFnot( $this->fDMLEnv->ynTestTypeInvariants($type) );
                $fType=$type;
                _ynifnot($this->isNil($pointee)) {
                        _yntIFnot($this->fDMLEnv->ynTestElementInvariants($pointee));
                }
                $this->fPointee=$pointee;//must be assigned prior to calling IsNull
        }endfunc1(yes)/*}}}*/

        func1 (__destruct())/*{{{*/
        {
        }endfunc1(yes)/*}}}*/

        func1 (IsNull())/*{{{*/
        {
                _ynif($this->isNil($this->fPointee)) {
                        addretflag1(yes);
                } else {
                        addretflag1(no);
                }
        }endfunc1()/*}}}*/

        protected func1 (isNil($pointee))/*{{{*/
        {
                if (nil===$pointee) {
                        addretflag1(yes);
                } else {
                        _tIF(empty($pointee));//something is wrong! because empty(nil)===TRUE
                        addretflag1(no);
                }
        }endfunc1()/*}}}*/

        func1 (SetNull())/*{{{*/
        {
                $this->SetPointee(nil);
                //$this->fPointee=nil;
        }endfunc1(yes)/*}}}*/

        func1 (SetPointee($zpointee))/*{{{*/
        {
                _yntIFnot($this->fDMLEnv->ynTestElementInvariants($zpointee));
                $this->fPointee=$zpointee;
        }endfunc1(yes)/*}}}*/

        func1 (GetPointee(&$zpointee))/*{{{*/
        {
                _ynif ($this->IsNull()) {
                        endnow1(no, kNull);
                }
                $zpointee=$this->fPointee;
        }endfunc1(yes)/*}}}*/

        func1 (GetEnvironment(&$env))/*{{{*/
        {
                $env=&$this->fDMLEnv;
        }endfunc1(yes)/*}}}*/
}


#endif //header ends
// vim: fdm=marker
//?>
