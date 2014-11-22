DRUM ON
=======

This is a FOSS drum app for Android. 

.. image:: http://tube42.github.io/drumon/img/img00.png


To download, go to https://play.google.com/store/apps/details?id=se.tube42.drum.android



Building
--------
To build this app, you will need

1. java, ant, android SDK and all that
2. sox for converting samples
3. imagemagic for converting PNG images
4. inkscape for rendering SVG files
5. hiero (from gdx nighly builds) for font generation
6. my own tool "marm" for content manegement
7. All these tools must be accessible from command line via scripts or aliases.


To setup the project and download required libs

* ant setup

To build the assets, you should do

* make

To build the project and run on desktop

* ant run


To build for android and upload it to your device

* ant debug install
