/****************************************************************************
*
*                             dmental links
*    Copyright (C) June 2006 AtKaaZ, AtKaaZ at users.sourceforge.net
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
* Description: define the input combinations used within ./dml_imagine (not yet)
*
****************************************************************************/

/*
the polish form of an expression can be represented as a graphical binary tree
ie.     a+b                     => +ab
                   <----|
        (a+b)*c+d*2        => +*+abc*d2
        ((2*x)^(3*y+z)-a)*(4*t-3)       =>      *-^*2x+*3yza-*4t3
        (2-(4+(3*5)))*(4+3)             =>      *-2+4*35+43
 * graful este parcurs in preordine (parse the graph from left+down to right+down)[preorder?] but the graph is currently drawn rotated 90deg the_up_to_left(that is look at the up side and move it to left while you rotate the graph) AND_THEN flip vertical[so you take the graph output by this prog and you flip it vertical(upside down; vertical mirror?) then you rotate the result 90deg right (while holding the bottom part fixed).
 * anyways L is left and R is right (after successful rotation)
*/

#include <iostream>
#include <allegro.h>
#include "pnotetrk.h"
#include "polishform.h"
#include "dmlenv.h"

using namespace std;

        TPolishForm *g_Expr;
        TLink *gLink;

/*****************************************************************/

/*****************************************************************/
void
ShowErrorPosition(
                const int a_ErrPos,
                const char * const a_InStr)
{
                printf("\n");
                char * beginstr = "\"";
                printf("%s%s\"\n", beginstr, a_InStr);
                for ( int i = 1 - strlen(beginstr); i <= a_ErrPos; i++ ) {
                        printf(" ");
                }//for
                printf("^ error here.\n");
}

/*****************************************************************/
void
Process(
                const EForms_t a_Form,
                const char * const a_Str)
{
        cout << "------------START-------------"<<endl;
        __tIF(NULL==a_Str);
        cout << "\t\t"<<a_Str<<endl;

        int flags=kSideOperandsImplyMul|kNextOpenBraceImpliesMul;
        __if (index(a_Str, ' ')) {
                //auto long operands if spaces are present in expression
                flags |= kLongOperands | kAllowOperandsLongerThanMax;
        }__fi

        std::string tmpStr(a_Str);

        EPFErrors_t err;
        __( err=g_Expr->MakeGraph(tmpStr,a_Form,flags,0,-1));
        __if ( err ) {
                //show error
                PFShowError(err);
                if ((err==kLeftUnclosedBraces)||(g_Expr->rOpenBraces != 0)) {
                        cout << g_Expr->rOpenBraces << " open braces left."<<endl;
                }
                ShowErrorPosition(g_Expr->rIndex, a_Str);
        } __fielse {
                cout << "root["<<g_Expr->rRoot.GetType()<<"]="<<g_Expr->rRoot.GetId()<<endl;
                /*_if (err=g_Expr.ShowExpr(root.GetId(),1,1)) {
                        cout << "Problem attempting to show expression from graph with root="<<root.GetId()<<" : ";
                        PFShowError(err);
                }_fi*/
        }__fi
#ifdef TRACKABLE_RETURNS
                ShowAllNotifications();
#endif
}

/*****************************************************************/
function
ShowAllNodesOfNode(
                TDMLCursor *m_Curs,
                const ENodeType_t a_NodeType,
                const NodeId_t a_NodeId,
                DbTxn *a_ParentTxn=NULL
                )
{
        cout << "-----------Show---"<< (a_NodeType==kGroup?"Group":"SubGroup") <<"-----"<< a_NodeId <<endl;
        __tIF(NULL == m_Curs);
        bool wasInited;
        __( wasInited=m_Curs->IsInited() );//std call, i know it doesn't throw but when it could who will add __() ?!
        if (! wasInited) {
                __tIFnok( m_Curs->InitFor(a_NodeType,a_NodeId, kNone, a_ParentTxn/*parent txn*/) );//prepare to parse kSubGroups of kGroup with id "A1"; create "A1" if not exists;
        }
        bool once=false;
        while (true) {
                NodeId_t node;

                function err;


                __( err=m_Curs->Get(node,kNextNode) );//first time kNextNode is kFirstNode
                if (kFuncNotFound == err) {
                        //once=true;
                        if (once) {
                                break;
                        } else {
                                __tIFnok( m_Curs->DeInit() );//release berkeleydb cursor
                                _FA(no records);
                        }
                } else __tIFnok(err);
                if (!once) {
                        once=true;
                }
                cout << "Node: "<<node<<endl;
        };
        if (! wasInited) {
                __tIFnok( m_Curs->DeInit() );//release berkeleydb cursor
        }
        _OK;
}

