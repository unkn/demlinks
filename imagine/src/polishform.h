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

//15June2006
/*
 * assumptions:
 * an operand is one char long
 * an operator is a sign, one char long
 * braces allowed
 * operator precedence considered
 * an operand can contain a sign ie. +a even like this a+-b OR a++--+-+-b
 */

#ifndef __POLISHFORM_H
#define __POLISHFORM_H
/*****************************************************************/
#include <iostream> //for size_t and some strcat alike operations inside .cpp
#include "dmlenv.h"
#include "pnotetrk.h"

//**********HOOKS

#define BDBCLOSE_HOOK \
        if (fLink) { \
                __(delete fLink;); \
                fLink=NULL;\
        }

#define AB_HOOK { \
        __(fLink->Abort(&fTxn)); \
        fTxn=NULL; \
}

//*************************CONSTANTS****************
#define _PF_LEFT 0
#define _PF_RIGHT (! _PF_LEFT)


#define _AF_STR_OPENBRACE "("
#define _AF_STR_CLOSEBRACE ")"
#define _OPERATOR_MUL "*"

#define _AF_NULL_OPERAND '0'

//the error occured at no position (ie. no error!)
#define _PF_NOPOSITION 0
#define _AF_NOPOSITION _PF_NOPOSITION

#define _ZERO_ 0 //don't change

#define _PF_LEVEL0 1

#define MAX_BRACES_DEPTH 200

#define _MAX_FUNIQ 4 //fUniq[_MAX_FUNIQ]
#define _LEADING_CHAR 'A'
#define _ENDING_CHAR 'Z'

//just a safeguard for strnlen()
#define MAX_OPERAND_LEN 20 //if kLongOperands is "enabled"


//*************************
typedef enum {
        kNoForm=0
        ,kPolishForm
        ,kArithmeticForm
//last:
        ,kMaxForms
} EForms_t;

typedef enum {//WARNING: if u mod/add smth here check the .cpp to mod/add there
        kPFNoError=0//don't change this "=0" because many ifs are if (err) then error_present
        ,kUndefinedError
        ,kTooDeep//open braces go too deep
        ,kNoMatchingOpenBrace//ERR_TOO_MANY_CLOSED_BRACES_NO_MATCHING_OPEN_BRACE
        ,kExpectedOperandNotOperator
        ,kExpectedOperandNotClosedBrace
        ,kExpectedOperandNotEOS//expected operand or '(', but got End of string
        ,kExpectedOperatorNotEOS
        ,kExpectedOperatorNotOpenBrace
        ,kExpectedOperatorNotClosedBrace
        ,kExpectedOperatorNotOperand
        ,kLeftUnclosedBraces
        ,kOperandLongerThanMax //MAX_OPERAND_LEN
        ,kUnallowedCharForOperand //see kAllowableOperandChars  in .cpp
        ,kUnexpectedEOS //when it's not expected
        ,kExpectedRightOperandNotEOS
        ,kExpectedLeftOperandNotEOS
        ,kReachedEOS //eatDelimiter
        ,kFailedCreatingRootExpressionInTree
        ,kRootNotFound
        ,kTemporaryIteratorExists
        ,kIndexAlreadyAtEdge
        ,kIncompleteParse
        ,kExpectedSimpleOperand
//last:
        ,kMaxPFErrors
} EPFErrors_t;
extern const char * PFErrorStrings[kMaxPFErrors];
//*************************
bool
IsOperator(const std::string a_Str);
bool
IsOperator(const char a_Char);//FIXME: multiple char operator?! :D
bool
IsOperandChar(const std::string a_Str);
bool
IsOperandChar(const char a_Char);
bool
IsDelimiter(const std::string a_Str);

/*****************************************************************/
int
GetLen(const char * const a_Str);
/*****************************************************************/
class TOperator {
private:
        std::string fId;//aka name, a way to identify it in a TLink environment
public:
        /*TOperator(const std::string a_Id){
                //constructor
                Set(a_Id);
        };*/

