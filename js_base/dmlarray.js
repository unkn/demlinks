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
* Description: this is going to implement level 0 demlinks using arrays in js
*               - requires prototype.js (1.5.0 ?)
*
*
***************************************************************************}}}*/


var cParents="AllParents";
var cChildren="AllChildren";
var cUp=cParents;
var cDown=cChildren;
var rnl="\n";

var oFirst=1;
var oLast=2;
var oPrev=4;
var oBefore=oPrev;
var oNext=8;
var oAfter=oNext;
var oCurrent=16;
var oPinPoint=oCurrent;

var validWheres=[oFirst, oLast, oPrev, oNext, oCurrent];//only one of these can be passed to a function accepting a Where param;  'o' comes from only one

//TODO: replace, insertafter/before Node, first last using splice()
//insert_InNode_WhichFamily_OfNode_   ("a", cChildren, "e", );
/*
var a=new Cursor_OnTree_OnSense_OfNode_DPSense(tree0, cDown, "a", cDown);//make a cursor on children of 'a' ; DP=domain pointer; DPSense=in which sense will the DP point to, usually this is the same as OnSense
a.Insert_WhatNode_Where_OfNode("f", kAfter, "e");//true/false, use one splice
a.Insert_WhatNode_Where_OfNode("g", kFirst);//true/false
a.Move_WhatNode_Where_OfNode("f", kBefore,"e");//use 2 splices, one del one insert
a.Move_WhatNode_Where_OfNode("f", kLast);
a.GetNode_Where(kFirst);
a.GetNode_Where(kNext);//repeat this, returns null if no more
a.Replace_WhatNode_Where_OfNode("f", kLast, "m");//delete f, insert m kLast
a.Delete_WhatNode("m");
a.GetTree();
a.GetCount();//return number of elements of children of "a" ie. array.length;
a.ClearAll();//empty all cChildren of "a"

var p=new Pointer_OnTree_OnSense(tree0, cDown);//with no domain
p.SetPointee("e");//or null
p.SetUndefined();
p.IsUndefined();
p.GetPointee();//=="e"; or null if null
p.GetTree();

var dp=new DomainPointer_FamilyKind_OnTree_OfFamily_OfNode(cParents,tree0, cChildren, "a");//pointer uses it's parents list with one element which points to any element which is child of "a"
dp.SetPointee("e");//limited to children of "a"; OR null
dp.GetPointee();//null if none set
dp.GetTree();//returns the tree of where this pointer is part of(or smth
dp.SetUndefined();//==SetPointee(null)
dp.IsUndefined();
//use array() named "arguments" inside a function!
try using prototype.js ie. object.extend() and stuff Class, $A()
*/

/*abcd={
        a:"sasad"
        ,ab:"undefined"
        };*/
/*var abcd=Class.create();
abcd.prototype={
        initialize: function() {
                        alert('init');
                }
};*/
/*var abcd=new Hash();
alert((null===abcd["length"]) || (null!=abcd["length"]));
alert(abcd.prototype);
alert(abcd.propertyIsEnumerable("length"));
var s="";
for (var b in abcd) {
        s+=b+rnl;
}
        alert(s);
//alert(Object.propertyIsEnumerable(Hash.lengt));
*/

//---------------------------BEGIN global funx
function IsDefined(val)/*{{{*/
{
        var exists=typeof(val)!=="undefined";
        var exists2=(null===val || null != val);//true=exists, even if it's set to null, but however it can be undefined in which case == null but it's not === null => thus it doesn't exist
        _tIF(exists !== exists2);
        return exists;
}/*}}}*/

function _t(what) //unconditional throw/*{{{*/
{
        showbacktrace;//it works with ff and firebug, unlike throw below, this makes firebug show backtrace
        throw new Error(what);
}/*}}}*/

function _tIF(bool)/*{{{*/
{
        if (bool) {
                _t("_tIF("+bool+")");
        }
}/*}}}*/

function _tIFnot(bool)/*{{{*/
{
        if (! bool) {
                _t("_tIFnot("+bool+")");
        }
}/*}}}*/
//---------------------------END global funx


