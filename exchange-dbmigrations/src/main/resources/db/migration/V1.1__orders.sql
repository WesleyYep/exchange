create table orders (
  id                  SERIAL,
  symbol              CHAR(8),
  quantity            INTEGER,
  price               NUMERIC(12, 4),
  side                CHAR(1),
  client_id           VARCHAR(64),
  order_type          CHAR(1),
  status              CHAR(1)
);

create table order_fill (
  id                  SERIAL,
  quantity            INTEGER,
  aggressor_client_id   VARCHAR(64),
  passive_client_id     VARCHAR(64)
);

