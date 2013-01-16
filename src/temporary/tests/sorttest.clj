(ns temporary.tests.sorttest)
;sources from here: http://rosettacode.org/wiki/Sorting_algorithms/Quicksort#Clojure
;and here: https://clojurefun.wordpress.com/2012/08/29/a-fast-quicksort-in-clojure/
;and here: http://java.dzone.com/articles/benchmarking-scala-against


;public static void quickSort(int[] array, int left, int right) {
;    if (right <= left) {
;        return;
;    }
;    int pivot = array[right];
;    int p = left;
;    int i = left;
;    while (i < right) {
;        if (array[i] < pivot) {
;            if (p != i) {
;                int tmp = array[p];
;                array[p] = array[i];
;                array[i] = tmp;
;            }
;            p += 1;
;        }
;        i += 1;
;    }
;    array[right] = array[p];
;    array[p] = pivot;
;    quickSort(array, left, p - 1);
;    quickSort(array, p + 1, right);
;}

(def howmany ;10000)
  1000000)

(def lowrange1 1)
(def highrange1 (+ lowrange1 (* 20 howmany)))

(defn randrange [lowrange highrange] 
  (+ (rand-int (- (+ 1 highrange) lowrange)) lowrange))

(defn randrange1 [] (randrange lowrange1 highrange1))

(def rands (repeatedly randrange1))

(defn qsort [L]
  (if (empty? L) 
      '()
      (let [[pivot & L2] L]
           (lazy-cat (qsort (for [y L2 :when (<  y pivot)] y))
                     (list pivot)
                     (qsort (for [y L2 :when (>= y pivot)] y))))))

(defn qsort2 [[pvt & rs]]
  (if pvt
    `(~@(qsort (filter #(<  % pvt) rs))
      ~pvt 
      ~@(qsort (filter #(>= % pvt) rs)))))

(defn qsort3 [[pivot & xs]]
  (when pivot
    (let [smaller #(< % pivot)]
      (lazy-cat (qsort (filter smaller xs))
		[pivot]
		(qsort (remove smaller xs))))))

(defn qsort4 [[pvt :as coll]]
  (when pvt
    (let [{left -1 mid 0 right 1} (group-by #(compare % pvt) coll)]
      (lazy-cat (qsort3 left) mid (qsort3 right)))))

(defn qsort5 [[pivot :as coll]]
  (when pivot
    (lazy-cat (qsort (filter #(< % pivot) coll))
              (filter #{pivot} coll)
              (qsort (filter #(> % pivot) coll)))))

;(def quicksort qsort)

;(time (quicksort (take howmany rands)))

(set! *unchecked-math* true)

(defmacro swap [a i j]
 `(let [a# ~a
        i# ~i
        j# ~j
        t# (aget a# i#)]
    (aset a# i# (aget a# j#))
    (aset a# j# t#)))

(defmacro apartition [a pivot i j]
 `(let [pivot# ~pivot]
    (loop [i# ~i
           j# ~j]
      (if (<= i# j#)
        (let [v# (aget ~a i#)]
          (if (< v# pivot#)
            (recur (inc i#) j#)
            (do
              (when (< i# j#)
                (aset ~a i# (aget ~a j#))
                (aset ~a j# v#))
              (recur i# (dec j#)))))
        i#))))

(defn qsort6
  ([^longs a]
    (qsort a 0 (alength a)))
  ([^longs a ^long lo ^long hi]
    (let [lo (int lo)
          hi (int hi)]
      (when
        (< (inc lo) hi)
        (let [pivot (aget a lo)
              split (dec (apartition a pivot (inc lo) (dec hi)))]
          (when (> split lo) (swap a lo split))
            (qsort a lo split)
            (qsort a (inc split) hi)))
      a)))


(def allqs [qsort qsort2 qsort3 qsort4 qsort5])

(defn do_all [listoffunx forarray]
  (loop [
         f1 (first listoffunx)
         rest1 (rest listoffunx)
;         currentIndex 
         ]
    (time (doall (f1 forarray)))
    (cond (not-empty rest1)
      (recur (first rest1) (rest rest1))
      )
  )
)

;(do_all allqs (take howmany rands))

(def xs (let [rnd (java.util.Random.)] 
  (long-array (repeatedly howmany #(.nextLong rnd)))))

(dotimes [i 10]
  (let [ys (long-array xs)]
    (time (last (qsort ys)))))

