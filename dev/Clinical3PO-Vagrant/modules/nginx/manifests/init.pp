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

class nginx {

  $path="/bin:/usr/bin:/sbin:/usr/sbin:/usr/local/bin"

  package { "nginx":
    ensure => installed,
  }
  ->
  service { 'nginx':
    ensure => running,
    enable => true,
  }
  ->
  file { "/etc/nginx/conf.d/default.conf":
    ensure => "file",
    content => template('nginx/defaultconf.erb'),
  }
  ->
  exec {"usermod_nginx":
    path => "$path",
    #unless => "grep -q 'hadoop\\S*c3po' /etc/group",
    unless => "getent group nginx | cut -d: -f4 | grep -s c3po",
    command => "usermod -aG nginx c3po",
    require => User['c3po'],
  }
  ->
  # a fuller example, including permissions and ownership
  file { '/var/www':
    ensure => 'directory',
    owner  => 'nginx',
    group  => 'nginx',
    mode   => '0770',
  }
  ->
  file { 'nginxwww':
   path  => '/var/www/html',
   ensure  =>  present,
   recurse => remote,
   source  =>  '/vagrant/modules/nginx/files/html',
   owner     => 'nginx',
   group   =>    'nginx',
   }
 
}
