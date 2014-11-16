
# script to convert all art

INKSCAPE="inkscape"

TARGETS += \
	res/drawable-ldpi/ic_launcher.png \
	res/drawable-mdpi/ic_launcher.png \
	res/drawable-hdpi/ic_launcher.png \
	res/drawable-xhdpi/ic_launcher.png \
	res/drawable-xxhdpi/ic_launcher.png
#	res/drawable-xxxhdpi/ic_launcher.png

TARGETS += marm samples


##

all: $(TARGETS)




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
	marm resize extra/fonts assets	
	marm resize extra/textures assets
	rm -rf assets/.temp
##

clean:
	rm -rf assets/1
	rm -rf assets/2
	rm -rf assets/4
	rm -rf $(CLEAN_ADD)
	
