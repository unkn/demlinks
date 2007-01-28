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
*
*
***************************************************************************}}}*/


var cParents="AllParents";
var cChildren="AllChildren";
var rnl="\n";

//TODO: replace, insertafter/before Node, first last using splice()
//insert_InNode_WhichFamily_OfNode_   ("a", cChildren, "e", );
/*
var a=new Cursor_OnTree_OfFamily_OfNode(tree1, cChildren,"a");//make a cursor on children of 'a'
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

var p=new Pointer_OnTree_OfFamily(tree1, cChildren);//with no domain
p.SetPointee("e");//or null
p.SetNull();
p.IsNull();
p.GetPointee();//=="e"; or null if null
p.GetTree();

var dp=new DomainPointer_FamilyKind_OnTree_OfFamily_OfNode(cParents,tree1, cChildren, "a");//pointer uses it's parents list with one element which points to any element which is child of "a"
dp.SetPointee("e");//limited to children of "a"; OR null
dp.GetPointee();//null if none set
dp.GetTree();//returns the tree of where this pointer is part of(or smth
dp.SetNull();//==SetPointee(null)
dp.IsNull();
//use array() named arguments inside a function!

*/

function GetList_OfFamily_OfNode(familytype,whichnode) /*{{{*/
{//doesn't create that which didn't exist! unlike _Ensure*.*
        var list=this.AllNodes[familytype];
        //if (null===list[whichnode] || typeof(list[whichnode]) != "object") {
        if (null===list[whichnode] || !Array.prototype.isPrototypeOf(list[whichnode])) {
                return null;
        }
        return list[whichnode];
}/*}}}*/

function _EnsureGetList_OfFamily_OfNode(familytype,whichnode) //private function, I wish/*{{{*/
{//always returns an array, even if it wasn't defined previously
        var list=this.AllNodes[familytype];
        if (null===list[whichnode] || !Array.prototype.isPrototypeOf(list[whichnode])) {
        //if (null===list[whichnode] || typeof(list[whichnode]) != "object") {
                list[whichnode]=new Array();
        }
        return list[whichnode];
}/*}}}*/

function NewPCrel(p,c)/*{{{*/
{//a relation can only exist once, ie. a->b once, not a->b and then a->b again, like a:{b,b} there are no DUP elements! dup elements would be on the next level
        if (!this.IsPCRel(p,c)) {
                this._EnsureGetList_OfFamily_OfNode(cChildren, p).push(c);//p->c
                this._EnsureGetList_OfFamily_OfNode(cParents, c).push(p);//c<-p
        }
}/*}}}*/

function toSource()/*{{{*/
{
        return this.AllNodes.toSource();
}/*}}}*/

function DelNode(n)/*{{{*/
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

}/*}}}*/

function DelPCRel(p,c) //PC=parent,child  (order of params)/*{{{*/
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
}/*}}}*/

function _AutoDelEmptyNode(n)/*{{{*/
{
        this._AutoDelEmptyNode_OfFamily(n, cParents);
        this._AutoDelEmptyNode_OfFamily(n, cChildren);
}/*}}}*/

function _AutoDelEmptyNode_OfFamily(whichnode, familytype)/*{{{*/
{
        var list=this.AllNodes[familytype];
        if (null !== list[whichnode] && Array.prototype.isPrototypeOf(list[whichnode])) {
                //list[whichnode]=list[whichnode].compact();//this should decrease performance
                if (list[whichnode].length<=0 ) {
                        //then it's empty to can delete it
                        delete list[whichnode];
                }
        }
}/*}}}*/

function IsNode(n)/*{{{*/
{//exists only if part of one or more relationships
        //no need to compact() the arrays since compacting is done on delete
        if (this.GetList_OfFamily_OfNode(cParents,n) || this.GetList_OfFamily_OfNode(cChildren,n) ) {
                return true;
        }
        return false;
}/*}}}*/

function _GetPCRel(p,c){//doesn't create those that don't exist/*{{{*/
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
}/*}}}*/

function IsPCRel(p,c)/*{{{*/
{
        if (null!=this._GetPCRel(p,c)){
                return true;
        }
        return false;
}/*}}}*/

function _showallof_family(family)/*{{{*/
{
        return this.AllNodes[family].toSource();
        //return Object.keys(this.AllNodes[family]);
/*        var par=Object.values(this.AllNodes[family]);
        var pl=(null!=par[0]?'"'+par[0]+'"':null);
        for (var i=1;i<par.length;i++) {
                pl+=',"'+par[i]+'"';
        }
        return pl;*/
}/*}}}*/

function inspect()/*{{{*/
{
        var pl=this._showallof_family(cParents);
        var cl=this._showallof_family(cChildren);
        return "ChildrenOf:"+rnl+cl+rnl+"ParentsOf:"+rnl+pl;
}/*}}}*/

function Tree()/*{{{*/
{
//vars:
        this.AllNodes=new Hash();
        this.AllNodes[cParents]=new Object();//not a hash because it'll overwrite some of its methods, and we need to support any index name!
        this.AllNodes[cChildren]=new Object();
//methods:
        this.NewPCRel=NewPCrel;
        this._EnsureGetList_OfFamily_OfNode=_EnsureGetList_OfFamily_OfNode;
        this.GetList_OfFamily_OfNode=GetList_OfFamily_OfNode;
        this.toSource=toSource;
        this.inspect=inspect;
        this.DelNode=DelNode;
        this.IsPCRel=IsPCRel;
        this.DelPCRel=DelPCRel;
        this.IsNode=IsNode;
        this._showallof_family=_showallof_family;
        this._GetPCRel=_GetPCRel;
        this._AutoDelEmptyNode_OfFamily=_AutoDelEmptyNode_OfFamily;
        this._AutoDelEmptyNode=_AutoDelEmptyNode;
}/*}}}*/

var tree1=new Tree();

var b=new Tree();//eval(AllNodes.toSource()));
//alert(b.toSource());

//alert(tree1.inspect());
//alert(tree1.IsPCRel("a","b"));
tree1.NewPCRel("a","b");
tree1.NewPCRel("a","d");
tree1.NewPCRel("a","e");
tree1.NewPCRel("a","e");
tree1.NewPCRel("f","a");
tree1.NewPCRel("f","b");
tree1.NewPCRel("g","a");
//alert(tree1.IsPCRel("a","e"));
alert(tree1.inspect());
//tree1.DelPCRel("a","e");
tree1.DelNode("a");
//alert(tree1.IsPCRel("a","b"));
//alert(tree1.IsPCRel("a","e"));
/*AllNodes[cParents]["merge"]=[];
AllNodes[cParents]["merge"].push("id2");
AllNodes[cParents]["merge"].push("id3");

b[cParents]["id4"]=[];
b[cParents]["id4"].push("id5");
*/
//alert(AllNodes.toSource());
//alert(b.toSource());
//b[cParents].merge(AllNodes[cParents]);
//alert(b.values()[0]);
//alert(tree1.toSource());
alert(tree1.inspect());
//var a=eval(AllNodes.toSource());


// vim: fdm=marker
