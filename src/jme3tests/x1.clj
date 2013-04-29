;code from: https://gist.github.com/weavejester/5484183

(ns jme3test1.x1
  (:import 
    com.jme3.app.SimpleApplication
    com.jme3.material.Material
    com.jme3.math.Vector3f
    com.jme3.scene.Geometry
    com.jme3.scene.shape.Box
    com.jme3.texture.Texture))

(defn application
  "Create an jMonkeyEngine application."
  [{:keys [init]}]
  (proxy [SimpleApplication] []
    (simpleInitApp [] (init this))))

(defn load-texture
  "Load a texture into the application."
  [app asset-path]
  (.loadTexture (.getAssetManager app) asset-path))

(defn box
  "Create a box between two 3D vectors."
  [[x1 y1 z1] [x2 y2 z2]]
  (Box. (Vector3f. x1 y1 z1) (Vector3f. x2 y2 z2)))

(defn material
  "Create a material for the application."
  [app {:keys [definition color-map]}]
  (let [mat (Material. (.getAssetManager app) definition)]
    (when color-map (.setTexture mat "ColorMap" color-map))
    mat))

(defn unshaded-material
  "Create a simple, textured, unshaded material."
  [app texture]
  (material app {:definition "Common/MatDefs/Misc/Unshaded.j3md"
                 :color-map texture}))

(defn geometry
  "Create a geometry for the application."
  [{:keys [name mesh material]}]
  (let [geom (Geometry. name)]
    (when mesh (.setMesh geom mesh))
    (when material (.setMaterial geom material))
    geom))

(defn demo-init [app]
  (let [texture (load-texture app "Interface/Logo/Monkey.jpg")]
    (.attachChild
      (.getRootNode app)
      (geometry {:name "Box"
                 :mesh (box [0 0 0] [1 1 1])
                 :material (unshaded-material app texture)}))))

(defn demo-app []
  (.start (application {:init demo-init})))

(demo-app)
