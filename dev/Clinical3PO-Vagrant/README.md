Notes
-----
HDP 2.3.4, OpenJDK 1.8, nginx, CentOS 6.7 64-bit VM, 400G disk, EPEL repo, Puppet 3, Tomcat, no SELinux, no firewall,


Usage
=====

Installing VirtualBOX
-----------------

Centos:

cd /etc/yum.repos.d
sudo wget http://download.virtualbox.org/virtualbox/rpm/rhel/virtualbox.repo



sudo yum --enablerepo rpmforge install dkms
or:
sudo yum --enablerepo epel install dkms 

sudo yum groupinstall "Development Tools"
sudo yum install kernel-devel

sudo yum install VirtualBox-5.0

sudo usermod -a -G vboxusers username


Windows:

Download VirtualBox 5.0.14: 
http://download.virtualbox.org/virtualbox/5.0.14/VirtualBox-5.0.14-105127-Win.exe
After VirtualBox installation finishes you will have to restart your computer. 


Install VBox on Windows. Using Windows VirtualBox Extension pack:

Download this:
http://download.virtualbox.org/virtualbox/5.0.14/Oracle_VM_VirtualBox_Extension_Pack-5.0.14.vbox-extpack

From VirtualBox main window, go to File->Preferences. This will open VirtualBox Preferences window. 
Navigate to Extension, Next, click on the small down arrow on the right side of the window. 
Navigate and select the Extension Pack you downloaded in the previous step. You will be asked to confirm VirtualBox Extension Pack setup.
Click “Install” to complete VirtualBox Extension Pack installation. You will have to reboot your host effect for the changes to take effect. 


Mac:

http://download.virtualbox.org/virtualbox/5.0.14/VirtualBox-5.0.14-105127-OSX.dmg

Download this:
http://download.virtualbox.org/virtualbox/5.0.14/Oracle_VM_VirtualBox_Extension_Pack-5.0.14.vbox-extpack

From VirtualBox main window, go to File->Preferences. This will open VirtualBox Preferences window. 
Navigate to Extension, Next, click on the small down arrow on the right side of the window. 
Navigate and select the Extension Pack you downloaded in the previous step. You will be asked to confirm VirtualBox Extension Pack setup.
Click “Install” to complete VirtualBox Extension Pack installation. You will have to reboot your host effect for the changes to take effect. 


Installing Vagrant
-----------------

Centos:
cd ~/usr/local/src
wget https://releases.hashicorp.com/vagrant/1.8.1/vagrant_1.8.1_x86_64.rpm
sudo rpm -i vagrant_1.8.1_x86_64.rpm 

## Install Vagrant plugins: 
gem install ffi
##Vagrant Hosts:
vagrant plugin install vagrant-hosts 
##Vagrant Cachier (for Repo caching):
vagrant plugin install vagrant-cachier

Windows:


https://releases.hashicorp.com/vagrant/1.8.1/vagrant_1.8.1.msi



Mac:

https://releases.hashicorp.com/vagrant/1.8.1/vagrant_1.8.1.dmg


Prepare Clinical3PO Cluster Planning file
----------------------

Example for 3 nodes

3 nodes without ambari:

under hdp_cluster_palnning:

```
3nodes-noambari.setup

{
  "domain": "clinical3po.org",
  "realm": "CLINICAL3PO.ORG",
  "security": false,
  "vm_mem": 5096,
  "server_mem": 4096,
  "client_mem": 3072,
  "clients" : [ "hdfs", "hive", "oozie", "pig", "tez", "yarn", "zk" ],
  "nodes": [
    { "hostname": "clinical3po-nn", "ip": "240.0.0.11",
      "roles": [ "kdc", "hive-db", "hive-meta", "nn", "yarn", "zk" ] },
    { "hostname": "clinical3po-dn", "ip": "240.0.0.12", "roles": [ "oozie", "slave", "zk" ] },
    { "hostname": "clinical3po-gw", "ip": "240.0.0.10", "roles": [  "nginx", "tomcat", "maven", "client", "c3po" ] }
  ]
}
```

Example for 5 nodes

5 nodes without ambari:

under hdp_cluster_palnning:

```
5nodes-noambari.setup

{
  "domain": "clinical3po.org",
  "realm": "CLINICAL3PO.ORG",
  "security": false,
  "vm_mem": 5096,
  "server_mem": 4096,
  "client_mem": 3172,
  "clients" : [ "hdfs", "hive", "oozie", "pig", "tez", "yarn", "zk" ],
  "nodes": [
    { "hostname": "clinical3po-nn", "ip": "240.0.0.11",
      "roles": [ "kdc", "hive-db", "hive-meta", "nn", "yarn", "zk" ] },
    { "hostname": "clinical3po-dn1", "ip": "240.0.0.12", "roles": [ "oozie", "slave", "zk" ] },
    { "hostname": "clinical3po-dn2", "ip": "240.0.0.13", "roles": [ "slave", "zk" ] },
    { "hostname": "clinical3po-dn3", "ip": "240.0.0.14", "roles": [ "slave", "zk" ] },
    { "hostname": "clinical3po-gw", "ip": "240.0.0.10", "roles": [  "nginx", "tomcat", "maven", "client", "c3po" ] }
  ]
}

```

Running VirtualBox Via Vagrant
--------------

Start:
vagrant up

Access via ssh:
```
vagrant ssh clinical3po-nn
```
or
```
vagrant ssh clinical3po-gw
```

linux user: 
    c3po
linux password: 
    %CannonStreetHospital%


Stop VirtualBox Via Vagrant
--------------
Stop:
stop all:
```
vagrant halt
```
or stop one:
```
vagrant halt clinical3po-gw
```

Delete VirtualBox Via Vagrant
--------------
Destroy it ( this command stops the running virutalbox machines and this command deletes all the files too:
delete all:
```
vagrant destroy
```
or delete one:
```
vagrant destroy clinical3po-gw
```

Users
--------------

Tomcat:
user:admin
password:PWc3po

mysql:
user:root
password:PWc3po
user:c3po
password:PWc3po


Remote Access to Clinical3PO
login to your VirtalBox Host computer, start firefox and type this url:
127.0.0.1:8888
or 
use other computer, start firefox, type this url:
xxx:8888
"xxx" should be your VirtalBox host computer name or ip address.

