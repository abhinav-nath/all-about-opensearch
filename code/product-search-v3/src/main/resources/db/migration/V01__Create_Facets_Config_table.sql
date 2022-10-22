create table IF NOT EXISTS facets_config
(
  code         VARCHAR(255) PRIMARY KEY,
  display_name VARCHAR(255) NOT NULL,
  sequence     INTEGER
);

INSERT INTO facets_config
VALUES ('color', 'Color', 2);

INSERT INTO facets_config
VALUES ('department', 'Department', 3);

INSERT INTO facets_config
VALUES ('brand', 'Brand', 1);