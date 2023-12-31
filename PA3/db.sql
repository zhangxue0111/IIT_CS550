CREATE DATABASE cs550;
USE cs550;

DROP TABLE IF EXISTS `server`;
CREATE TABLE `server`(
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) DEFAULT NULL,
    `ip` VARCHAR(255) DEFAULT NULL,
    `port` INT(11),
    `status` VARCHAR(255) DEFAULT NULL,
    `service` VARCHAR(255) DEFAULT NULL,
     PRIMARY KEY (`id`)
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `peer`;
CREATE TABLE `peer`(
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `peerid` INT(11) DEFAULT NULL,
  `peername` VARCHAR(255) DEFAULT NULL,
  `filename` VARCHAR(255) DEFAULT NULL,
  `filepath` VARCHAR(255) DEFAULT NULL,
  `filesize` BIGINT(11),
  `version` INT(11) DEFAULT NULL,
  `type` VARCHAR(255) DEFAULT NULL,
  `state` VARCHAR(255) DEFAULT NULL,
  `ttr` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

