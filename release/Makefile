all: build-jar build-site build-deb

build-jar:
	ant -S -f /home/cristofer/git/ezhome/controller/ezHomeLib -Dnb.internal.action.name=rebuild clean jar
	ant -S -f /home/cristofer/git/ezhome/controller/ezHomeController -Dnb.internal.action.name=rebuild clean jar
	ant -S -f /home/cristofer/git/ezhome/controller/ezHomeStandardLib -Dnb.internal.action.name=rebuild clean jar
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
	cp ../controller/ezHomeStandardLib/dist/ezHomeStandardLib.jar build/opt/ezhome/extLibs/
build-site:
	cd ../local-site/frontend/ && ng build --prod
copy-site:
	cp -R ../local-site/frontend/dist build/opt/ezhome/local-site
build-deb: clean-build create-build copy-skeleton copy-jar copy-site
	mkdir -p binary
	dpkg-deb --build build binary
index-repo:
	dpkg-scanpackages binary /dev/null | gzip -9c > binary/Packages.gz
send-remote: index-repo
	scp -i ~/git/desktop-configs/scripts/CRISTOFER.pem binary/* ec2-user@ec2-52-67-166-196.sa-east-1.compute.amazonaws.com:/var/www/html/repository/binary/

release: all send-remote

	
