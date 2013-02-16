; Copyright (c) 2012-2013, AtKaaZ
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.

;change(?) git-windows symlinks
;tested to work on windows7 64bit on the emacs-live repo, 
;make sure you set repo-path below

(ns cgws.notcore
  (:import java.io.File)
  (:import java.io.BufferedReader)
  (:require [datest1.ret :as r] :reload-all)
  (:require [runtime.q :as q] :reload-all)
  )

(set! 
  *warn-on-reflection*
  true)

(def ^:dynamic *separator* java.io.File/separator)

;;TODO: give meaningful names to the let variables

(def fileClass java.io.File)

(def ^java.io.File repo-path 
  (q/newInstanceOfClass fileClass 
    (str
      "s:" 
      *separator* 
      "workspace2013"
      *separator*
      "emacs-live"
      *separator*
      )
    )
  )

(def ^String repoBranch "master")

(declare transform_flatfiles_to_symlinks)

(defn -main [& args] 
  (comment (println "this is meant to transform git checkout symlinks(which are just flat files due to core.symlinks=false)\n"
           "into real symlinks.\n"
           "But this is meant only for windows because git doesn't know how to do that in windows."
           ))
  (println "passed args:" args)
  (time 
    (dorun 
      (map println 
        (transform_flatfiles_to_symlinks repo-path repoBranch))
      )
    )
;(r/getIfExists :key {:map 1})
  )


;(System/getenv)

(defn start-process
  "env or workdir can be nil to signify inherit from parent process"
  ;  .waitFor
  [^String cmd 
   ^{:tag "[Ljava.lang.String;"} env ;clojure type hint for java's array of String 
   ^File workdir]
  (let [^Runtime rt (Runtime/getRuntime)
        ^Process process (.exec rt cmd env workdir)]
    process
    )
  )

(defn getBufferedInputFromProcess
  [
   ^Process process]
  (let [
        ^InputStream is (.getInputStream process);  Implementation note: It is a good idea for the input stream to be buffered. 
        ^BufferedReader br (clojure.java.io/reader is)
        ]
    br
    )
  )

(defn shellify [command workdir]
  (getBufferedInputFromProcess (start-process command nil workdir))
  )



(defn gitlstree_cmd [branch]
  (str "git ls-tree -r" " \"" branch "\"")) ;branch must be quoted since it can contain at least "&"

(def symlink-filemode "120000")