/*****************************************************************/
/*****************************************************************/
/*****************************************************************/
int main(const int argc, const char **argv)
{
        __tIF(0 != allegro_init());

        std::string envHomePath("./dbhome/");
        __(gLink=new TLink(envHomePath));
        __tIF(NULL==gLink);


        __(g_Expr = new TPolishForm(gLink));

        //__(g_Expr->Init());



//        for (int i=0;i<1;i++) {
//***************************************** from polishform to graph
        char *str="*-^*2x+*3yza-*4t3";
        __(Process(kPolishForm, str));
        __(Process(kPolishForm, "+-*3*^x2y*5*^x2^y2*2*^y3x"));
        __(Process(kPolishForm, "+-*3*^x 2 y*5*^x 2^y 2*2*^y 3 x"));//intend to add variant with operands more than one char long
        __(Process(kPolishForm, "+-ab"));
// ***************************************** from arithmetic form to graph

        __(Process(kArithmeticForm,"a+(b+c)+d"););

        __(Process(kArithmeticForm,"a+(b-c-d+(((e+f"););
        __(Process(kArithmeticForm,"a++d"););
        __(Process(kArithmeticForm,"(-a+b*c)"));
        __(Process(kArithmeticForm,"b+(-a)"););
        __(Process(kArithmeticForm,"+(-a+b)"););
        __(Process(kArithmeticForm,"-a*b"););
        __(Process(kArithmeticForm,"-a*(b+c)"););
        __(Process(kArithmeticForm,"(b)-a*(-b+c)"););
        __(Process(kArithmeticForm,"(b)-a*-b"););
        __(Process(kArithmeticForm,"a+b*c+d"));
        __(Process(kArithmeticForm,"a+(b*c+d)"));
        __(Process(kArithmeticForm,"a+(b+c)*d"));
        __(Process(kArithmeticForm,"a*(b+c)*d"));
        __(Process(kArithmeticForm,"(a+e)*(b+c)*d+f"));
        __(Process(kArithmeticForm,"a*b+f"));
        __(Process(kArithmeticForm,"-a+b+c"));
        __(Process(kArithmeticForm,"-a*b+c"));
        __(Process(kArithmeticForm,"-a*(-b)+c"));
        __(Process(kArithmeticForm,"a"));
        __(Process(kArithmeticForm,"g+"));
        __(Process(kArithmeticForm,"+j"));
        __(Process(kArithmeticForm,"(a"));
        __(Process(kArithmeticForm,"(abc)-2"));
        __(Process(kArithmeticForm,"(abc)- 2"));
        __(Process(kArithmeticForm,"(ab c)- 2"));
        __(Process(kArithmeticForm,"3(ab+c)d"));
        __(Process(kArithmeticForm,"3(ab+c)   d"));
        __(Process(kArithmeticForm,"a+b "));
        __(Process(kArithmeticForm,"L+E+F+T+(A/B)*1^5"));
        __(Process(kArithmeticForm,"A+B*C^D"));
        __(Process(kArithmeticForm,"A+E+K+B*C+F*G^D"));
        __(Process(kArithmeticForm,"B+C*F*G*H^D"));
        __(Process(kArithmeticForm,"  (  (  A  )  )  "));
        __(Process(kArithmeticForm,"  a  +  b  "));
        __(Process(kArithmeticForm,"  +  b  "));
        __(Process(kArithmeticForm," A "));
        __(Process(kArithmeticForm," A + b "));
        __(Process(kArithmeticForm,"+b"));
        __(Process(kArithmeticForm,"   "));
        __(Process(kArithmeticForm,"   a"));
        __(Process(kArithmeticForm,"a   "));
        __(Process(kArithmeticForm,"d+(a+b)+c   "));
        __(Process(kArithmeticForm,"a+b+c+d+e+f   "));
        __(Process(kArithmeticForm,"+b*c   "));
        __(Process(kArithmeticForm,"a + ( b ) * c   "));
        __(Process(kArithmeticForm,"a + "));
        __(Process(kArithmeticForm,"a  "));
        __(Process(kArithmeticForm,"a+-b  "));
        __(Process(kArithmeticForm,"  a  +  (  -  b  )   "));
        __(Process(kArithmeticForm,"  a  +    -  b     "));
        __(Process(kArithmeticForm,"  a  +    (-  b    (((("));
        __(Process(kArithmeticForm,"  a  +    (-  b    (()("));
        __(Process(kArithmeticForm,"a+-+-+------b  "));
        __(Process(kArithmeticForm,"a+- + -+   --    - ---b  "));
        __(Process(kArithmeticForm,"a+- + -+   -*+    - ---b  "));
        __(Process(kArithmeticForm,"a+b+c+d+e*f*g*h*i^j^k^l^m*n*o*p+q+r+s-t"));
        __(Process(kArithmeticForm,"a^b^c^d^e*f*g*h*i-j-k-l+m"));
        __(Process(kArithmeticForm,"a*(b*c^d)^e*f*g+h+i"));
        __(Process(kArithmeticForm,"a^(b*c^d)^e*f*g+h+i"));
        __(Process(kArithmeticForm,"a^b^(c^!d^(--e(a+b))*f+g)*(g*h*i-j-k)-l+m"));
        __(Process(kArithmeticForm,"a^b^(c^!d i^(--e(a+b))*f+g)*(g*h*i-j-k)-l+m"));
        __(Process(kArithmeticForm,"((2*x)^(3*y+z)-a)*(4*t-3)"));
        __(Process(kArithmeticForm," _KEY_ESC _KEY_ESC2"));
        __(Process(kArithmeticForm," _KEY_ESC *~KEY_ESC2"));
        __(Process(kArithmeticForm," ~KEY_LSHIFT"));//press & release(harder to parse)
        __(Process(kArithmeticForm," _KEY_TILDE & `KEY_TILDE"));
        __(Process(kArithmeticForm," _KEY_ESC | (_KEY_ALT & _KEY_Q)"));
        __(Process(kArithmeticForm," (_KEY_LSHIFT & `KEY_LSHIFT) | (_KEY_RSHIFT & `KEY_RSHIFT)"));
        __(Process(kArithmeticForm," (_KEY_CTRL & _KEY_Q) | (_KEY_CTRL & _KEY_X)"));//how the fsck do u handle this? multiple trees associated with one action!
        __(Process(kArithmeticForm," _KEY_CTRL & (_KEY_Q | _KEY_X)"));//i know this is the correct form
//        }//for
        __(Process(kArithmeticForm," KEY_CTRL"));//i know this is the correct form
        __(Process(kArithmeticForm,"a+(b+c)+d"););

        __tIFnok( g_Expr->ShowContents() );
        __(delete g_Expr);//gLink should still be open and available after this!
//***************************************** END

        TDMLPointer *mePoints;
        __( mePoints=new TDMLPointer(gLink) );
        __tIFnok( mePoints->Init("ptrA", kCreateNodeIfNotExists) );
        __tIFnok( mePoints->DeInit() );
        __tIFnok( mePoints->Init("ComposedOperand", kCreateNodeIfNotExists | kTruncateIfMoreThanOneNode | kKeepPrevValue) );
        __tIFnok( mePoints->DeInit() );

        TDMLCursor *meCurs;
        __( meCurs=new TDMLCursor(gLink) );//done after DBs are inited!!!

        __tIFnok( ShowAllNodesOfNode(meCurs, kGroup,"ComposedOperand") );
        __tIFnok( ShowAllNodesOfNode(meCurs, kSubGroup,"B") );

        cout << "---WRite"<<endl;
        //DbTxn *tmp1;
        //__tIFnok(gLink->NewTransaction(NULL,&tmp1 ));

        NodeId_t nod,nod2;
        nod2="B";
        __tIFnok( meCurs->InitFor(kSubGroup,nod2) );//prepare to parse kSubGroups of kGroup with id "A1"; create "A1" if not exists; DB_WRITECURSOR acquire write locks with this cursor
        //__tIFnok( meCurs->Put("J", kBeforeNode, "C") );
        //__tIFnok( meCurs->Put("F", kThisNode, "J") );
        __tIFnok( meCurs->Put("G", kLastNode) );
        __tIFnok( meCurs->Put("1", kFirstNode) );
        __tIFnok( meCurs->Put("Z", kLastNode) );
        //__tIFnok( meCurs->Put("J", kBeforeNode, "G") );
        //__tIFnok( meCurs->Put("F", kThisNode, "J") );
        __tIFnok( meCurs->Put("J", kBeforeNode, "C") );
        __tIFnok( meCurs->Put("F", kThisNode, "J") );
        __tIFnok( ShowAllNodesOfNode(meCurs, kSubGroup,nod2) );
        db_recno_t here=0;
        __sIFnok( meCurs->Count(here) );
        cout << here << " records so far." <<endl;
        //__tIFnok( meCurs->Get(nod, kFirstNode) );
        //cout<<nod<<endl;
        __tIFnok( meCurs->DeInit() );//release berkeleydb cursor
        cout << "---DeInit-ed"<<endl;

        //__tIFnok( gLink->Commit(&tmp1) );


        __tIFnok( ShowAllNodesOfNode(meCurs, kSubGroup,nod2) );
        //__tIFnok( ShowAllNodesOfNode(meCurs, kGroup,"F",NULL) );
        //__sIFnok( ShowAllNodesOfNode(meCurs, kGroup,"J",NULL) );//obv. none!

        __( delete meCurs );//gLink should still be open and available after this!
        __( delete mePoints );
        mePoints=NULL;
        meCurs=NULL;

        __tIFnok( gLink->ShowContents() );
        __( delete gLink );

//***
        printf("All ok.\n");
}//main

