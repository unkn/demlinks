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
p.SetNull();
p.IsNull();
p.GetPointee();//=="e"; or null if null
p.GetTree();

var dp=new DomainPointer_FamilyKind_OnTree_OfFamily_OfNode(cParents,tree0, cChildren, "a");//pointer uses it's parents list with one element which points to any element which is child of "a"
dp.SetPointee("e");//limited to children of "a"; OR null
dp.GetPointee();//null if none set
dp.GetTree();//returns the tree of where this pointer is part of(or smth
dp.SetNull();//==SetPointee(null)
dp.IsNull();
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
        initialize: function()
        {
        }

        ,IsValidVal: function(val)
        {
                return ('string'===typeof(val) && val.length>0);
        }

        ,DelValue: function(val)
        {
                _tIFnot(this.IsValidVal(val));
                this.splice(this.indexOf(val),1);
        }

        ,AppendValue: function(val)
        {
                _tIFnot(this.IsValidVal(val));
                this.push(val);
        }

        ,IsValue: function(val)
        {
                return -1 != this.indexOf(val);
        }

        ,IsValidWhere: function(where)/*{{{*/
        {
        }/*}}}*/

        ,inspect: function()
        {
                return this.toSource();
        }

});/*}}}*/

var HashL0=Class.create();/*{{{*/
HashL0.prototype=Object.extend(new Hash(), {
        initialize: function()
        {
                this.magicheader="id_";
//last:
                this.__diffsize=Hash.prototype.size.apply(this) + 1;//which is this property
        }

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

        ,GetVal_OfKey: function(key)/*{{{*/
        {
                var tmp=this[this.MangleKey(key)];
                return tmp;//undefined if not exists, and null if it exists and it's set to null, value otherwise; user IsDefined() to test
        }/*}}}*/

        ,Set_OfKey_Val: function(key, val)/*{{{*/
        {
                this[this.MangleKey(key)]=val;//val can be null
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
        {
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
//alert(a.GetVal_OfKey("doh"));
a.Set_OfKey_Val("k1","a");
a.Set_OfKey_Val("k2",Array("mm","dd","ee"));
//alert(a.GetVal_OfKey("k1"))
alert(a.GetVal_OfKey("k2"))
alert(IsDefined(a.GetVal_OfKey("k2")))
//alert(typeof(a.GetVal_OfKey("k2"))=="undefined")
//alert(a.size());
str="";
for (var b in a) {
        str+=b+rnl;
}
alert(str);
*/
/*var tr=Class.create();
tr.prototype=Object.extend(new HashL0(), {
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
                //this.AllNodes.Set_OfKey_Val(cParents, new HashL0());
                //this.AllNodes[cParents]=new HashL0();//not a hash because it'll overwrite some of its methods, and we need to support any index name!
                //this.AllNodes.Set_OfKey_Val(cChildren, new HashL0());
                //this.AllNodes[cChildren]=new HashL0();
                //alert('TreeL0 init');
        }
//add mangle for node ID before storage and unmangle on read... smth like prepend a "0" so ids like "length" or "prototype" don't overwrite the properties existent in $H()
//or use Object.propertyIsEnumerable(property) and only prepend "0" to those IDs which are not enumerable so they won't overwrite methods


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
                        n=this._GetNode(node);
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
        list=this._Get_Family_OfNode(familytype, whichnode);
        if (!IsDefined(list)) {
                var node=this._Ensure_GetNode(whichnode);
                //list=
                _tIF(IsDefined(node.GetVal_OfKey(familytype)));//safe check
                //if (!IsDefined(list)) {//create
                //var xx=new UniqListL0();
                //alert(UniqListL0.prototype.isPrototypeOf(xx));
                node.Set_OfKey_Val(familytype, new UniqListL0());//we repeat do-while to test this was properly entered
                //}
                //_tIFnot(UniqListL0.prototype.isPrototypeOf(node.GetVal_OfKey(familytype)));
        }
        cnt++;
        _tIF(cnt>2);//no more than 2 times
} while (!IsDefined(list));

        return list;
}/*}}}*/

        ,_GetNode: function(node)/*{{{*/
        {
                _tIFnot(this.IsValidNodeName(node));
                var nodvar= this.AllNodes.GetVal_OfKey(node);//can be undefined
                _tIF( IsDefined(nodvar) && (!HashL0.prototype.isPrototypeOf(nodvar)) );
                return nodvar;
        }/*}}}*/

        ,_Get_Family_OfNode: function(fam,node)/*{{{*/
        {
                _tIFnot(this.IsValidFamily(fam));
                _tIFnot(this.IsValidNodeName(node));

                var nodvar=this._GetNode(node);//node tested inhere
                var got=undefined;
                if (IsDefined(nodvar)) {
                        got=nodvar.GetVal_OfKey(fam);
                        //_tIFnot(IsDefined(got) && !UniqListL0.prototype.isPrototypeOf(got));
                }
                return got;
        }/*}}}*/
/*}}}*/

//TODO: DelNode()

        ,DelRel_Node_Sense_Node:function(n1, sense, n2) //PC=parent,child  (order of params)/*{{{*/
{
 //       var ar=this._GetArrayPosOf_PCRel(p,c);
   //     if (IsDefined(ar)) {
        if (this.IsRel_Node_Sense_Node(n1,sense,n2)) {

                var cl=this._Get_Family_OfNode(sense, n1);
                _tIFnot(IsDefined(cl));
                cl.DelValue(n2);//cl.splice(ar[1],1);

                //this._AutoDelEmptyNode(c);

                var pl=this._Get_Family_OfNode(this.GetOppositeSense(sense),n2);
                _tIFnot(IsDefined(pl));// is unlikely because of prev. if
                pl.DelValue(n1);//pl.splice(ar[0],1);

                //this._AutoDelEmptyNode(p);
        }
}/*}}}*/

        ,IsNode:function(n)/*{{{*/
{//exists only if part of one or more relationships
        //no need to compact() the arrays since compacting is done on delete
        if (IsDefined(_GetNode(n))) {
                return true;
        }
        return false;
}/*}}}*/

        ,_IsSemiRel_Node_Sense_Node: function(n1,sense,n2) // a, cDown, b/*{{{*/
        {//one sense testing, the other(opposing sense) must also be true, ie. b, cUp, a
                _tIFnot(this.IsValidSense(sense));
                _tIFnot(this.IsValidNodeName(n1));
                _tIFnot(this.IsValidNodeName(n2));

                var first=this._Get_Family_OfNode(sense,n1);
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
                /*var str="";
                for (var i in this.AllNodes) {
                        str+=i+rnl;
                }
                return str;*/
                //var ar=this.AllNodes.GetKeys();
                var str="";
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
                /*for (var key in ar) {
                        str+=ar[key]+" ";
                }*/
                return str;
        }/*}}}*/

};/*}}}*/

//------------------------------------------------------------------------------------------

var PointerL0_OnTree_OnSense=Class.create();/*{{{*/
PointerL0_OnTree_OnSense.prototype={
        initialize: function(tree, sense){
                _tIFnot(TreeL0.prototype.isPrototypeOf(tree));
                _tIFnot(tree.IsValidSense(sense));
                alert('initing');
        }
};/*}}}*/

var TreeL1=Class.create();
TreeL1.prototype=Object.extend(new TreeL0(), {
        initialize: function() {
        }
/*        speak: function(wh) {
                //TreeL0.prototype.speak.apply(this,[wh]);//this is how calling a base class method is done
                //TreeL0.prototype.speak(wh);//or this which doesn't WORK in some cases!
                alert("2:"+this.e);
        }*/
});

//------------------------------------------------------------------------------------------

//test section follows:

//var tree1=new TreeL1();
//tree1.speak("about it");


var tree0=new TreeL0();
//var p1=new PointerL0_OnTree_OnSense(tree0,cDown);

//var b=new TreeL0();//eval(AllNodes.toSource()));
tree0.NewPCRel("a","b");
tree0.NewPCRel("a","d");
tree0.NewPCRel("a","e");
tree0.NewPCRel("a","e");
tree0.NewPCRel("f","a");
tree0.NewPCRel("f","b");
tree0.NewPCRel("g","a");
alert(tree0.IsPCRel("a","e"));
//alert(tree0.toSource());
alert(tree0.inspect());
//alert(tree0.inspect());
//tree0.DelNode("a");
//alert(tree0.IsPCRel("a","b"));
//alert(tree0.inspect());


// vim: fdm=marker
