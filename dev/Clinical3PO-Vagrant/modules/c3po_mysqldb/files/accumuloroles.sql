CREATE TABLE IF NOT EXISTS `accumuloroles` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL DEFAULT '',
  `userId` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_accumulorole_userid` FOREIGN KEY (`userId`) REFERENCES `users` (`id`)
)DEFAULT CHARSET=utf8;

