create table IF NOT EXISTS facets_config
(
  code             VARCHAR(255) PRIMARY KEY,
  display_name     VARCHAR(255) NOT NULL,
  sequence         INTEGER,
  is_measurement   BOOLEAN,
  measurement_units JSONB
);

INSERT INTO facets_config VALUES ('price', 'Price', 4, false, null);
INSERT INTO facets_config VALUES ('color', 'Color', 2, false, null);
INSERT INTO facets_config VALUES ('department', 'Department', 3, false, null);
INSERT INTO facets_config VALUES ('brand', 'Brand', 1, false, null);
INSERT INTO facets_config VALUES ('length', 'Length', 5, true, '{"default": "inches", "metric": "inches", "imperial": "centimetres"}');
INSERT INTO facets_config VALUES ('width', 'Width', 6, true, '{"default": "inches", "metric": "inches", "imperial": "centimetres"}');
INSERT INTO facets_config VALUES ('height', 'Height', 7, true, '{"default": "inches", "metric": "inches", "imperial": "centimetres"}');
INSERT INTO facets_config VALUES ('weight', 'Weight', 8, true, '{"default": "pounds", "metric": "pounds", "imperial": "kilograms"}');