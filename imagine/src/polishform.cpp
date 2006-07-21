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


#include "pnotetrk.h"
#include "classhit.h"
#include "polishform.h"

using namespace std;

#define MAX_STR_LEN 65530 //for strnlen()


const std::string kIDExpressions="Expression";
const std::string kIDComposedOperand="ComposedOperand";
const std::string kIDLeftOperand="LeftOperand";
const std::string kIDRightOperand="RightOperand";
const std::string kIDOperator="OPerator";

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
#define THROW_HOOK ;
        _tIF(a_Str.empty());
        _tIF(a_Str.length() >1 );//only considering one char anyways, but passed more than one, so could be a bug earlier which we signal here
        bool eval;
        _(eval=IsOperator(a_Str.at(0)));
        return eval;
#undef THROW_HOOK
}

bool
IsOperator(const char a_Char)
{
#define THROW_HOOK ;
        _tIF('\0'==a_Char);
        return (NULL != index(kIDOperators,int(a_Char)));
#undef THROW_HOOK
}


bool
IsAllowedOperatorForLackingLeftOperand(const char a_Char)
{
#define THROW_HOOK ;
        _tIF('\0'==a_Char);
        return (NULL != index(kIDLackingLeft_Operators,int(a_Char)));
#undef THROW_HOOK
}
bool
IsAllowedOperatorForLackingLeftOperand(const std::string a_Str)
{
#define THROW_HOOK
        _tIF(a_Str.empty());
        _tIF(a_Str.length() >1 );//only considering one char anyways, but passed more than one, so could be a bug earlier which we signal here
        bool eval;
        _(eval=IsAllowedOperatorForLackingLeftOperand(a_Str.at(0)));
        return eval;
#undef THROW_HOOK
}

/*****************************************************************/
int
GetLen(const char * const a_Str)
{
#define THROW_HOOK ;
        _tIF(NULL==a_Str);
        int len=strnlen(a_Str, MAX_STR_LEN);
        return len;
#undef THROW_HOOK
}
/*****************************************************************/

/*****************************************************************/
int
CmpPrecedence(
                const TOperator &a_First
                ,const TOperator &a_Second)
{//ugly hack lazyness-based
#define THROW_HOOK ;
        //cout << a_First.GetId() << " "<< a_Second.GetId();

        _tIF(a_First.IsNotDefined());
        _tIF(a_Second.IsNotDefined());
        _tIF(a_First.GetId().length()>1);
        _tIF(a_Second.GetId().length()>1);
        //only 1 char long operators are considered
        char *x=index(kIDOperators,int(a_First.GetId().at(0)));
        char *y=index(kIDOperators,int(a_Second.GetId().at(0)));
        _tIF(NULL==x);
        _tIF(NULL==y);
        int a=(x-kIDOperators);
        int b=(y-kIDOperators);
        _tIF(a<0);
        _tIF(b<0);
        int len;
        _(len=GetLen(kIDOperatorsPrecedence));
        _tIF(a>=len);
        _tIF(b>=len);
        a=kIDOperatorsPrecedence[a] -0x30;
        b=kIDOperatorsPrecedence[b] -0x30;
        //cout << " => "<<a<<" "<<b<<endl;
        if (a>b)
                return +1;
        if (a==b)
                return 0;
        else
                return -1;
#undef THROW_HOOK
}

/*****************************************************************/

/*****************************************************************/
/*****************************************************************/
/*****************************************************************/
void
PFShowError(const EPFErrors_t a_Err)
{
#define THROW_HOOK ;
        _tIF( a_Err < 0);
        _tIF( a_Err >= kMaxPFErrors);
        cout << PFErrorStrings[a_Err]<<endl;
#undef THROW_HOOK
}
/*****************************************************************/
/*****************************************************************/
//constructor1
TPolishForm::TPolishForm():
        fLink(NULL)
        ,fTxn(NULL)
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
        for (int i=0; i<_MAX_FUNIQ; i++)
                fUniq[i]=_LEADING_CHAR;
}
void
TPolishForm::Init()
{
#define THROW_HOOK ;
        _tIF(fLink); //calling twice ? throw exception if so
        std::string envHomePath("./dbhome/");
        _(fLink=new TLink(envHomePath));
        _tIF(NULL==fLink);
#undef THROW_HOOK
}

