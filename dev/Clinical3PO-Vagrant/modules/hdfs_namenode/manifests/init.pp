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

class hdfs_namenode {
  require hdfs_client
  require hadoop_server

  $PATH="/bin:/usr/bin"

  if $security == "true" {
    require kerberos_http
    file { "${hdfs_client::keytab_dir}/nn.keytab":
      ensure => file,
      source => "/vagrant/generated/keytabs/${hostname}/nn.keytab",
      owner => hdfs,
      group => hadoop,
      mode => '400',
    }
    ->
    exec { "kinit -k -t ${hdfs_client::keytab_dir}/nn.keytab nn/${hostname}.${domain}":
      path => $PATH,
      user => hdfs,
    }
    ->
    Package["hadoop_${rpm_version}-hdfs-namenode"]
  }

  package { "hadoop_${rpm_version}-hdfs-namenode" :
    ensure => installed,
  }
  ->
  exec { "hdp-select set hadoop-hdfs-namenode ${hdp_version}":
    cwd => "/",
    path => "$path",
  }
  ->
  file { "/etc/init.d/hadoop-hdfs-namenode":
    ensure => 'link',
    target => "/usr/hdp/current/hadoop-hdfs-namenode/etc/rc.d/init.d/hadoop-hdfs-namenode",
  }
  ->
  exec {"namenode-format":
    command => "hadoop namenode -format",
    path => "$PATH",
    creates => "${hdfs_client::data_dir}/hdfs/namenode",
    user => "hdfs",
    require => Package["hadoop_${rpm_version}-hdfs-namenode"],
  }
  ->
  service {"hadoop-hdfs-namenode":
    ensure => running,
    enable => true,
  }
  ->
  exec {"yarn-home-mkdir":
    command => "hdfs dfs -mkdir -p /user/yarn",
    unless => "hdfs dfs -test -e /user/yarn",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"yarn-home-chown":
    command => "hdfs dfs -chown yarn:yarn /user/yarn",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"yarn-home-chmod":
    command => "hdfs dfs -chmod 755 /user/yarn",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"yarn-history-mkdir":
    command => "hdfs dfs -mkdir -p /user/yarn/history",
    unless => "hdfs dfs -test -e /user/yarn/history",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"yarn-history-chmod":
    command => "hdfs dfs -chmod 775 /user/yarn/history",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"yarn-history-chown":
    command => "hdfs dfs -chown -R mapred:mapred /user/yarn/history",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"yarn-app-logs-mkdir":
    command => "hdfs dfs -mkdir /user/yarn/app-logs",
    unless => "hdfs dfs -test -e /user/yarn/app-logs",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"yarn-app-logs-chmod":
    command => "hdfs dfs -chmod 1777 /user/yarn/app-logs",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"yarn-app-logs-chown":
    command => "hdfs dfs -chown yarn:mapred /user/yarn/app-logs",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"c3po-home-mkdir":
    command => "hdfs dfs -mkdir /user/c3po",
    unless => "hdfs dfs -test -e /user/c3po",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"c3po-home-chown":
    command => "hdfs dfs -chown -R c3po:hadoop /user/c3po",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"c3po-home-chmod":
    command => "hdfs dfs -chmod -R 775 /user/c3po",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"c3po-cdw-mkdir":
    command => "hadoop fs -mkdir -p /data/C3PO_CDW/care_site /data/C3PO_CDW/cohort /data/C3PO_CDW/condition_era /data/C3PO_CDW/death /data/C3PO_CDW/ drug_cost /data/C3PO_CDW/drug_era /data/C3PO_CDW/durg_exposure /data/C3PO_CDW/location /data/C3PO_CDW/measurement /data/C3PO_CDW/note /data/C3PO_CDW/observation /data/C3PO_CDW/observation_period /data/C3PO_CDW/organization /data/C3PO_CDW/payer_plan_period /data/C3PO_CDW/person /data/C3PO_CDW/procedure_cost /data/C3PO_CDW/procedure_occurrence /data/C3PO_CDW/provider /data/C3PO_CDW/specimen /data/C3PO_CDW/visit_occurrence",
    unless => "hdfs dfs -test -e /data/C3PO_CDW",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"c3po-cdw-chown":
    command => "hdfs dfs -chown -R c3po:hadoop /data",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"c3po-cdw-chmod":
    command => "hdfs dfs -chmod -R 775 /data",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"hive-home-mkdir":
    command => "hdfs dfs -mkdir /user/hive",
    unless => "hdfs dfs -test -e /user/hive",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"hive-home-chown":
    command => "hdfs dfs -chown hive:hive /user/hive",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"oozie-home":
    command => "hdfs dfs -mkdir -p /user/oozie",
    unless => "hdfs dfs -test -e /user/oozie",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"oozie-home-chown":
    command => "hdfs dfs -chown oozie:oozie /user/oozie",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"hive-warehouse":
    command => "hdfs dfs -mkdir -p /apps/hive/warehouse",
    unless => "hdfs dfs -test -e /apps/hive/warehouse",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"hive-warehouse-chown":
    command => "hdfs dfs -chown hive:hive /apps/hive/warehouse",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"hive-warehouse-chmod":
    command => "hdfs dfs -chmod 1777 /apps/hive/warehouse",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"hbase-warehouse":
    command => "hdfs dfs -mkdir -p /apps/hbase",
    unless => "hdfs dfs -test -e /apps/hbase",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"hbase-warehouse-chown":
    command => "hdfs dfs -chown hbase:hbase /apps/hbase",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"hbase-warehouse-chmod":
    command => "hdfs dfs -chmod 1777 /apps/hbase",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"hdfs-tmp":
    command => "hdfs dfs -mkdir /tmp",
    unless => "hdfs dfs -test -e /tmp",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"hdfs-tmp-chmod":
    command => "hdfs dfs -chmod 1777 /tmp",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"mr-tarball-dir":
    command => "hdfs dfs -mkdir -p /hdp/apps/${hdp_version}/mapreduce",
    unless => "hdfs dfs -test -e /hdp/apps/${hdp_version}/mapreduce",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"tez-tarball-dir":
    command => "hdfs dfs -mkdir -p /hdp/apps/${hdp_version}/tez",
    unless => "hdfs dfs -test -e /hdp/apps/${hdp_version}/tez",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"pig-tarball-dir":
    command => "hdfs dfs -mkdir -p /hdp/apps/${hdp_version}/pig",
    unless => "hdfs dfs -test -e /hdp/apps/${hdp_version}/pig",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"hive-tarball-dir":
    command => "hdfs dfs -mkdir -p /hdp/apps/${hdp_version}/hive",
    unless => "hdfs dfs -test -e /hdp/apps/${hdp_version}/hive",
    path => "$PATH",
    user => "hdfs",
  }
  ->
  exec {"tarball-chmod":
    command => "hdfs dfs -chmod -R +rX /hdp",
    path => "$PATH",
    user => "hdfs",
  }
}
