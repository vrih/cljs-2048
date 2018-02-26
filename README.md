# cljs-2048

An implementation of 2048 in clojurescript/reagent that runs entirely in the browser.

## Overview

This is a quick thrown together in a weekend version of 2048. It uses Reagent and Figwheel for rapid development.

## Setup

To get an interactive development environment run:

    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).
This will auto compile and send all changes to the browser.

To clean all compiled files:

    lein clean

To create a production build run:

    lein do clean, cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL. 


## Running tests

    lein doo phantom test

