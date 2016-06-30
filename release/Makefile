build:

clean-build:
	rm build -Rf
create-build:
	mkdir -p build
copy-skeleton:
	chmod 755 skeleton/DEBIAN/postinst
	chmod 755 skeleton/DEBIAN/preinst
	cp skeleton/* -Rp build/
copy-jar:
	cp ../controller/ezHomeController/dist/ezHomeController.jar build/opt/ezhome/
	cp -R ../controller/ezHomeController/dist/lib build/opt/ezhome/
build-deb: clean-build create-build copy-skeleton copy-jar
	mkdir -p dist
	dpkg-deb --build build dist
release: build-deb 
all: release
