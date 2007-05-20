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
* Description:
*    <POLISHFORM, of an arithmetic expression,
*        how to build a tree graph from polish form or arithmetic form expr.>
*
****************************************************************************/

#include <iostream>
#include "pnotetrk.h"
#include "dmlenv.h"
#include "polishform.h"
#include "portability.h"

using namespace std;

#define MAX_STR_LEN 65530 //for strnlen()


const NodeId_t kIDExpressions="Expression";
const NodeId_t kIDComposedOperand="ComposedOperand";
const NodeId_t kIDLeftOperand="LeftOperand";
const NodeId_t kIDRightOperand="RightOperand";
const NodeId_t kIDOperator="OPerator";

const char * PFErrorStrings[kMaxPFErrors]={
        "All OK."
        ,"Undefined error"
        ,"Open braces '(' go too deep, check MAX_BRACES_DEPTH !"
        ,"Too many closed braces ')' , no matching open one."
        ,"Expected operand or '(' but got operator."
        ,"Expected operand or '(' but got ')', => unnecessary pair of braces!"
        ,"Expected operand or '(' but got end of string."
        ,"Expected operator, got end of string."
        ,"Expected operator, got '('"
        ,"Expected operator, got ')'"
        ,"Expected operator, got operand (see kSideOperandsImplyMul)"
        ,"kLeftUnclosedBraces"
        ,"Detected operand longer than MAX_OPERAND_LEN and kAllowOperandsLongerThanMax was not set to override this"
        ,"Invalid char in operand name (see kAllowableOperandChars)"
        ,"kUnexpectedEOS,Unexpected end of leftExpression"
        ,"Expected right operand, got EOS"
        ,"kExpectedLeftOperandNotEOS, Expected left operand, got EOS"
        ,"kReachedEOS, Reached EndOfString"
        ,"kFailedCreatingRootExpressionInTree, Making connection to RootId inside the tree(db) failed for some buggy reason"
        ,"kRootNotFound,Root of leftExpression(a composed operand Id) not found in kIDExpressions"
        ,"kTemporaryIteratorExists"
        ,"kIndexAlreadyAtEdge"
        ,"kIncompleteParse"
        ,"kExpectedSimpleOperand"
};


/*****************************************************************/
const char * kDelimiters=" ";//one char based delimiters used to delimit operands/operator ie. a + b <=> a+b
const char * kAllowableOperandChars="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890_";
          const char * kIDOperators="+-" _OPERATOR_MUL "&|/:^!~_`";
          const char * kIDLackingLeft_Operators="+-!~_`";
const char * kIDOperatorsPrecedence="00" "1"           "111123333";//higher number higher precedence

/*****************************************************************/
bool
IsOperator(const std::string a_Str)
{
        __tIF(a_Str.empty());
        __tIF(a_Str.length() >1 );//only considering one char anyways, but passed more than one, so could be a bug earlier which we signal here
        bool eval;
        __(eval=IsOperator(a_Str.at(0)));
        return eval;
}

bool
IsOperator(const char a_Char)
{
        __tIF('\0'==a_Char);
        return (NULL != index(kIDOperators,int(a_Char)));
}


bool
IsAllowedOperatorForLackingLeftOperand(const char a_Char)
{
        __tIF('\0'==a_Char);
        return (NULL != index(kIDLackingLeft_Operators,int(a_Char)));
}
bool
IsAllowedOperatorForLackingLeftOperand(const std::string a_Str)
{
        __tIF(a_Str.empty());
        __tIF(a_Str.length() >1 );//only considering one char anyways, but passed more than one, so could be a bug earlier which we signal here
        bool eval;
        __(eval=IsAllowedOperatorForLackingLeftOperand(a_Str.at(0)));
        return eval;
}

/*****************************************************************/
int
GetLen(const char * const a_Str)
{
        __tIF(NULL==a_Str);
        int len;
        __(len=strnlen(a_Str, MAX_STR_LEN));
        return len;
}
/*****************************************************************/

