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

include stdlib
include ldap

$testdata = "output-uids.ldif"

# ldap model
class knox_ldap {


    class { 'ldap::server': }
        ldap::define::domain {'clinical3po.vm':
            basedn   => 'dc=clinical3po,dc=org',
                rootdn   => 'cn=admin',
                rootpw   => 'PWc3po',
                auth_who => 'anonymous'
        }

        ldap::define::schema {'brodate':
        ensure => present,
        source => dos2unix('puppet:///modules/ldap/schema/brodate.schema'),
        }

        ldap::define::schema {'sudo':
        ensure => present,
        source => dos2unix('puppet:///modules/ldap/schema/sudo.schema'),
        }

        ldap::define::schema {'misc':
        ensure => present,
        source => dos2unix('puppet:///modules/ldap/schema/misc.schema'),
        }

        ldap::define::schema {'openssh':
        ensure => present,
        source => dos2unix('puppet:///modules/ldap/schema/openssh.schema'),
        }

        ldap::define::schema {'inetorgperson':
        ensure => present,
        source => dos2unix('puppet:///modules/ldap/schema/inetorgperson.schema'),
        }

        file {
        '/etc/ldap/schema/nis.schema':
            ensure => file,
            source => dos2unix('puppet:///modules/ldap/schema/nis.schema');
        }

        ldap::define::schema {'dyngroup':
        ensure => present,
        source => dos2unix('puppet:///modules/ldap/schema/dyngroup.schema'),
        }

        exec {"$testdata":
        require   => [Ldap::Define::Schema["dyngroup", "inetorgperson", "brodate", "sudo", "misc", "openssh"], File["/etc/ldap/schema/nis.schema"]], 
        command   => "/usr/bin/ldapadd -w test -D 'cn=dsadmin,dc=brodate,dc=net' -H ldap://localhost -f /vagrant_data/deploy/${testdata}"
        }

        exec {'input_ldap_test_data.ldif':
        require   => [Ldap::Define::Schema["dyngroup", "inetorgperson", "brodate", "sudo", "misc", "openssh"], File["/etc/ldap/schema/nis.schema"], Exec["$testdata"]], 
        command   => '/usr/bin/ldapadd -w test -D "cn=dsadmin,dc=brodate,dc=net" -H ldap://localhost -f /vagrant_data/deploy/ldap_test_data.ldif -c'
        }

}

