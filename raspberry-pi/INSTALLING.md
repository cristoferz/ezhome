# Raspberry Pi Installation
Raspberry Pi is the most recommended server for use with ezHome. That's because Raspberry Pi is smaller and cheaper than 
a normal computer and can easily be installed phisically together with arduino and other electrical parts of the project. 

But, there's no problem to use another kind of hardware for the project. Raspberry is only recommended, but not mandatory. 
This manual focus on the process of Installation of a clean Raspberry Pi, based on Raspbian OS. 

## Requirements
For innital configuration of Raspberry Pi you will need the following:
* HDMI device to use as monitor for Raspberry Pi. Can be a TV or a PC monitor
* A USB Keyboard connected to any USB port of Raspberry Pi
* A network connection for your Pi. Older versions only support cable connections, but new ones can use wifi connection 

## Installing OS
This process is basically follow the instructions on (Raspberry Pi Official Website)[https://www.raspberrypi.org/documentation/installation/installing-images/] 
for installation of Raspbian OS. It's possible also to install NOOBS first and than change to raspbian. The most important is to have
the Raspberry Pi installed with Raspbian.

## Configuring Network
Next step is to configure the network of raspberry. On most environments network is auto-configured (via DHCP), but is necessary
for us to take the assigned IP for future uses. This can be done by issuing the following command:

```
pi@raspberrypi:~ $ ifconfig
eth0      Link encap:Ethernet  HWaddr b8:27:eb:5e:cf:65  
          inet addr:192.168.1.16  Bcast:192.168.1.255  Mask:255.255.255.0
          inet6 addr: fe80::8f35:15ad:b593:a02c/64 Scope:Link
          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1
          RX packets:1308 errors:0 dropped:2 overruns:0 frame:0
          TX packets:384 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:1000 
          RX bytes:229929 (224.5 KiB)  TX bytes:41889 (40.9 KiB)

lo        Link encap:Local Loopback  
          inet addr:127.0.0.1  Mask:255.0.0.0
          inet6 addr: ::1/128 Scope:Host
          UP LOOPBACK RUNNING  MTU:65536  Metric:1
          RX packets:329 errors:0 dropped:0 overruns:0 frame:0
          TX packets:329 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:1 
          RX bytes:31906 (31.1 KiB)  TX bytes:31906 (31.1 KiB)

```

The information that we need is the "inet addr: " for the eth0 interface. On our example is "192.168.1.16".

## Adding ezHome APT Repository and installing ezHome
First step on installation of ezHome is the configuration of APT repository. This will make easier to install 
and update ezHome with newer versions. To do this you need to login on raspberry and type the following commands:
```
echo "deb http://ec2-52-67-166-196.sa-east-1.compute.amazonaws.com/repository/ binary/" | sudo tee -a /etc/apt/sources.list.d/ezhome.list
sudo apt-get update
sudo apt-get install ezhome 
```

The output will be something like the following

```
pi@raspberrypi:~ $ sudo apt-get install ezhome 
Reading package lists... Done
Building dependency tree       
Reading state information... Done
The following extra packages will be installed:
  libpq5 librxtx-java libxi6 libxrender1 libxtst6 oracle-java8-jdk postgresql-9.4 postgresql-client-9.4 postgresql-client-common postgresql-common ssl-cert x11-common
Suggested packages:
  oidentd ident-server locales-all postgresql-doc-9.4 openssl-blacklist
The following NEW packages will be installed:
  ezhome libpq5 librxtx-java libxi6 libxrender1 libxtst6 oracle-java8-jdk postgresql-9.4 postgresql-client-9.4 postgresql-client-common postgresql-common ssl-cert x11-common
0 upgraded, 13 newly installed, 0 to remove and 26 not upgraded.
Need to get 68.2 MB of archives.
After this operation, 196 MB of additional disk space will be used.
Do you want to continue? [Y/n] y
WARNING: The following packages cannot be authenticated!
  ezhome
Install these packages without verification? [y/N] y
```
After that, ezHome libraries will be installed successfully.

Ps.: For new, is normal to see the message

```
WARNING: The following packages cannot be authenticated!
  ezhome
```

That's because ezHome .deb package is not signed (we are working on this...). 

## Innitializing ezHome Database


