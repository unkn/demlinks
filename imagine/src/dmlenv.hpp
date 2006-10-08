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
* Description: this is an include for the dmlenvl?.cpp files only!
*
****************************************************************************/

#ifdef DMLENV_HPP_
        #error included twice
#else
        #define DMLENV_HPP_

using namespace std;

/*************debug vars*/
//show debug statistics such as key+value
//#define SHOWKEYVAL
//#define SHOWCONTENTS //if disabled, the consistency check is still performed, just no records are displayed on console
//#define SHOWTXNS
/*************/

/****************************/
//some notions:
//a (consistent) link is formed of two: forward link AND reverse link
//a forward link(FL) is a kGroup -> kSubGroup ie. A->B in primary
//a reverse link(RL) is a kSubGroup <- kGroup ie. B<-A in secondary
//a reverse link is a complementary to a forward link (and a forward link is the complementary of a reverse link)
//PRI   |       SEC
//--------------------
//A->B  |       B<-A
//G->sG |      sG<-G
//FL    |       RL
//a complementary link(CL) is the opposite of a FL/RL ie. complementary link of a forward link is the reverse link; complementary link of a reverse link is a forward link
//a semi link is half of a link ie. either A->B , OR, B<-A .  HALF!
//a self link(SL) is a FL&RL that points to self ie. A->A | A<-A  => usually regarded as a NULL pointer
//a pointer is that which points to something; is the part on the left ie. A from A->B ; OR B from B<-A
//a pointee is that which is being pointed by a pointer; the part on the right ie. B from A->B; OR A from B<-A
//<- or -> doesn't indicate which is the pointer(usually) it means that the part on the '-' side ie. A from A->B is the kGroup and the part on the '>' or '<' side is the kSubGroup ie. B of B<-A is the kSubGroup and this link denotes a reverse link representation
//in our system if a RL exists then a FL counterpart must also exist (unless a bug is present) also if a FL exists the RL complement must be present also; this is what we call a (consistent) link within our lowest level system of demlinks; if you go lower than that you're here in C++ code
//a node can be any of kGroup(pointer) or kSubGroup(pointee)

/****************************/
/*****************************************************************/
/*****************************************************************/
/********************************************************/
#define _makeFLAG(whatFlag ) \
        __tIF(whatFlag == (tmpSafeFlags & whatFlag) );/*already happened! this could mean there are two flags ANDing gt 0, or even worse, equal */ \
        bool fl_##whatFlag=whatFlag == (tmpFlags & whatFlag); \
        if (fl_##whatFlag) { \
                if (gTrackFlags) { cout << "detected flag "#whatFlag<< endl; }\
                tmpFlags -= whatFlag; /*substract this flag*/\
                tmpSafeFlags += whatFlag; /*substract this flag*/\
        } /* tmpFlags is a variable that supposedly is already defined */

#define _makeUniqueFLAG(whatFlag ) \
        bool fl_##whatFlag=whatFlag == (tmpFlags & whatFlag); \
        if (fl_##whatFlag) { \
                tmpFlags -= whatFlag; /*substract this flag*/\
                if (0 != tmpFlags) { \
                        _ht(this flag is present but not uniquely present: ie. there are more others); \
                }\
        } /* tmpFlags is a variable that supposedly is already defined */

#define CURSOR_ABORT_HOOK \
        __(fLink->Abort(&fThisTxn)); \
        fThisTxn=NULL;

#define CURSOR_CLOSE_HOOK \
        if (fCursor) { \
                __tIF(0 != fCursor->close()); \
        }


#define ABORT_HOOK \
        __(this->Abort(&thisTxn));

#define ENVCLOSE_HOOK \
        if (fDBEnviron != NULL) { \
            /*throws*/__(fDBEnviron->close(0););\
                fDBEnviron=NULL;       \
        }

#define DB1CLOSE_HOOK \
        if (g_DBGroupToSubGroup != NULL) { \
            /*not*/___(g_DBGroupToSubGroup->close(0)); \
                g_DBGroupToSubGroup=NULL;\
        }

#define DB2CLOSE_HOOK \
        if (g_DBSubGroupFromGroup != NULL) { \
            /*not*/___(g_DBSubGroupFromGroup->close(0)); \
                g_DBSubGroupFromGroup=NULL;\
        }

//berkeley db environ+dbases shutdown on exception, used only where aplicable
#define SAFEBDBCLOSE_HOOK \
        DB1CLOSE_HOOK\
        DB2CLOSE_HOOK\
        ENVCLOSE_HOOK


/*******************************/
/*******************************/
/*******************************/
/*******************************/
/*******************************/
/*******************************/
#endif

