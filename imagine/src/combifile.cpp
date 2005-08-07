/****************************************************************************
*
*                             dmental links
*    Copyright (C) 2005 AtKaaZ, AtKaaZ at users.sourceforge.net
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
*
****************************************************************************/

#include <stdio.h>
#include <errno.h>

#include "_gcdefs.h"

/*****************************************************************************/

#include "combifile.h"
//#include "pnotetrk.h"
#include "genericinput.h"
//#include "input.h"


/*****************************************************************************/
EFunctionReturnTypes_t
TFacileFile::adv_read(void *dest,int a_howmany)
{
        ERR_IF(a_howmany != read(fFileHandle, dest, a_howmany),
                        return kFuncFailed);
        return kFuncOK;
}

/*****************************************************************************/
EFunctionReturnTypes_t
TFacileFile::adv_read_int(int *dest)
{

        ERR_IF(kFuncOK != adv_read((void *)dest,sizeof(*dest)),
                        return kFuncFailed);
        return kFuncOK;
}

/*****************************************************************************/
EFunctionReturnTypes_t
TFacileFile::Open(const char *whatfile, int flags)
{
        fFileHandle = ::open(
                        whatfile,
                        flags,
                        S_IREAD | S_IWRITE);

        ERR_IF(fFileHandle <= 0,
                        return kFuncFailed);//if open failed
        return kFuncOK;
}

/*****************************************************************************/
EFunctionReturnTypes_t
TFacileFile::Close()
{
        ERR_IF( 0 != ::close(fFileHandle),
                        return kFuncFailed);
        fFileHandle = -1;// u never know, or is it just me? ;)
        return kFuncOK;
}