/*****************************************************************/
int
CmpPrecedence(
                const TOperator &a_First
                ,const TOperator &a_Second)
{//ugly hack lazyness-based
        //cout << a_First.GetId() << " "<< a_Second.GetId();

        __tIF(a_First.IsNotDefined());
        __tIF(a_Second.IsNotDefined());
        __tIF(a_First.GetId().length()>1);
        __tIF(a_Second.GetId().length()>1);
        //only 1 char long operators are considered
        char *x;
        __(x=index(kIDOperators,int(a_First.GetId().at(0))));
        char *y;
        __(y=index(kIDOperators,int(a_Second.GetId().at(0))));
        __tIF(NULL==x);
        __tIF(NULL==y);
        int a=(x-kIDOperators);
        int b=(y-kIDOperators);
        __tIF(a<0);
        __tIF(b<0);
        int len;
        __(len=GetLen(kIDOperatorsPrecedence));
        __tIF(a>=len);
        __tIF(b>=len);
        a=kIDOperatorsPrecedence[a] -0x30;
        b=kIDOperatorsPrecedence[b] -0x30;
        //cout << " => "<<a<<" "<<b<<endl;
        if (a>b)
                return +1;
        if (a==b)
                return 0;
        else
                return -1;
}

/*****************************************************************/

/*****************************************************************/
/*****************************************************************/
/*****************************************************************/
void
PFShowError(const EPFErrors_t a_Err)
{
        __tIF( a_Err < 0);
        __tIF( a_Err >= kMaxPFErrors);
        cout << PFErrorStrings[a_Err]<<endl;
}
/*****************************************************************/
/*****************************************************************/
//constructor1
TPolishForm::TPolishForm(TLink *m_WorkingOnThisTLink):
        fTxn(NULL)
        ,fSense(kForward)
        ,fLevel(_PF_LEVEL0)
                /*a_Form(kNoForm),
                fLowerBound(0),
                fHigherBound(-1),
                fExprFlags(0),
                rErr(kPFNoError),
                rOpenBraces(0),
                rIndex(0)
{
        fStr.clear();
        rRoot.Clear();*/
{
        __tIF(NULL == m_WorkingOnThisTLink);
        fLink=m_WorkingOnThisTLink;
        for (int i=0; i<_MAX_FUNIQ; i++)
                fUniq[i]=_LEADING_CHAR;
        fUniq[_MAX_FUNIQ]='\0';
}
/*void
TPolishForm::Init()
{
        //__tIF(fLink); //calling twice ? throw exception if so
        //std::string envHomePath("./dbhome/");
        //__(fLink=new TLink(envHomePath));
        //__tIF(NULL==fLink);
}
*/
/*****************************************************************/
/*****************************************************************/
//destructor
TPolishForm::~TPolishForm()
{
#define THROW_HOOK \
        BDBCLOSE_HOOK

        __tIF(NULL == fLink);//cannot be
        _htIF(fTxn);//a still open transaction?! this is a bad bug:P

        //BDBCLOSE_HOOK;don't do this on deinit, only when errors are found and thrown
#undef THROW_HOOK
}


