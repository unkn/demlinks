# just type wmake

#############################################################################
#
#                             dmental links
#	Copyright (c) 28 Feb 2005 AtKaaZ, AtKaaZ at users.sourceforge.net
#
#  ========================================================================
#
#    This program is free software; you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation; either version 2 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program; if not, write to the Free Software
#    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
#
#  ========================================================================
#
# Description:
#
#############################################################################


base = .
srcdir = $(base)\src
bindir = $(base)\bin
objdir = $(base)\obj
tmpdir = $(base)\tmp
errdir = $(base)\err
exe = DMLtest2.exe
outfile = $(bindir)\$(exe)

phfile = $(tmpdir)\prechdrs.hdr

objs = $(objdir)\test02.obj &
	$(objdir)\dmentalx.obj &
	$(objdir)\errtrack.obj &
	$(objdir)\petrackr.obj &
	$(objdir)\nicef.obj &
	$(objdir)\atom.obj &
	$(objdir)\group.obj &
	$(objdir)\acatom.obj &
	$(objdir)\gcatom.obj &
	$(objdir)\eatom.obj &
	$(objdir)\aca_list.obj &
	$(objdir)\gca_list.obj &
	$(objdir)\ea_list.obj &
	$(objdir)\acalitem.obj &
	$(objdir)\gcalitem.obj &
	$(objdir)\eal_item.obj
	

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

.AFTER
	@echo Done.
	
# explicit rules

all :  $(outfile) .SYMBOLIC
	@cd $(bindir)

.ERASE
$(outfile) : $(objs)
	*wlink $(LNKOPT) system $(SYSTEM) name $(outfile) file {$(objs)}

clean : cleanerr .SYMBOLIC
        if exist $(objdir)\*.obj del $(objdir)\*.obj
        if exist $(bindir)\*.exe del $(bindir)\*.exe
	if exist $(phfile) del $(phfile)

cleanerr : .SYMBOLIC
	if exist $(errdir)\*.err del $(errdir)\*.err

killdat : .SYMBOLIC
	if exist $(tmpdir)\*.dat del $(tmpdir)\*.dat
	if exist $(bindir)\*.dat del $(bindir)\*.dat

cleanall : clean killdat .SYMBOLIC
	@%null

# implicit rules

.obj : $(objdir)
.cpp : $(srcdir)
.cpp.obj : .AUTODEPEND
        *$(CC) $(srcdir)\$^& -fo=$(objdir)\$^& $(CFLAGS)