/*****************************************************************************/
EFunctionReturnTypes_t
TFacileFile::LoadAll(int a_AllocatedActions, int a_MaxTriggers)
{
        //allocate and init them all
        int howmanyonfile=0;
        ERR_IF(kFuncOK != adv_read_int(&howmanyonfile),
                        return kFuncFailed);
        ERR_IF(howmanyonfile != a_AllocatedActions,
                        return kFuncFailed);

        for (int i=0;i<a_AllocatedActions;i++) {
                AllActions[i]=new TAnyAction;
             ERR_IF(kFuncOK !=
                AllActions[i]->SetFunx(
                                activation_funcs[i],
                                deactivation_funcs[i]),
                        return kFuncFailed);
        }//for

        int onfile_Input2ActAction;//for current action
        //see         Act_st Act[kMaxTriggers];
        /*
         * enum {
         *         kInput2ActivateAction=0,
         *                 kInput2DeactivateAction
                ,kMaxTriggers};
         * */
        ERR_IF(kFuncOK != adv_read_int(&onfile_Input2ActAction),
                        return kFuncFailed);
        ERR_IF( onfile_Input2ActAction != a_MaxTriggers,
                        return kFuncFailed);

for (int i=0;i<a_AllocatedActions;i++) {
        for (int k=0;k<a_MaxTriggers;k++) {
                Act_st temp=AllActions[i]->Act[k];
                ERR_IF(kFuncOK != adv_read_int(&temp.CombiVars[kHowManyType][kInputInt]),
                                return kFuncFailed);
                ERR_IF(kFuncOK != adv_read_int(&temp.CombiVars[kHowManyType][kKeyInt]),
                                return kFuncFailed);
                ERR_IF(kFuncOK != adv_read_int(&temp.CombiVars[kHowManyType][kMouseInt]),
                                return kFuncFailed);
                ERR_IF(kFuncOK !=
                                adv_read(temp.CombiInputBuf,
                                 temp.CombiVars[kHowManyType][kInputInt]*
                                 sizeof(temp.CombiInputBuf[0])),
                                return kFuncFailed);
                ERR_IF(kFuncOK !=
                                adv_read(temp.CombiKeyBuf,
                                  temp.CombiVars[kHowManyType][kKeyInt]*
                                  sizeof(temp.CombiKeyBuf[0])),
                                return kFuncFailed);
                ERR_IF(kFuncOK !=
                                adv_read(temp.CombiMouseBuf,
                                 temp.CombiVars[kHowManyType][kMouseInt]*
                                 sizeof(temp.CombiMouseBuf[0])),
                                return kFuncFailed);
                /* read from disk into <temp> then:*/
                AllActions[i]->Act[k]=temp;
                /*
                AllActions[i]->SetCombiInputBuf(k,
                                temp.CombiInputBuf,
                                temp.CombiVars[kHowManyType][kInputInt]);
                AllActions[i]->SetCombiKeyBuf(k,
                                temp.CombiKeyBuf,
                                temp.CombiVars[kHowManyType][kKeyInt]);
                AllActions[i]->SetCombiMouseBuf(k,
                                temp.CombiMouseBuf,
                                temp.CombiVars[kHowManyType][kMouseInt]);*/
        }//for2
}//for1
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
TFacileFile::AllInOne(
                const char *fromfilename,
                int a_AllocatedActions, int a_MaxTriggers)
{
        ERR_IF(kFuncOK != Open(fromfilename,O_RDWR | O_CREAT | O_BINARY | O_NDELAY),
                        return kFuncFailed);

        ERR_IF( kFuncOK !=
                        LoadAll(a_AllocatedActions,a_MaxTriggers),
                        return kFuncFailed);

//done with the file closing it:
        ERR_IF(kFuncOK != Close(),
                        return kFuncFailed);
        return kFuncOK;
}


/*****************************************************************************/
EFunctionReturnTypes_t
TFacileFile::SaveAllInOne(
                const char *tofilename,
                int a_AllocatedActions, int a_MaxTriggers)
{
        ERR_IF(kFuncOK != Open(tofilename,O_RDWR | O_CREAT | O_BINARY | O_NDELAY | O_TRUNC),
                        return kFuncFailed);

        ERR_IF( kFuncOK !=
                        SaveAll(a_AllocatedActions,a_MaxTriggers),
                        return kFuncFailed);

//done with the file closing it:
        ERR_IF(kFuncOK != Close(),
                        return kFuncFailed);
        return kFuncOK;
}


/*****************************************************************************/
EFunctionReturnTypes_t
TFacileFile::SaveAll(int a_AllocatedActions, int a_MaxTriggers)
{
        //allocate and init them all
        LAME_PROGRAMMER_IF( kAllocatedActions != a_AllocatedActions,
                        return kFuncFailed);
        ERR_IF(kFuncOK != adv_save_int(&a_AllocatedActions),
                        return kFuncFailed);


        LAME_PROGRAMMER_IF( kMaxTriggers != a_MaxTriggers,
                        return kFuncFailed);
        ERR_IF(kFuncOK != adv_save_int(&a_MaxTriggers),
                        return kFuncFailed);

for (int i=0;i<a_AllocatedActions;i++) {

        for (int k=0;k<a_MaxTriggers;k++) {
                Act_st temp=AllActions[i]->Act[k];
                /*printf("!%d/%d %d %d ptr(%p)!\n",i,k,
                                temp.CombiVars[kHowManyType][kInputInt],
                                AllActions[i]->Act[k].CombiVars[kHowManyType][kInputInt],
                                AllActions[i]->Act
                                );*/
                //first one must be defined(first=activateAction)
                PARANOID_IF((k==0)&&(temp.CombiVars[kHowManyType][kInputInt] <=0),
                                return kFuncFailed);
                ERR_IF(kFuncOK != adv_save_int(&temp.CombiVars[kHowManyType][kInputInt]),
                                return kFuncFailed);

                /*PARANOID_IF(temp.CombiVars[kHowManyType][kKeyInt] <=0,
                                return kFuncFailed);*/
                ERR_IF(kFuncOK != adv_save_int(&temp.CombiVars[kHowManyType][kKeyInt]),
                                return kFuncFailed);

/*                PARANOID_IF(temp.CombiVars[kHowManyType][kMouseInt] <=0,
                                return kFuncFailed);*/
                ERR_IF(kFuncOK != adv_save_int(&temp.CombiVars[kHowManyType][kMouseInt]),
                                return kFuncFailed);
                ERR_IF(kFuncOK !=
                                adv_save(temp.CombiInputBuf,
                                  temp.CombiVars[kHowManyType][kInputInt]*
                                  sizeof(temp.CombiInputBuf[0])),
                                return kFuncFailed);
                ERR_IF(kFuncOK !=
                                adv_save(temp.CombiKeyBuf,
                                        temp.CombiVars[kHowManyType][kKeyInt]*
                                        sizeof(temp.CombiKeyBuf[0])),
                                return kFuncFailed);
                ERR_IF(kFuncOK !=
                                adv_save(temp.CombiMouseBuf,
                                        temp.CombiVars[kHowManyType][kMouseInt]*
                                        sizeof(temp.CombiMouseBuf[0])),
                                return kFuncFailed);

        }//for2
}//for1
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
TFacileFile::adv_save(const void *from,int a_howmany)
{
        //int write_ret=write(fFileHandle, from, a_howmany);
        //fprintf(stderr,"!%d!\n",write_ret);
        //perror(sys_errlist[errno]);
        if (a_howmany==0)
                return kFuncOK;
        ERR_IF(a_howmany != write(fFileHandle, from, a_howmany),
                        return kFuncFailed);
        return kFuncOK;
}

/*****************************************************************************/
EFunctionReturnTypes_t
TFacileFile::adv_save_int(const int *from)
{

        ERR_IF(kFuncOK != adv_save(from,sizeof(int)),
                        return kFuncFailed);
        return kFuncOK;
}

/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
