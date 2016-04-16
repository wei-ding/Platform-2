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

class tomcat {

  require jdk

  $path="/bin:/usr/bin:/sbin:/usr/sbin:/usr/local/bin"

  exec { 'installtomcat':
    path => $path,
    cwd => "/tmp",
    user => 'root',
    command => "wget http://archive.apache.org/dist/tomcat/tomcat-7/v7.0.67/bin/apache-tomcat-7.0.67.tar.gz && tar xzvf apache-tomcat-7.0.67.tar.gz -C /opt/ && ln -s /opt/apache-tomcat-7.0.67 /opt/apache-tomcat",
  }
  ->
  file { "/etc/profile.d/tomcat.sh":
    ensure => file,
    mode => 0765,
    content => regsubst(template('tomcat/tomcat-env.erb'), '\r\n', "\n", 'EMG'),
  }
  ->
  exec { "chmodtomcat":
    path => $path,
    command => "chmod +x /etc/profile.d/tomcat.sh",
  }
  ->
  file { "/opt/apache-tomcat-7.0.67/conf/tomcat-users.xml":
    ensure => file,
    owner => c3po,
    group => 'hadoop',
    mode => 0760,
    content => dos2unix(template('tomcat/tomcat-users.erb')),
  }
  ->
  exec { "chowntomcat":
    path => $path,
    user => 'root',
    command => "chown -R c3po:hadoop /opt/apache-tomcat-7.0.67 && chmod -R ug+rw /opt/apache-tomcat-7.0.67",
  }
  ->
  file { "/etc/init.d/tomcat":
    ensure => file,
    source => "puppet:///files/etc/init.d/tomcat",
    owner => root,
    group => hadoop,
  }
  ->
  service { "tomcat":
    ensure => running,
    enable => true,
  }

}
