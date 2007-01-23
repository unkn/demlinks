" added on 19-20 December 2006 by AtKaaZ for demlinks
syn case match

syn keyword phpException	__ _r _yntIF _yntIFnot _tIF _tIFnot _TRY _ifnot exit _if beginprogram endprogram _ynif _ynifnot _artIFnot _artIF _arif _arifnot isFlagL1
syn keyword phpException	_ynifL0
"syn match   phpException    /\<d[_a-z0-9]\+[_a-z0-9]*/

syn keyword phpBoolean		yes no ok bad __VA_ARGS__ __construct __destruct

"syn match   cppMethodOrField    /\.[_A-Za-z]\+[_A-Za-z0-9]*/
"syn match   cppMethodOrField    /->[_A-Za-z]\+[_A-Za-z0-9]*/

syn match   phpBoolean    /\<k[A-Z]\+/ " [_A-Za-z0-9]*\>/
syn match   cppgGlobalVar    /\<g[_A-Z0-9]\+[_A-Za-z0-9]*\>/

syn match   cppFieldOfClassOrStruc    /\<$f[A-Z0-9]\+[_A-Za-z0-9]*\>/

"hi cppMethodOrField ctermfg=cyan
"hi phpMemberSelector ctermfg=cyan
hi phpVarSelector ctermfg=white
hi phpIdentifier ctermfg=yellow
"hi phpMethods ctermfg=red

"syn keyword cppType		function EFunctionReturnTypes_t
"syn match   cppType		"::"
hi Type ctermfg=lightgreen
hi Repeat ctermfg=blue

syn keyword phpDefine funcL0 getalist endnowL0 endfuncL0 appendtolist quitmsg dropmsg nl rnl br nocol greencol redcol getline getfile tab show dParentsOf dChildrenOf dGetAll rtab
syn keyword phpDefine purplecol except throw_exception bluecol addretflagL0 addretflag0 adef rdef isflag isValue_InList keepflagsL0 keepflags0 space rspace
syn keyword phpDefine funcL1 endfuncL1 addretflagL1 delretflagL1 setretflagL1 countretflagsL1 endnowL1 boolfunc ynboolfunc ynfunc procedure isReturnStateList ynIsNotGood
syn keyword phpDefine AllowReentry DisallowReentry funcL0re funcL0_part1of2 funcL0_part2of2 funcL1re funcL1_part1of2 funcL1_part2of2 DisallowLocalReentry DisallowGlobalReentry AllowLocalReentry AllowGlobalReentry func0 endfunc0 func0re endfunc0re endnowL0_part1of2 endnowL0_part2of2 endnowL0re endfuncL0re endnow0 func1 func1re endfunc1re _arifL0
syn keyword phpDefine endfunc1 keepflags1 addretflag1 isFlagL0_InReturn emptystr endnow1 nil
"syn match phpCoreConstant     /\<T[A-Z0-9]\+[_A-Za-z0-9]*/
"hi phpCoreConstant ctermfg=6
syn match phpCoreConstant     /\<[_a-zA-Z]*L[0-9]\+[_a-zA-Z]*/
hi phpCoreConstant ctermfg=2

"syn match   cppUserType		/[_A-Za-z]\+[_A-Za-z0-9]*[A-Za-z0-9]_t/
"syn keyword cppUserType         this
"hi cppUserType ctermfg=darkgreen

"syn keyword cppDefine		define
"hi cppDefine ctermfg=lightmagenta

"syn keyword cppIdent		TDMLPointer MDMLDomainPointer MDMLFIFOBuffer TDMLCursor TLink

"syn match cppReadOnlyParam		/a_[A-Z0-9]\+[_A-Za-z0-9]*/
"hi cppReadOnlyParam ctermfg=yellow
"syn match cppReadWriteParam		/m_[A-Z0-9]\+[_A-Za-z0-9]*/
"hi cppReadWriteParam ctermfg=lightblue

syn match phpOperator		"=\+"
syn match phpOperator		"*\+"
syn match phpOperator		"\.\+"
syn match phpOperator		","
syn match phpOperator		"&\+"
syn match phpOperator		"("
syn match phpOperator		")"
syn match phpOperator		"!"
syn match phpOperator		"|"
syn match phpOperator		"++"
syn match phpOperator		"--"
hi phpOperator ctermfg=red

syn match    phpParent     "#.\{-}\(?>\|$\)\@="     contained contains=phpTodo

"syn match cppBlockBegin		"{"
"hi cppBlockBegin ctermfg=white ctermbg=magenta
hi phpParent ctermfg=white ctermbg=magenta
"syn match cppBlockEnd		"}"
"hi cppBlockEnd ctermfg=cyan ctermbg=blue

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


hi Folded term=standout ctermfg=blue ctermbg=0 guifg=Black guibg=#e3c1a5


" End Of Add
