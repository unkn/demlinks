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
* Description: just testing stuff 
*
****************************************************************************/


#include <stdlib.h>
#include <stdio.h>
#include <conio.h>
#include <io.h>

#include "petrackr.h"
#include "dmentalx.h"


dmentalix *test2;

int main(){
    init_error_tracker();
    
    test2=new dmentalix;
    ab_ifnot(test2);

    printf("Erase old files?\n");
    printf("drop anykey to YES or ESC to skip...\n");
    basic_element c=(basic_element)(getch());
    if (c!=27) {
        unlinkall(_fnames);//so we kill the files we can use strictADDelemental()
    }
    
    ab_ifnot(test2->init(_fnames));

    if (c==27) goto skipadd;

    printf("Attempting to add eatoms with BE#0..#255 takes 15seconds\n");
    printf("drop anykey to begin or ESC to skip...\n");
    if (getch()==27) goto skipadd;
    
    c=0;
    while ( !( (kbhit())&&(getch()) ) ){
        printf("attempting to add basic_elemnt==char(%d)",c);
        ab_if_error_after_statement(
            atomID bebe=test2->strict_add_atom_type_E(c);//only used with unlink()
        );
        printf(" :has: atomID==%ld\n",bebe);
        if (c++==255) break;
    }//while

skipadd:
    
    atomID prev=_noID_;
    atomID fromhere;//no warnings
    atomID bebe;
    groupID newgid;

    printf("Attempting to add acatoms to each eatom ~ 30secs\n");
    printf("drop anykey to begin or ESC to skip...\n");
    if (getch()==27) goto skipacatoms;
    
    c=0;
    while ( !( (kbhit())&&(getch()) ) ){
        printf("attempt2add ACatom to BE==char(%d)",c);
        ab_if_error_after_statement(
            atomID _atomID_typeE=test2->find_atomID_type_E(c);
        );
        ab_if_error_after_statement(
            bebe=test2->strict_add_atom_type_AC_after_prev(_atomID_typeE,_noID_,prev);//only used with unlink()
        );
        prev=bebe;//at this point we have prev bebe->next=_noID_ unless that
        //funxion updates prev->next to US, which will do! and should DO!
        printf(" :acatom has: atomID==%ld\n",bebe);
        if (c++==255) break;
    }//while

    printf("Trying to group all prev, into a new group...5-10 seconds per call\n");
    printf("drop anykey to begin or ESC to skip...\n");
    if (getch()==27) goto skipgrpadd;

    printf("working...\n");
    //bebe is last, but `add...' will find parse the chain for the `head' atomID of the chain
    ab_if_error_after_statement(
        newgid=test2->add_group_with_headatom(&bebe);
    );
    printf("added gID==%ld which points to head atomID==%ld\n",newgid,bebe);
    

skipgrpadd:
    printf("Trying to parse chain from the last added acatom\n");
    printf("drop anykey to begin or ESC to skip...\n");
    if (getch()==27) goto skipacatoms;
    
    fromhere=prev;

    while ( !( (kbhit())&&(getch()) ) ){
        ab_if_error_after_statement(
            fromhere=test2->get_prev_atomID_in_chain(fromhere)
        );
        if (fromhere==_noID_) break;//don't report atomID==0
        printf("goinUP&passing thru (some not checked type) of atom with atomID==%ld\n",fromhere);
    }//while


    printf("Trying to get list of clones for all eatoms\n");
    printf("drop anykey to begin or ESC to skip...\n");
    if (getch()==27) goto skipacatoms;

  c=0;
  while ( !( (kbhit())&&(getch()) ) ){

    atomID typeEid;
    ab_if_error_after_statement(
        typeEid=test2->find_atomID_type_E(c);
    );

    eatomslist_itemID head;

    ab_ifnot( test2->get_atomID_s_headIDof_eatomslistofclones(typeEid,head) );
//head might be _noID_ after above call ^ w/o any err to be caused.

    while (
           ( !( (kbhit())&&(getch()) ) )
           && (head != _noID_)
          )
    {
        deref_eatomslist_itemID_type inhead;
        ab_ifnot( test2->get_eatomslist_item_withID(head,inhead) );
        printf("atomID==%ld points to BE#%d which is US(=atomID(%ld))\n",inhead.ptr2atom_that_points_to_US,c,typeEid);
        head=inhead.nextINlist;
    }//while
    if (c++==255) break;//quit after pr0xexin last
  }//while

skipacatoms:
    srand(982);
    printf("Trying find, in random order 256 times takes 5seconds w/optimiz\n");
    printf("drop anykey to begin or ESC to skip...\n");
    if (getch()==27) goto skiprnd;
    
    c=0;
    while ( !( (kbhit())&&(getch()) ) ){
        c--;
        basic_element d=(basic_element)(rand());//no warnings
        printf("cnt %d find atomID of eatom with BE#%d",c,d);
        ab_if_error_after_statement(
            atomID elder=test2->find_atomID_type_E(d);
        );
        if (elder==0) printf(" not found!\n");
        else printf(" :atomIDis: %ld\n",elder);
        if (c==0) break;
    }//while2

skiprnd:
    printf("Trying find, in backward order, takes 5secs w/optimiz\n");
    printf("drop anykey to begin or ESC to skip...\n");
    if (getch()==27) goto skipord;

    c=0;
    while ( !( (kbhit())&&(getch()) ) ){
        c--;
        printf("find atomID of a type E atom (eatom) that has basic_elem #%d",c);
        ab_if_error_after_statement(
            atomID elder=test2->find_atomID_type_E(c);
        );
        if (elder==0) printf(" not found!\n");
        else printf(" :atomIDis: %ld\n",elder);
        if (c==0) break;
    }//while3


skipord:
    ab_ifnot(test2->shutdown());
    delete test2;

//last in line
    deinit_error_tracker();
    printf("\nDone...press key\n");
    getch();
    return 0;
}

