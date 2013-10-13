# twolegs view

# --- !Ups

CREATE VIEW `twolegs` AS SELECT l.underlier, stocks.last_trade AS undLast, 
l.exp_unixtime AS expires, l.symbol AS longSym, l.bid AS longBid, l.call_or_put AS callOrPut, 
l.ask AS longAsk, l.strike AS longStrike, s.symbol AS shortSym, s.bid AS shortBid, 
s.ask AS shortAsk, s.strike AS shortStrike
FROM options AS l JOIN options AS s ON l.underlier=s.underlier AND l.exp_unixtime=s.exp_unixtime AND l.call_or_put=s.call_or_put
JOIN stocks ON l.underlier=stocks.symbol 
WHERE l.bid > 0.05 AND s.ask > 0.05 
AND l.strike BETWEEN (stocks.last_trade*0.95) AND (stocks.last_trade*1.05) 
AND s.strike BETWEEN (stocks.last_trade*0.95) AND (stocks.last_trade*1.05)

# --- !Downs

DROP VIEW `twolegs`;