/*****************************************************************/
/*****************************************************************/
/*****************************************************************/
/* we assume we're getting a ptr to a string containing a polishform notation of an arithmetic leftExpression
*this is a recursive function (calls itself)
*common Index used, common string(to parse) used between recursive calls
* returns the position of the last error
*/
EPFErrors_t
TPolishForm::polishToGraph()
{//ie. a_Str = "*-^*2x+*3yza-*4t3";

#define THROW_HOOK \
        __(rRoot.Clear());//guaranteed NULL return on error

        EPFErrors_t err;

        _hif ( kReachedEOS != (err=eatDelimiter() )) {
                _htIF(err != kPFNoError);//just in case eatDelimiter returns more than one different error (currently kReachedEOS) need to be handled here, since it's assumed that if not kReachedEOS then there are no errors

                std::string curChar;
                _hif ( err=getCurChar(curChar) ) {
                        _hret(err);//EOS handled above
                }_fih

                _hif (IsOperator(curChar)) {//is operator and curChar is set to it!
                        //going LEFT

                        //pos for next
                        _h( pos4Next(); );

                        _hif ( err=polishToGraph() ) {
                                _hret(kExpectedLeftOperandNotEOS);
                        }_fih
                        TOperand left=rRoot;

                        //going RIGHT

                        //pos for next is autopositioned

                        _hif ( err=polishToGraph() ) {
                                _hret(kExpectedRightOperandNotEOS);
                        }_fih

                        TOperator sign;
                        _h(sign.SetId(curChar) );
                        _h( rRoot=makeOperand(left,sign,rRoot) );
                        _ret(kPFNoError);
                } _fihelse {//not operator
                        //because we're called recursive, we don't know if this is the first call, if it were then this should be an error if we're not at end of string
                        _hif ( err=getSimpleOperand()) {
                                _hret(err);
                        }_fih
                        _ret(kPFNoError);//so far so good
                }_fih
        } _fihelse {
                _hret(kUnexpectedEOS);
        }_fih
#undef THROW_HOOK
}

