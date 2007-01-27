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

function _exists_Node_InFamily(whichnode, familytype) //private function, I wish
{
        var list=this.AllNodes[familytype];
        if (null===list[whichnode] || typeof(list[whichnode]) != "object") {
                return false;
        }
        return true;
}
function _ensure_Node_InFamily(whichnode, familytype) //private function, I wish
{
        if (!this._exists_Node_InFamily(whichnode, familytype)) {
                this.AllNodes[familytype][whichnode]=new Array();
        }
}

function NewPCrel(p,c)
{
        this._ensure_Node_InFamily(p, cChildren);
        this._ensure_Node_InFamily(c, cParents);
        this.AllNodes[cParents][c].push(p);
        this.AllNodes[cChildren][p].push(c);
}

function toSource()
{
        return this.AllNodes.toSource();
}


function IsPCRel(p,c)
{
        if ( (this._exists_Node_InFamily(c, cParents)) &&
             (this._exists_Node_InFamily(p, cChildren)) ) {
                if ( (this.AllNodes[cParents][c].indexOf(p) != -1)
                   &&(this.AllNodes[cChildren][p].indexOf(c) != -1) ){ //exists
                        return true;
                }
        }
        return false;
}

function inspect()//BAD
{
        var ar=Object.values(this.AllNodes[cParents]);
        var str;
        for (var i=0;i<ar.length();i++) {
                str+=ar[i]+',';
        }
        return str;
}

function Tree()
{
//vars:
        this.AllNodes=new Hash();
        this.AllNodes[cParents]=new Object();//not a hash because it'll overwrite some of its methods, and we need to support any index name!
        this.AllNodes[cChildren]=new Object();
//methods:
        this.NewPCRel=NewPCrel;
        this._ensure_Node_InFamily=_ensure_Node_InFamily;
        this._exists_Node_InFamily=_exists_Node_InFamily;
        this.toSource=toSource;
        this.inspect=inspect;
        this.IsPCRel=IsPCRel;
}

var tree1=new Tree();

var b=new Tree();//eval(AllNodes.toSource()));
//alert(b.toSource());

alert(tree1.IsPCRel("a","b"));
tree1.NewPCRel("a","b");
alert(tree1.IsPCRel("a","b"));
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
alert(tree1.toSource());
//var a=eval(AllNodes.toSource());


