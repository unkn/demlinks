@echo off
wpp386 %1 -fo=..\obj\ -d2 -fhwe -wx -mf -zq -fh=..\tmp\prechdrs.hdr -er -fr=..\err\
if errorlevel 1 cd ..\err
pause
rem if errorlevel 1 pause A key to return to VI...
