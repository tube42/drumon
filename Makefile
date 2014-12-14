#
# Makefile is used for assets, because we are too cool for ant
#

INKSCAPE=inkscape

MARM = java -jar libs/bin/marm_app.jar hiero=libs/bin/hiero

##

all: marm samples \
	res/drawable-ldpi/ic_launcher.png \
	res/drawable-mdpi/ic_launcher.png \
	res/drawable-hdpi/ic_launcher.png \
	res/drawable-xhdpi/ic_launcher.png \
	res/drawable-xxhdpi/ic_launcher.png
#	res/drawable-xxxhdpi/ic_launcher.png



# icon
res/drawable-ldpi/ic_launcher.png: extra/icon.svg
	$(INKSCAPE) -z extra/icon.svg  -w 36 -h 36 -e $@
	
res/drawable-mdpi/ic_launcher.png: extra/icon.svg
	$(INKSCAPE) -z extra/icon.svg  -w 48 -h 48 -e $@

res/drawable-hdpi/ic_launcher.png: extra/icon.svg
	$(INKSCAPE) -z extra/icon.svg  -w 72 -h 72 -e $@
	
res/drawable-xhdpi/ic_launcher.png: extra/icon.svg
	$(INKSCAPE) -z extra/icon.svg  -w 96 -h 96 -e $@

res/drawable-xxhdpi/ic_launcher.png: extra/icon.svg
	$(INKSCAPE) -z extra/icon.svg  -w 144 -h 144 -e $@

#res/drawable-xxxhdpi/ic_launcher.png: extra/icon.svg
#	$(INKSCAPE) -z extra/icon.svg  -w 192 -h 192 -e $@



##
samples:
	make -C extra/samples clean all

marm:
	rm -rf assets/1
	rm -rf assets/2
	rm -rf assets/4
	$(MARM) resize extra/fonts assets
	$(MARM) resize extra/textures assets
	rm -rf assets/.temp


##

clean:
	rm -rf assets/1
	rm -rf assets/2
	rm -rf assets/4
	rm -rf assets/samples	
	rm -rf $(CLEAN_ADD)
	
