#  Licensed to the Apache Software Foundation (ASF) under one or more
#   contributor license agreements.  See the NOTICE file distributed with
#   this work for additional information regarding copyright ownership.
#   The ASF licenses this file to You under the Apache License, Version 2.0
#   (the "License"); you may not use this file except in compliance with
#   the License.  You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.

class c3po_java {
  require mysql_client
  require hdfs_client
  require maven
  require c3po_codebase
  require c3po_mysqldb

  $path="/bin:/usr/bin"

  # a fuller example, including permissions and ownership
  file { '/home/c3po/.m2':
    ensure => 'directory',
    owner  => 'c3po',
    group  => 'hadoop',
    mode   => '0770',
  }
  ->
  file { '/home/c3po/clinical3PO-app-data':
    ensure => 'directory',
    owner  => 'c3po',
    group  => 'hadoop',
    mode   => '0770',
  }
  ->
  file { '/home/c3po/clinical3PO-hadoop-output':
    ensure => 'directory',
    owner  => 'c3po',
    group  => 'hadoop',
    mode   => '0770',
  }
  ->
  file { '/home/c3po/clinical3PO-hadoop-scripts':
    ensure => 'directory',
    owner  => 'c3po',
    group  => 'hadoop',
    mode   => '0770',
  }
  ->
  file { '/home/c3po/clinical3PO-logs':
    ensure => 'directory',
    owner  => 'c3po',
    group  => 'hadoop',
    mode   => '0770',
  }
  ->
  exec {"c3po-cdw-mkdir":
    command => "hadoop fs -mkdir -p /data/C3PO_CDW/care_site /data/C3PO_CDW/cohort /data/C3PO_CDW/condition_era /data/C3PO_CDW/death /data/C3PO_CDW/ drug_cost /data/C3PO_CDW/drug_era /data/C3PO_CDW/durg_exposure /data/C3PO_CDW/location /data/C3PO_CDW/measurement /data/C3PO_CDW/note /data/C3PO_CDW/observation /data/C3PO_CDW/observation_period /data/C3PO_CDW/organization /data/C3PO_CDW/payer_plan_period /data/C3PO_CDW/person /data/C3PO_CDW/procedure_cost /data/C3PO_CDW/procedure_occurrence /data/C3PO_CDW/provider /data/C3PO_CDW/specimen /data/C3PO_CDW/visit_occurrence",
    unless => "hdfs dfs -test -e /data/C3PO_CDW",
    path => "$path",
    user => "hdfs",
  }
  ->
  exec {"c3po-cdw-chown-1st":
    command => "hdfs dfs -chown -R c3po:hadoop /data",
    path => "$path",
    user => "hdfs",
  }
  ->
  exec {"c3po-cdw-chmod-1st":
    command => "hdfs dfs -chmod -R 775 /data",
    path => "$path",
    user => "hdfs",
  }
  ->
  file { "/tmp/concept.txt":
    ensure => file,
    source => dos2unix("puppet:///files/datasource/physionet-challenge-2012/headeronly/concept.txt"),
    owner => c3po,
    group => hadoop,
  }
  ->
  exec { "copyhdfsdata_concept":
    path => $path,
    unless => "hdfs dfs -test -e /data/C3PO_CDW/concept.txt",
    command => "hdfs dfs -copyFromLocal /tmp/concept.txt /data/C3PO_CDW/concept.txt",
    user => "c3po",
  }
  ->
  file { "/tmp/person-merged.txt":
    ensure => file,
    source => dos2unix("puppet:///files/datasource/physionet-challenge-2012/headeronly/person-merged.txt"),
    owner => c3po,
    group => hadoop,
  }
  ->
  exec { "copyhdfsdata_person":
    path => $path,
    unless => "hdfs dfs -test -e /data/C3PO_CDW/person-merged.txt",
    command => "hdfs dfs -copyFromLocal /tmp/person-merged.txt /data/C3PO_CDW/person-merged.txt",
    user => "c3po",
  }
  ->
  file { "/tmp/death-merged.txt":
    ensure => file,
    source => dos2unix("puppet:///files/datasource/physionet-challenge-2012/headeronly/death-merged.txt"),
    owner => c3po,
    group => hadoop,
  }
  ->
  exec { "copyhdfsdata_death":
    path => $path,
    unless => "hdfs dfs -test -e /data/C3PO_CDW/death-merged.txt",
    command => "hdfs dfs -copyFromLocal /tmp/death-merged.txt /data/C3PO_CDW/death-merged.txt",
    user => "c3po",
  }
  ->
  file { "/tmp/observation-merged.txt":
    ensure => file,
    source => dos2unix("puppet:///files/datasource/physionet-challenge-2012/headeronly/observation-merged.txt"),
    owner => c3po,
    group => hadoop,
  }
  ->
  exec { "copyhdfsdata_obs":
    path => $path,
    unless => "hdfs dfs -test -e /data/C3PO_CDW/observation-merged.txt",
    command => "hdfs dfs -copyFromLocal /tmp/observation-merged.txt /data/C3PO_CDW/observation-merged.txt",
    user => "c3po",
  }
  ->
  exec {"c3po-cdw-chown-2nd":
    command => "hdfs dfs -chown -R c3po:hadoop /data",
    path => "$path",
    user => "hdfs",
  }
  ->
  exec {"c3po-cdw-chmod-2nd":
    command => "hdfs dfs -chmod -R 775 /data",
    path => "$path",
    user => "hdfs",
  }
  ->
  file { 'sourcemlflex':
    path  => '/home/c3po/ML-Flex',
    ensure  =>  present,
    source  =>  '/home/c3po/codebase/Clinical3PO-Platform/dev/ML-Flex',
    owner     => 'c3po',
    group   =>    'hadoop',
  }
  ->
  exec { "chmodc3pomlflex":
    path => $path,
    command => "sudo chmod ug+rw /home/c3po/ML-Flex",
  }
  ->
  file { "/home/c3po/.m2/settings.xml":
    ensure => file,
    owner => c3po,
    group => 'hadoop',
    mode  => 0770,
    content => regsubst(dos2unix(template('c3po_java/settingsxml.erb')), '\n\n\<?xml' ,'<?xml', 'EMG'),
  }
  ->
  file { "/tmp/mvnc3po-java.sh":
    ensure => file,
    owner => 'c3po',
    mode => 0776,
    content => dos2unix(template('c3po_java/mvnc3po-java.erb')),
  }
  ->
  exec { "c3po-java-mvn":
    path => $path,
    user => 'c3po',
    provider => 'shell',
    command => "/tmp/mvnc3po-java.sh",
  }

}
