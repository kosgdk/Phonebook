DROP TABLE IF EXISTS PUBLIC.contacts;
CREATE TABLE PUBLIC.contacts
(
  id BIGINT PRIMARY KEY NOT NULL IDENTITY,
  name VARCHAR(50) NOT NULL,
  phone VARCHAR(11) NOT NULL
);
CREATE UNIQUE INDEX "table_name_name_uindex" ON PUBLIC.contacts (name);
CREATE UNIQUE INDEX "table_name_phone_uindex" ON PUBLIC.contacts (phone);