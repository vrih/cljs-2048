(ns runners.doo
  (:require [doo.runner :refer-macros [doo-all-tests]]
                                      [cljs-2048.test-core]))

(doo-all-tests #"(cljs-2048)\..*-test")

