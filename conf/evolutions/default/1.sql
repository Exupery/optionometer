# Stocks schema

# --- !Ups

CREATE TABLE `stocks` (
  `symbol` varchar(8) NOT NULL,
  `last_trade` decimal(10,4) DEFAULT '0.0000',
  `last_trade_time` bigint(20) DEFAULT '0',
  `dbupdate_time` bigint(20) DEFAULT '0',
  PRIMARY KEY (`symbol`),
  UNIQUE KEY `symbol_UNIQUE` (`symbol`),
  KEY `last_INDEX` (`last_trade`),
  KEY `dbupdate_INDEX` (`dbupdate_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# --- !Downs

DROP TABLE `stocks`;