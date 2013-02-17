(ns hermes.stuff.hermes1-test
  (:require [hermes.stuff.hermesutil-test :as hut] :reload-all)
  (:require [runtime.q :as q] :reload-all)
  (:require [datest1.ret :as r])
  )


(binding [
          hut/*conf* (r/getExistingKey KEY_InMemoryGraph)
          ]
  (q/gotests)
  )