/*****************************************************************/
/*****************************************************************/
//destructor
TPolishForm::~TPolishForm()
{
#define THROW_HOOK \
        BDBCLOSE_HOOK

        _tIF(fTxn);//a still open transaction?! this is a bad bug:P

        BDBCLOSE_HOOK;
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

        _if ( kReachedEOS != (err=eatDelimiter() )) {
                _tIF(err != kPFNoError);//just in case eatDelimiter returns more than one different error (currently kAlreadyAtEOS) need to be handled here, since it's assumed that if not kAlreadyAtEOS then there are no errors

                std::string curChar;
                _if ( err=getCurChar(curChar) ) {
                        _hret err;//EOS handled above
                }_fi

                _if (IsOperator(curChar)) {//is operator and curChar is set to it!
                        //going LEFT

                        //pos for next
                        _( pos4Next(); );

                        _if ( err=polishToGraph() ) {
                                _hret kExpectedLeftOperandNotEOS;
                        }_fi
                        TOperand left=rRoot;

                        //going RIGHT

                        //pos for next is autopositioned

                        _if ( err=polishToGraph() ) {
                                _hret kExpectedRightOperandNotEOS;
                        }_fi

                        TOperator sign;
                        _(sign.SetId(curChar););
                        _( rRoot=makeOperand(left,sign,rRoot);
                         );
                        return kPFNoError;
                } else {//not operator
                        //because we're called recursive, we don't know if this is the first call, if it were then this should be an error if we're not at end of string
                        _if ( err=getSimpleOperand()) {
                                _hret err;
                        }_fi
                        return kPFNoError;//so far so good
                }_fi
        } else {
                _hret kUnexpectedEOS;
        }_fi
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

        _(rRoot.Clear());

        EPFErrors_t err;

        rOpenBraces=0;//a must! before the call to getExpr()

{//block
        _tIF(a_Str.empty());//FIXME: use _reterr or smth

        fStr=a_Str;
        int length;
        _(length=fStr.length());//default
        _if ((a_HigherBound < 0)||(a_HigherBound >= length)) {
                fHigherBound=length-1;
        } else {
                fHigherBound=a_HigherBound;
        }_fi

        fLowerBound=a_LowerBound;

        if (fLowerBound < 0){
                fLowerBound=0;
        } else if (fLowerBound > fHigherBound) {
                fLowerBound=fHigherBound;//1 char heh
                }

}//endblock

        fExprFlags = a_ExprFlags;


        _(fLink->NewTransaction(NULL /*parent*/,&fTxn););

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
        _(initIndex());
        _( rOperand.Clear() );
        _( rOperator.Clear() );
        fLevel=_PF_LEVEL0;
        _( err = getExpr(rOperand,rOperator) );
        _tIF(rOperator.IsDefined());
        //_tIF(1 != fLevel);

        if (err) {
                _reterr err;
        } else {
                _tIF(rOpenBraces<0 );//impossible

                if (rOpenBraces != 0) {//some braces not closed
                        //"a+(b-c-d+(((e+f" is considered valid until here
                        _reterr kLeftUnclosedBraces;
                }

                _( rRoot = rOperand );
                _tIF(rRoot.IsNotDefined());
                ETLinkErrors_t tlerr;
                _if ((tlerr=fLink->NewLink(kIDExpressions,rRoot.GetId(),fTxn))) {
                        if (tlerr!=kTLAlreadyExists) {
                                _(TLShowError(tlerr));
                                _reterr kFailedCreatingRootExpressionInTree;
                        }
                        //ignoring if already exists
                }_fi
        }//fi err

} else { if (a_Form == kPolishForm) {
                fSense=kForward;_(initIndex());
                {//block
                        std::string c;
                        _if ( err=getCharAt(fLowerBound,c) ) {
                                _hret err;
                        }_fi
                _if ( ! IsOperator(c) ) {
                //first char must be an operator, otherwise invalid polish form
                        _reterr kExpectedOperatorNotOperand;//there are no parantheses in polish form (only operands and operators)
                }_fi
                }//block


        _( err= polishToGraph();
         );
        if (err != kPFNoError) {
                _reterr err;
        } else {//noerror:
                _if (rIndex <= fHigherBound) {//we didn't parse it all
                        _reterr kIncompleteParse;
                }_fi

                _(fLink->NewLink(kIDExpressions,rRoot.GetId(),fTxn);
                 );
        }

} else {
        _t("undefined form");
}//fi
}

