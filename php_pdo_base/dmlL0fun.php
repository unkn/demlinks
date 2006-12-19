//<?php
//dmlL0functions
//header starts
#ifndef DMLL0FUN_PHP
#define DMLL0FUN_PHP

#include "shortdef.php"
#include "debug.php"
#include "dmlL0def.php"
#include "color.php"


class dmlL0
{
        private $qNodeNames,$qRelations,$qNodeName,$qParentNodeID,$qChildNodeID,$qNodeID;//q from quote
        private $sqlNewNode;
        private static $fDBHandle=null;
        public $fFirstTime;//created table
        private $fPrepNewNode;//prepared statement handler
        private $fParamNewNode;//string param of prepared statement handler for NewNode
        public $dbh;

        private $sqlGetNodeArray;
        private $fParamNodeName;
        private $fPrepGetNodeArray;

        private $sqlDelNode;
        private $fParamDelNode;
        private $fPrepDelNode;

        /* quote functions {{{*/
        function fieldquote($whatfield)
        {
                //since we're in sqlite we're gonna quote the field with "" and the value with ''
                return '"'.$whatfield.'"';
        }

        function valquote($whatval)
        {
                return $this->fDBHandle->quote($whatval);
        }

        function tablequote($whattable)
        {
                return $this->valquote($whattable);
        }/*}}}*/

        function __construct()/*{{{*/
        {
                deb(dbeg,"dmlL0:construct:begin");

                // create a SQLite3 database file with PDO and return a database handle (Object Oriented)
                _c( $this->fDBHandle = new PDO('sqlite:'.dbasename,''/*user*/,''/*pwd*/,
                                array(PDO::ATTR_PERSISTENT => true)) );//singleton?

                $this->qNodeNames = $this->tablequote(dNodeNames);
                $this->qRelations = $this->tablequote(dRelations);
                $this->qNodeName = $this->fieldquote(dNodeName);
                $this->qParentNodeID = $this->fieldquote(dParentNodeID);
                $this->qChildNodeID = $this->fieldquote(dChildNodeID);
                $this->qNodeID = $this->fieldquote(dNodeID);
                define(paramNodeName,paramprefix.dNodeName);

                _if( $this->CreateDB() ) {
                        $this->fFirstTime=TRUE;
                        //echo "First time run!".nl;
                }else{
                        $this->fFirstTime=FALSE;
                        //echo "...using prev. defined table".nl;
                }

                //---------
                $this->sqlNewNode = 'INSERT INTO '.$this->qNodeNames.' ('.$this->qNodeName.') VALUES ('.paramNodeName.')';//table name needs to be quoted

                _t( $this->fPrepNewNode = $this->fDBHandle->prepare($this->sqlNewNode) );//can't prepare unless the table already exists!
                _t( $this->fPrepNewNode->bindParam(paramNodeName, $this->fParamNewNode, PDO::PARAM_STR) ); //, PDO::PARAM_INT);
                //---------
                $this->sqlGetNodeArray = 'SELECT * FROM '.$this->qNodeNames.' WHERE '.$this->qNodeName.' = '.paramNodeName;
                //echo $this->sqlGetNodeArray;die();
                _t( $this->fPrepGetNodeArray = $this->fDBHandle->prepare($this->sqlGetNodeArray) );
                _t( $this->fPrepGetNodeArray->bindParam(paramNodeName, $this->fParamNodeName, PDO::PARAM_STR) ); //, PDO::PARAM_INT);
                //---------
                $this->sqlDelNode = 'DELETE FROM '.$this->qNodeNames.' WHERE '.$this->qNodeName.' = '.paramNodeName;
                //echo $this->sqlDelNode;
                _t( $this->fPrepDelNode = $this->fDBHandle->prepare($this->sqlDelNode) );
                _t( $this->fPrepDelNode->bindParam(paramNodeName, $this->fParamDelNode, PDO::PARAM_STR) );
                //---------
                deb(dend,"dmlL0:construct:done.");
        }/*}}}*/