var UniqListL0=Class.create();/*{{{*/
UniqListL0.prototype=Object.extend(new Array(), {//this will make sure it only keeps uniq values
        initialize: function()/*{{{*/
        {
                var i=0;
                while (i<arguments.length) {
                        this.AppendValue(arguments[i]);
                        i++;
                }
                arguments.length=0;
        }/*}}}*/

        ,IsValidVal: function(val)/*{{{*/
        {
                return (IsDefined(val)&&('string'===typeof(val) && val.length>0));
        }/*}}}*/

        ,DelValue: function(val)/*{{{*/
        {
                _tIFnot(this.IsValidVal(val));
                this.splice(this.indexOf(val),1);
        }/*}}}*/

        ,AppendValue: function(val)/*{{{*/
        {
                _tIFnot(this.IsValidVal(val));
                this.push(val);
        }/*}}}*/

        ,IsValue: function(val)/*{{{*/
        {
                _tIFnot(this.IsValidVal(val));
                return -1 != this.indexOf(val);
        }/*}}}*/

        ,IsValidWhere: function(where)/*{{{*/
        {
                /*where.Is=function (what) {
                        return where.indexOf(what)!=-1;
                };*/

                //alert(where.Is(0));
                if (IsDefined(where)) {// && Int.prototype.isPrototypeOf(where)) {
                        if (validWheres.indexOf(where) != -1) {
                                return true;
                        }
/*                        if (!onebad) {
                                //dup check
                                if (where.join()===where.uniq().join()) {
                                        //opposite values cannot coexist:
                                        return true;
                                }
                        }*/
                }
                return false;
        }/*}}}*/

        ,GetIndex_OfVal: function(val) {
                _tIFnot(this.IsValidVal(val));
                var ind=this.indexOf(val);
                if (-1 == ind) {
                        return undefined;
                }
                return ind;
        }

        ,IsValidIndex: function(index)
        {
                return (IsDefined(index) &&((index>=0) && (index < this.length)));
        }

        ,GetValue_OfIndex: function(index) {
                _tIFnot(this.IsValidIndex(index));
                return this[index];//never undefined
        }

        ,GetIndex_Where_Node: function(where,val)
        {
                //alert(IsDefined(undefined -1));
                _tIFnot(this.IsValidWhere(where));
                if ( (where === oBefore) || (where === oAfter) ) {
                                //get index of val
                                //calc if prev index is not out of scope
                                //get val of prev index
                        _tIFnot(this.IsValidVal(val));
                        var ind=this.GetIndex_OfVal(val);
                        if (this.IsValidIndex(ind)) {
                                ind= ind + (where===oAfter?+1:-1);
                                if (this.IsValidIndex(ind)) {
                                        //return this.GetValue_OfIndex(ind));//can't be undefined
                                        return ind;
                                }
                        }
                        return undefined;
                }

                if ( (where === oFirst) || (where === oLast) ) {
                        _tIF(IsDefined(val));
                        var ind=(where===oFirst?0:this.length-1);
                        if (this.IsValidIndex(ind)) {
                                return ind;
                        }
                        return undefined;
                }
                if (where===oPinPoint) {
                        _tIFnot(IsDefined(val));
                        return this.GetIndex_OfVal(val);
                }
                _t('impossible, IsValidWhere must not do it\'s job');
        }

        ,inspect: function()/*{{{*/
        {
                return this.toSource();
        }/*}}}*/


});/*}}}*/

