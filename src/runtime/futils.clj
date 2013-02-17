(ns runtime.futils
  (:require [runtime.q :as q] :reload-all)
  (:require [datest1.ret :as r])
  )

(r/defSym2Key KEY_FileIsDirectory :Directory)
(r/defSym2Key KEY_FileIsFile :File)



(defn isDir? [file]
  (let [^java.io.File ffile (clojure.java.io/as-file file)]
    (.isDirectory ffile)
    )
  )

(defn isFile? [file]
  (let [^java.io.File ffile (clojure.java.io/as-file file)]
    (.isFile ffile)
    )
  )

(defn typeOfFile [file]
  (let [^java.io.File ffile (clojure.java.io/as-file file)]
    (cond
      (isDir? ffile) {(r/getExistingKey KEY_FileIsDirectory) ffile}
      (isFile? ffile) {(r/getExistingKey KEY_FileIsFile) ffile}
      )
    )
  )

(defn delete-file
"
an implementation that returns the true/false status
which clojure.java.io/delete-file doesn't do(tested in 1.5.0-RC14)
thanks to Sean Corfield, I'm made aware that
(clojure.java.io/delete-file \"file\" :not-deleted)
can be used to return :not-deleted when failed, however I still don't agree with this
optimization-based-implementation, so I still want to get true/false from this, even though
I could make a new function on top of the original delete-file function, because the original
does have it's use, ie. it allows me to return whatever value i want(except false/nil) when deletion fails,
tho doesn't allow me to return whatever I want when it succeeds, let's just say that
I wouldn't wanna implement it like that ever (not consciously anyway).
"
  [f & [silently]]
  (let [
        ff (clojure.java.io/as-file f)
        typee (typeOfFile ff)
        ret (.delete ff)
        _ (q/log :debug (str "deleted=`" ret "` " typee))
        ]
    (cond (or ret silently)
      ret
      :else
      (throw (java.io.IOException. (str "Couldn't delete " f)))
      )
    )
  )

(defn deleteFolderRecursively
  "input: file or string"
  [folderToDelete & [silent]]
  (let [^java.io.File fdir (clojure.java.io/as-file folderToDelete)
        everyFileAtThisLevel (map #(clojure.java.io/file fdir %)
                               (seq (.list fdir)))
        ]
    (doseq [^java.io.File eachFD everyFileAtThisLevel]
      (cond (.isDirectory eachFD)
        (deleteFolderRecursively eachFD silent)
        :else
        (delete-file eachFD silent)
        )
      )
    (delete-file fdir silent)
    )
  )

(defn getUniqueFile 
"
pass nil to in-path  if the default temporary-file directory is to be used
 The default temporary-file directory is specified by the system property java.io.tmpdir
 aka (System/getProperty \"java.io.tmpdir\")
returns: java.io.File
"
[& [in-path prefix suffix]]
  (java.io.File/createTempFile
    (or prefix "unq")
    suffix ; may be nil, in which case the suffix ".tmp" will be used
    (clojure.java.io/as-file in-path)
    )
  )

(defn getUniqueFolder
  [& [in-path prefix suffix]]
  ;(delay 
    (try
      (let [^java.io.File uniqueFile (getUniqueFile in-path prefix suffix)
            ]
        (q/assumedTrue (.exists uniqueFile) (.isFile uniqueFile))
        (q/assumedFalse (.isDirectory uniqueFile))
        (delete-file uniqueFile false)
        (q/assumedFalse (.exists uniqueFile))
        (.mkdirs uniqueFile);XXX: just in case %tmp% folder/parents don't exit
        (q/assumedTrue (.exists uniqueFile) (.isDirectory uniqueFile))
        uniqueFile
        )
      (catch Throwable t 
        (do 
          (throw t);
          ;(rethro t) ;CompilerException java.lang.UnsupportedOperationException: Can't eval locals, compiling:(runtime\q.clj:1070:11) 
          )
        )
      )
   ; )
  )

(q/deftest test_asfile
  (let [x (q/newInstanceOfClass java.io.File "s")
        y (clojure.java.io/as-file x)
        ]
    (q/is (= x y))
    )
  )

;last lines
(q/show_state)
(q/gotests)