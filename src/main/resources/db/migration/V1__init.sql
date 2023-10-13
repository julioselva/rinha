CREATE TABLE person
(
  id         UUID PRIMARY KEY NOT NULL,
  nome       VARCHAR(255)     NOT NULL,
  apelido    VARCHAR(255)     NOT NULL,
  nascimento DATE             NOT NULL,
  stack      VARCHAR(255)[]   NULL,

  CONSTRAINT person_apelido_u_idx UNIQUE (apelido)
);
