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
* Description: define the input combinations used within ./dml_imagine
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

using namespace std;

        TPolishForm g_Expr;

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
        cout << "-------------------------"<<endl;
        __tIF(NULL==a_Str);
        cout << "\t\t"<<a_Str<<endl;

        int flags=kSideOperandsImplyMul|kNextOpenBraceImpliesMul;
        __if (index(a_Str, ' ')) {
                //auto long operands if spaces are present in expression
                flags |= kLongOperands | kAllowOperandsLongerThanMax;
        }__fi

        std::string tmpStr(a_Str);

        EPFErrors_t err;
        __( err=g_Expr.MakeGraph(tmpStr,a_Form,flags,0,-1));
        __if ( err ) {
                //show error
                PFShowError(err);
                if ((err==kLeftUnclosedBraces)||(g_Expr.rOpenBraces != 0)) {
                        cout << g_Expr.rOpenBraces << " open braces left."<<endl;
                }
                ShowErrorPosition(g_Expr.rIndex, a_Str);
        } else {
                cout << "root["<<g_Expr.rRoot.GetType()<<"]="<<g_Expr.rRoot.GetId()<<endl;
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
/*****************************************************************/
int main(const int argc, const char **argv)
{
        __tIF(0 != allegro_init());

        __(g_Expr.Init());



//        for (int i=0;i<1;i++) {
//***************************************** from polishform to graph
/*
        //default string, if not specified
        char *str="*-^*2x+*3yza-*4t3";
        _(Process(kPolishForm, str));
        _(Process(kPolishForm, "+-*3*^x2y*5*^x2^y2*2*^y3x"));
        _(Process(kPolishForm, "+-*3*^x 2 y*5*^x 2^y 2*2*^y 3 x"));//intend to add variant with operands more than one char long
        _(Process(kPolishForm, "+-ab"));
// ***************************************** from arithmetic form to graph

        _(Process(kArithmeticForm,"a+(b+c)+d"););

        _(Process(kArithmeticForm,"a+(b-c-d+(((e+f"););
        _(Process(kArithmeticForm,"a++d"););
        _(Process(kArithmeticForm,"(-a+b*c)"));
        _(Process(kArithmeticForm,"b+(-a)"););
        _(Process(kArithmeticForm,"+(-a+b)"););
        _(Process(kArithmeticForm,"-a*b"););
        _(Process(kArithmeticForm,"-a*(b+c)"););
        _(Process(kArithmeticForm,"(b)-a*(-b+c)"););
        _(Process(kArithmeticForm,"(b)-a*-b"););
        _(Process(kArithmeticForm,"a+b*c+d"));
        _(Process(kArithmeticForm,"a+(b*c+d)"));
        _(Process(kArithmeticForm,"a+(b+c)*d"));
        _(Process(kArithmeticForm,"a*(b+c)*d"));
        _(Process(kArithmeticForm,"(a+e)*(b+c)*d+f"));
        _(Process(kArithmeticForm,"a*b+f"));
        _(Process(kArithmeticForm,"-a+b+c"));
        _(Process(kArithmeticForm,"-a*b+c"));
        _(Process(kArithmeticForm,"-a*(-b)+c"));
        _(Process(kArithmeticForm,"a"));
        _(Process(kArithmeticForm,"g+"));
        _(Process(kArithmeticForm,"+j"));
        _(Process(kArithmeticForm,"(a"));
        _(Process(kArithmeticForm,"(abc)-2"));
        _(Process(kArithmeticForm,"(abc)- 2"));
        _(Process(kArithmeticForm,"(ab c)- 2"));
        _(Process(kArithmeticForm,"3(ab+c)d"));
        _(Process(kArithmeticForm,"3(ab+c)   d"));
        _(Process(kArithmeticForm,"a+b "));
        _(Process(kArithmeticForm,"L+E+F+T+(A/B)*1^5"));
        _(Process(kArithmeticForm,"A+B*C^D"));
        _(Process(kArithmeticForm,"A+E+K+B*C+F*G^D"));
        _(Process(kArithmeticForm,"B+C*F*G*H^D"));
        _(Process(kArithmeticForm,"  (  (  A  )  )  "));
        _(Process(kArithmeticForm,"  a  +  b  "));
        _(Process(kArithmeticForm,"  +  b  "));
        _(Process(kArithmeticForm," A "));
        _(Process(kArithmeticForm," A + b "));
        _(Process(kArithmeticForm,"+b"));
        _(Process(kArithmeticForm,"   "));
        _(Process(kArithmeticForm,"   a"));
        _(Process(kArithmeticForm,"a   "));
        _(Process(kArithmeticForm,"d+(a+b)+c   "));
        _(Process(kArithmeticForm,"a+b+c+d+e+f   "));
        _(Process(kArithmeticForm,"+b*c   "));
        _(Process(kArithmeticForm,"a + ( b ) * c   "));
        _(Process(kArithmeticForm,"a + "));
        _(Process(kArithmeticForm,"a  "));
        _(Process(kArithmeticForm,"a+-b  "));
        _(Process(kArithmeticForm,"  a  +  (  -  b  )   "));
        _(Process(kArithmeticForm,"  a  +    -  b     "));
        _(Process(kArithmeticForm,"  a  +    (-  b    (((("));
        _(Process(kArithmeticForm,"  a  +    (-  b    (()("));
        _(Process(kArithmeticForm,"a+-+-+------b  "));
        _(Process(kArithmeticForm,"a+- + -+   --    - ---b  "));
        _(Process(kArithmeticForm,"a+- + -+   -*+    - ---b  "));
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
*/
        __(Process(kArithmeticForm," _KEY_TILDE & `KEY_TILDE"));
        __(Process(kArithmeticForm," _KEY_ESC | (_KEY_ALT & _KEY_Q)"));
        __(Process(kArithmeticForm," (_KEY_LSHIFT & `KEY_LSHIFT) | (_KEY_RSHIFT & `KEY_RSHIFT)"));
        __(Process(kArithmeticForm," (_KEY_CTRL & _KEY_Q) | (_KEY_CTRL & _KEY_X)"));//how the fsck do u handle this? multiple trees associated with one action!
        __(Process(kArithmeticForm," _KEY_CTRL & (_KEY_Q | _KEY_X)"));//i know this is the correct form
//        }//for

        g_Expr.ShowContents();
//***************************************** END
        printf("All ok.\n");
}//main

