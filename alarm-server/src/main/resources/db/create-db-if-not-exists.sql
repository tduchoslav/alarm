CREATE TABLE IF NOT EXISTS ALARM_STATUS (
  id     INTEGER PRIMARY KEY,
  running BOOLEAN DEFAULT FALSE  NOT NULL,
);

INSERT INTO ALARM_STATUS (SELECT 1 as a, FALSE as b FROM (SELECT 1 as a, count(*) FROM ALARM_STATUS) WHERE a NOT IN (SELECT a FROM ALARM_STATUS));


CREATE TABLE IF NOT EXISTS ALARM_SENT_EMAIL (
  id     INTEGER PRIMARY KEY,
  sent_tmstmp BIGINT NOT NULL,
  email_msg  CHARACTER(200)	
);

 