CREATE TABLE IF NOT EXISTS `searchrepository` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime NOT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  `outputFileName` varchar(200) NOT NULL,
  `searchOn` varchar(20) NOT NULL,
  `searchParameters` varchar(200) NOT NULL,
  `outputDirectory` varchar(200) NOT NULL,
  `searchStartTime` datetime NOT NULL,
  `searchEndTime` datetime DEFAULT NULL,
  `status` varchar(100) NOT NULL,
  `hadoopOutputDirectory` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8;

/* Alter table for adding user details */

alter table searchrepository modify searchon varchar(200), modify hadoopOutputDirectory varchar(200) NULL;
alter table searchrepository add searchBy int(10) unsigned;
alter table searchrepository add constraint `fk_searchBy_userid` FOREIGN KEY (`searchBy`) REFERENCES `users` (`id`);
alter table searchrepository modify searchParameters varchar(200) null;
alter table searchrepository modify searchParameters varchar(20000) null;


