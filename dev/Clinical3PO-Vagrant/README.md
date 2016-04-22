# Use Vagrant to Set Up a virtual Clinical3PO Cluster

The Clincial3PO Cluster includes HDP and many of its sub-projects:.

  - Hortonworks HDP 2.4.0
    -   Apache Hadoop 2.7.1
    -   Apache Hive 1.2.1
    -   Apache HBase 1.1.2
    -   Apache ZooKeeper 3.4.6
    -   Apache Oozie 4.2.0
    -   Apache Knox 0.6.0
    -   Apache Pig 0.15.0
    -   Apache Tez 0.7.0
  - OpenJDK 1.8
  - MySQL 5.1
  - Tomcat 7.0.67
  - Maven 3.3.9
  - Nginx
  - mlflex
  - REDExHadoop
  - Clinical3PO Platform

## Instruction: Deploying the cluster
   First please install [Virtual Box](https://www.virtualbox.org/wiki/Downloads) (free) and [Vagrant](https://www.vagrantup.com/downloads.html) (free) for your host os platform. 

1. Install VirtualBOX

    1. Disable Hyper-V

        If you have hyper-V installed on Windows, Microsoft’s hypervisor (hyper-V) takes over and won’t let VirtualBox or VMware access the Intel VT-x hardware. You’ll see error messages about Intel VT-x being unavabilable, even though it’s enabled on your computer.

        *  In the elevated PowerShell window, copy and paste the command below, press Enter.
        ```
        Disable-WindowsOptionalFeature -Online -FeatureName Microsoft-Hyper-V-All
        ```
        or 
        ```
        dism.exe /Online /Disable-Feature:Microsoft-Hyper-V-All
        ```
        *  Type Y and press Enter when prompted to restart the computer.

    2. Verify the Virtualization extensions are enabled in BIOS on Your Host Computer

        Many of the steps below may vary depending on your motherboard, processor type, and OEM. Refer to your system's accompanying documentation for the correct information on configuring your system. Please look for an option named “Intel Virtualization Technology,” “Intel VT-x,” “Intel Virtual Technology,” “Virtualization Extensions,” “Vanderpool,” or something similar. 

        * Reboot the computer and open the system's BIOS menu. This can usually be done by pressing the delete key, the F1 key or Alt and F4 keys depending on the system.
        * Open the Settings or Configuration submenu, look for an option named “Intel Virtualization Technology,” “Intel VT-x,” “Intel Virtual Technology,” “Virtualization Extensions,” “Vanderpool,” or something similar. Please Find it and Enable it.
        * Enable Intel VTd or AMD IOMMU, if the options are available. Intel VTd and AMD IOMMU are used for PCI passthrough.
        * Select Save & Exit.

    3. Installing VirtualBOX

       - Centos:

            ```
            cd /etc/yum.repos.d
            sudo wget http://download.virtualbox.org/virtualbox/rpm/rhel/virtualbox.repo
            ```

            Install dkms
            ```sh
            sudo yum --enablerepo rpmforge install dkms
            ```
            or:
            ```sh
            sudo yum --enablerepo epel install dkms 
            ```
            Install VirutalBox
            ```sh
            sudo yum groupinstall "Development Tools"
            sudo yum install kernel-devel
            sudo yum install VirtualBox-5.0
            ```
            Add the current user into the "vboxusers" group
            ```
            sudo usermod -a -G vboxusers #Your User Name is Here"
            ```
            Reboot your linux os
            ```sh
            sudo reboot
            ```
            

        - Windows:

            - Download [Virtual Box](https://www.virtualbox.org/wiki/Downloads) 5.0.14 or or higher version: 
                - [VirtualBox 5.0.14 Download Link](http://download.virtualbox.org/virtualbox/5.0.14/VirtualBox-5.0.14-105127-Win.exe)
                - After VirtualBox installation finishes you will have to restart your computer. ( Or reboot later after finishing the Vagrant installation)

            - (Optional) If you want even more features on VirtualBox, like Support for a virtual USB 2.0/3.0 controller (EHCI/xHCI), VirtualBox RDP: support for proprietary remote connection protocol developed by Microsoft and Citrix. Please Install VirtualBox Extension pack on your windows host os.
                - Download [VirutalBox Extension Pack](https://www.virtualbox.org/wiki/Downloads) for VirtualBox 5.0.14 or higher version to match your VirutalBox:
            [VirtualBox Extension Pack 5.0.14 Download Link](http://download.virtualbox.org/virtualbox/5.0.14/Oracle_VM_VirtualBox_Extension_Pack-5.0.14.vbox-extpack)
            
                -  From VirtualBox main window, go to File->Preferences. This will open VirtualBox Preferences window. 
                ```
                    Navigate to Extension, Next, click on the small down arrow on the right side of the window. 
                    ```
                    ```
                    Navigate and select the Extension Pack you downloaded in the previous step. You will be asked to confirm VirtualBox Extension Pack setup.
                    ```
                    ```
                Click “Install” to complete VirtualBox Extension Pack installation. You will have to reboot your host effect for the changes to take effect. 
                    ```

        - Mac:

            - Download [Virtual Box](https://www.virtualbox.org/wiki/Downloads) 5.0.14 or or higher version: 
                - [VirtualBox 5.0.14 Download Link](http://download.virtualbox.org/virtualbox/5.0.14/VirtualBox-5.0.14-105127-OSX.dmg)
        

### 2. Install [Vagrant](https://www.vagrantup.com/downloads.html)
----------------------
- Centos:
  - Install [Vagrant](https://www.vagrantup.com/downloads.html) 
    ```
    cd ~/usr/local/src
    wget https://releases.hashicorp.com/vagrant/1.8.1/vagrant_1.8.1_x86_64.rpm
    sudo rpm -i vagrant_1.8.1_x86_64.rpm 
    ``` 

  - Install [Vagrant](https://www.vagrantup.com/downloads.html) plugins: 
     ```
     gem install ffi
     ```
  
- Windows:

  - Download [Vagrant](https://www.vagrantup.com/downloads.html) 1.8.1 or higher version:
    - [Vagrant 1.8.1 Download link ](https://releases.hashicorp.com/vagrant/1.8.1/vagrant_1.8.1.msi)
  - After Vagrant installation finishes you will have to restart your computer. 

- Mac:

  -   Download [Vagrant](https://www.vagrantup.com/downloads.html) 1.8.1 or higher version:
    - [Vagrant 1.8.1 Download link](https://releases.hashicorp.com/vagrant/1.8.1/vagrant_1.8.1.dmg)

### 3. Bring the Clinical3PO cluster up.
----------------------

1. Download Clinical3PO Source code

   Go to [Clinical3PO](www.clinical3po.org) [Platform GitHub webpage](https://github.com/Clinical3PO/Platform), click [Download ZIP](https://github.com/Clinical3PO/Platform/archive/master.zip) Button, the download file name is Platform-master.zip, unzip it, then go to Platform-master/dev/Clinical3PO-Vagrant/ folder.


2. Prepare Clinical3PO Cluster Planning file

    All of your Clinical3PO Cluster Planning setup files are under Platform-master/dev/Clinical3PO-Vagrant/hdp_cluster_planning/ folder.

    The default planning file is 2nodes-noambari.setup, if you want to change it to 3 nodes or more, please modify 
    the Platform-master/dev/Clinical3PO-Vagrant/hdp_cluster_planning/Vagrantfile:

    You will find it in line 20 to line 22

    ```
    # HDP Cluster Planning setup search path:
    $planning_path = ["current.planning",
    "hdp_cluster_planning/2nodes-noambari.setup"]
    ```

    Replace "2nodes-noambari.setup" to other planning file name if you want to try install more data nodes in your host computer:

    - Preconfigured Clinical3PO planning setup file for 3 nodes without Ambari:
            
      Notes:
            
      > Those preconfigured Clinical3PO Planning setup files are json files, we can mofidy them before we bring the clinical3po cluster up.
            
      > This will set up 3 machines - Each of them will have tow CPUs and 5096MB of RAM, If this is too much for your machine, please feel free to adjust this setup json file as you needs.
            
      > domain and realm: you can change it to your domain name, or leave it as it is.
            
      > security: if change false to true, Vagrant will install knox on gateway node. 
            
      > vm_mem: the memory size to use for VirutalBox machine. 
            
      > server_mem: the memory size to use for Java virtual machine (JVM) on NameNode.
            
      > client_mem: the memory size to use for Java virtual machine (JVM) on DataNode and Gateway mechine.
            
      > "clients": We will add Spark, and Accumulo later.
            
      > "nodes": Name node must have "nn", and "zk", DataNodes must have "slave" and "zk". We can setup the "hive-db", "hive-meta", "oozie", and "yarn" to Namenode or datanode. our Clinical3PO Gateway must has [  "nginx", "tomcat", "maven", "client", "c3po" ], we can change the hostname and ip address, but first three octets of ip address must use the same format.
            
      File name: 3nodes-noambari.setup

      ```
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

    - Preconfigured Clinical3PO planning setup file for 5 nodes without Ambari:

      File name: 5nodes-noambari.setupExample

      ```
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

3. Starting the cluster

   -  go to Platform-master/dev/Clinical3PO-Vagrant/ folder Via Shell.

   - Windows 

     Open Powershell window, run:
     ```
     cd Downloads\Platform.master\dev\Clinical3PO-Vagrant
     ```
   - Mac or Linux

     Open Terminal window, run:
     ```
     cd ~/Downloads/Platform.master/dev/Clinical3PO-Vagrant
     ```
   - Run Varant up to start the Clinical3PO cluster

     ```
     vagrant up
     ```

4. Stopping the cluster

   -  go to Platform-master/dev/Clinical3PO-Vagrant/ folder Via Shell.
   
   - Winsows 
   
     Open Powershell window, run:
     ```
     cd Downloads\Platform.master\dev\Clinical3PO-Vagrant
     ```
     - Mac or Linux
     Open Terminal window, run:
     ```
     cd ~/Downloads/Platform.master/dev/Clinical3PO-Vagrant
     ```
     - ###### Run Varant halt to stop the Clinical3PO cluster
     ```
     vagrant halt
     ```

5. Delete the cluster

   -  go to Platform-master/dev/Clinical3PO-Vagrant/ folder Via Shell.
   
   - Winsows 

     Open Powershell window, run:

     ```
     cd Downloads\Platform.master\dev\Clinical3PO-Vagrant
     ```

   - Mac or Linux

     Open Terminal window, run:

     ```
     cd ~/Downloads/Platform.master/dev/Clinical3PO-Vagrant
     ```

   - Run Varant destroy to stop the Clinical3PO cluster

     ```
     vagrant destroy
     ```

## Interacting with the cluster

1. Access the Clinical3PO cluster machines via ssh:

   > notes:
        
   > All the commands are running in Powershell, or Terminal if the host machine is Linux or Mac.
        
   > The working directory must be Platform-master/dev/Clinical3PO-Vagrant/hdp_cluster_planning/.
        
   > The ssh password is    %CannonStreetHospital%
        
   - SSH to Namenode:

     ```
     vagrant ssh clinical3po-nn
     ```

   - SSH to Datanode1:

     ```
     vagrant ssh clinical3po-dn1
     ```
   - SSH to Clinical3PO Gateway:

     ```
     vagrant ssh clinical3po-gw
     ```
        
2. Web interface

   You can access all services of the cluster with your firefox or IE.

   - [namenode ui](http://127.0.0.1:50070/dfshealth.html)
        
   - [resourcemanager ui](http://127.0.0.1:8088/cluster)
        
   - [job history server](http://127.0.0.1:19888/jobhistory)
        
   - [Clinical3PO Stack Web Entry](http://127.0.0.1:8888)
        
3. User and Password

   1. linux system: 
        
      > Notes:

      > run command: passwd to change the default password after you login if you like.

      ```
      User: c3po
      Password: %CannonStreetHospital%
      ```
            
   2. Tomcat:         

      ```
      user:admin
      password:PWc3po
      ```
   3. Mysql:         

      ```
      user:root
      password:PWc3po
      user:c3po
      password:PWc3po
      ```

## Version

0.2

## Tech

Clinical3PO Vagrant uses a number of open source projects to work properly:

* [Vagrant] - 
* [VirutalBox] - 
* [HDP] - 

And of course Clinical3PO Vagrant itself is open source with a [public repository][Clinical3PO]
 on GitHub.

## Todos

 - Write Troubleshooting
 - Add HPC Cluster
 - Hive Datawarehouse
 - Add Accumulo support
 - Add Spark support
 - Add ETL support
 - Add Hue support

## License
----

[The Apache License](http://www.apache.org/licenses/LICENSE-2.0)
