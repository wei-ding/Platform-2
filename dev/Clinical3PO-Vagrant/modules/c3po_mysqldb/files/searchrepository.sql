CREATE TABLE IF NOT EXISTS `searchrepository` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime NOT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  `outputFileName` varchar(200) NOT NULL,
  `searchon` varchar(200) DEFAULT NULL,
  `searchParameters` varchar(20000) DEFAULT NULL,
  `outputDirectory` varchar(200) NOT NULL,
  `searchStartTime` datetime NOT NULL,
  `searchEndTime` datetime DEFAULT NULL,
  `status` varchar(100) NOT NULL,
  `hadoopOutputDirectory` varchar(200) DEFAULT NULL,
  `searchBy` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_searchBy_userid` (`searchBy`)
) DEFAULT CHARSET=utf8;

