CREATE TABLE IF NOT EXISTS `movement` (
  `movement_id` int(11) NOT NULL AUTO_INCREMENT,
  `date` datetime(6) DEFAULT NULL,
  `dest_account_id` int(11) NOT NULL,
  `detail` varchar(255) DEFAULT NULL,
  `value` float NOT NULL,
  `account_id` int(11) NOT NULL,
  PRIMARY KEY (`movement_id`),
  KEY `FKoemeananv9w9qnbcoccbl70a0` (`account_id`),
  CONSTRAINT `FKoemeananv9w9qnbcoccbl70a0` FOREIGN KEY (`account_id`) REFERENCES `account` (`account_id`)
)