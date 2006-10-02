" added on 19-20 Sept 2006 by AtKaaZ for demlinks
syn keyword cppExceptions	__s _sTRY TRY _TRY __ __fIFnok __tIF __tIFnok __if _fret _hret _OK _htIF __fielse __fi _hdoIFnok
syn keyword cppExceptions	_konfiodhelse _konfiodh __doIFok __kofiod _hreterr _hokret ERR_IF ERR FAIL ADD_NOTE
syn keyword cppExceptions	THROW_HOOK logic_error _ht _ret _F _FA INFO _htIFnok __sIFnok _h ___ __t EXIT AEXIT EXIT_IF exit
syn keyword cppExceptions	WARN WARN_IF BUG BUG_IF INFO_IF PARANOID_IF PARANOID TRAP gTrackFRETs abort atexit

syn keyword cppBoolean		kFuncOK __VA_ARGS__ kFuncFailed kFuncNoLowLevelInputs kFuncNoGenericInputs  kFuncNoActions
syn keyword cppBoolean          kFuncAlreadyExists kFuncNotFound kFuncInexistentNodeNotCreated kFuncExistentSingleNodeNotOverwritten kFuncMoreThanOneNodeNotTruncated kFuncNULLPointer kMaxFuncErrors

syn keyword cppMacroSpecials    __VA_ARGS__
hi cppMacroSpecials ctermfg=red ctermbg=magenta

syn match   cppMethodOrField    /\.[_A-Za-z]\+[_A-Za-z0-9]*/
syn match   cppMethodOrField    /->[_A-Za-z]\+[_A-Za-z0-9]*/

syn match   cppkConsts    /\<k[A-Z0-9]\+/ " [_A-Za-z0-9]*\>/
syn match   cppgGlobalVar    /\<g[_A-Z0-9]\+[_A-Za-z0-9]*\>/

syn match   cppFieldOfClassOrStruc    /\<f[A-Z0-9]\+[_A-Za-z0-9]*\>/

hi cppMethodOrField ctermfg=cyan

syn keyword cppType		function EFunctionReturnTypes_t
syn match   cppType		"::"
hi Type ctermfg=lightgreen

syn match   cppUserType		/[_A-Za-z]\+[_A-Za-z0-9]*[A-Za-z0-9]_t/
syn keyword cppUserType         this
hi cppUserType ctermfg=darkgreen

syn keyword cppDefine		define
hi cppDefine ctermfg=lightmagenta

"syn keyword cppIdent		TDMLPointer MDMLDomainPointer MDMLFIFOBuffer TDMLCursor TLink

syn match cppReadOnlyParam		/a_[A-Z0-9]\+[_A-Za-z0-9]*/
hi cppReadOnlyParam ctermfg=yellow
syn match cppReadWriteParam		/m_[A-Z0-9]\+[_A-Za-z0-9]*/
hi cppReadWriteParam ctermfg=lightblue

syn match cppOperator		"=\+"
syn match cppOperator		"*\+"
"syn match cppOperator		"\.\+" don't defined this or cppMethodOrField won't work
syn match cppOperator		","
syn match cppOperator		"&\+"
syn match cppOperator		"("
syn match cppOperator		")"
syn match cppOperator		"!"
syn match cppOperator		"|"
hi cppOperator ctermfg=red

syn match cppBlockBegin		"{"
hi cppBlockBegin ctermfg=white ctermbg=magenta
syn match cppBlockEnd		"}"
hi cppBlockEnd ctermfg=cyan ctermbg=blue

syn match cppDerivedClass     /\<M[A-Z0-9]\+[_A-Za-z0-9]*/
hi cppDerivedClass ctermfg=darkcyan

syn match cppBaseClass     /\<T[A-Z0-9]\+[_A-Za-z0-9]*/
hi cppBaseClass ctermfg=darkgreen

hi cppFieldOfClassOrStruc ctermfg=darkblue

hi cppkConsts ctermbg=black ctermfg=darkred

hi cppgGlobalVar ctermbg=cyan ctermfg=black

hi Search ctermfg=white
hi Comment ctermfg=darkgray ctermbg=black
"cterm=reverse
"ctermfg=bg ctermbg=fg
hi Todo ctermfg=white ctermbg=green
"hi Normal ctermfg=white ctermbg=black
" End Of Add
