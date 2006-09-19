#!/bin/bash

set +f

if [ -z "$HOME" ]; then
        HOME=~
fi

#setable constants
OURFILE="vim_syntax_cpp.vim.inc"
HOMEPLUGINDIR="$HOME/.vim/plugin"
SYNTAXFILE="cpp.vim"
SYNTAXDIR="syntax"
HOMESYNTAXDIR="$HOME/.vim/$SYNTAXDIR"
HOMESYNTAXFILE="$HOMESYNTAXDIR/$SYNTAXFILE"
HOMEDEMFILE="$HOMESYNTAXDIR/$OURFILE"
POSSIBLELOCATIONS="/usr/share/vim/"*"/$SYNTAXDIR /usr/local/share/vim/"*"/$SYNTAXDIR"
HOMECSCOPEMAPS="$HOMEPLUGINDIR/cscope_maps.vim"
INETCSCOPEMAPS="http://cscope.sourceforge.net/cscope_maps.vim"
#done vars

        current_script_runname="$0"
        current_script_filename="${current_script_runname##*/}"
        current_script_dirname="${current_script_runname%%/*}"
        if [ "$current_script_dirname" == "." ]; then
                current_script_dirname="`pwd`"
        fi
        current_script_fullname="${current_script_dirname}/${current_script_filename}"

OURFULLPATHFILE="$current_script_dirname/$OURFILE"

FULLPATH2SYNTAXFILE="`find $POSSIBLELOCATIONS -name "$SYNTAXFILE" 2>/dev/null | head -1`"
if [ ! "$?" -eq "0" -o -z "$FULLPATH2SYNTAXFILE" ]; then
        FULLPATH2SYNTAXFILE="`locate "$SYNTAXFILE" 2>/dev/null | grep "$SYNTAXDIR/$SYNTAXFILE" |head -1`"
fi

#make sure directories exit
mkdir -p -- "$HOMEPLUGINDIR"
mkdir -p -- "$HOMESYNTAXDIR"

#the following will add cscope support for vim
        #fetch the vim plugin from the internet if it doesn't already exist
        if [ ! -r "$HOMECSCOPEMAPS" ]; then
                wget --output-document "$HOMECSCOPEMAPS" -- "INETCSCOPEMAPS"
        fi

#the following will add extra highlighting for cpp files with vim
if [ ! -r "$HOMESYNTAXFILE" ]; then
        #cp -- "$FULLPATH2SYNTAXFILE" "$HOMESYNTAXFILE"
        #       ln -s destlink  sourcefile
        #ln -s -- "$HOMEDEMFILE" "$OURFULLPATHFILE"
cat >>"$HOMESYNTAXFILE" <<EOF
if version >= 600
  source $FULLPATH2SYNTAXFILE
  unlet b:current_syntax
  source $OURFULLPATHFILE
  let b:current_syntax = "cpp"
endif
EOF
fi