EPFErrors_t
TPolishForm::MakeGraph(
                const std::string a_Str,
                const EForms_t a_Form,
                const int a_ExprFlags,
                const int a_LowerBound,
                const int a_HigherBound
                )
{//a_StartFrom is left at pos where error occured if any, otherwise ==0

#define THROW_HOOK \
        BDBCLOSE_HOOK

        _h(rRoot.Clear());

        EPFErrors_t err;

        rOpenBraces=0;//a must! before the call to getExpr()

{//block
        _htIF(a_Str.empty());//FIXME: use _reterr or smth

        fStr=a_Str;
        int length;
        _h(length=fStr.length());//default
        _hif ((a_HigherBound < 0)||(a_HigherBound >= length)) {
                fHigherBound=length-1;
        } _fihelse {
                fHigherBound=a_HigherBound;
        }_fih

        fLowerBound=a_LowerBound;

        if (fLowerBound < 0){
                fLowerBound=0;
        } else if (fLowerBound > fHigherBound) {
                fLowerBound=fHigherBound;//1 char heh
                }

}//endblock

        fExprFlags = a_ExprFlags;


        _h(fLink->NewTransaction(NULL /*parent*/,&fTxn););

#undef ERR_HOOK
#define ERR_HOOK \
        AB_HOOK

#undef THROW_HOOK
#define THROW_HOOK \
        ERR_HOOK \
        BDBCLOSE_HOOK

if (a_Form == kArithmeticForm) {

        TOperand rOperand;
        TOperator rOperator;
        fSense=kForward;
        _h(initIndex());
        _h( rOperand.Clear() );
        _h( rOperator.Clear() );
        fLevel=_PF_LEVEL0;
        _h( err = getExpr(rOperand,rOperator) );
        _htIF(rOperator.IsDefined());
        //_tIF(1 != fLevel);

        if (err) {
                _hreterr(err);
        } else {
                _htIF(rOpenBraces < 0 );//impossible

                if (rOpenBraces != 0) {//some braces not closed
                        //"a+(b-c-d+(((e+f" is considered valid until here
                        _hreterr(kLeftUnclosedBraces);
                }

                _h( rRoot = rOperand );
                _htIF(rRoot.IsNotDefined());
                function tlerr;
                _hif ( kFuncOK != (tlerr=fLink->NewLink(kIDExpressions,rRoot.GetId(),fTxn))) {
                        if (tlerr != kFuncAlreadyExists) {
                                cout << tlerr<<endl;
                                _hreterr(kFailedCreatingRootExpressionInTree);
                        }
                        //ignoring if already exists
                }_fih
        }//fi err

} else { if (a_Form == kPolishForm) {
                fSense=kForward;
                _h(initIndex());
                {//block
                        std::string c;
                        _hif ( err=getCharAt(fLowerBound,c) ) {
                                _hret(err);
                        }_fih
                _hif ( ! IsOperator(c) ) {
                //first char must be an operator, otherwise invalid polish form
                        _hreterr(kExpectedOperatorNotOperand);//there are no parantheses in polish form (only operands and operators)
                }_fih
                }//block


        _h( err= polishToGraph();
         );
        if (err != kPFNoError) {
                _hreterr(err);
        } else {//noerror:
                _hif (rIndex <= fHigherBound) {//we didn't parse it all
                        _hreterr(kIncompleteParse);
                }_fih

                _htIFnok(fLink->NewLink(kIDExpressions,rRoot.GetId(),fTxn)
                 );
        }

} else {
        _ht("undefined form");
}//fi normal
}

#undef ERR_HOOK
#undef THROW_HOOK
#define THROW_HOOK BDBCLOSE_HOOK

        _h(fLink->Commit(&fTxn););


//all ok:
        _ret(kPFNoError);//no errors
#undef THROW_HOOK
}
/********/
EPFErrors_t
TPolishForm::setLessBraces()
{
        if (rOpenBraces >= MAX_BRACES_DEPTH) {//trying to go too deep
                return kTooDeep;
        }
        rOpenBraces++;
        return kPFNoError;
}
/********/
EPFErrors_t
TPolishForm::setMoreBraces()
{
        if (rOpenBraces <= 0) {//too many close braces
                return kNoMatchingOpenBrace;
        }
        rOpenBraces--;
        return kPFNoError;
}
/********/
bool
TPolishForm::IsIndexAtEdge(int a_Ofs)
{//checks if a_Index is within bounds to access an element of a char * string except when a_Index points to the \0 (NUL) characted and end of string (returns true)
        //_tIF(rIndex < fLowerBound);//bug
        //_tIF(rIndex > fHigherBound);//bug
        if (a_Ofs<0)
                a_Ofs=rIndex;
        if ((a_Ofs < fLowerBound)||(a_Ofs > fHigherBound)) {
                return true;//however index is not addressable!
        }
        return false;
}
/********/
bool
IsOperandChar(const std::string a_Str)
{
        __tIF(a_Str.empty());
        __tIF(a_Str.length() >1);
        bool b;
        __( b=(NULL != index(kAllowableOperandChars, int(a_Str.at(0)))) );
        return b;
}
/********/
bool
IsOperandChar(const char a_Char)
{
        __tIF('\0'==a_Char);
        bool b;
        __( b=(NULL != index(kAllowableOperandChars, int(a_Char))) );
        return b;
}
/********/
bool
IsDelimiter(const std::string a_Str)
{
        __tIF(a_Str.empty());
        __tIF(a_Str.length() >1);
        bool b;
        __( b=(NULL != index(kDelimiters, int(a_Str.at(0)))) );
        return b;
}
/********/
EPFErrors_t
TPolishForm::getComposedOperand(
                TOperand &m_Into
                )
{//autoadvances such that on exit a_Index points to next char to be processed (that is if nothing failed)
#define THROW_HOOK \
        __(m_Into.Clear());//guaranteeing a NULL param return on failure

        _htIF(fStr.empty());//bug

        EPFErrors_t err;

        std::string curChar;
        _hif ( err=getCurChar(curChar) ) {
                _htIF( kUnexpectedEOS == err );//this cannot happen here, handled above in getExpr() function
             /*   if (err == kUnexpectedEOS) {
                         _hret kExpectedOperandNotEOS;//this cannot happen here, handled above in getExpr() function
                }*/
                _hret(err);//if any other error happened
        }_fih

        _hif( IsOperator(curChar) ) {//FIXME a++B is allowed since we don't know when we're first time a+  or second time +B
                _hif (IsAllowedOperatorForLackingLeftOperand(curChar)) {
                        //_(m_Into.SetId("0");)
                        //return kPFNoError;
                        _hret(kPFNoError);//allowing missing operand before operator, and not moving to next (we remain stationary on the operator(sign))
                }_fih
                _hret(kExpectedOperandNotOperator);
        }_fih

        _hif (curChar == _AF_STR_CLOSEBRACE) {
                _hret(kExpectedOperandNotClosedBrace);
        }_fih

        _hif (curChar == _AF_STR_OPENBRACE) {//a begining of an leftExpression that is to be considered as an operand, considering fSense if kBackward
                _hif ( (err=setLessBraces()) ) {
                        _hret(err);
                }_fih

                _hif (err=pos4Next()) {
                        _hret(kExpectedOperandNotEOS);
                }_fih

                TOperand l;
                TOperator p;
                        int prevLevel=fLevel;
                        fLevel=_PF_LEVEL0;
                        _hif ( err = getExpr(l,p) ) {
                                _hret(err);//some nasty error
                        }_fih
                        fLevel=prevLevel;

                        _htIF(l.IsNotDefined());//impossible

                _h(m_Into=l );
        } _fihelse {
                //if not operator AND not ')' AND not '(' then assumed simple operand
                _hif ( err= getSimpleOperand()) {
                        _hret(err);
                }_fih
                _h( m_Into=rRoot );
        }_fih

        //cout<<"Operand:"<<m_Into.GetId()<<endl;
        _ret(kPFNoError);
#undef THROW_HOOK
}
/********/
EPFErrors_t
TPolishForm::getSimpleOperand()//into rRoot
{
#define THROW_HOOK \
        __(rRoot.Clear());//guaranteeing a NULL param return on failure

        EPFErrors_t err;
                std::string curChar;
                _hif ( err=getCurChar(curChar) ) {
                        if (kUnexpectedEOS == err) {
                                _hret(kExpectedSimpleOperand);
                        }
                        _hret(err);
                }_fih

        _hif (!IsOperandChar(curChar)) {
                _hret(kUnallowedCharForOperand);
        }_fih

        _hif (fExprFlags & kLongOperands) {

                //taking max operand length (ie. until EOS)
                std::string tmp;
                do {
                        _h(tmp.append(curChar););

                        _h( pos4Next(); );

                        _hif ( err=getCurChar(curChar) ) {
                                if (err==kUnexpectedEOS) {
                                        break;
                                }
                                _hret(err);
                        }_fih
                        //until:
                        _hif (!IsOperandChar(curChar)) {
                                break;
                        }_fih

                        _hif ( (! (fExprFlags & kAllowOperandsLongerThanMax)) && (tmp.length() >= MAX_OPERAND_LEN)) {
                                        _hret(kOperandLongerThanMax);
                        }_fih

                        //all constraints ok, then we get this next and add it later at top of 'do'
                } while (true);

                rRoot.Set(kSimpleOperand, tmp);
                //already at next or EOS
        } _fihelse {//one char operands
                rRoot.Set(kSimpleOperand, curChar);
                _h( pos4Next(); );
        }_fih

        _ret(kPFNoError);
#undef THROW_HOOK
}
/********/
EPFErrors_t
TPolishForm::eatDelimiter(
                )
{
        while (true) {
                std::string curChar;
                EPFErrors_t err;
                __if ( err=getCurChar(curChar) ) {
                        if (err==kUnexpectedEOS) {
                                return kReachedEOS;
                        }
                        return err;
                }__fi

                __if (!IsDelimiter(curChar)) {
                        break;
                }__fi

                __(pos4Next(););
        }

        return kPFNoError;
}
/********/
//parsing from right to left: getrightoperand, getoperator, getleftexpr; then group 2 of 3 considering precedence of operators
EPFErrors_t
TPolishForm::getExpr(
                        TOperand &m_Operand,//prev
                        TOperator &m_Operator//prev
                )
{
        //uncomputed params return empty on failure
#define THROW_HOOK \
        __(m_Operand.Clear()); \
        __(m_Operator.Clear());

#define OK_HOOK \
        __(m_Operator.Clear());

        EPFErrors_t err;

        TOperator prevOperator;
        TOperand prevOperand;

        do {
                _h( prevOperand = m_Operand );
                _h( prevOperator = m_Operator );

                _hif ( err=getBoth(m_Operand, m_Operator) ) {
                        _hret(err);
                }_fih

                _hif (m_Operator.IsDefined()) {
                        _hif (((prevOperator.IsDefined())&&(CmpPrecedence(prevOperator/*left*/, m_Operator/*right*/) < 0))
                                || (m_Operand.IsNotDefined())) {//ie. a++b
                                        ++fLevel;
                                        _hif ( err = getExpr(m_Operand,m_Operator) ) {
                                                _hret(err);
                                        }_fih
                                        --fLevel;
                                        //_tIF(m_Operator.IsDefined());
                        }_fih
                }_fih

                _hif (prevOperator.IsDefined()) {
                        //_tIF(prevOperand.IsNotDefined());//_+ eliptic left operand
                        _h( m_Operand = makeOperand(prevOperand, prevOperator, m_Operand) );
                        _hif ((m_Operator.IsNotDefined())||((CmpPrecedence(prevOperator/*left*/, m_Operator/*right*/) > 0)&&(fLevel > _PF_LEVEL0))) {
                                _ret(kPFNoError);//back a level
                        }_fih
                }_fih

                _hif (m_Operator.IsNotDefined()) {
                        _ret(kPFNoError);//back a level
                }_fih
        } while (true);
#undef THROW_HOOK
#undef OK_HOOK
}