        TOperator(){//constructor
                __(this->Clear());
        };

        ~TOperator(){};//destructor

        TOperator&
        TOperator::operator=(const TOperator & source) {
                if (&source==this)
                        return *this;
                fId=source.fId;
                return *this;
        }

        inline bool
        IsNotDefined() const {
                return (!IsDefined());
        };
        inline bool
        IsDefined() const {
                return (!fId.empty());
        };

        inline void
        Clear() {
                fId.clear();
        };

        inline std::string
        GetId() const {
                return fId;
        };

        inline void
        SetId(const char a_Char){
                fId=a_Char;
        }
        inline void
        SetId(const std::string a_Id){
                __tIF(a_Id.empty());
                __tIF(a_Id.length() > 1);//only allowing 1 char long operators
                fId=a_Id;
        };

};//class TOperator (aka sign)
/*****************************************************************/
typedef enum {
        kUndefinedOperand=0
        ,kSimpleOperand
        ,kComposedOperand//of two other operands which may or may not be simple
//last:
        ,kMaxOperandTypes
} EOperandTypes_t;

class TOperand {
private:
        std::string fId;//aka name, a way to identify it in a TLink environment
        EOperandTypes_t fType;
public:
        void
        Set(const EOperandTypes_t a_Type, const std::string a_Id){
                SetType(a_Type);
                SetId(a_Id);
        };

        TOperand(const EOperandTypes_t a_Type, const std::string a_Id){
                //constructor
                Set(a_Type,a_Id);
        };

        TOperand(){//constructor
                this->Clear();
        };

        ~TOperand(){};//destructor

        TOperand&
        TOperand::operator=(const TOperand & source)
        {
                if (&source==this)
                        return *this;

                fId=source.fId;
                fType=source.fType;

                return *this;
        }

        inline bool
        IsNotDefined() const {
                return (!IsDefined());
        };
        inline bool
        IsDefined() const {
                __tIF(fType==kMaxOperandTypes);//total bug
                if (fType == kUndefinedOperand) {
                        WARN_IF( !fId.empty() );//possible bug somewhere outside
                        return false;//not defined
                }
                return true;//is defined
        };

        inline void
        Clear() {
                fType=kUndefinedOperand;
                fId.clear();
        };

        EOperandTypes_t inline
        GetType() const{
                return fType;
        };

        inline std::string
        GetId() const {
                return fId;
        };

        inline void
        SetId(const std::string a_Id){
                __tIF(a_Id.empty());
                fId=a_Id;
        };

        inline void
        SetType(const EOperandTypes_t a_Type){
                fType=a_Type;
        };
};//TOperand aka term

const int kLongOperands=1;
const int kNextOpenBraceImpliesMul=2;//ie. a(b+c) <=> a*(b+c) but (a+b)c NOT (see below)

const int kSideOperandsImplyMul=4; //ie. abc <=>a*b*c OR (a+b)c <=> (a+b)*c but not a(b+c) <=> a*(b+c) NOT! (see above)
//a) b  <=> a)*b this means operand <then> operand => mul; not closebrace then operand => mul

const int kAllowOperandsLongerThanMax=8;//no MAX_OPERAND_LEN constrain on operand length
/*****************************************************************/
typedef enum {
        kForward=0,
        kBackward
} ESense_t;
/*****************************************************************/
/*****************************************************************/
class TPolishForm {
        private:

                TLink *fLink;
                char fUniq[_MAX_FUNIQ+1];//includes a \0 for later init with std::string newvar(fUniq); fixed
                DbTxn *fTxn;//tmp transaction used when needed

                std::string fStr;
                int fLowerBound;//0 based
                int fHigherBound;//the offset in string; <=0 means .length() modified after call
                int fExprFlags;
                //----------

                ESense_t fSense;

                int fLevel;//used inside getExpr() mainly


