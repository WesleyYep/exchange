create table orders (
  id                  SERIAL,
  symbol              CHAR(8),
  quantity            INTEGER,
  price               NUMERIC(12, 4),
  side                CHAR(1),
  clientId            INTEGER,
  orderType           VARCHAR(20)
);

create table order_fill (
  id                  SERIAL,
  quantity            INTEGER,
  aggressorClientId   INTEGER,
  passiveClientId     INTEGER
);

