/****************************************************************************
*
*                             dmental links
*    Copyright (c) 28 Feb 2005 AtKaaZ, AtKaaZ at users.sourceforge.net
*    Portions Copyright (c) 1983-2002 Sybase, Inc. All Rights Reserved.
*
*  ========================================================================
*
*    This file contains Original Code and/or Modifications of Original
*    Code as defined in and that are subject to the Sybase Open Watcom
*    Public License version 1.0 (the 'License'). You may not use this file
*    except in compliance with the License. BY USING THIS FILE YOU AGREE TO
*    ALL TERMS AND CONDITIONS OF THE LICENSE. A copy of the License is
*    provided with the Original Code and Modifications, and is also
*    available at www.sybase.com/developer/opensource.
*
*    The Original Code and all software distributed under the License are
*    distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
*    EXPRESS OR IMPLIED, AND SYBASE AND ALL CONTRIBUTORS HEREBY DISCLAIM
*    ALL SUCH WARRANTIES, INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF
*    MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR
*    NON-INFRINGEMENT. Please see the License for the specific language
*    governing rights and limitations under the License.
*
*  ========================================================================
*
* Description: driver program for a char-based world (just testing stuff)
*
****************************************************************************/
/*
for theory see the file ..\txt\charwrld.txt
*/

#include <stdlib.h>
#include <stdio.h>
#include <conio.h>
#include <io.h>

#include "petrackr.h"
#include "dmentalx.h"
#include "nicef.h"


//#define PARANOIA_CHECKS
#undef PARANOIA_CHECKS //some extra (lame)checks

#define num_cached_records 2048
#define cfg_fname "test03.cfg"

nicefi *lamecfg;
dmentalix *test3;
enum enum_specials{
_spec_filename=0,
_spec_dirname,
_spec_file,
_spec_dir,
_spec_dircontents,
_spec_dir_dirlist,
_spec_dir_filelist,
_spec_contents,
_spec_root,
_spec_word,
numspex};

atomID _specials[numspex];//there are some special atomIDs recognized internally; faster if stored in this way

reterrt get_word(const char *w, groupID &dest){
//attempts to find the word's groupID, if the word exists
//if not exist, dest=_noID_, funx returns 0 if errors which is prolly bad
//FIXME:
    ret_ok();
}

reterrt abs_add_word(const char *w,groupID &dest){
//not checking if the word already exists, dangerous tho, shouldn't be called
//outside add_word()
//FIXME:
    ret_ok();
}

reterrt add_word(const char *w,groupID &dest){
//adds one word ie. "file" 
//checks for existing, if the word exists we will return it's groupID
    groupID tmpgid;
    ret_ifnot( get_word(w,tmpgid) );
    if (!tmpgid){//not found, add it
        ret_ifnot( abs_add_word(w,tmpgid) );
#ifdef PARANOIA_CHECKS
        ret_if(tmpgid==_noID_);//funny couldn't manage to add the word
#endif
    }//fi
    dest=tmpgid;
    ret_ok();
}


reterrt make_new_config(){
//assuming, not checking if, dmentalix(test3->) is already inited since we
//add atoms and stuff

    //FIXME: add the new atoms into _specials[]

    groupID dummygroupID=test3->add_empty_group();
    ret_if(dummygroupID == _noID_);

    atomID prev=_noID_;//first one has no `.prev'
    for (long i=0;i<numspex;i++){//write'm all
        ret_if_error_after_statement(
            prev=test3->strict_add_atom_type_GC_after_prev(dummygroupID,_noID_,prev) 
        );//why not keep all specials chained ;) and they all ref. to dummygroupID
        _specials[i]=prev;
        ret_ifnot( lamecfg->writerec(1+i,&_specials[i]) );
    }//for

    ret_ok();
}

reterrt read_config(){
    for (long i=0;i<numspex;i++){//read them all
        ret_ifnot( lamecfg->readrec(i+1,&_specials[i]) );
    }//for

    ret_ok();
}

int main(){
    init_error_tracker();
    
    test3=new dmentalix;
    ab_ifnot(test3);

//    unlink(cfg_fname);//FIXME: temporary shit
        
    lamecfg=new nicefi;
    ab_ifnot(lamecfg);
    ab_ifnot( lamecfg->open(cfg_fname,0,sizeof(atomID),numspex) );
    int rc=read_config();
    if (! rc ) {
        purge_all_errors();
        printf("Unable to read config file, recreating everything!\n");
        printf("Erasing data files...\n");
        unlinkall(_fnames);//we don't care about errors here
    }//fi 

    printf("Attempting to open data files...\n");
    ab_ifnot( test3->init(_fnames,num_cached_records) );
    
    if (! rc) {
        printf("Making new config...\n");
        ab_ifnot( make_new_config() );//must already be inited ~ dmentalix
    }//fi
    delete lamecfg;//w/ autoclose, we nolonger need the config file


//end of story
    printf("Flushing writes and shutting down...\n");
    ab_ifnot(test3->shutdown());
    delete test3;

//last in line
    deinit_error_tracker();
    printf("\nAll Done...press key\n");
    getch();
    return 0;
}

