DRUM ON
=======

This is a FOSS drum app for Android. 

.. image:: http://tube42.github.io/drumon/img/img00.png


To download, go to https://play.google.com/store/apps/details?id=se.tube42.drum.android



Building
--------

To build this app, you will need

1. java, ant, android SDK and all that
2. The rest (libgdx, marm, tweeny, ks, ...) is donwloaded when you do setup
3. Assets, a default set is downloaded during setup

To setup the project and download required libraries, binaries (all FOSS) and assets

* ant setup

To build the project and run on desktop

* ant run

To build for android and upload it to your device

* ant debug install

Assets
------

Asset sources are found under the extra folder. To compile assets you will need the following tools:

1. "sox" for converting samples
2. ImageMagic for converting PNG images
3. Inkscape for rendering SVG files

To build the assets, you should do

* make

Note that "ant setup" will overwrite your generated assets.


Continuous Whatever...
----------------------

Automatic builds on TravisCI: 

.. image:: https://travis-ci.org/tube42/Drum-On.svg
    :target: https://travis-ci.org/tube42/Drum-On

We cant compile our assets on a CI server yet, so beware that artifacts may use old assets...
