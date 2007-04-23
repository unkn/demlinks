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
* Description: demlinks level 0 defines, also used by level 1
*
***************************************************************************}}}*/


define('dbhost',"localhost");
define('dbname',"demlinks_db");
define('dbuser',"demlinks_user");
define('dbpwd',"dml");
define('dbtracefile',"./main.log");
/*
define('dNodeNames',"NodeNames");//table name
define('dRelations',"Relations");//table name
define('dNodeName',"NodeName");//table name
define('dParentNodeID',"ParentID");//table name
define('dChildNodeID',"ChildID");//table name
define('dNodeID',"ID");//table name
*/
//define('paramprefix',":");
//define('paramNodeName',paramprefix.dNodeName);
//define('paramNodeID',paramprefix.dNodeID);

        /* quote functions {{{*/
        function fieldquote($whatfield)
        {
                return tablequote($whatfield);
        }

        function dataquote($whatval)//' ' quotes are still necessary
        {
                return pg_escape_string($whatval);
        }

        function valuequote($val)
        {
                return "'".dataquote($val)."'";
        }

        function tablequote($whattable)
        {
                return '"'.dataquote($whattable).'"';
        }/*}}}*/

function ddef($var,$table=false)
{
        define('d'.$var,$var);
        $q=(true===$table?tablequote($var):fieldquote($var));
        define('q'.$var, $q);
}

ddef('NodeNames',true);//created qNodeNames and dNodeNames where the former is quoted(dNodeNames) and the latter is ='NodeNames'(string)
ddef('Relations',true);
ddef('Name');
ddef('ParentID');
ddef('ChildID');
ddef('ID');
ddef('GetName');
ddef('GetID');
ddef('DelID');
ddef('DelName');
ddef('EnsureName');
ddef('Show');

// vim: fdm=marker
?>
