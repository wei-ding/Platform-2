Notes on the Clinical3PO HPC Cluster Software Stack
-----
Provisioning system

The Clinical3PO HPC cluster includes a few different types of computers:

* A master node: runs MPICH scheduler, monitoring
* One or more compute nodes which accomplish the actual computation.
 
All the nodes include:
OpenJDK 1.8, nginx, CentOS 6.7 64-bit VM, EPEL repo, Puppet 4, no SELinux, no firewall, nginx, OpenJDK 1.8, 

MPICH - Library used for writing/running parallel applications
Slurm - Queuing system for scheduling jobs on the cluster and handling load balancing.
NFS - Network File System for sharing folders across the cluster.
SciPy - Scientific algorithms library for Python (compiled against ATLAS for 8-cpu instance types)
NumPy - Fast array and numerical library for Python (compiled against ATLAS for 8-cpu instance types)
iPython - An advanced interactive shell for Python.
Python-2.7 
Hadoop 2.7.2


Dependencies
=====

Clinical3PO HPC Cluster has a minimal set of dependencies listed below:
Amazon EC2 account.
Python2.6+
Paramiko 
Boto

Usage
=====

Installing 
-----------------