/********/
EPFErrors_t
TPolishForm::getBoth(
                        TOperand &m_Operand,//prev
                        TOperator &m_Operator//prev
                )
{
#define THROW_HOOK \
        __(m_Operand.Clear()); \
        __(m_Operator.Clear());

#define OK_HOOK \
        __(m_Operator.Clear());

        EPFErrors_t err;
//1) skip delimiters
        _hif ( err=eatDelimiter() ) {
                if (kReachedEOS == err) {
                        //_if (prevOperand.IsNotDefined()) {//first time?
                                _hret(kExpectedOperandNotEOS);//only if first time
                        //}_fi
                } else {
                        _hret(err);
                }
        }_fih

//2) get operand

        _hif ( (err=getComposedOperand(m_Operand)) ) {
                _hret(err);
        }_fih
        //_tIF(m_Operand.IsNotDefined());//impossible bug

//3) skip more delimiters, or return operand only, if EOS
        _hif ( err=eatDelimiter() ) {
                if (kReachedEOS == err) {
                        _hokret(kPFNoError);//return only right operand
                }
                _hret(err);
        }_fih

//4) check for unnecessary group of braces ie. ((a+b))
        std::string curChar;
        _hif ( err=getCurChar(curChar) ) {
                _hret(err);//EOS handled above
        }_fih

        _hif (curChar == _AF_STR_CLOSEBRACE) {//unnecessary group of braces ie. ((a+b))+c OR (a)+(b)
                //but we ignore them
                _hif ( (err=setMoreBraces()) ) {
                       _hret(err);
                }_fih
                _h( pos4Next(); );
                //counting on caller to eat delims after this
                _hokret(kPFNoError);//return only operand, Operator is set to null and perhaps there's more to parse of fStr
        }_fih

//5) get operator


        _hif ( (err=getOperator(m_Operator)) ) {
                _hret(err);
        }_fih

        _htIF(m_Operator.IsNotDefined());//impossible bug

//6) skip more delimiters
        _hif ( err=eatDelimiter() ) {
                if (kReachedEOS == err) {
                        _hret(kExpectedRightOperandNotEOS);
                }
                //ie. a+
                _hret(err);
        }_fih
//if we're here, there are more to go ie. a+ b*c  [after 'a+ '] handled outside this funx
        _ret(kPFNoError);
#undef THROW_HOOK
#undef OK_HOOK
}
/********/
EPFErrors_t
TPolishForm::getOperator(
                TOperator &m_Into
                )
{
#define THROW_HOOK \
        __(m_Into.Clear());//guaranteeing a NULL param return on failure


        std::string curChar;
        EPFErrors_t err;

        _hif ( err=getCurChar(curChar) ) {
                if (err==kUnexpectedEOS) {
                        _hret(kExpectedOperatorNotEOS);
                }
                _hret(err);
        }_fih


        _htIF(curChar == _AF_STR_CLOSEBRACE);//this cannot happen if called from getBoth()
        /*if (curChar == _AF_STR_CLOSEBRACE) {//handled in getExpr()
                BUG(prolly a bug because this should not happen);
                _hret kExpectedOperatorNotOpenBrace;
        }*/

        if (curChar == _AF_STR_OPENBRACE) {//assuming multiplication '*' automul1
                if (fExprFlags & kNextOpenBraceImpliesMul) {
                        _h(m_Into.SetId(_OPERATOR_MUL););//default
                        //and we don't advance!
                } else {
                        _hret(kExpectedOperatorNotClosedBrace);
                }
        } else {//not any brace
                _hif ( IsOperator(curChar) ) {//we got what we wanted here!
                        m_Into.SetId(curChar);
                        _h( pos4Next(); );
                } _fihelse {//automul2
                        _hif (!IsOperandChar(curChar)) {//expecting operand
                                _hret(kUnallowedCharForOperand);//but got invalid char for it to be part of a valid operand
                        }_fih
                        _hif (fExprFlags & kSideOperandsImplyMul) {
                                _h(m_Into.SetId(_OPERATOR_MUL););//assuming multiplication if ie. a+bc <=> a+b*c OR abc def <=> abc*def
                        } _fihelse {
                                _hret(kExpectedOperatorNotOperand);
                        }_fih
                }_fih
        }//else

        _ret(kPFNoError);
#undef THROW_HOOK
}
/***************************/
std::string
TPolishForm::getUniqueStr()
{

        //getting current
        std::string tmp(fUniq);
        //setting the next val for the future
        bool carry=false;
        for (int i=_MAX_FUNIQ-1;i>=0;i--) {
                if ((carry)||(i==_MAX_FUNIQ-1)){//first char? or carry ? then increment it
                        __if (fUniq[i] >= _ENDING_CHAR) {
                                fUniq[i] = _LEADING_CHAR;
                                carry=true;
                                __tIF(i==0);//overflow
                        } __fielse {
                                (fUniq[i])++;
                                if (carry)
                                        carry=false;
                        }__fi
                }
        }//for
        while ((tmp.length()>1)&&(tmp.at(0)==_LEADING_CHAR)) {
                __( tmp.erase(0,1); );
        };

        //printf("!%s!",tmp.c_str());
        //cout << "!"<<tmp<<"!\n";
        return tmp;
}
/***************************/
TOperand
TPolishForm::makeOperand(
                                const TOperand &a_Left,
                                const TOperator &a_Operator,
                                const TOperand &a_Right
                )
