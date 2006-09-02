AC_DEFUN([AX_BERKELEY_DB],
[
  old_LIBS="$LIBS"

  minversion=ifelse([$1], ,,$1)

  db_HDR=""
  db_LIBS=""

  if test -z $minversion ; then
      minvermajor=0
      minverminor=0
      minverpatch=0
      AC_MSG_CHECKING([for Berkeley DB])
  else
      minvermajor=`echo $minversion | cut -d. -f1`
      minverminor=`echo $minversion | cut -d. -f2`
      minverpatch=`echo $minversion | cut -d. -f3`
      minvermajor=${minvermajor:-0}
      minverminor=${minverminor:-0}
      minverpatch=${minverpatch:-0}
      AC_MSG_CHECKING([for Berkeley DB >= $minversion])
  fi

  for version in "" 4.6 4.5 4.4 4.3 4.2 4.1 4.0  ; do

    if test -z $version ; then
        db_lib="-ldb"
	   db_lib_cxx="-ldb_cxx"
        try_headers="db_cxx.h"
    else
        db_lib="-ldb-$version"
	   db_lib_cxx="-ldb_cxx-$version"
        try_headers="db$version/db_cxx.h db`echo $version | sed -e 's,\..*,,g'`/db_cxx.h db`echo $version | sed -e 's,\.,,g'`/db_cxx.h"
    fi

    LIBS="$old_LIBS $db_lib_cxx"

    for db_hdr in $try_headers ; do
        if test -z $db_HDR ; then
            AC_LINK_IFELSE(
                [AC_LANG_PROGRAM(
                    [
                        #include <${db_hdr}>
                    ],
                    [
                        #if !((DB_VERSION_MAJOR > (${minvermajor}) || \
                              (DB_VERSION_MAJOR == (${minvermajor}) && \
                                    DB_VERSION_MINOR > (${minverminor})) || \
                              (DB_VERSION_MAJOR == (${minvermajor}) && \
                                    DB_VERSION_MINOR == (${minverminor}) && \
                                    DB_VERSION_PATCH >= (${minverpatch}))))
                            #error "too old version"
                        #endif

                        DB *db;
                        db_create(&db, NULL, 0);
                    ])],
                [
                    AC_MSG_RESULT([header $db_hdr, library $db_lib_cxx])

                    db_HDR="$db_hdr"
				db_HDR_CXX="$db_lib_cxx"
                    db_LIBS="$db_lib_cxx"
				break 2
                ])
        fi
    done
  done

  LIBS="$old_LIBS"

  if test -z $db_HDR ; then
    AC_MSG_RESULT([not found])
    ifelse([$3], , :, [$3])
  else
    ##AC_DEFINE(db_HDR, ["$db_HDR"])
    AC_DEFINE_UNQUOTED(DB_HEADER, [<$db_hdr>], [What db one should use])
##    echo "#ifndef HAVE_BERKELEY_DB" > config-inc.h
##    echo "#define HAVE_BERKELEY_DB" >> config-inc.h
##    echo "#include <$db_hdr>" >> config-inc.h
##    echo "#endif" >> config-inc.h
    ##AC_SUBST(DB_HEADER)
    AC_SUBST(db_LIBS)

    ifelse([$2], , :, [$2])
  fi
])