var HashL0=Class.create();/*{{{*/
HashL0.prototype=Object.extend(new Hash(), {
        initialize: function()/*{{{*/
        {
                this.magicheader="id_";
//last:
                this.__diffsize=Hash.prototype.size.apply(this) + 1;//which is this property
        }/*}}}*/

        ,size: function()/*{{{*/
        {
                var size=Hash.prototype.size.apply(this) - this.__diffsize;
                _tIFnot(size>=0);
                return size;
        }/*}}}*/

        ,MangleKey: function(key)//modifies the key so it won't overwrite existing methods or properties/*{{{*/
        {
                return this.magicheader+key;
        }/*}}}*/

        ,UnmangleKey: function(mangledkey)/*{{{*/
        {
                _tIFnot(this.IsMangled(mangledkey));//not a mangled key? bug in program maybe!
                return mangledkey.substr(this.magicheader.length);//skip header
        }/*}}}*/

        ,IsMangled: function(mangledkey)/*{{{*/
        {
                if (typeof(mangledkey)==='string') {
                        return mangledkey.substr(0, this.magicheader.length ) === this.magicheader;
                }
                return false;
        }/*}}}*/

        ,GetValue_OfKey: function(key)/*{{{*/
        {
                var tmp=this[this.MangleKey(key)];
                return tmp;//undefined if not exists, and null if it exists and it's set to null, value otherwise; user IsDefined() to test
        }/*}}}*/

        ,Set_OfKey_Val: function(key, val)/*{{{*/
        {
                this[this.MangleKey(key)]=val;//val can be null
        }/*}}}*/

        ,DelKey: function(key)/*{{{*/
        {
                var val=this.GetValue_OfKey(key);
                if (IsDefined(key)) {
                        //alert(val.toSource());
                        //_tIF(typeof(val)==='object');//unallocated child, responsibility of the caller
                        delete this[this.MangleKey(key)];
                }
        }/*}}}*/

/*        ,add_key_value_where_ofkey: function(key,value,where,ofkey) //"a",array("b","d"),kAfter,"c"
        {
                if (null!=where) {
                        _tIFnot(IsValidWhere(where));
                }
        }

        ,IsValidWhere: function(where)
        {
        }
*/
        ,PropertyExists:function (prop)/*{{{*/ //unused
        {
                return IsDefined(this[prop]);//handled situation when property is set to null thus it exists; also sees non-enumerable ones ie. 'length'
        }/*}}}*/

        ,inspect: function()/*{{{*/
        {
                var str="";
                this.each( function(s) { //s.key=nodeID, s.value=object HashL0
                        var family=s.value.inspect();//s.key=cParents or cChildren, s.value is object HashL0
/*                        s.value.each( function(fam) {//fam.key=AllParents or AllChildren, fam.value=array of nodeIDs
                                var nodelist="";
                                fam.value.each( function(value){
                                        nodelist+=value+that.nodedelimiter;
                                });//.toSource() also works
                                family+=fam.key+"( "+nodelist.truncate(nodelist.length-that.nodedelimiter.length,"")+" )"+that.famdelimiter;//AllChildren:a,b,c,d
                        });*/
                        //str+=s.key+": "+family.truncate(family.length-that.famdelimiter.length,"")+rnl;//a: AllChildren:b,c,d ! AllParents:q,e
                        str+=s.key+": "+family;
                });
                return str+rnl;
        }/*}}}*/

        ,toSource: function()/*{{{*/
        {
                var str="";
                var that=this;
                for (var key in this) {
                        if (this.IsMangled(key)) {
                                if (typeof(this[key])==='object') {
                                        str+=key+":"+this[key].toSource();
                                } else {
                                        str+=this[key]+",";
                                }
                        }
                }
                /*this.each( function(pair) {
                        if (that.IsMangled.apply(that,[pair.key])) {
                                str+=pair.value+',';
                        }
                });*/
                //alert(str.length);
                /*if (str.charAt(str.length)===',') {
                        str=str.truncate(str.length-1,"");
                }*/
                return str+rnl;
        }/*}}}*/

        ,each: function(iteratorfunc)/*{{{*/
        {//beware don't use this with an iterator that deletes elements
                var that=this;
                Hash.prototype.each.apply(this, [ function(pair) {
                        if (that.IsMangled(pair.key)) {
                                iteratorfunc({ key:that.UnmangleKey(pair.key), value:pair.value });
                        }
                }]);
        }/*}}}*/

        ,GetKeys: function()/*{{{*/
        {
                var ar=new Array();
                var that=this;
                this.each( function(pair) {
                        ar.push(pair.key);
                });
                /*this.each( function(pair) {
                        if (that.IsMangled(pair.key)) {
                                ar.push(that.UnmangleKey(pair.key));
                        }
                });*/
                /*for (var key in this) {
                        if (this.IsMangled(key)) {
                                ar.push(this.UnmangleKey(key));
                        }
                }*/
                return ar;
        }/*}}}*/

});/*}}}*/