//allocates space on heap on each call
{

        __tIF(a_Operator.IsNotDefined());
        __tIF( a_Right.IsNotDefined() );//mandatory a_Right exist

        TOperand compOp;
        __( compOp.Set(kComposedOperand, getUniqueStr()); );

        __( cout << compOp.GetId() << " = " << a_Left.GetId() << " " << a_Operator.GetId() << " " << a_Right.GetId() << endl; );
        __(fLink->NewLink(kIDComposedOperand, compOp.GetId(), fTxn));

        __if (a_Left.IsDefined()) {//a_Left exists
                __if (a_Left.GetType() == kSimpleOperand) {
                        __(fLink->NewLink(kIDLeftOperand,a_Left.GetId(),fTxn) );
                }__fi
                __(fLink->NewLink(compOp.GetId(),a_Left.GetId(),fTxn) );
        }__fi

        __if (a_Right.GetType() == kSimpleOperand) {
                __(fLink->NewLink(kIDRightOperand, a_Right.GetId(),fTxn));
        }__fi

        __(fLink->NewLink(compOp.GetId(),a_Operator.GetId(),fTxn));

        __(fLink->NewLink(compOp.GetId(), a_Right.GetId(),fTxn));


        __(fLink->NewLink(kIDOperator,a_Operator.GetId(),fTxn));
        //there is a good reason why __tIFnok() isn't used; because Nodes may already exist and NewLink would fail!

        return compOp;
}

