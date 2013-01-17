(defproject demlinks "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "same as Clojure"
            }
  :dependencies [
                 [org.clojure/clojure "1.5.0-RC2"]
                 [com.datomic/datomic-free "0.8.3731"]
                 [midje "1.5-alpha7"]
                 [org.clojure/tools.trace "0.7.5"]
                 ]
  
  ;; Emit warnings on all reflection calls.
  :warn-on-reflection true
  
  ;; Set this in order to only use the :repositories you list below.
;  :omit-default-repositories true
  
  ;; Override location of the local maven repository. Relative to project root.
;  :local-repo "local-m2"
  
  ;; If you'd rather use a different directory structure, you can set these.
  ;; Paths that contain "inputs" are vectors, "outputs" are strings.
  ;this seems to have no effect in eclipse+ccw
;  :source-paths ["src" "src/main/clojure"]
;  :java-source-paths ["src/main/java"] ; Java source is stored separately.
;  :test-paths ["test" "src/test/clojure"]
;  :resource-paths ["src/main/resource"] ; non-code files included
;  :native-path "src/native"        ; where to extract native dependencies  
  :compile-path "target/classes"   ; for .class files
  :target-path "target/"           ; where to place the project's jar file
;  :jar-name "sample.jar"           ; name of the jar produced by 'lein jar'
;  :uberjar-name "sample-standalone.jar" ; as above for uberjar
  
  ;; Options to pass to java compiler for java source,
  ;; exactly the same as command line arguments to javac
  :javac-options ["-target" "1.7" "-source" "1.7" "-Xlint:-options" "-g:source,lines" "-encoding" "utf8"]
  
  ;; You can set JVM-level options here.
  :jvm-opts ["-Xmx1g" "-ea"]
  
  ;;project JVM
  ;;wonder how this works on *nix boxes
  :java-cmd "c:\\program files\\java\\jdk1.7.0_09\\bin\\java.exe"
  
  ;;required lein version
  :min-lein-version "2.0.0"
  
  ;; Leave the contents of :source-paths out of jars (for AOT projects)
  :omit-source false
  
  
    ;; Control the context in which your project code is evaluated.
  ;; Defaults to :subprocess, but can also be :leiningen (for plugins)
  ;; or :classloader (experimental) to avoid starting a subprocess.
  :eval-in :subprocess
    
  ;; Enable bootclasspath optimization. This improves boot time but interferes
  ;; with using things like pomegranate at runtime and using Clojure 1.2.
  :bootclasspath true
    
  :plugins [
            [lein-midje "3.0-alpha3"]
            ]
  
  :repl-options [
                 :init (do (require 'clojure.repl) (println "here we are in" *ns*))
                 ;; If nREPL takes too long to load it may timeout,
                 :timeout 10000
                 :caught clj-stacktrace.repl/pst+
                 ] 
  
  )