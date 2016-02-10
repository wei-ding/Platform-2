Packer Centos template
==============

Packer template to make VirtualBox images.

Notes
-----
CentOS 64-bit VM, 400G disk, EPEL repo, Puppet 4, no SELinux, no firewall,


Usage
=====

Installing VirtualBOX
-----------------

cd /etc/yum.repos.d
sudo wget http://download.virtualbox.org/virtualbox/rpm/rhel/virtualbox.repo



sudo yum --enablerepo rpmforge install dkms
or:
sudo yum --enablerepo epel install dkms 

sudo yum groupinstall "Development Tools"
sudo yum install kernel-devel

sudo yum install VirtualBox-5.0

sudo usermod -a -G vboxusers username

Installing Vagrant
-----------------

cd ~/usr/local/src
wget https://releases.hashicorp.com/vagrant/1.8.1/vagrant_1.8.1_x86_64.rpm
sudo rpm -i vagrant_1.8.1_x86_64.rpm 


Get Clinical3PO CentOS 6.7 iso file
-----------------
wget http://c3po-develop.tk/downloads/clinical3po-vagrant-centos-6.7.box


Prepare Clinical3PO Cluster Planning file
----------------------

Example var file for 3 nodes

vars-centos7.json

```json
{
  "iso_url": "/home/frey/Downloads/CentOS-7-x86_64-Minimal.iso",
  "iso_checksum": "f90e4d28fa377669b2db16cbcb451fcb9a89d2460e3645993e30e137ac37d284",
  "iso_checksum_type": "sha256",
  "guest_additions_path": "VBoxGuestAdditions.iso",
  "redhat_release": "7.2"
}

```
Example var file for CentOS 6.x:

vars-centos6.json

```json
{
  "iso_url": "/home/frey/Downloads/CentOS-6.7-x86_64-minimal.iso",
  "iso_checksum": "9d3fec5897be6b3fed4d3dda80b8fa7bb62c616bbfd4bdcd27295ca9b764f498",
  "iso_checksum_type": "sha256",
  "guest_additions_path": "VBoxGuestAdditions.iso",
  "redhat_release": "6.7"
}

}

```

Running Packer
--------------

`$ packer build -var-file=vars-centos6.json vagrant-centos.json` 
