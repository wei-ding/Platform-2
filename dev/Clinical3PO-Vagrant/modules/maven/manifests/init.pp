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

class maven {

  require jdk

  $path="/bin:/usr/bin:/sbin:/usr/sbin:/usr/local/bin"

  exec { 'installmaven':
    path => $path,
    cwd => "/tmp",
    user => 'root',
    command => "wget http://apache.cs.utah.edu/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz && tar xzvf apache-maven-3.3.9-bin.tar.gz -C /opt/ && ln -s /opt/apache-maven-3.3.9/ /opt/maven",
  }
  ->
  file { "/etc/profile.d/maven.sh":
    ensure => file,
    mode => 0755,
    content => dos2unix(template('maven/maven-env.erb')),
  }
  ->
  exec { "chmodmaven":
    path => $path,
    user => 'root',
    provider => 'shell',
    command => "chmod +x /etc/profile.d/maven.sh",
  }

}
