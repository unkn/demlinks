# DemLinks
=============

- planning

old irrelevant image follows:

![very old image from cvs/svn rev. 102](http://sourceforge.net/dbimage.php?id=85462)


Requirements(for coding/running tests):

* jdk7

* eclipse sdk (optional: 4.1 Build id: M20110729-2001)

* eclipse egit plugin

* junit 4
junit4.9b3.zip

	http://cloud.github.com/downloads/KentBeck/junit/junit4.9b3.zip
	unzip directly in .\lib\junit\ folder which is in current project, preserving folder structure of course.

* get the native libraries
db.jar and libdb52.dll and libdb_java52.dll from Berkeley DB 11gR2 (11.2.5.2.28) (not java edition)

	put those in .\lib\berkeleydb\ folder, or have them in PATH, except db.jar which needs to be in that folder.
	you can use Berkeley DB 5.2.28.msi Windows installer but this means you've to use 32bit jdk due to dlls being for 32bit
or better yet:

	see build_bdb.txt to build the 3 files yourself via C++ within Visual Studio 2010

------------------

These contents are in README.md file which is in Markdown format.
