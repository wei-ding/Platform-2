include camptocamp-openldap 

# ldap model
class ldap {
    class { 'openldap::server': }
    openldap::server::database { 'dc=clinical3po,dc=org':
      ensure => present,
    }
}
