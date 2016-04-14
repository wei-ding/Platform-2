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

class hive_db {
  $PATH = "/bin:/usr/bin"
  
  #package {"mysql57":
  #  provider=>rpm,
  #    ensure=>installed,
  #    install_options => ['--nodeps'],
  #    source=>"http://dev.mysql.com/get/mysql57-community-release-el6-7.noarch.rpm",
  #  }
  #  ->
  package { 'mysql-server':
    ensure => installed,
  }
  ->
  service { 'mysqld':
    ensure => running,
    enable => true,
  }
  ->
  #exec { "secure-mysqld":
  #  command => "mysql_secure_installation < files/secure-mysql.txt",
  #    path => "${PATH}",
  #    cwd => "/vagrant/modules/hive_db",
  #  }
  #  ->
  file { "/tmp/init-root-pwd.sh":
    ensure => file,
    owner => root,
    mode => 0700,
    content => template('hive_db/init-root-pwd.sh'),
  }
  ->
  exec { "c3po-mysqldb-init":
    path => $path,
    command => "/tmp/init-root-pwd.sh",
  }
  ->
  exec { "add-remote-root":
    command => "/vagrant/modules/hive_db/files/add-remote-root.sh",
    path => $PATH,
  }
  ->
  exec { "create-hivedb":
    command => "mysql -u root --password=PWc3po < files/setup-hive.txt",
    path => "${PATH}",
    cwd => "/vagrant/modules/hive_db",
    creates => "/var/lib/mysql/hive",
  }
}
