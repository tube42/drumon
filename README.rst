DRUM ON
=======

This is a FOSS drum app for Android. 

.. image:: http://tube42.github.io/drumon/img/img00.png


To download, go to https://play.google.com/store/apps/details?id=se.tube42.drum.android



Building
--------
To build this app, you will need

1. java, ant, android SDK and all that
2. "sox" for converting samples
3. ImageMagic for converting PNG images
4. Inkscape for rendering SVG files
5. The rest (libgdx, marm, tweeny, ks, ...) is donwloaded when you do setup


To setup the project and download required libs and binaries (all FOSS)

* ant setup

To build the assets, you should do

* make

To build the project and run on desktop

* ant run


To build for android and upload it to your device

* ant debug install
