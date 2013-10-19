# Options schema

# --- !Ups

CREATE TABLE `options` (
  `symbol` varchar(24) NOT NULL,
  `bid` decimal(10,4) NOT NULL DEFAULT '0.0000',
  `ask` decimal(10,4) NOT NULL DEFAULT '0.0000',
  `strike` decimal(8,2) NOT NULL DEFAULT '0.0000',
  `volume` int(11) DEFAULT NULL,
  `open_interest` int(11) DEFAULT NULL,
  `exp_day` smallint(6) DEFAULT NULL,
  `exp_month` smallint(6) DEFAULT NULL,
  `exp_year` smallint(6) DEFAULT NULL,
  `exp_unixtime` bigint(20) NOT NULL DEFAULT '0',
  `call_or_put` char(1) NOT NULL,
  `underlier` varchar(8) NOT NULL,
  `last_tick_time` bigint(20) DEFAULT '0',
  `dbupdate_time` bigint(20) DEFAULT '0',
  PRIMARY KEY (`symbol`),
  UNIQUE KEY `symbol_UNIQUE` (`symbol`),
  KEY `underlier_strike_COMPOUND` (`underlier`,`strike`),
  KEY `expiry_COMPOUND` (`exp_year`,`exp_month`,`exp_day`),
  KEY `strike_INDEX` (`strike`),
  KEY `callput_INDEX` (`call_or_put`),
  KEY `bid_INDEX` (`bid`),
  KEY `ask_INDEX` (`ask`),
  KEY `expiry_time_INDEX` (`exp_unixtime`),
  KEY `dbupdate_INDEX` (`dbupdate_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

# --- !Downs

DROP TABLE `options`;