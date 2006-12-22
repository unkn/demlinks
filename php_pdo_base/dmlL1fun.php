//<?php
//dmlL1functions
//header starts
#ifndef DMLL1FUN_PHP
#define DMLL1FUN_PHP

#include "shortdef.php"
#include "debug.php"
//#include "dmlL0def.php"
#include "dmlL0fun.php"
#include "color.php"


class dmlL1 extends dmlL0
{
        private $fParamNodeName;
        private $sqlGetNodeID;
        private $fPrepGetNodeID;

        private $sqlNewNode;
        private $fPrepNewNode;//prepared statement handler

        func (__construct(), dconstr)/*{{{*/
        {
                parent::__construct();
                //--------- get ID by Name
                $this->sqlGetNodeID = 'SELECT '.$this->qNodeID.' FROM '.$this->qNodeNames.' WHERE '.$this->qNodeName.' = '.paramNodeName;
                _tIFnot( $this->fPrepGetNodeID = $this->fDBHandle->prepare($this->sqlGetNodeID) );
                _tIFnot( $this->fPrepGetNodeID->bindParam(paramNodeName, $this->fParamNodeName, PDO::PARAM_STR) );
                //---------
                $this->sqlNewNode = 'INSERT INTO '.$this->qNodeNames.' ('.$this->qNodeName.') VALUES ('.paramNodeName.')';//table name needs to be quoted

                _tIFnot( $this->fPrepNewNode = $this->fDBHandle->prepare($this->sqlNewNode) );//can't prepare unless the table already exists!
                _tIFnot( $this->fPrepNewNode->bindParam(paramNodeName, $this->fParamNodeName, PDO::PARAM_STR) ); //, PDO::PARAM_INT);
                //---------

        }endfunc(yes)/*}}}*/

        func (__destruct(), ddestr)/*{{{*/
        {
                parent::__destruct();
        }endfunc(yes)/*}}}*/

        func (AddName($nodename),dadd)/*{{{*/
        {
                _tIFnot( isGood($nodename) );//must not be empty or so; if it is then maybe's a bug outside this func provided user shall never call this func with an empty param value
                _ifnot ($this->GetID($id,$nodename)) {
                        deb(ddbadd,"attempting physical addition: ".$nodename);
                        $this->fParamNodeName=$nodename;
                        _tIFnot( $this->fPrepNewNode->execute() );//error here? it probably already exists! error in GetID maybe
                        deb(ddbadd,greencol."succeded".nocol." physical addition: ".$nodename);
                }
        }endfunc(ok)/*}}}*/

        func (GetID(&$id,$nodename),dget)// returns ID by Name /*{{{*/
        {
                _tIFnot($nodename);
                $this->fParamNodeName = $nodename;
                _if ( $this->fPrepGetNodeID->execute() ) {
                        __( $ar=$this->fPrepGetNodeID->FetchAll() );
                        //print_r($ar);
                        $id=(string)$ar[0][dNodeID];
                        //print_r($ar);
                        //echo $ar[0][dNodeID]."!".$id;
                }
        }endfunc($id)/*}}}*/

        func (IsName($nodename), dis)/*{{{*/
        {
                _tIFnot($nodename);
                __( $exists=$this->GetID($id,$nodename) );
                _tIF(isGood($exists) && isNotGood($id) );
        }endfunc($exists)/*}}}*/

        func (DelName($nodename), ddel)/*{{{*/
        {
                _tIF(isNotGood($nodename));
                _if ($this->GetID($id,$nodename)) {
                        _tIFnot( $this->DelID($id) );
                }
        }endfunc(ok)/*}}}*/

} //class

#endif //header ends
// vim: fdm=marker
//?>
