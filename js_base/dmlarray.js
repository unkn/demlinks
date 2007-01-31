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

var p=new Pointer_OnTree_OfFamily(tree0, cChildren);//with no domain
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
//use array() named arguments inside a function!
try using prototype.js ie. object.extend() and stuff Class, $A()
*/

var TreeL0=Class.create();/*{{{*/
TreeL0.prototype={
        initialize: function(){//constructor
                this.AllNodes=$H();
                this.AllNodes[cParents]={};//not a hash because it'll overwrite some of its methods, and we need to support any index name!
                this.AllNodes[cChildren]={};
                //alert('TreeL0 init');
        },

        GetList_OfFamily_OfNode:function (familytype,whichnode) /*{{{*/
{//doesn't create that which didn't exist! unlike _Ensure*.*
        var list=this.AllNodes[familytype];
        //if (null===list[whichnode] || typeof(list[whichnode]) != "object") {
        if (null===list[whichnode] || !Array.prototype.isPrototypeOf(list[whichnode])) {
                return null;
        }
        return list[whichnode];
},/*}}}*/

        _AutoDelEmptyNode:function(n)/*{{{*//*{{{*/
{
        this._AutoDelEmptyNode_OfFamily(n, cParents);
        this._AutoDelEmptyNode_OfFamily(n, cChildren);
},/*}}}*/

        _AutoDelEmptyNode_OfFamily:function(whichnode, familytype)/*{{{*/
{
        var list=this.AllNodes[familytype];
        if (null !== list[whichnode] && Array.prototype.isPrototypeOf(list[whichnode])) {
                //list[whichnode]=list[whichnode].compact();//this should decrease performance
                if (list[whichnode].length<=0 ) {
                        //then it's empty to can delete it
                        delete list[whichnode];
                }
        }
},/*}}}*/

        _GetPCRel:function(p,c)//doesn't create those that don't exist/*{{{*/
{
        var pl=this.GetList_OfFamily_OfNode(cParents, c);
        var cl=this.GetList_OfFamily_OfNode(cChildren, p);
        if (null==pl || null==cl) {//it'd be a bug if one of pl or cl is not -1 at this point!
                return null;
        }
        var pi=pl.indexOf(p);
        var ci=cl.indexOf(c);
        if ( (pi != -1) && (ci != -1) ) {//exist
                return new Array(pi,ci);//return index of p, and index of c, in their respective lists, to avoid dup searches via indexOf
        }
},/*}}}*/

        _showallof_family:function(family)/*{{{*/
{
        var a=$A(Object.keys(this.AllNodes[family]));
        var str="";
        var that=this;
        a.each(function (elem) { str+=elem+":"+that.AllNodes[family][elem].toSource()+rnl; } );
        return str;
},/*}}}*/

        _EnsureGetList_OfFamily_OfNode:function(familytype,whichnode) //private function, I wish/*{{{*/
{//always returns an array, even if it wasn't defined previously
        var list=this.AllNodes[familytype];
        if (null===list[whichnode] || !Array.prototype.isPrototypeOf(list[whichnode])) {
        //if (null===list[whichnode] || typeof(list[whichnode]) != "object") {
                list[whichnode]=new Array();
        }
        return list[whichnode];
},/*}}}*/
/*}}}*/

        toSource:function()/*{{{*/
{
        return this.AllNodes.toSource();
},/*}}}*/

        DelNode:function(n)/*{{{*/
{
        var pl=this.GetList_OfFamily_OfNode(cParents,n);
        var cl=this.GetList_OfFamily_OfNode(cChildren,n);
        if (null!=pl) {
                //evaporate all parents, cleanly
                while (pl.length>0) {
                        this.DelPCRel(pl[0],n);
                }

/*                pl.each(function(elem) {
                        alert(this);
                        this.DelPCRel(elem,n);
                });*/
                //delete this.AllNodes[cParents][n];//pl.clear();
        }
        if (null!=cl) {
                //evaporate all parents, cleanly
                while (cl.length>0) {
                        this.DelPCRel(n,cl[0]);
                }
        }

},/*}}}*/

        DelPCRel:function(p,c) //PC=parent,child  (order of params)/*{{{*/
{
        var ar=this._GetPCRel(p,c);
        if (null != ar) {
                var pl=this.GetList_OfFamily_OfNode(cParents, c);
                if (null!=pl) {//null==pl is unlikely because of prev. if
                        pl.splice(ar[0],1);
                }
                this._AutoDelEmptyNode(c);

                var cl=this.GetList_OfFamily_OfNode(cChildren, p);
                if (null!=cl) {
                        cl.splice(ar[1],1);
                }
                this._AutoDelEmptyNode(p);
        }
},/*}}}*/

        IsNode:function(n)/*{{{*/
{//exists only if part of one or more relationships
        //no need to compact() the arrays since compacting is done on delete
        if (this.GetList_OfFamily_OfNode(cParents,n) || this.GetList_OfFamily_OfNode(cChildren,n) ) {
                return true;
        }
        return false;
},/*}}}*/

        IsPCRel:function(p,c)/*{{{*/
{
        if (null!=this._GetPCRel(p,c)){
                return true;
        }
        return false;
},/*}}}*/

        NewPCRel:function (p,c)/*{{{*/
{//a relation can only exist once, ie. a->b once, not a->b and then a->b again, like a:{b,b} there are no DUP elements! dup elements would be on the next level
        if (!this.IsPCRel(p,c)) {
                this._EnsureGetList_OfFamily_OfNode(cChildren, p).push(c);//p->c
                this._EnsureGetList_OfFamily_OfNode(cParents, c).push(p);//c<-p
        }
},/*}}}*/

        inspect:function()/*{{{*/
{
        var pl=this._showallof_family(cParents);
        var cl=this._showallof_family(cChildren);
        return "ChildrenOf:"+rnl+cl+rnl+"ParentsOf:"+rnl+pl;
}/*}}}*/

};/*}}}*/

//------------------------------------------------------------------------------------------

/*var TreeL1=Class.create();
TreeL1.prototype={
        initialize: function(){
                alert('initing');
        },
        speak: function(what) {
                this.e="ceva";
                alert("1:"+this.e);
        }
};*/

var TreeL1=Class.create();
TreeL1.prototype=Object.extend(new TreeL0(), {
        initialize: function() {
        }
/*        speak: function(wh) {
                //TreeL0.prototype.speak.apply(this,[wh]);//this is how calling a base class method is done
                //TreeL0.prototype.speak(wh);//or this
                alert("2:"+this.e);
        }*/
});

//------------------------------------------------------------------------------------------

//test section follows:

//var tree1=new TreeL1();
//tree1.speak("about it");


var tree0=new TreeL0();

var b=new TreeL0();//eval(AllNodes.toSource()));
tree0.NewPCRel("a","b");
tree0.NewPCRel("a","d");
tree0.NewPCRel("a","e");
tree0.NewPCRel("a","e");
tree0.NewPCRel("f","a");
tree0.NewPCRel("f","b");
tree0.NewPCRel("g","a");
alert(tree0.IsPCRel("a","e"));
alert(tree0.inspect());
tree0.DelNode("a");
alert(tree0.IsPCRel("a","b"));
alert(tree0.inspect());


// vim: fdm=marker
