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

# This class ensures that the c3po user is properly setup and cleans up
# the veewee user from the base box.
class vm_users {

  $path="/bin:/usr/bin"
  $password = '%CannonStreetHospital%'

  package { "expect":
    ensure => installed,
  }
  ->
  user { 'c3po':
    path => "$path",
    ensure => present,
    gid => wheel,
    groups => ['users', 'ruby','c3po'],
    password => generate('/bin/sh', '-c', "mkpasswd -m sha-512 ${password} | tr -d '\n'"),
    membership => inclusive,
  }
  ->
  user {"root":
    path => "$path",
    ensure => 'present',
    password => generate('/bin/sh', '-c', "mkpasswd -m sha-512 ${password} | tr -d '\n'"),
  }
}
