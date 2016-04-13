CREATE TABLE IF NOT EXISTS `users` (
  `userName` varchar(50) NOT NULL,
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `password` varchar(50) DEFAULT NULL,
  `accountNonExpired` tinyint(4) NOT NULL DEFAULT '0',
  `email` varchar(50) DEFAULT NULL,
  `firstName` varchar(50) DEFAULT NULL,
  `lastName` varchar(50) DEFAULT NULL,
  `accountNonLocked` tinyint(4) NOT NULL DEFAULT '0',
  `credentialsNonExpired` tinyint(4) NOT NULL DEFAULT '0',
  `enabled` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8;
