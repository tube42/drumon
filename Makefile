#
# Makefile is used for assets, because we are too cool for ant
#

INKSCAPE=inkscape

MARM = java -jar libs/bin/marm_app.jar hiero=libs/bin/hiero

##

all: icons marm samples


icons:
	mkdir -p res/drawable-ldpi
	mkdir -p res/drawable-mdpi
	mkdir -p res/drawable-hdpi
	mkdir -p res/drawable-xhdpi
	mkdir -p res/drawable-xxhdpi
	$(INKSCAPE) -z extra/icon.svg  -w 36 -h 36 -e res/drawable-ldpi/ic_launcher.png
	$(INKSCAPE) -z extra/icon.svg  -w 48 -h 48 -e res/drawable-mdpi/ic_launcher.png
	$(INKSCAPE) -z extra/icon.svg  -w 72 -h 72 -e res/drawable-hdpi/ic_launcher.png
	$(INKSCAPE) -z extra/icon.svg  -w 96 -h 96 -e res/drawable-xhdpi/ic_launcher.png
	$(INKSCAPE) -z extra/icon.svg  -w 144 -h 144 -e res/drawable-xhdpi/ic_launcher.png

samples:
	make -C extra/samples clean all

marm:
	rm -rf assets/1
	rm -rf assets/2
	rm -rf assets/4
	$(MARM) resize extra/textures assets

##

clean:
	rm -rf assets/1
	rm -rf assets/2
	rm -rf assets/4
	rm -rf assets/samples
	rm -rf $(CLEAN_ADD)
	rm -rf res/drawable-*/ic_launcher.png
