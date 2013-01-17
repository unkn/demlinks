; Copyright (c) AtKaaZ and contributors.
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.

;change(?) git windows symlinks
(ns cgws.core
  (:import java.io.File)
  (:require [datest1.ret :as ret] :reload-all)
  )

;;TODO: give meaningful names to the let variables

;(def xmxm java.io.File);well this blows

(declare transform-flatfiles-to-symlinks)

(def repo-path (new java.io.File "s:\\workspace2012\\emacs-live\\"))

(def branch "master")

(defn -main [& args] 
  (comment (println "this is meant to transform git checkout symlinks(which are just flat files due to core.symlinks=false)\n"
           "into real symlinks.\n"
           "But this is meant only for windows because git doesn't know how to do that in windows."
           ))
  (println "passed args:" args)
  (time (transform-flatfiles-to-symlinks repo-path))
(ret/get :key {:map 1})
  )


;(System/getenv)

(defn start-process 
  ;  .waitFor
  [^String cmd 
   ^{:tag "[Ljava.lang.String;"} env ;array of String 
   ^File workdir]
  (let [^Runtime rt (Runtime/getRuntime)
        ^Process process (.exec rt cmd env workdir)]
    process
    )
  )

(defn exec-cmd
  "env or workdir can be nil to signify inherit from parent process"
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
  (exec-cmd (start-process command nil workdir))
  )



(def gitlstree_cmd (str "git ls-tree -r" " \"" branch "\""));branch must be quoted since it can contain at least "&"

(def symlink-filemode "120000")

(defn get_all_symlinks_from_repo 
  "returns a list of all symlinks inside the specified git repository 
   as relative paths to the provided repo-path"
  [repo-path]
  (with-open [input (shellify gitlstree_cmd repo-path)]
    (let [lazylines (line-seq input)
          ]
      (disj 
        (set
          (map #(
                  let [ss (clojure.string/split ^String % #"\s")
                       filemode (first ss)
                       ]
                  (if (.equals filemode symlink-filemode)
                    (last ss)
                    nil
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
            (throw (new Exception "not implemented"))
            )
          
          isDir?
          (let [
                deleted? (println ".delete" ffull)]
            (if (not deleted?)
              (println "cannot delete:" ffull)
              (let [process (start-process 
                              (str "cmd.exe /c mklink /d \"" full-path "\" \"" sym-to "\"") 
                              nil 
                              fparent-folder
                              )
                    _ (.waitFor process)
                    exitcode (.exitValue process)
                    what (str "a symlink to a folder: " full-path " -> " sym-to)
                    ]
                
                (if (= 0 exitcode)
                  (println "made" what)
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

(def KEY_lines_count :lines_count)
(def KEY_points_to :points_to)

(defn parse-flatfile-wannabe-symlink 
  "ie. parses a file like this:
s:\\workspace2012\\emacs-live\\packs\\dev\\lang-pack\\lib\\actionscript-mode
which contains this line(basically without newlines):
../vendor/submodules/actionscript-mode

"
  [flat_file_symlink]
  (with-open [rdr (clojure.java.io/reader flat_file_symlink)]
    (let [
          all_lines (line-seq rdr)
          all_non_empty_lines (filter #(not (empty? %)) all_lines)
          how_many_lines (count all_lines)
          how_many_nonEmpty_lines (count all_non_empty_lines)
          ^String points_to (first all_lines)
          ]
      {:valid? (and (= 1 how_many_lines) (empty? points_to))
       :flatfile flat_file_symlink 
       KEY_lines_count how_many_lines
       :non_empty_lines_count how_many_nonEmpty_lines
       KEY_points_to points_to
       }
      )
    )
  )

(defn get_lines_count [parsed_ff]
  {:pre [(map? parsed_ff)]
   }
  (let [found (find parsed_ff KEY_lines_count)
        _ (assert found "you tried to access a 'field' that didn't exist, you should've checked before!")
        ;ie. non nil, it's [key value] vector
        ]
    (second found)
    )
  )

(defn get_points_to [parsed_ff]
  {:pre [(map? parsed_ff)]
   }
  (let [found (find parsed_ff KEY_points_to)
        _ (assert found "you tried to access a 'field' that didn't exist, you should've checked before!")
        ;ie. non nil, it's [key value] vector
        ]
    (second found)
    )
  )

(defn transform-flatfile-to-symlink [flat-file]
  (let [
        ^{:tag java.io.File} ffull (.getAbsoluteFile (clojure.java.io/as-file flat-file))
        ^String full-path (.getAbsolutePath ffull)
        zmap (parse-flatfile-wannabe-symlink full-path)
        ;TODO: handle case when symlink is invalid
        how-many-lines (get_lines_count zmap)
        ^String sym-to (get_points_to zmap)
        ]
    
    (if (not= 1 how-many-lines)
      (println "ignoring unexpected symlink format(or probably already" 
               ;FIXME: handle this better somehow ie. symlink to a symlink
               "a symlink to a file and now seeing file's contents): " full-path)
      (make-symlink ffull sym-to)
      )
    )
  )
  
  
(defn transform-flatfiles-to-symlinks [repo-path]
  (println "Transform begins...")
  (doseq [
          ; the flat file which is supposed to be a symlink to the location which is specified inside its contents
          rel-symlink (get_all_symlinks_from_repo repo-path)
          ]
    (let [;TODO: move this as :let inside doseq
          ^{:tag java.io.File} abs-symlink (new java.io.File (str repo-path (java.io.File/separator) rel-symlink))
          ]
      (if (.isFile abs-symlink);when core.symlinks=false  all symlinks are just files after checkout
        (transform-flatfile-to-symlink abs-symlink)
        (println (str "ignoring " 
                      (if (.isDirectory abs-symlink) 
                        "directory"
                        "unknown-type(not dir not file)";this requires changing this code to handle this new type
                        )
                      ": " abs-symlink))
        )
      
      )
    )
  (println "Transform ends...")
  )



