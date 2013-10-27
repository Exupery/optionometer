# Stocks schema

# --- !Ups

CREATE TABLE "stocks" (
  "symbol" varchar(8) NOT NULL,
  "last_trade" decimal(10,4) NOT NULL DEFAULT '0.0000',
  "last_trade_time" bigint DEFAULT '0',
  "dbupdate_time" bigint DEFAULT '0',
  PRIMARY KEY ("symbol"),
  UNIQUE ("symbol")
);

CREATE INDEX ON "stocks" ("last_trade");
CREATE INDEX ON "stocks" ("dbupdate_time");

# --- !Downs

DROP TABLE "stocks";