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
* Description:  
*
****************************************************************************/


//REMEBER: If something goes wrong, it's chained!
//[it's either the first item or anyother item of a chain,
// it's up to you to make it the last item in the chain. Act now ;;)]

#include <process.h>
#include <stdio.h>

#include "petrackr.h"
#include "eatom.h"



eatomID if_eatom::find_basic_element_and_ret_eatomID(const basic_element what2search){
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());
#endif
    deref_eatomID_type tmpinto;
    return ( find_eatom(&tmpinto,what2search) );
}

eatomID if_eatom::find_eatom(deref_eatomID_type *into,const basic_element searchme){
/*
    this funx is kinda lame because it has to parse all records to see if
there's such a basic_elemen within the eatoms. Actually we think that we only
put 256 eatoms (uniq) and there won't be much problem finding them, even tho
we allocated a long. In the future if all works out we'll change the eatom
interface and storage in so that it'll resemble with the nicefi:: idea that
seeking the basic_element #215(for example) will seek to recno #215 and there
it is. Thus this implies that we have them in consecutive order, so we can't
have #254 w/o having #253 and all the prev from #0..#252. Thus one may think
howto add just the eatom's basic_elem #2GB w/o having to create all prev.
But, we shouldn't generalize the eatoms , they should only be #0..#255 or at
worse, about hmm, #0..#64KB
*/
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());
#endif
    ret_if(into==NULL);//`into' param must be preallocated

    //find element here
#ifdef CHECK_howmany_FUNX
    ret_if_error_after_statement(
#endif
    long hm=howmany();//getting 0 both if err and if howmany==0 records
#ifdef CHECK_howmany_FUNX
    );
    //^^^^^^^^ if howmany() had an error, we exit.
#endif
    long i;

#ifdef TRY_OPTIMIZED_GUESS_FIND_first
//optimized guess took at most 5seconds to scan each of both with srand(982)
// and backward order
    i=searchme+1;
    nicefi::readrec(i,into);//recnum `i' means eatomID actually.
    if ( into->basicelementdata==searchme ){
        //found it
        return i;//eatomID
    }//fi
#endif

#ifdef USE_PARTS_AND_SPLIT_AREA_OF_FIND_experimental
//with parts, took 2m31sec to scan all #0..#255 in backward direction
//without parts, took 2m30sec to scan all #0..#255 in backward direction
//SO IT'S THE SAME when parsing in order
//with parts, took 2m17s to scan randomly srand(982) with rand()
//without parts, took 2m14s to scan randomly srand(982) with rand()
//THUS parts AREN'T WORTH IT, wasn't such a good idea in the 1st place...
//`parts' are subject to removal in the future...

    #define parts 10  //set the speed of find here, not too high tho!
    long x10[parts];//0..9  attempting 10x speed;
    if (hm>=parts*2) {//important IF
        x10[0]=_FIRST_RECORD_;
        long divided=hm/parts;
        for (i=1;i<parts;i++) {//1..9 parse
            x10[i]=x10[i-1]+divided;
        }//for
        while (x10[parts-1]<=hm){//if last elem x10[9]==hm then we stop
        for (i=0;i<parts;i++){//0..9 parse
            nicefi::readrec(x10[i],into);//recnum `i' means eatomID actually.
            if ( into->basicelementdata==searchme ){
                //found it
                return x10[i];//eatomID
            }//fi
            x10[i]++;
        }//for
        }//while
    /*
        draft:
        0   1   2   3   4   5   6   7   8   9   10
    20  1   3   5   7   9   11  13  15  17  19  21
    40  1   5   9   13  17  21  25  29  33  37  41
    31  1   4   7   10  13  16  19  22  25  28  31
    22  1   3   5   7   9   11  13  15  17  19  21
    23  1   3   5   7   9   11  13  15  17  19  21
    29  1   3   5   7   9   11  13  15  17  19  21
    */
        #undef parts
    }//if hm>=20
    else { //if hm<20 we shouldn't bother
#endif
        for (i=_FIRST_RECORD_;i<=hm;i++){
            nicefi::readrec(i,into);//recnum `i' means eatomID actually.
            if ( into->basicelementdata==searchme ){
                //found it
                return i;//eatomID
            }//fi
        }//rof
#ifdef USE_PARTS_AND_SPLIT_AREA_OF_FIND_experimental
    }//esle
#endif
    
    //well we didn't find shit
    return 0;//eatomID cannot be zero, so...
}

long if_eatom::howmany(){ 
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());
#endif
    return nicefi::getnumrecords();
}

long if_eatom::addnew(const deref_eatomID_type *from){
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());
#endif
    long neweatomID=howmany()+1;
    writewithID(neweatomID,from);
    return neweatomID;
}

reterrt if_eatom::getwithID(const eatomID whateatomID, deref_eatomID_type *into){
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());
#endif
    ret_ifnot(nicefi::readrec(whateatomID,into));
    ret_ok();
}

reterrt if_eatom::writewithID(const eatomID whateatomID, const deref_eatomID_type *from){
#ifdef WASINITED_SAFETY
    ret_ifnot(wasinited());
#endif
    ret_ifnot(nicefi::writerec(whateatomID,from));
    ret_ok();
}                                          
                                            
if_eatom::~if_eatom(){
#ifdef WASINITED_SAFETY //if unset, user must use shutdown() before destruct.
    if (wasinited())
        shutdown(); 
#endif
}

if_eatom::if_eatom():
    its_recsize(sizeof(deref_eatomID_type))
{
#ifdef WASINITED_SAFETY
    setdeinited();
#endif
}

reterrt if_eatom::init(const char * fname){
#ifdef WASINITED_SAFETY
    ret_if(wasinited());
#endif
    ret_ifnot(nicefi::open(fname,0,its_recsize));
#ifdef WASINITED_SAFETY
    setinited();
#endif
    ret_ok();
}

reterrt if_eatom::shutdown(){
#ifdef WASINITED_SAFETY
    if (wasinited()) {
#endif
        ret_ifnot(nicefi::close());
#ifdef WASINITED_SAFETY
        setdeinited();
    }
#endif
    ret_ok();
}

void if_eatom::compose(
    deref_eatomID_type *into,
    atomID ptrback2atomID_for_faster_search_when_single,
    eatoms_listID ptr2list,
    basic_element basicelementdata
)
{
    _3in2(ptrback2atomID_for_faster_search_when_single,ptr2list,basicelementdata);
}

