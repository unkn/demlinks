(defproject demlinks "0.0.1-SNAPSHOT"
  :description "FIXME: [planning] write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "same as Clojure"
            }
  :dependencies [
                 ;[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojure "1.6.0-master-SNAPSHOT"]
                 [com.datomic/datomic-free "0.8.3803"] ;database
                 ;[midje "1.5-alpha7"];don't wanna use this because it compiles all .clj files even if it doesn't need to
                 [org.clojure/tools.trace "0.7.5"]
                 [prismatic/plumbing "0.0.1"]
                 ;[org.flatland/useful "0.9.0"];for defalias, atm., nomore due to ns reload and ns-unmap-like behaviour causing IllegalStateException deftest already refers to: #'runtime.q/deftest in namespace: runtime.q_test  clojure.lang.Namespace.warnOrFailOnReplace (Namespace.java:88)
                 [quil "1.6.0"] ;2D/3D drawing
                 [robert/hooke "1.3.0"] ;hook functions
                 [clojurewerkz/titanium "1.0.0-alpha1"] ;graphdb
                 [com.taoensso/timbre "1.5.2"] ;logging/profiling, https://github.com/ptaoussanis/timbre
                 ;[slingshot "0.10.3"] ;for exceptions try/catch
                 [hermes "0.2.6"]
                 [com.tinkerpop.blueprints/blueprints-core "2.2.0"]
                 [com.thinkaurelius.titan/titan "0.2.0"]
                 [seesaw "1.4.3"]
                 [backtick "0.3.0-SNAPSHOT"] ;ie. (template {:a (+ 1 2) :b ~(+ 1 3)}) will eval only ~ parts
                 [clj-ns-browser "1.3.1"]
                 [repetition-hunter "0.2.0"]
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
  :java-cmd "c:\\program files\\java\\jdk1.7.0_17\\bin\\java.exe"
  
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
  :bootclasspath false;false is needed here for JME3 specified in :resource-paths below; else it will fail with ClassNotFoundException com.jme3.app.SimpleApplication  java.net.URLClassLoader$1.run (URLClassLoader.java:366)

  :resource-paths ["resources", "../jme3_engine/dist/lib/*"] ;needs eclipse project present: jme3_engine from trunk/engine ie. https://code.google.com/p/jmonkeyengine/source/browse/#svn%2Ftrunk%2Fengine
    
  :plugins [
            ;[lein-midje "3.0-alpha3"];don't wanna use this because it compiles all .clj files even if it doesn't need to
            ]
  
  
  :repl-options [
                 :init (do (require 'clojure.repl) (println "here we are in" *ns*))
                 ;; If nREPL takes too long to load it may timeout,
                 :timeout 10000
                 :caught clj-stacktrace.repl/pst+
                 ] 

  :repositories {"sonatype-oss-public"
               "https://oss.sonatype.org/content/groups/public/"}  
  )