#undef ERR_HOOK
#undef THROW_HOOK
#define THROW_HOOK BDBCLOSE_HOOK

        _(fLink->Commit(&fTxn););


//all ok:
        return kPFNoError;//no errors
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
#define THROW_HOOK
        //_tIF(rIndex < fLowerBound);//bug
        //_tIF(rIndex > fHigherBound);//bug
        if (a_Ofs<0)
                a_Ofs=rIndex;
        if ((a_Ofs < fLowerBound)||(a_Ofs > fHigherBound)) {
                return true;//however index is not addressable!
        }
        return false;
#undef THROW_HOOK
}
/********/
bool
IsOperandChar(const std::string a_Str)
{
#define THROW_HOOK
        _tIF(a_Str.empty());
        _tIF(a_Str.length() >1);
        return (NULL != index(kAllowableOperandChars, int(a_Str.at(0))));
#undef THROW_HOOK
}
/********/
bool
IsOperandChar(const char a_Char)
{
#define THROW_HOOK
        _tIF('\0'==a_Char);
        return (NULL != index(kAllowableOperandChars, int(a_Char)));
#undef THROW_HOOK
}
/********/
bool
IsDelimiter(const std::string a_Str)
{
#define THROW_HOOK
        _tIF(a_Str.empty());
        _tIF(a_Str.length() >1);
        return (NULL != index(kDelimiters, int(a_Str.at(0))));
#undef THROW_HOOK
}
/********/
EPFErrors_t
TPolishForm::getComposedOperand(
                TOperand &m_Into
                )
{//autoadvances such that on exit a_Index points to next char to be processed (that is if nothing failed)
#define THROW_HOOK \
        __(m_Into.Clear());//guaranteeing a NULL param return on failure

        _tIF(fStr.empty());//bug

        EPFErrors_t err;

        std::string curChar;
        _if ( err=getCurChar(curChar) ) {
                _tIF( kUnexpectedEOS == err );//this cannot happen here, handled above in getExpr() function
             /*   if (err == kUnexpectedEOS) {
                         _hret kExpectedOperandNotEOS;//this cannot happen here, handled above in getExpr() function
                }*/
                _hret err;//if any other error happened
        }_fi

        _if( IsOperator(curChar) ) {//FIXME a++B is allowed since we don't know when we're first time a+  or second time +B
                _if (IsAllowedOperatorForLackingLeftOperand(curChar)) {
                        //_(m_Into.SetId("0");)
                        //return kPFNoError;
                        _hret kPFNoError;//allowing missing operand before operator, and not moving to next (we remain stationary on the operator(sign))
                }_fi
                _hret kExpectedOperandNotOperator;
        }_fi

        _if (curChar == _AF_STR_CLOSEBRACE) {
                _hret kExpectedOperandNotClosedBrace;
        }_fi

        _if (curChar == _AF_STR_OPENBRACE) {//a begining of an leftExpression that is to be considered as an operand, considering fSense if kBackward
                _if ( (err=setLessBraces()) ) {
                        _hret err;
                }_fi

                _if (err=pos4Next()) {
                        _hret kExpectedOperandNotEOS;
                }_fi

                TOperand l;
                TOperator p;
                        int prevLevel=fLevel;
                        fLevel=_PF_LEVEL0;
                        _if ( err = getExpr(l,p) ) {
                                _hret err;//some nasty error
                        }_fi
                        fLevel=prevLevel;

                        _tIF(l.IsNotDefined());//impossible

                _(m_Into=l );
        } else {
                //if not operator AND not ')' AND not '(' then assumed simple operand
                _if ( err= getSimpleOperand()) {
                        _hret err;
                }_fi
                m_Into=rRoot;
        }_fi

        //cout<<"Operand:"<<m_Into.GetId()<<endl;
        return kPFNoError;
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
                _if ( err=getCurChar(curChar) ) {
                        if (kUnexpectedEOS == err) {
                                _hret kExpectedSimpleOperand;
                        }
                        _hret err;
                }_fi

        _if (!IsOperandChar(curChar)) {
                _hret kUnallowedCharForOperand;
        }_fi

        _if (fExprFlags & kLongOperands) {

                //taking max operand length (ie. until EOS)
                std::string tmp;
                do {
                        _(tmp.append(curChar););

                        _( pos4Next(); );

                        _if ( err=getCurChar(curChar) ) {
                                if (err==kUnexpectedEOS) {
                                        break;
                                }
                                _hret err;
                        }_fi
                        //until:
                        _if (!IsOperandChar(curChar)) {
                                break;
                        }_fi

                        _if ( (! (fExprFlags & kAllowOperandsLongerThanMax)) && (tmp.length() >= MAX_OPERAND_LEN)) {
                                        _hret kOperandLongerThanMax;
                        }_fi

                        //all constraints ok, then we get this next and add it later at top of 'do'
                } while (true);

                rRoot.Set(kSimpleOperand, tmp);
                //already at next or EOS
        } else {//one char operands
                rRoot.Set(kSimpleOperand, curChar);
                _( pos4Next(); );
        }_fi

        return kPFNoError;
#undef THROW_HOOK
}
/********/
EPFErrors_t
TPolishForm::eatDelimiter(
                )
{
#define THROW_HOOK
        while (true) {
                std::string curChar;
                EPFErrors_t err;
                _if ( err=getCurChar(curChar) ) {
                        if (err==kUnexpectedEOS) {
                                return kReachedEOS;
                        }
                        return err;
                }_fi

                _if (!IsDelimiter(curChar)) {
                        break;
                }_fi

                _(pos4Next(););
        }

        return kPFNoError;
#undef THROW_HOOK
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
                _( prevOperand = m_Operand );
                _( prevOperator = m_Operator );

                _if ( err=getBoth(m_Operand, m_Operator) ) {
                        _hret err;
                }_fi

                _if (m_Operator.IsDefined()) {
                        _if (((prevOperator.IsDefined())&&(CmpPrecedence(prevOperator/*left*/, m_Operator/*right*/) < 0))
                                || (m_Operand.IsNotDefined())) {//ie. a++b
                                        ++fLevel;
                                        _if ( err = getExpr(m_Operand,m_Operator) ) {
                                                _hret err;
                                        }_fi
                                        --fLevel;
                                        //_tIF(m_Operator.IsDefined());
                        }_fi
                }_fi

                _if (prevOperator.IsDefined()) {
                        //_tIF(prevOperand.IsNotDefined());//_+ eliptic left operand
                        _( m_Operand = makeOperand(prevOperand, prevOperator, m_Operand) );
                        _if ((m_Operator.IsNotDefined())||((CmpPrecedence(prevOperator/*left*/, m_Operator/*right*/) > 0)&&(fLevel > _PF_LEVEL0))) {
                                return kPFNoError;//back a level
                        }_fi
                }_fi

                _if (m_Operator.IsNotDefined()) {
                        return kPFNoError;//back a level
                }_fi
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
        _if ( err=eatDelimiter() ) {
                if (kReachedEOS == err) {
                        //_if (prevOperand.IsNotDefined()) {//first time?
                                _hret kExpectedOperandNotEOS;//only if first time
                        //}_fi
                } else {
                        _hret err;
                }
        }_fi

//2) get operand

        _if ( (err=getComposedOperand(m_Operand)) ) {
                _hret err;
        }_fi
        //_tIF(m_Operand.IsNotDefined());//impossible bug

//3) skip more delimiters, or return operand only, if EOS
        _if ( err=eatDelimiter() ) {
                if (kReachedEOS == err) {
                        _okret kPFNoError;//return only right operand
                }
                _hret err;
        }_fi

//4) check for unnecessary group of braces ie. ((a+b))
        std::string curChar;
        _if ( err=getCurChar(curChar) ) {
                _hret err;//EOS handled above
        }_fi

        _if (curChar == _AF_STR_CLOSEBRACE) {//unnecessary group of braces ie. ((a+b))+c OR (a)+(b)
                //but we ignore them
                _if ( (err=setMoreBraces()) ) {
                       _hret err;
                }_fi
                _( pos4Next(); );
                //counting on caller to eat delims after this
                _okret kPFNoError;//return only operand, Operator is set to null and perhaps there's more to parse of fStr
        }_fi

//5) get operator


        _if ( (err=getOperator(m_Operator)) ) {
                _hret err;
        }_fi

        _tIF(m_Operator.IsNotDefined());//impossible bug

//6) skip more delimiters
        _if ( err=eatDelimiter() ) {
                if (kReachedEOS == err) {
                        _hret kExpectedRightOperandNotEOS;
                }
                //ie. a+
                _hret err;
        }_fi
//if we're here, there are more to go ie. a+ b*c  [after 'a+ '] handled outside this funx
        return kPFNoError;
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

        _if ( err=getCurChar(curChar) ) {
                if (err==kUnexpectedEOS) {
                        _hret kExpectedOperatorNotEOS;
                }
                _hret err;
        }_fi


        _tIF(curChar == _AF_STR_CLOSEBRACE);//this cannot happen if called from getBoth()
        /*if (curChar == _AF_STR_CLOSEBRACE) {//handled in getExpr()
                BUG(prolly a bug because this should not happen);
                _hret kExpectedOperatorNotOpenBrace;
        }*/

        if (curChar == _AF_STR_OPENBRACE) {//assuming multiplication '*' automul1
                if (fExprFlags & kNextOpenBraceImpliesMul) {
                        _(m_Into.SetId(_OPERATOR_MUL););//default
                        //and we don't advance!
                } else {
                        _hret kExpectedOperatorNotClosedBrace;
                }
        } else {//not any brace
                _if ( IsOperator(curChar) ) {//we got what we wanted here!
                        m_Into.SetId(curChar);
                        _( pos4Next(); );
                } else {//automul2
                        _if (!IsOperandChar(curChar)) {//expecting operand
                                _hret kUnallowedCharForOperand;//but got invalid char for it to be part of a valid operand
                        }_fi
                        _if (fExprFlags & kSideOperandsImplyMul) {
                                _(m_Into.SetId(_OPERATOR_MUL););//assuming multiplication if ie. a+bc <=> a+b*c OR abc def <=> abc*def
                        } else {
                                _hret kExpectedOperatorNotOperand;
                        }_fi
                }_fi
        }//else

        return kPFNoError;
#undef THROW_HOOK
}
/***************************/
std::string
TPolishForm::getUniqueStr()
{
#define THROW_HOOK ;

        //getting current
        std::string tmp(fUniq);
        //setting the next val for the future
        bool carry=false;
        for (int i=_MAX_FUNIQ-1;i>=0;i--) {
                if ((carry)||(i==_MAX_FUNIQ-1)){//first char? or carry ? then increment it
                        _if (fUniq[i]>=_ENDING_CHAR) {
                                fUniq[i]=_LEADING_CHAR;
                                carry=true;
                                _tIF(i==0);//overflow
                        } else {
                                (fUniq[i])++;
                                if (carry)
                                        carry=false;
                        }_fi
                }
        }//for
        while ((tmp.length()>1)&&(tmp.at(0)==_LEADING_CHAR)) {
                _( tmp.erase(0,1); );
        };

        return tmp;
#undef THROW_HOOK
}
/***************************
EPFErrors_t
TPolishForm::ShowExpr(
        const std::string a_Root
        ,int a_TotalHorizLevel
        ,int a_VertLevel
        )
{
#define THROW_HOOK
        ETLinkErrors_t err;
        _if (kTLNotFound==(err=fLink->IsLink(kIDExpressions, a_Root))) {
                return kRootNotFound;
        }_fi
        _tIF(err!=kTLNoError);//other error returned by IsLink (this is not expected!)
        //FIXME
        std::string i1("iter1");
        _if (kTLNoError== (err=fLink->IsGroup(kGroup,i1))) {//must not be a kGroup, can be a kSubGroup
                //FIXME: remove all elements if exists or smth
                return kTemporaryIteratorExists;//impossible, perhaps only on unclean shutdown when interrupted after InitIterator and before DeInit
        }_fi //put this inside MakeIterator
        _tIF(kTLNotFound != err);
//
        TLinkIterator iter1;
        _if ( err=iter1.InitIterator(i1, a_Root, kGroup, DB_NEXT) ) {
                _(TLShowError(err););
                return kFailedCreatingIterator;
        }_fi

        *int cnt;
        _( cnt=iter1.Count() );
        if ((cnt > 3)||(cnt<2)) {
                return kBrokenRoot; //must have 2 or 3 elements
        }*

        std::string current, leftOperand, rightOperand, sign;
        err=kTLNoError; horizLevel=1;
while (err==kTLNoError) {
        _( iter1.GetCurrent(current) );
_if (IsConnection(kIDComposedOperand, current)) {
        ShowExpr(current, a_TotalHorizLevel+horizLevel, next(a_VertLevel));//going deeper
} else {
        _if (IsConnection(kIDLeftOperand, current)) {
                        _tIF(!leftOperand.empty());//got here twice?
                        leftOperand=current;
        } else { _if (IsConnection(kIDRightOperand, current)) {
                        _tIF(!rightOperand.empty());//got here twice?
                        rightOperand=current;
                } else { _if (IsConnection(kIDOperator, current)) {
                                _tIF(!sign.empty());//got here twice?
                                sign=current;
                        }_fi
                }_fi
        }_fi
}_fi
        _( err=iter1.Next() );
        horizLevel++;
}//while
        _( iter1.DeInit() );
        horizLevel--;
        if ((horizLevel > 3)||(horizLevel < 2)) {
                return kBrokenRoot; //must have 2 or 3 elements, not more not less
        }

        //could be 2 operands and no sign(operator)
        _if (sign.empty()) {
                return kOperatorNotFound;//and since we have at least 2 things they must be both left+right operands that we got so far
        }_fi

        //so we have the a_VertLevel, the horizLevel and
postponed! until after we make input handling in demlinks with what we have//
        return kPFNoError;
#undef THROW_HOOK
}
***************************/
TOperand
TPolishForm::makeOperand(
                                const TOperand &a_Left,
                                const TOperator &a_Operator,
                                const TOperand &a_Right
                )
