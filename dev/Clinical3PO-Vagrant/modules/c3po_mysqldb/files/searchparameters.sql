CREATE TABLE IF NOT EXISTS `searchparameters` (
  `id` int(11) NOT NULL AUTO_INCREMENT, 
  `jobId` int(11) NOT NULL,
  `key` varchar(200) NOT NULL,
  `value` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_searchrepository_id` FOREIGN KEY (`jobId`) REFERENCES `searchrepository` (`id`)
) DEFAULT CHARSET=utf8;

alter table searchparameters add groupId int(10) unsigned null;
alter table searchparameters modify `value` varchar(20000) null;
