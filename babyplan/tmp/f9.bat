@echo off
cd ..
wmake
if errorlevel 1 goto doomed
rem cd bin
dmltest3.exe
cd ..\src
pause
exit
:doomed
cd src
pause press a key to return to VI...
