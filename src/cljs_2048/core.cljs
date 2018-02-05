(ns cljs-2048.core
  (:require
   [reagent.core :as reagent]))

(enable-console-print!)

(defn generate-board []
  [[0 0 0 0]
   [0 0 0 0]
   [0 0 2 0]
   [0 0 0 0]])

(defn get-score [old new]
  (let [a 
        (frequencies (flatten old))
        b (frequencies (flatten new))]
  (reduce +
          (filter #(> % 0)
                  (map (fn [x] (/ (* (first x)
                                    (- (last x)
                                       (get a (first x) 0)))
                                 2))
                       b)))))

(defonce app-state (reagent/atom {:board (generate-board) :score 0 :victory false :defeat false}))
 
;; define your app data so that it doesn't get over-written on reload

(defn get-empty-spaces [board]
  (filter some?
          (mapcat identity
                  (map-indexed 
                   (fn [i x]
                     (map-indexed
                      (fn [j y] (when (= 0 y)
                                 (list i j)))
                      x))
                   board))))

(defn add-piece
  "count the number of 0s on the board and add at least one number"
  [board]
  (let [spaces (get-empty-spaces board)]
    (assoc-in (vec (map vec board)) (rand-nth spaces) 2)))

(defn push-right [row]
  (let [tail (filter #(> % 0) row)
        shift (- (count row) (count tail))
        padding (map (fn [x] 0) (range 0 shift))]
    (concat padding tail)))

(defn merge-right
  "from the right - merge 2 adjacent cells, setting the outermost one to 0"
  [row]
  (loop [[x & xs] (reverse row)
         merged false
         output []]
    (cond
      (nil? x) (reverse output)
      (true? merged) (recur xs false (concat output [0]))
      (= x (first xs)) (recur xs true (concat output [(* 2 x)]))
      :else (recur xs false (concat output [x])))))

(defn row-move [row]
  (-> row
      push-right
      merge-right
      push-right))

(defn move-board-right [board] (map #(row-move %) board))

(defn move-board-left [board]
  (->> board
       (map reverse)
       move-board-right
       (map reverse)))  

(defn move-board-up [board]
  (->> board
       (apply map list)
       (map reverse)
       move-board-right
       (map reverse)
       (apply map list)))

(defn move-board-down [board]
  (->> board
       (apply map list)
       move-board-right
       (apply map list)))

(defn victory? [board] (some true? (map (fn [x] (some #(= 2048 %) x)) board)))

(defn defeat? [board]
  (cond
    (> get-empty-spaces 0) false
    (and 
     (= (move-board-up board) board)
     (= (move-board-down board) board)
     (= (move-board-right board) board)
     (= (move-board-left board) board)) true
    :else false))

(defn move
  ([direction]
   (move direction (:board @app-state)))
  ([direction board]
   (let [b 
         (case direction
           :right (move-board-right board)
           :left (move-board-left board)
           :up (move-board-up board)
           :down (move-board-down board))]
     (cond
       (victory? b) (do (swap! app-state assoc :victory true)
                        (swap! app-state update :score + (get-score board b))
                        b)
       (defeat? b) (do (swap! app-state assoc :defeat true)
                       (swap! app-state update :score + (get-score board b))
                       b)
       (not (= b board)) (do
                           (swap! app-state update :score + (/ (get-score board b) 2 ))
                           (add-piece b))
       :else board))))

(defn render-cell [row]
  (map (fn [x] [:span {:class (str "cell n" x)} (if (= x 0) " " x)]) row))
 
(defn render-board [board] 
  (map (fn [x] [:div {:class "row"}(render-cell x)]) board))

(def codename
  {37 :left
   38 :up
   39 :right
   40 :down
   32 :space})

(def action
  {:right #(move :right)
   :left #(move :left)
   :up #(move :up)
   :down #(move :down)})

(defn handle-keydown [e]
  (let [b (:board @app-state)]
    (when-let [f (action (codename (.-keyCode e)))]
      (.preventDefault e)
      (.log js/console (f b))
      (swap! app-state assoc :board (f)))))


(defn reset []
  (swap! app-state assoc :board (generate-board) :score 0 :victory false :defeat false)
  )

(defn page [state]
  (let [b (:board @state)]
 
    [:div
     [:nav {:class "navbar navbar-default"} 2048 [:span (:score @state)][:span {:on-click #(reset)} "Reset"]]
     [:div {:id "board"} 
           (render-board b)]
     (when (:victory @state)
       [:div "Victory"])
     (when (:defeat @state)
       [:div {:id "defeat"} "Defeat"])]))
  
(defn on-js-reload []
  (reagent/render [page app-state] (.getElementById js/document "app")))
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)

(defn dev-setup []
  (when ^boolean js/goog.DEBUG
    (enable-console-print!)
    (println "dev mode")))
 
(defn ^:export main []
  (dev-setup)
  (on-js-reload)
 (.addEventListener js/document "keydown" handle-keydown)
  )


;(swap! app-state assoc :board (generate-board))

;(assoc-in [[0 0 2 2][2 2 0 0]] (list 0 1) 2)

;(add-piece [[0 0 2 2][2 2 0 0]])

 
