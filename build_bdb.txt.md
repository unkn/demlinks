this will describe how to build berkeleydb native .dll (for windows) and the .jar
from f3e25ba30ea351354118bb7785075f5f  db-5.2.28.zip
============

instructions are found in 
docs/installation/build_win.html
docs/installation/build_win_java.html
after unzipping that file

-------------------
here are the simple steps that I did for win7 64bit jdk7 64bit:
unzip db-5.2.28.zip into this project's folder then subfolder .\lib\berkeleydb
this is how it looks after:

	c:\workspace\demlinks\lib\berkeleydb\db-5.2.28\*.*

1. got Visual Studio 2010

	7790db7d2aac9e1ee8baa34d42988577689c9e7a *VS2010UltimTrial.iso

2. installed on custom:

	Visual C++
that's it(didn't install any other options; ie. deselected them). 

3. open this solution: 

	db-5.2.28\build_windows\Berkeley_DB_vs2010.sln
that means:
Choose File -> Open -> Project/Solution.... In the build_windows directory, select Berkeley_DB_vs2010.sln and click Open.

4. I chose Debug and x64 in those drop downs on the tool bar.

5. right-click on the Berkeley_DB_vs2010 solution and select Build Solution.

6. meanwhile go to db_java project which is a child in the solution's tree on the left
In Visual Studio 2010 - Right-click db_java project,

7.choose Properties->Configuration Properties-> VC++ Directories
* WARN: these will change when you change the build type on step 4. so you need to redo this step for that new type
OR, try to select Configuration: All Configurations before you change theese paths

in Include Directories append:

	C:\Program Files\Java\jdk1.7.0\include;C:\Program Files\Java\jdk1.7.0\include\win32

(that is win32 there even though this is jdk7 64bit, and on win7 64bit)

in Executable Directories append:

	C:\Program Files\Java\jdk1.7.0\bin



8. wait for main solution to complete build(else menu items are disabled anyway), then 
right-click on db_java and select Build (or Rebuild) 

* NOTE: the db.jar file is the same regardless of what Debug or Release you chose
same contents inside it, tested with md5

9. potentially make links:

	cd db-5.2.28
	mklink ..\db.jar build_windows\x64\Debug\db.jar
	mklink ..\libdb_java52d.dll build_windows\x64\Debug\libdb_java52d.dll
	mklink ..\libdb52d.dll build_windows\x64\Debug\libdb_java52d.dll



* NOTE: the Debug version is slower by 3.4 times than Release;
Debug:
	adding from [0 to 111800) add100 executed in: 6,590 ms
	all above adds/check (aka part2) executed in 34,619 ms
Release:
	adding from [0 to 111800) add100 executed in: 1,903 ms
	all above adds/check (aka part2) executed in 10,003 ms




The Markdown format is used for this file.