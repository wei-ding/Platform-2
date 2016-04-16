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

class c3po_mysqldb {
  require mysql_client
  require hive_client
  require c3po_codebase

  $path="/bin:/usr/bin"

  file { "/tmp/create-c3po-mysqldb-user.sh":
    ensure => file,
    owner => 'c3po',
    mode => 0776,
    content => regsubst(template('c3po_mysqldb/create-c3po-mysqldb-user.erb'), '\r\n', "\n", 'EMG'),
  }
  ->
  exec { "c3po-mysqldb-user":
    path => $path,
    user => 'c3po',
    provider => 'shell',
    command => "/tmp/create-c3po-mysqldb-user.sh",
  }
  ->
  file { "/tmp/init-c3po-mysqldb.sh":
    ensure => file,
    owner => 'c3po',
    mode => 0776,
    content => dos2unix(template('c3po_mysqldb/init-c3po-mysqldb.erb')),
  }
  ->
  exec { "c3po-mysqldb-init":
    path => $path,
    provider => 'shell',
    user => 'c3po',
    command => "/tmp/init-c3po-mysqldb.sh",
  }

}