        function __destruct()/*{{{*/
        {
                deb(dbeg,"destruct:begin");
                $fDBHandle=null;
                deb(dend,"destruct:done.");
        }/*}}}*/

        func (CreateDB(),dcrea)/*{{{*/
        {

                $sqlNodeNames = 'CREATE TABLE '.$this->qNodeNames.
                            ' ('.$this->qNodeID.' INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, '.$this->qNodeName.' VARCHAR(256) UNIQUE NOT NULL)';
                $sqlRelations = 'CREATE TABLE '.$this->qRelations.
                            ' ('.$this->qParentNodeID.' INTEGER PRIMARY KEY , '.$this->qChildNodeID.' INTEGER SECONDARY KEY)';
                _t( $this->OpenTransaction());
                _c( $res = $this->fDBHandle->exec($sqlNodeNames) );
                if (evalgood($res)) {
                        _c( $res = $this->fDBHandle->exec($sqlRelations) );
                }

                if (evalgood($res)) {
                        _t( $this->CloseTransaction() );
                        $ret= TRUE;
                }else{
                        _t( $this->AbortTransaction() );
                        $ret= FALSE;
                }
        } endfunc($ret,dcrea)/*}}}*/

//------------------------ transactions/*{{{*/
        func (OpenTransaction(), dbegtr) //only one active transaction at a time; PDO limitation?!/*{{{*/
        {
                _t( $bt=$this->fDBHandle->beginTransaction() );
                deb(dend,"OpenTransaction():$bt");
        }endfunc($bt, dbegtr)/*}}}*/

        func (CloseTransaction(),dendtr)/*{{{*/
        {
                _t( $ci=$this->fDBHandle->commit() );
                deb(dend,"CloseTransaction():$ci!");
        }endfunc($ci, dendtr)/*}}}*/

        func (AbortTransaction(), dabtr)/*{{{*/
        {
                _t( $rb=$this->fDBHandle->rollBack() );
                deb(dend,"AbortTransaction():$rb!");
        }endfunc($rb, dabtr)/*}}}*/
//------------------------/*}}}*/

        func (AddNode($nodename),dadd)/*{{{*/
        {
                deb(dbeg,"AddNode('".$nodename."'):begin:");
                _t( evalgood($nodename) );//must not be empty or so; it it is then maybe's a bug outside this func provided user shall never call this func with an empty param value
                $this->fParamNewNode=$nodename;
                _t( $ret=evalgood( $this->fPrepNewNode->execute() ) );
        }endfunc($ret,dadd)/*}}}*/

        func (IsNode($nodename), dis)/*{{{*/
        {
                _t(evalgood($nodename));
                _c( $ar=$this->GetNodeArray($nodename) );
                $ret= (1==count($ar)?yes:no);
        }endfunc($ret,dis)/*}}}*/

        func (GetNodeArray($nodename),dget)/*{{{*/
        {
                _t(evalgood($nodename));
                $this->fParamNodeName = $nodename;
                _t( $this->fPrepGetNodeArray->execute() );
                $ar=$this->fPrepGetNodeArray->FetchAll();
                //print_r($ar);
        }endfunc($ar,dget)/*}}}*/

        func (DelNode($which), ddel)/*{{{*/
        {
                _t(evalgood($which));
                $this->fParamDelNode = $which;
                _c( $ret=evalgood( $mod=$this->fPrepDelNode->execute() ) );
        }endfunc($ret,ddel)/*}}}*/

        function Show()//temp/*{{{*/
        {
                deb(dbeg,"Show()");
                $sqlGetView = 'SELECT * FROM '.$this->qNodeNames;//.' WHERE page = '.$pageVisit;
                _t( $result=$this->fDBHandle->query($sqlGetView) );
                deb(dend,"end Show()");
                return $result;
        }/*}}}*/
//------------------------
        func (SetRelation($direction, $nodename1, $nodename2))/*{{{*/
        {
        }endfunc($ret,dset)/*}}}*/
//------------------------
} //class

#endif //header ends
// vim: fdm=marker
//?>
