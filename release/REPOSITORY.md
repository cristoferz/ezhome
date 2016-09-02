# Using ezHome APT repository
To enable and install ezhome from our APT repository, follow this steps:

Add repository do sources.list:

    sudo echo "deb http://ezhome.cjz.com.br/apt stable main" > /etc/apt/sources.list.d/ezhome.list

Add GPG key to your trusted keys:

    cd /etc/apt/trusted.gpg.d/
    sudo wget http://ezhome.cjz.com.br/apt/ezcorp.gpg


Update the apt-get:

    sudo apt-get update

Install ezhome Controller software:

    sudo apt-get install ezhome


A service named ezhome will be created, started and added to automatic initialization on boot.