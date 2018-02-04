(ns cljs-2048.test-core
  (:require [cljs-2048.core :as sut]
            [cljs.test :as t :include-macros true])
  )


(t/deftest test-shift-to-right
  (t/testing "Move all the way to right"
    (t/is (= [0 0 0 2] (sut/push-right [2 0 0 0]))))
  (t/testing "Move one step to the right"
    (t/is (= [0 0 0 2] (sut/push-right [0 0 2 0]))))
  (t/testing "Don't move already correct"
    (t/is (= [0 0 2 2] (sut/push-right [0 0 2 2]))))
  (t/testing "Move spaces out of the middle"
    (t/is (= [0 0 2 2] (sut/push-right [2 0 2 0])))))


(t/deftest test-merging
  (t/testing "Merge one pair"
    (t/is (= [0 0 0 4] (sut/merge-right [0 0 2 2]))))
  (t/testing "Merge paie in middle"
    (t/is (= [0 0 4 4] (sut/merge-right [0 2 2 4]))))
  (t/testing "Merge pair to gap"
    (t/is (= [0 2 0 8] (sut/merge-right [0 2 4 4])))))

(t/deftest test-row-move
  (t/testing "Merge and push"
    (t/is (= [0 0 2 8]) (sut/row-move [0 2 4 4]))))

(t/deftest test-move-board
  (t/testing "Reconcile board to the right"
    (t/is (= (sut/move-board-right [[0 0 2 2]
                              [0 2 0 0]
                              [2 0 0 0]
                              [2 2 0 0]])
             [[0 0 0 4]
              [0 0 0 2]
              [0 0 0 2]
              [0 0 0 4]]))))


(t/deftest test-move-board-down
  (t/testing "Reconcile move board down"
    (t/is (= (sut/move-board-down [[0 0 2 2]
                               [0 2 0 0]
                               [2 0 0 0]
                               [2 2 0 0]])
             [[0 0 0 0]
              [0 0 0 0]
              [0 0 0 0]
              [4 4 2 2]]))))

(t/deftest test-move-board-left
  (t/testing "Reconcile move board left"
    (t/is 
     (= (sut/move-board-left [[0 0 2 2]
                          [0 2 0 0]
                          [2 0 0 0]
                          [2 2 0 0]])
        [[4 0 0 0]
         [2 0 0 0]
         [2 0 0 0]
         [4 0 0 0]]))))

(t/deftest test-move-board-up
  (t/testing "Reconcile move borad up"
    (t/is 
     (= (sut/move-board-up [[0 0 2 2]
                        [0 2 0 0]
                        [2 0 0 0]
                        [2 2 0 0]])
        [[4 4 2 2]
         [0 0 0 0]
         [0 0 0 0]
         [0 0 0 0]]))))

(t/deftest test-victory
  (t/testing "Victory on 2048"
    (t/is (true? (sut/victory
                  [[0 0 0 0]
                   [0 2 0 4]
                   [2048 0 0 0]
                   [0 0 0 0]]))))
  (t/testing "No Victory witout 2048"
    (t/is (not (true? (sut/victory
                       [[0 0 0 0]
                        [0 2 0 4]]))))))

(t/deftest test-game
  (t/testing "Full play"
    (t/is (true?
           (->> [[0 0 0 1024]
                [0 1024 0 0]]
                sut/move-board-right
                sut/move-board-down
                sut/victory)))))

(t/run-tests)
