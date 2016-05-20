create table orders (
  id                  SERIAL PRIMARY KEY,
  instrument_id       CHAR(8),
  quantity            INTEGER,
  price               NUMERIC(12, 4),
  side                CHAR(1),
  client_id           INTEGER,
  order_type          CHAR(1),
  status              VARCHAR(48),
  order_submitter     VARCHAR(64)
);

create table order_fill (
  id                  SERIAL PRIMARY KEY,
  order_id            INTEGER REFERENCES orders(id),
  matched_order_id    INTEGER REFERENCES orders(id),
  price               NUMERIC(12, 4),
  quantity            INTEGER
);

