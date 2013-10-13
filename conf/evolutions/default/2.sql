# Options schema

# --- !Ups

CREATE TABLE `options` (
  `symbol` varchar(24) NOT NULL,
  `bid` decimal(10,4) DEFAULT NULL,
  `ask` decimal(10,4) DEFAULT NULL,
  `strike` decimal(8,2) DEFAULT NULL,
  `volume` int(11) DEFAULT NULL,
  `open_interest` int(11) DEFAULT NULL,
  `exp_day` smallint(6) DEFAULT NULL,
  `exp_month` smallint(6) DEFAULT NULL,
  `exp_year` smallint(6) DEFAULT NULL,
  `exp_unixtime` bigint(20) DEFAULT NULL,
  `call_or_put` char(1) DEFAULT NULL,
  `underlier` varchar(8) DEFAULT NULL,
  `last_tick_time` bigint(20) DEFAULT '0',
  `dbupdate_time` bigint(20) DEFAULT '0',
  PRIMARY KEY (`symbol`),
  UNIQUE KEY `symbol_UNIQUE` (`symbol`),
  KEY `underlier_strike_COMPOUND` (`underlier`,`strike`),
  KEY `expiry_COMPOUND` (`exp_year`,`exp_month`,`exp_day`),
  KEY `callput_INDEX` (`call_or_put`),
  KEY `bid_INDEX` (`bid`),
  KEY `ask_INDEX` (`ask`),
  KEY `expiry_time_INDEX` (`exp_unixtime`),
  KEY `dbupdate_INDEX` (`dbupdate_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

# --- !Downs

DROP TABLE `options`;