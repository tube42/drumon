DRUM ON
=======

This is a FOSS drum app for Android. 

.. image:: http://tube42.github.io/drumon/img/img00.png


To download, go to https://play.google.com/store/apps/details?id=se.tube42.drum.android



Building
--------
To build this app, you will need

# java, ant, android SDK and all that
# sox for converting samples
# imagemagic for converting PNG images
# inkscape for rendering SVG files
# hiero (from gdx nighly builds) for font generation
# my own tool "marm" for content manegement

Furthermore, these tools must be accessible from command line via scripts or aliases.


To setup the project and download required lins

# ant setup

To build the assets, you should do

# make

To build the project and run on desktop

# ant run


To build for android and upload it to your device

# ant debug install