/*var a=new HashL0();
//alert(a.UnmangleKey(a.MangleKey("doh")));
//alert(a.GetValue_OfKey("doh"));
a.Set_OfKey_Val("k1","a");
a.Set_OfKey_Val("k2",Array("mm","dd","ee"));
//alert(a.GetValue_OfKey("k1"))
alert(a.GetValue_OfKey("k2"))
alert(IsDefined(a.GetValue_OfKey("k2")))
//alert(typeof(a.GetValue_OfKey("k2"))=="undefined")
//alert(a.size());
str="";
for (var b in a) {
        str+=b+rnl;
}
alert(str);
*/
/*var tr=Class.create();
tr.prototype=Object.extend(HashL0, {
        initialize: function() {
        }
        ,sz:function()
        {
                return this.size();
        }
});
var k=new tr();
alert(k.sz());
exit;
*/

/*alert(a.toSource());
//alert(a.entries().toSource());
alert(a instanceof Hash);
exit;*/


var TreeL0=Class.create();/*{{{*/
TreeL0.prototype={
        initialize: function(){//constructor
                this.famdelimiter=" AND ";
                this.nodedelimiter=", ";
                this.AllNodes=new HashL0();
        }

        ,IsValidSense:function(sense)/*{{{*/
        {
                if (sense===cUp || sense===cDown) {
                        return true;
                }
                return false;
        }/*}}}*/

        ,IsValidFamily: function(fam)/*{{{*/
        {
                return this.IsValidSense(fam);
        }/*}}}*/

        ,IsValidNodeName: function (node)/*{{{*/
        {
                //if (String.prototype.isPrototypeOf(node)) {
                if ('string'===typeof(node)) {
                        if (node.length > 0) {
                                return true;
                        }
                }
                return false;
        }/*}}}*/

        ,GetOppositeSense: function(sense)/*{{{*/
        {
                _tIFnot(this.IsValidSense(sense));
                switch (sense) {
                        case cUp:
                                return cDown;
                                break;
                        case cDown:
                                return cUp;
                                break;
                        default:
                                _t("critical failure, better yet: evil");
                }
        }/*}}}*/

//private functions:/*{{{*/

        ,_Ensure_GetNode: function(node)/*{{{*/
        {
                var cnt=0;
                var n;
                do {
                        n=this._GetRefTo_Node(node);
                        if (!IsDefined(n)) {
                                this.AllNodes.Set_OfKey_Val(node, new HashL0());
                        }
                        cnt++;
                        _tIF(cnt>2);//this shouldn't repeat more than 2 times
                } while (!IsDefined(n));
                return n;
        }/*}}}*/

        ,_EnsureGetList_OfFamily_OfNode:function(familytype,whichnode) //private function, I wish/*{{{*/
{//always returns an array, even if it wasn't defined previously
var cnt=0;
var list;
do {
        list=this._GetRefTo_Family_OfNode(familytype, whichnode);
        if (!IsDefined(list)) {
                var node=this._Ensure_GetNode(whichnode);
                //list=
                _tIF(IsDefined(node.GetValue_OfKey(familytype)));//safe check
                //if (!IsDefined(list)) {//create
                //var xx=new UniqListL0();
                //alert(UniqListL0.prototype.isPrototypeOf(xx));
                node.Set_OfKey_Val(familytype, new UniqListL0());//we repeat do-while to test this was properly entered and has proper type, this is done via _GetRefTo_Family_OfNode()
                //}
                //_tIFnot(UniqListL0.prototype.isPrototypeOf(node.GetValue_OfKey(familytype)));
        }
        cnt++;
        _tIF(cnt>2);//no more than 2 times
} while (!IsDefined(list));

        return list;
}/*}}}*/

        ,_GetRefTo_Node: function(node)/*{{{*/
        {
                _tIFnot(this.IsValidNodeName(node));
                var nodvar= this.AllNodes.GetValue_OfKey(node);//can be undefined
                _tIF( IsDefined(nodvar) && (!HashL0.prototype.isPrototypeOf(nodvar)) );
                return nodvar;
        }/*}}}*/

        ,_GetRefTo_Family_OfNode: function(fam,node)/*{{{*/
        {
                _tIFnot(this.IsValidFamily(fam));
                _tIFnot(this.IsValidNodeName(node));

                var nodvar=this._GetRefTo_Node(node);//node tested inhere
                var got=undefined;
                if (IsDefined(nodvar)) {
                        got=nodvar.GetValue_OfKey(fam);
                        //_tIFnot(IsDefined(got) && !UniqListL0.prototype.isPrototypeOf(got));
                }
                return got;
        }/*}}}*/

        ,_AutoDelEmptyNode: function(nodeid)/*{{{*/
        {
                var node=this._GetRefTo_Node(nodeid);
                if (IsDefined(node)){
                        var p=this._AutoDel_NodeVar_Family(node, cParents);
                        var c=this._AutoDel_NodeVar_Family(node, cChildren);
                        if (c && p) {
                                //both gone, then node has to go too
                                this.AllNodes.DelKey(nodeid);
                        }
                }
        }/*}}}*/

        ,_AutoDel_NodeVar_Family: function(noderef, fam)/*{{{*/
        {
                //no checks FIXME
                var famref=noderef.GetValue_OfKey(fam);
                if (IsDefined(famref)) {
                        if (famref.size() > 0) {
                                return false;//not empty!
                        }
                        //empty:
                        noderef.DelKey(fam);
                }
                return true;//deleted, or already inexistent
        }/*}}}*/
/*}}}*/

        ,_DelAll_Family_OfNode: function(fam, node)/*{{{*/
        {
                _tIFnot(this.IsValidNodeName(node));
                _tIFnot(this.IsValidFamily(fam));

                var fref=this._GetRefTo_Family_OfNode(fam, node);
                if (IsDefined(fref)) {
                        //here we clean all elements of family
                        var that=this;
                        while (fref.size() >0) {
                                var first=fref.first();
                                _tIFnot(IsDefined(first));
                                //alert("del:"+node+" "+fam+" "+first);
                                this.DelRel_Node_Sense_Node(node, fam, first);
                        }
                }
        }/*}}}*/

        ,DelNode: function(nodeid)/*{{{*/
        {
                var node=this._GetRefTo_Node(nodeid);
                if (IsDefined(node)) {
                        //we got a node to delete
                        this._DelAll_Family_OfNode(cParents, nodeid);
                        this._DelAll_Family_OfNode(cChildren, nodeid);
                        /*if (p && c) {
                                //both families are gone, so has the node
                                this.AllNodes.DelKey(nodeid);
                        }*/
                }
        }/*}}}*/

        ,_SemiDel_Node_Where_DelNode: function(n1, sense, n2)/*{{{*/
        {
                var cl=this._GetRefTo_Family_OfNode(sense, n1);
                _tIFnot(IsDefined(cl));
                cl.DelValue(n2);//cl.splice(ar[1],1);

                this._AutoDelEmptyNode(n1);
        }/*}}}*/

        ,DelRel_Node_Sense_Node:function(n1, sense, n2) //PC=parent,child  (order of params)/*{{{*/
{
 //       var ar=this._GetArrayPosOf_PCRel(p,c);
   //     if (IsDefined(ar)) {
        if (this.IsRel_Node_Sense_Node(n1,sense,n2)) {

                this._SemiDel_Node_Where_DelNode(n1,sense,n2);
                this._SemiDel_Node_Where_DelNode(n2, this.GetOppositeSense(sense), n1);
        }
}/*}}}*/

        ,DelPCRel: function(p,c)/*{{{*/
        {
                this.DelRel_Node_Sense_Node(p, cDown, c);
        }/*}}}*/

        ,IsNode:function(n)/*{{{*/
{//exists only if part of one or more relationships
        //no need to compact() the arrays since compacting is done on delete
        if (IsDefined(this._GetRefTo_Node(n))) {
                return true;
        }
        return false;
}/*}}}*/

        ,_IsSemiRel_Node_Sense_Node: function(n1,sense,n2) // a, cDown, b/*{{{*/
        {//one sense testing, the other(opposing sense) must also be true, ie. b, cUp, a
                _tIFnot(this.IsValidSense(sense));
                _tIFnot(this.IsValidNodeName(n1));
                _tIFnot(this.IsValidNodeName(n2));

                var first=this._GetRefTo_Family_OfNode(sense,n1);
                if (IsDefined(first)) {
                        return first.IsValue(n2);
                }
                return false;
        }/*}}}*/

        ,IsRel_Node_Sense_Node:function(n1,sense,n2)/*{{{*/
{
        _tIFnot(this.IsValidSense(sense));
        _tIFnot(this.IsValidNodeName(n1));
        _tIFnot(this.IsValidNodeName(n2));

        if (this._IsSemiRel_Node_Sense_Node(n1, sense, n2)) {//a->b
                _tIFnot(this._IsSemiRel_Node_Sense_Node(n2, this.GetOppositeSense(sense), n1));//this is a MUST, or something is wrong in implementation  ;   // b<-a
                return true;
        }
        return false;
}/*}}}*/

        ,IsPCRel: function(p,c)/*{{{*/
        {
                return this.IsRel_Node_Sense_Node(p, cDown, c);
        }/*}}}*/

        ,NewRel_Node_Sense_Node:function (n1, sense, n2)/*{{{*/
{//a relation can only exist once, ie. a->b once, not a->b and then a->b again, like a:{b,b} there are no DUP elements! dup elements would be on the next level
        if (!this.IsRel_Node_Sense_Node(n1, sense, n2)) {
                this._EnsureGetList_OfFamily_OfNode(cChildren, n1).AppendValue(n2);//p->c
                this._EnsureGetList_OfFamily_OfNode(cParents, n2).AppendValue(n1);//c<-p
        }
}/*}}}*/

        ,NewPCRel: function (p,c)/*{{{*/
        {
                this.NewRel_Node_Sense_Node(p, cDown, c);
        }/*}}}*/

        ,toSource: function()/*{{{*/
        {
                //for (var iter in
                return this.AllNodes.toSource();
        }/*}}}*/

        ,inspect: function()/*{{{*/
        {
                return this.AllNodes.inspect();
/*                var str="";
                var that=this;
                this.AllNodes.each( function(s) { //s.key=nodeID, s.value=object HashL0
                        var family="";
                        s.value.each( function(fam) {//fam.key=AllParents or AllChildren, fam.value=array of nodeIDs
                                var nodelist="";
                                fam.value.each( function(value){
                                        nodelist+=value+that.nodedelimiter;
                                });//.toSource() also works
                                family+=fam.key+"( "+nodelist.truncate(nodelist.length-that.nodedelimiter.length,"")+" )"+that.famdelimiter;//AllChildren:a,b,c,d
                        });
                        str+=s.key+": "+family.truncate(family.length-that.famdelimiter.length,"")+rnl;//a: AllChildren:b,c,d ! AllParents:q,e
                });
                return str;*/
        }/*}}}*/

        ,GetPointerL0_OnSense: function(sense)
        {
                return new PointerL0_OnTree_OnSense(this, sense);
        }

        ,GetDomainPointerL1_OnDomainNode_OnSense: function(dnode, sense)
        {
                return new DomainPointerL1_OnTree_OnDomainNode_OnSense(this, dnode, sense);
        }

};/*}}}*/

