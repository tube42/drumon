DRUM ON
=======

This is a FOSS drum app for Android.

.. raw:: html

    <a href="https://f-droid.org/packages/se.tube42.drum.android"> <img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" alt="Get it on F-Droid" height="80"> </a>


.. image:: extra/screenshot/demo.gif

It all started some years ago when someone showed me an iOS app that was
supposed to be 'intuitive' and 'magical'. I bet myself I could create
something better for Android in just a weekend.

And I did exactly that, it just took a couple of years...


Building
--------

To run this app, you will need Java and Android SDKs.

To build the project and run on desktop

.. code:: shell

    ./gradlew desktop:run

To build for android and upload it to your device

.. code:: shell

    ./gradlew installDebug

Assets
------

Asset sources are found under the extra folder. To compile assets you will need the following tools:

1. "sox" for converting samples
2. ImageMagic for converting PNG images
3. Inkscape for rendering SVG files

That is,

.. code:: shell

    sudo apt-get install sox imagemagick inkscape
    make setup

To build the assets, you should do

.. code:: shell

    make