//allocates space on heap on each call
{
#define THROW_HOOK ;

        _tIF(a_Operator.IsNotDefined());
        _tIF( a_Right.IsNotDefined() );//mandatory a_Right exist

        TOperand compOp;
        _( compOp.Set(kComposedOperand, getUniqueStr()); );

//#ifdef SHOWKEYVAL
        _( cout << compOp.GetId() << " = " << a_Left.GetId() << " " << a_Operator.GetId() << " " << a_Right.GetId() << endl; );
//#endif
        _(fLink->NewLink(kIDComposedOperand, compOp.GetId(), fTxn););

        _if (a_Left.IsDefined()) {//a_Left exists
                _if (a_Left.GetType() == kSimpleOperand) {
                        _(fLink->NewLink(kIDLeftOperand,a_Left.GetId(),fTxn); );
                }_fi
                _(fLink->NewLink(compOp.GetId(),a_Left.GetId(),fTxn); );
        }_fi

        _if (a_Right.GetType() == kSimpleOperand) {
                _(fLink->NewLink(kIDRightOperand, a_Right.GetId(),fTxn););
        }_fi

        _(fLink->NewLink(compOp.GetId(),a_Operator.GetId(),fTxn););

        _(fLink->NewLink(compOp.GetId(), a_Right.GetId(),fTxn););


        _(fLink->NewLink(kIDOperator,a_Operator.GetId(),fTxn););

        return compOp;
#undef THROW_HOOK
}

