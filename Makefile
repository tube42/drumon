#
# Makefile is used for assets, because we are too cool for ant
#

INKSCAPE=inkscape


MARM = java -jar submodules/marm/marm_app.jar hiero=libs/bin/hiero -v

##

all: icons marm samples


icons:
	mkdir -p android/res/drawable-ldpi
	mkdir -p android/res/drawable-mdpi
	mkdir -p android/res/drawable-hdpi
	mkdir -p android/res/drawable-xhdpi
	mkdir -p android/res/drawable-xxhdpi
	mkdir -p android/res/drawable-xxxhdpi
	$(INKSCAPE) -z extra/icon.svg  -w 36 -h 36 -e android/res/drawable-ldpi/ic_launcher.png
	$(INKSCAPE) -z extra/icon.svg  -w 48 -h 48 -e android/res/drawable-mdpi/ic_launcher.png
	$(INKSCAPE) -z extra/icon.svg  -w 72 -h 72 -e android/res/drawable-hdpi/ic_launcher.png
	$(INKSCAPE) -z extra/icon.svg  -w 96 -h 96 -e android/res/drawable-xhdpi/ic_launcher.png
	$(INKSCAPE) -z extra/icon.svg  -w 144 -h 144 -e android/res/drawable-xxhdpi/ic_launcher.png
	$(INKSCAPE) -z extra/icon.svg  -w 192 -h 192 -e android/res/drawable-xxxhdpi/ic_launcher.png

samples:
	make -C extra/samples clean all

marm:
	rm -rf android/assets/1
	rm -rf android/assets/2
	rm -rf android/assets/4
	$(MARM) resize extra/textures android/assets

##

clean:
	rm -rf assets/1
	rm -rf assets/2
	rm -rf assets/4
	rm -rf assets/samples
	rm -rf $(CLEAN_ADD)
	rm -rf res/drawable-*/ic_launcher.png

setup:
	cd submodules/marm && ant dist