//------------------------------------------------------------------------------------------

var PointerL0_OnTree_OnSense=Class.create();/*{{{*/
//function PointerL0_OnTree_OnSense(tree, sense)
//{
PointerL0_OnTree_OnSense.prototype={
        initialize: function(tree, sense)/*{{{*/
        {
                if (arguments.length>0) {
                        this.evilInit(tree, sense);
                }
        }/*}}}*/

        ,evilInit: function(tree, sense)/*{{{*/
        {
                this.SetTree(tree);
                this.SetSense(sense);
                this.SetUndefined();
        }/*}}}*/

//PointerL0_OnTree_OnSense.prototype.SetPointee=function (node)
        ,SetPointee: function(node)/*{{{*/
        {
                if (IsDefined(node)){
                        //_tIFnot(this.tree.IsValidNodeName(node));
                        _tIFnot(this.GetTree().IsNode(node));
                }
                this.pointee=node;//can be undefined
        }/*}}}*/

//PointerL0_OnTree_OnSense.prototype.SetUndefined=function ()
        ,SetUndefined: function()/*{{{*/
        {
                this.SetPointee();
        }/*}}}*/

//PointerL0_OnTree_OnSense.prototype.IsUndefined=function ()
        ,IsUndefined: function()/*{{{*/
        {
                return !this.IsDefined();
        }/*}}}*/

//PointerL0_OnTree_OnSense.prototype.IsDefined=function ()
        ,IsDefined: function()/*{{{*/
        {
                return IsDefined(this.GetPointee());
        }/*}}}*/

//PointerL0_OnTree_OnSense.prototype.GetPointee=function ()
        ,GetPointee: function()/*{{{*/
        {
                //_tIFnot(this.tree.IsNode(this.pointee));//just a safety check
                return this.pointee;//undefined==null
        }/*}}}*/

//PointerL0_OnTree_OnSense.prototype.GetTree=function ()
        ,SetTree: function(tree)/*{{{*/
        {
                _tIFnot(TreeL0.prototype.isPrototypeOf(tree));
                this.tree=tree;
        }/*}}}*/

        ,GetTree: function()/*{{{*/
        {
                _tIFnot(TreeL0.prototype.isPrototypeOf(this.tree));//safety
                return this.tree;
        }/*}}}*/

        ,GetSense: function()/*{{{*/
        {
                _tIFnot(this.GetTree().IsValidSense(this.sense));//safety check
                return this.sense;
        }/*}}}*/

        ,SetSense: function(sense)/*{{{*/
        {
                _tIFnot(this.GetTree().IsValidSense(sense));
                this.sense=sense;
        }/*}}}*/
};/*}}}*/


