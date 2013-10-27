# Options schema

# --- !Ups

CREATE TABLE "options" (
  "symbol" varchar(24) NOT NULL,
  "bid" decimal(10,4) NOT NULL DEFAULT '0.0000',
  "ask" decimal(10,4) NOT NULL DEFAULT '0.0000',
  "strike" decimal(8,2) NOT NULL DEFAULT '0.0000',
  "volume" int DEFAULT '0',
  "open_interest" int DEFAULT '0',
  "exp_day" smallint DEFAULT '0',
  "exp_month" smallint DEFAULT '0',
  "exp_year" smallint DEFAULT '0',
  "exp_unixtime" bigint NOT NULL DEFAULT '0',
  "call_or_put" char(1) NOT NULL,
  "underlier" varchar(8) NOT NULL,
  "last_tick_time" bigint DEFAULT '0',
  "dbupdate_time" bigint DEFAULT '0',
  PRIMARY KEY ("symbol"),
  UNIQUE ("symbol")
);

CREATE INDEX ON "options" ("underlier","strike");
CREATE INDEX ON "options" ("exp_year","exp_month","exp_day");
CREATE INDEX ON "options" ("strike");
CREATE INDEX ON "options" ("call_or_put");
CREATE INDEX ON "options" ("bid");
CREATE INDEX ON "options" ("ask");
CREATE INDEX ON "options" ("exp_unixtime");
CREATE INDEX ON "options" ("dbupdate_time");

# --- !Downs

DROP TABLE "options";