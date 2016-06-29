build:
	ant -f /home/cristofer/git/ezhome/controller/ezHomeController -Dnb.internal.action.name=rebuild clean jar;
build-deb:
	mkdir -p build/opt/ezHome/
	mkdir -p build/DEBIAN
	cp -R ezHomeController/dist/*.jar build/opt/ezHome/
	cp -R ezHomeController/dist/lib build/opt/ezHome/
	cp -R DEBIAN/* build/DEBIAN/
	cd build
	dpkg-deb --build build releases
all:
	echo "teste"   
