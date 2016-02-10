# See README.md for details.
class openldap::server::slapdconf {

  if ! defined(Class['openldap::server']) {
    fail 'class ::openldap::server has not been evaluated'
  }

  case $::openldap::server::provider {
    augeas: {
      file { $::openldap::server::conffile:
        ensure => present,
        owner  => $::openldap::server::owner,
        group  => $::openldap::server::group,
        mode   => '0640',
      }
    }
    olc: {
      file { $::openldap::server::confdir:
        ensure => directory,
        owner  => $::openldap::server::owner,
        group  => $::openldap::server::group,
        mode   => '0750',
        force  => true,
      }
    }
    default: {
      fail 'provider must be one of "olc" or "augeas"'
    }
  }

  if $::openldap::server::ssl_cert {
    if $::openldap::server::ssl_key {
      validate_absolute_path($::openldap::server::ssl_cert)
      validate_absolute_path($::openldap::server::ssl_key)
      openldap::server::globalconf { 'TLSCertificateFile':
        value => $::openldap::server::ssl_cert,
      }
      openldap::server::globalconf { 'TLSCertificateKeyFile':
        value => $::openldap::server::ssl_key,
      }
      if $::openldap::server::ssl_ca {
        validate_absolute_path($::openldap::server::ssl_ca)
        openldap::server::globalconf { 'TLSCACertificateFile':
          value => $::openldap::server::ssl_ca,
        }
      }
    } else {
      fail 'You must specify a ssl_key'
    }
  } elsif $::openldap::server::ssl_key {
    fail 'You must specify a ssl_cert'
  }

  openldap::server::database { 'dc=clinical3po,dc=org':
    ensure => absent,
  }

  create_resources('openldap::server::database', $::openldap::server::databases)

}