                /*TOperand rLeftOperand;
                TOperator rOperator;
                TOperand rRightOperand;*/

///////////////////////////
                std::string
                getUniqueStr();

                //recursive function
                EPFErrors_t
                polishToGraph();

                //recursive function
                EPFErrors_t
                getExpr(
                        TOperand &m_Operand,//prev
                        TOperator &m_Operator//prev
                        );
                EPFErrors_t
                getBoth(
                        TOperand &m_Operand,//prev
                        TOperator &m_Operator//prev
                        );

                TOperand
                makeOperand(
                                const TOperand &a_Left,
                                const TOperator &a_Operator,
                                const TOperand &a_Right
                           );

                EPFErrors_t
                setMoreBraces();

                EPFErrors_t
                setLessBraces();

                //recursive function
                EPFErrors_t
                getComposedOperand(
                        TOperand &m_Into
                                );

                EPFErrors_t
                getSimpleOperand(
                                );

                EPFErrors_t
                getOperator(
                        TOperator &m_Into
                                );

EPFErrors_t
TPolishForm::eatDelimiter(
                );

                EPFErrors_t
                initIndex(){
                //init rIndex
                        if (fSense==kBackward) {
                                rIndex=fHigherBound;
                        } else { if (fSense==kForward) {
                                        rIndex=fLowerBound;
                                } else {
                                        __t("not left, not right then what?");
                                }
                }
                        return kPFNoError;
                }


                EPFErrors_t
                pos4Next(){
                        __if (IsIndexAtEdge()) {
                                _fret(kIndexAlreadyAtEdge);
                        }__fi

                        if (fSense==kBackward) {
                                rIndex--;
                        } else { if (fSense==kForward) {
                                        rIndex++;
                                } else {
                                        __t("not left, not right then what?");
                                }
                        }

                        __if (IsIndexAtEdge()) {
                                _fret(kReachedEOS);
                        }__fi

                        return kPFNoError;
                };

                EPFErrors_t
                getCurChar(std::string &m_Into) {
#define THROW_HOOK \
                m_Into.clear();

                        EPFErrors_t err;
                        _hif ( err=getCharAt(rIndex,m_Into) ) {
                                _hret(err);
                        }_fih

                        _ret(kPFNoError);
#undef THROW_HOOK
                };

                EPFErrors_t
                getCharAt(int a_Offset, std::string &m_Into) {
#define THROW_HOOK \
                m_Into.clear();

                        _hif (IsIndexAtEdge(a_Offset)) {
                                _hret(kUnexpectedEOS);
                        }_fih

                        _h( m_Into=fStr.at(a_Offset));

                        _ret(kPFNoError);
#undef THROW_HOOK
                };

                bool
                IsIndexAtEdge(int a_Ofs=-1);

        TPolishForm();//constructor;purposely made private so it cannot be used
public:
        int rOpenBraces;//returned if err==kLeftUnclosedBraces
        TOperand rRoot;//Id of head of tree representing expression
        int rIndex;
//the structors
                TPolishForm(TLink *m_WorkingOnThisTLink);
                ~TPolishForm();
//those that DO
void
TPolishForm::Init();


/*
                EPFErrors_t
                ShowExpr(
                        const std::string a_Root
                        ,int a_TotalHorizLevel
                        ,int a_VertLevel
                         );
*/
                EPFErrors_t
                MakeGraph(
                        const std::string a_Str,
                        const EForms_t a_Form,
                        const int a_ExprFlags,
                        const int a_LowerBound,
                        const int a_HigherBound
                                );



                function
                ShowContents(){
                        __fIFnok( fLink->ShowContents());
                        _OK;
                };
};//class TPolishForm

/*****************************************************************/
void
PFShowError(const EPFErrors_t a_Err);
/*****************************************************************/
int
CmpPrecedence(
                const TOperator &a_First
                ,const TOperator &a_Second);

/*****************************************************************/

#endif
