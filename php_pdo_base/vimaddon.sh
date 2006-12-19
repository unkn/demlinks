#!/bin/sh

#              +f      enable pathname expansion.
set +f
#set -vx

if [ -z "$HOME" ]; then
        HOME=~
fi

#setable constants
OURFILE="vim_syntax_php.vim"
HOMEPLUGINDIR="$HOME/.vim/plugin"
SYNTAXFILE="php.vim"
SYNTAXDIR="syntax"
HOMESYNTAXDIR="$HOME/.vim/$SYNTAXDIR"
HOMESYNTAXFILE="$HOMESYNTAXDIR/$SYNTAXFILE"
HOMEDEMFILE="$HOMESYNTAXDIR/$OURFILE"
POSSIBLELOCATIONS="/usr/share/vim/"*"/$SYNTAXDIR /usr/local/share/vim/"*"/$SYNTAXDIR /usr/local/share/vim70/"*"/$SYNTAXDIR /usr/local/share/vim64/"*"/$SYNTAXDIR"
HOMECSCOPEMAPS="$HOMEPLUGINDIR/cscope_maps.vim"
INETCSCOPEMAPS="http://cscope.sourceforge.net/cscope_maps.vim"
#done vars

        current_script_runname="$0"
        current_script_filename="${current_script_runname##*/}"
        current_script_dirname="${current_script_runname%%/*}"
        if [ "x$current_script_dirname" = "x." ]; then
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
                wget --output-document "$HOMECSCOPEMAPS" -- "$INETCSCOPEMAPS"
        fi

#the following will add extra highlighting for php files with vim
#these php files are supposed to be piped thru the cpp preprocessor
zline="  source $OURFULLPATHFILE"
grep -q "^${zline}$" "$HOMESYNTAXFILE"
if [ ! "$?" -eq "0" -o ! -r "$HOMESYNTAXFILE" ]; then
cat >"$HOMESYNTAXFILE" <<EOF
if version >= 600
  source $FULLPATH2SYNTAXFILE
  unlet b:current_syntax
$zline
  let b:current_syntax = "php"
endif
EOF
        echo "updated $HOMESYNTAXFILE"
fi