//------------------------------------------------------------------------------------------
var DomainPointerL1_OnTree_OnDomainNode_OnSense=Class.create();/*{{{*/
DomainPointerL1_OnTree_OnDomainNode_OnSense.prototype=Object.extend(new PointerL0_OnTree_OnSense(), {
        initialize: function(tree, dnode, sense) {/*{{{*/
                this.evilInit(tree,sense);
                this.SetDomain(dnode);
                //PointerL0_OnTree_OnSense.apply(this,[tree,sense]);
                //PointerL0_OnTree_OnSense.initialize.apply(tree,sense);
        }/*}}}*/

        ,SetDomain: function(dnode)/*{{{*/
        {
                _tIFnot(this.GetTree().IsNode(dnode));
                this.domainNode=dnode;
        }/*}}}*/

        ,GetDomain: function()/*{{{*/
        {
                return this.domainNode;
        }/*}}}*/

        ,SetPointee: function(node)/*{{{*/
        {
                if (IsDefined(node)) {
                        _tIFnot(this.IsOfDomain_Node(node));
                }
                PointerL0_OnTree_OnSense.prototype.SetPointee.apply(this,[node]);
        }/*}}}*/

        ,IsOfDomain_Node: function(node)/*{{{*/
        {
                return (IsDefined(node) && this.GetTree().IsRel_Node_Sense_Node(this.GetDomain(), this.GetSense(), node));
        }/*}}}*/
});/*}}}*/
//------------------------------------------------------------------------------------------