(defn get_all_symlinks_from_repo
  "returns a list of all symlinks inside the specified git repository 
   as relative paths to the provided repo-path"
  [repo-path branch]
  (with-open [^BufferedReader input (shellify (gitlstree_cmd branch) repo-path)]
    (let [lazylines (line-seq input)
          ]
      (disj ;remove nils from result
        (set
          (map (fn [aline]
                 (let [
                       ss (clojure.string/split ^String aline	 #"\s")
                       ^String filemode (first ss)
                       ]
                   (cond (.equals filemode symlink-filemode)
                     (last ss)
                     :else
                     nil
                     )
                   )
                 )
            lazylines
            )
          )
        nil)
      )
    )
  )


(defn make-symlink
  "the-link is the destination symlink which is to be created and will point to the-real-file-or-dir
   the-real-file-or-dir is the existing path absolute (or relative to the the-link's parent folder) 
     to which the-link will point to"
  [the-link;File or String 
   ^String the-real-file-or-dir]
  (;TODO: must handle symlink to symlink cases, we get here and they are valid:
    let [
         ^File ffull (clojure.java.io/as-file the-link)
         ^String full-path (.getAbsolutePath ffull)
         ^File fparent-folder (.getParentFile ffull)
         ^String parent-folder (.getParent ffull)
         ^File ffull-sym-path (new File parent-folder the-real-file-or-dir)
         ^String sym-to (.getAbsolutePath ffull-sym-path)
         isFile? (.isFile ffull-sym-path)
         isDir? (.isDirectory ffull-sym-path)
         ]
    (cond isFile?
          (do 
            (println "making a symlink to a file" full-path " -> " sym-to)
            (q/ni)
            )
          
          isDir?
          (let [
                deleted? ;true;
                (q/delete-file ffull true)
                ;(println ".delete" ffull);FIXME: actually delete here
                ]
            (cond (not deleted?)
              (println "cannot delete:" ffull)
              :else
              (let [^Process process (start-process 
                              (str "cmd.exe /c mklink /d \"" full-path "\" \"" sym-to "\"") 
                              nil 
                              fparent-folder
                              )
                    _ (.waitFor process)
                    exitcode (.exitValue process)
                    what (str "a symlink to a folder: " full-path " -> " sym-to)
                    ]
                
                (cond (= 0 exitcode)
                  (println "made" what)
                  :else
                  (println "failed to make" what)
                  )
                )
              )
            )
          
          :else
          (println "symlink points to currently non-existing path(not file not dir), sym:" ffull-sym-path 
                   "path:" full-path "original sym:" sym-to "exists?:" (.exists ffull-sym-path))
          );cond
    );let
  );defn

(r/defSym2Key KEY_LinesCount :LinesCount)
(r/defSym2Key KEY_PointsTo :PointsTo)
(r/defSym2Key KEY_NonEmptyLinesCount :non_empty_lines_count)
(r/defSym2Key KEY_FlatFile :flatfile)
(r/defSym2Key KEY_isValid :valid?)

(defn parse-flatfile-wannabe-symlink 
"
ie. parses a file like this:
s:\\workspace2012\\emacs-live\\packs\\dev\\lang-pack\\lib\\actionscript-mode
which contains this line(basically without newlines):
../vendor/submodules/actionscript-mode
"
  [flat_file_symlink]
  (with-open [rdr (clojure.java.io/reader flat_file_symlink)]
    (let [
          allLines (line-seq rdr)
          allNonEmptyLines (filter #(not (empty? %)) allLines)
          howManyLines (count allLines)
          howManyNonEmptyLines (count allNonEmptyLines)
          ^String pointsTo (first allLines)
          ]
      {
       (r/getExistingKey KEY_isValid) (and (= 1 howManyLines) (empty? pointsTo))
       (r/getExistingKey KEY_FlatFile) flat_file_symlink
       (r/getExistingKey KEY_LinesCount) howManyLines
       (r/getExistingKey KEY_NonEmptyLinesCount) howManyNonEmptyLines
       (r/getExistingKey KEY_PointsTo) pointsTo
       }
      )
    )
  )

(defn getLinesCount [parsed_ff]
  (r/getRetField parsed_ff KEY_LinesCount)
  )

(defn getPointsTo [parsed_ff]
  (r/getRetField parsed_ff KEY_PointsTo)
  )



(defn transform_flatfile_to_symlink [flat-file]
  (let [
        ^{:tag java.io.File} ffull (.getAbsoluteFile (clojure.java.io/as-file flat-file))
        ^String full-path (.getAbsolutePath ffull)
        zmap (parse-flatfile-wannabe-symlink full-path)
        ;TODO: handle case when symlink is invalid
        howManyLines (getLinesCount zmap)
        ^String sym-to (getPointsTo zmap)
        ]
    
    (cond (not= 1 howManyLines)
      (println "ignoring unexpected symlink format(or probably already" 
               ;FIXME: handle this better somehow ie. symlink to a symlink
               "a symlink to a file and now seeing file's contents): " full-path)
      :else
      (make-symlink ffull sym-to)
      )
    )
  )
  
  
(defn transform_flatfiles_to_symlinks 
  [^java.io.File repo-path
   ^java.lang.String branch]
  
  (println "Transform begins...")
  (let [ret (doall 
              (remove nil? 
                (for [
                      ; the flat file which is supposed to be a symlink to the location which is specified inside its contents
                      rel_symlink (get_all_symlinks_from_repo repo-path branch)
                      ;_ (println (str (.toString (.getAbsolutePath repo-path)) *separator* rel_symlink));repo-path rel_symlink)
                      ;x (println "a")
                      ;_ (println repo-path);XXX: wtf, if I comment out this line, it throws below?!
                      ;^{:tag java.io.File} 
                      ]
                  (let [
                        ^java.io.File abs_symlink 
                        (q/newInstanceOfClass fileClass
                          ;(new java.io.File 
                          (str 
                            (.toString 
                              (.getAbsolutePath repo-path);IllegalArgumentException Don't know how to create ISeq from: java.io.File
                              ) 
                            *separator* 
                            rel_symlink)
                          )
                        ]
                    (cond (.isFile abs_symlink);when core.symlinks=false  all symlinks are just files after checkout
                      (transform_flatfile_to_symlink abs_symlink)
                      :else
                      (let [
                            dir? (.isDirectory abs_symlink)
                            ftype (cond dir?
                                    :directory
                                    :else
                                    :unknown-type ;XXX: this requires changing this code to handle this new type
                                    )
                            ret2 (sorted-map 
                                   :ignored ftype
                                   :what abs_symlink
                                   )
                            ]
                        
                        (println (str "ignoring " 
                                   (cond dir? 
                                     "directory"
                                     :else
                     "unknown-type(not dir not file)"
                     )
                                   ": " abs_symlink))
                        
                        ret2
                        )
                      )
                    )
                  );for
                )
              )
        ]
    (println "Transform ends...")
    ret
    )
  )



