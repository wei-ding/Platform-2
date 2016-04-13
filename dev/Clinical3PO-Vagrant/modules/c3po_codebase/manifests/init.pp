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

class c3po_codebase {
  require hdfs_client
  require maven

  $path="/bin:/usr/bin"

  # a fuller example, including permissions and ownership
  file { '/home/c3po/codebase':
    ensure => 'directory',
    owner  => 'c3po',
    group  => 'hadoop',
    mode   => '0770',
  }
  ->
  vcsrepo { '/home/c3po/codebase/Clinical3PO-Platform':
    ensure   => latest,
    provider => git,
    source   => 'https://github.com/Clinical3PO/Platform.git',
    user     => 'c3po',
    owner    => 'c3po',
    group    => 'hadoop',
  }
  ->
  exec { "chmodc3postage":
    path => $path,
    command => "sudo chmod ug+rw /home/c3po/codebase/Clinical3PO-Platform",
  }
}