//------------------------------------------------------------------------------------------

//test section follows:

var tree0=new TreeL0();
//var p1=new PointerL0_OnTree_OnSense(tree0,cDown);
var p1=tree0.GetPointerL0_OnSense(cDown);

//var b=new TreeL0();//eval(AllNodes.toSource()));
tree0.NewPCRel("a","b");
tree0.NewPCRel("a","d");
tree0.NewPCRel("a","e");
tree0.NewPCRel("a","e");
tree0.NewPCRel("f","a");
tree0.NewPCRel("f","b");
tree0.NewPCRel("g","a");
alert(p1.GetPointee());
p1.SetPointee("g");
alert(p1.GetPointee());
var p2=tree0.GetDomainPointerL1_OnDomainNode_OnSense("a", cDown);
//alert(p2.IsDefined());
alert(p2.GetPointee());
p2.SetPointee("e");
alert(p2.GetPointee());
p2.SetPointee("b");
alert(p2.GetPointee());

//var p3=tree0.GetPointerL0_OnSense(cDown);alert(p3.GetPointee());

//alert(tree0.IsPCRel("a","e"));
//alert(tree0.inspect());
/*tree0.DelPCRel("a","e");
tree0.DelPCRel("a","b");
tree0.DelPCRel("a","d");
tree0.DelPCRel("f","a");*/
//tree0.DelPCRel("g","a");
alert(tree0.inspect());
//alert(tree0.IsNode("a"));
//tree0.DelNode("a");
tree0.DelNode("f");
//alert(tree0.IsNode("a"));
//alert(tree0.IsPCRel("a","e"));
//alert(tree0.toSource());
alert(tree0.inspect());
//alert(tree0.inspect());
//tree0.DelNode("a");
//alert(tree0.IsPCRel("a","b"));
//alert(tree0.inspect());


// vim: fdm=marker
