# this is for Open Watcom, just type: wmake -f makefile.wat

#############################################################################
#
#                             dmental links
#    Copyright (c) 28 Feb 2005 AtKaaZ, AtKaaZ at users.sourceforge.net
#    Portions Copyright (c) 1983-2002 Sybase, Inc. All Rights Reserved.
#
#  ========================================================================
#
#    This file contains Original Code and/or Modifications of Original
#    Code as defined in and that are subject to the Sybase Open Watcom
#    Public License version 1.0 (the 'License'). You may not use this file
#    except in compliance with the License. BY USING THIS FILE YOU AGREE TO
#    ALL TERMS AND CONDITIONS OF THE LICENSE. A copy of the License is
#    provided with the Original Code and Modifications, and is also
#    available at www.sybase.com/developer/opensource.
#
#    The Original Code and all software distributed under the License are
#    distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER
#    EXPRESS OR IMPLIED, AND SYBASE AND ALL CONTRIBUTORS HEREBY DISCLAIM
#    ALL SUCH WARRANTIES, INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF
#    MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR
#    NON-INFRINGEMENT. Please see the License for the specific language
#    governing rights and limitations under the License.
#
#  ========================================================================
#
# Description: makefile to use with Open Watcom compiler
#
#############################################################################


base = .
srcdir = $(base)\src
bindir = $(base)\bin
objdir = $(base)\obj
tmpdir = $(base)\tmp
errdir = $(base)\err
exe = DMLtest3.exe
outfile = $(bindir)\$(exe)

phfile = $(tmpdir)\prechdrs.hdr

objs =  $(objdir)\dmltest3.obj &
        $(objdir)\notetrk.obj &
        $(objdir)\pnotetrk.obj &
        $(objdir)\recstor.obj


# that's all ~ above

SYSTEM = dos4g # mumu.regexp

# name the compiler
CC = wpp386

MODEL = f       # flat
WARNINGS = -wx -fhwe -fhq
CACHE = -fh=$(phfile)
DEBug = -d2
ERRors = -fr=$(errdir)\$^&.err -er
VERBOSITY = -zq

# options to use
CFLAGS = -m$(MODEL) $(VERBOSITY) $(CACHE) $(DEBug) $(ERRors) $(WARNINGS)
LNKOPT = option quiet

.EXTENSIONS:
.EXTENSIONS: .exe .obj .h .cpp

.ERROR
        @cd $(errdir)
        @echo ERRORsssssssss!

.BEFORE
        @set INCLUDE=.;$(%watcom)\h
        @set DOS4G=QUIET
        @if not exist $(bindir) mkdir $(bindir)
        @if not exist $(objdir) mkdir $(objdir)
        @if not exist $(tmpdir) mkdir $(tmpdir)
        @if not exist $(errdir) mkdir $(errdir)

.AFTER
    @echo Done.

# explicit rules

all :  $(outfile) .SYMBOLIC
        @cd $(bindir)

.ERASE
$(outfile) : $(objs)
        *wlink $(LNKOPT) system $(SYSTEM) name $(outfile) file {$(objs)}

clean : cleanerr .SYMBOLIC
        @if exist $(objdir)\*.obj del $(objdir)\*.obj
        @if exist $(bindir)\*.exe del $(bindir)\*.exe
        @if exist $(phfile) del $(phfile)

cleanerr : .SYMBOLIC
        @if exist $(errdir)\*.err del $(errdir)\*.err

killdat : .SYMBOLIC
        @if exist $(tmpdir)\*.dat attrib -shra $(tmpdir)\*.dat
        @if exist $(tmpdir)\*.dat del $(tmpdir)\*.dat
        @if exist $(bindir)\*.dat attrib -shra $(bindir)\*.dat
        @if exist $(bindir)\*.dat del $(bindir)\*.dat
        @if exist $(bindir)\*.cfg attrib -shra $(bindir)\*.cfg
        @if exist $(bindir)\*.cfg del $(bindir)\*.cfg

cleanall : clean killdat .SYMBOLIC
        @%null

# implicit rules

.obj : $(objdir)
.cpp : $(srcdir)
.cpp.obj : .AUTODEPEND
        *$(CC) $(srcdir)\$^& -fo=$(objdir)\$^& $(CFLAGS)


