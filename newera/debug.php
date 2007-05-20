<?php
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
* Description: useful debug functions
*
***************************************************************************}}}*/

require_once("shortdef.php");

function pg_die($msg)
{
        debug_print_backtrace();
        die($msg .": ". pg_last_error());
}


function report($msg)
{
        echo "Report: ".$msg.nl;
        debug_print_backtrace();
}

function exceptifnot($bool,$msg="IFnot")
{//handling not just bool
        if (failed($bool)) {
                except($msg);
        }
}

function exceptif($bool,$msg="IF")
{//handling not just bool
        if (!failed($bool)) {
                except($msg);
        }
}

function except($msg)
{
        echo "Except: ".$msg.nl;
        throw new Exception($msg);
}

function failed($var,$allowemptystr=false)
{
        return !allok($var,$allowemptystr);
}

function allok($var,$allowemptystr=false)
{//don't use exceptif inside this func
        if(yes!==ok || no!==bad) {//safety precaution
                except('yes!==ok || no!==bad');
        }

        if (true===is_null($var)) {
                return false;
        }
        if ( (false===isset($var)) || (no===$var) ){
                return false;
        }
//prior all:
        if (yes===$var) {
                return true;
        }
//eof

        if (true===is_string($var) || true===is_array($var)) {
                if ((true===$allowemptystr) || ( false===empty($var) )) { //non empty (array or str: both cases handled here)
                        if ( isValidReturn($var) ) { //is_array($var) && in_array(kReturnStateList_type,$var) ) {
                                if (in_array(yes,$var) && in_array(no, $var) ) {
                                        except("both yes and no present, bug!");
                                }
                                if (in_array(yes, $var)) {
                                        return true;
                                } else {
                                        if (in_array(no, $var)) {
                                                return false;
                                        }
                                        except("invalid kReturnStateList_type! neither yes, nor no");
                                }
                        }
                        return true; //a non kReturnStateList_type
                } else {
                        return false;
                }
        }
        if (true===is_numeric($var)) { //numeric
                return true;
        }
        if (true===is_bool($var)) {
                return $var;//(true===$var?true:false);
        }
        if (true===is_object($var) ){
                return true;
        }

        return true; //any other object
} //funcL0


function retValue($var)
{
        $ret='';
        if (true===is_array($var)) {
                foreach ($var as $v) {
                        if (!empty($ret)) {
                                $ret.=" ";
                        }
                        $ret.="'$v'";
                }
        } else {
                if (!empty($var)) {
                        $ret="'$var'";
                }
        }
        return $ret;
}

function show($val)
{
        echo retValue($val).nl;
}

function initret(&$ret)
{
        $ret=array(kReturnStateList_type);
}

function isValidReturn($ret)
{
        return is_array($ret) && in_array(kReturnStateList_type,$ret);
}

function ensureexists(&$towhatarray, $listofvalues/*string or array of strings*/)/*{{{*/
//makes sure that values exist in array
//all values are copied, not referenced! because, in my view, references are too subtle in php to be used consistently
{
        exceptifnot(is_array($towhatarray),__LINE__.__FILE__);
        if (!is_array($listofvalues)) {
                exceptifnot(is_string($listofvalues),__LINE__.__FILE__);//if not array then must be string
                $listofvalues=array($listofvalues);
        }

        foreach ( $listofvalues as $val ) {
                //print_r($val);
                if (no !== $val) {
                        //print_r($val);
                        exceptif(failed($val));
                        //_yntIF(ynIsNotGood($val));
                }

                if (! in_array($val, $towhatarray) ) {
                        $towhatarray[]=$val;
                }
        }
}/*}}}*/


$maxDifferentReturnItems=0;//don't need to change this

function rdef($what)/*{{{*/
{
        global $maxDifferentReturnItems;
        if (TRUE===defined($what)) {
                die("already defined $what".nl);
        }
        define("$what","$what");
        ++$maxDifferentReturnItems;
}/*}}}*/

define('kReturnStateList_type',"flagged as kReturnStateList_type"/*this array is a ReturnStateList type; this element is a way for failed() to determine that"*/);
//this is the kind that flags the return array from the function so that failed() knows it's a return value instead of a normal array returned by ie. fetchAll()
//define kConsts aka return types here:
rdef('kAlready');
rdef('kAdded');
rdef('kCreatedDBNodeNames');
rdef('kCreatedDBRelations');
rdef('kEmpty');
rdef('kOneElement');//one element detected, expected list; but it's ok even this way, we made that element into an one element list
rdef('kWasUnset');//prior to call to a function, some var was unset, now after call it's set



// vim: fdm=marker

?>
