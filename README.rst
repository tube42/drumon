DRUM ON
=======

.. image:: https://travis-ci.org/tube42/drumon.svg
    :target: https://travis-ci.org/tube42/drumon

This is a FOSS drum app for Android.

.. image:: http://tube42.github.io/drum/img/img00.png

Downloads:

* `Google Play <https://play.google.com/store/apps/details?id=se.tube42.drum.android>`_
* `F-Droid <https://f-droid.org/repository/browse/?fdid=se.tube42.drum.android>`_
* `APK <https://tube42.github.io/drum/bin/drum-release.apk>`_ (signed with Play key)


Building
--------

To build this app, you will need

1. java, ant, android SDK and all that
2. The rest (libgdx, marm, tweeny, ks, ...) is downloaded when you do setup
3. Assets, a default set is downloaded during setup

To setup the project and download required libraries, binaries (all FOSS) and assets

.. code:: shell

    ant setup

To build the project and run on desktop

.. code:: shell

    ant run

To build for android and upload it to your device

.. code:: shell

    ant debug install

Assets
------

Asset sources are found under the extra folder. To compile assets you will need the following tools:

1. "sox" for converting samples
2. ImageMagic for converting PNG images
3. Inkscape for rendering SVG files

That is,

.. code:: shell

    sudo apt-get install sox imagemagick inkscape

    
To build the assets, you should do

.. code:: shell

    make

Note that "ant setup" will overwrite your generated assets.
