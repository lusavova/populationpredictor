CREATE DATABASE IF NOT EXISTS population_predictor;

USE population_predictor;

CREATE TABLE IF NOT EXISTS countries (
  id VARCHAR(36) PRIMARY KEY,
  name VARCHAR(255),
  UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS population_info (
  id VARCHAR(36) PRIMARY KEY,
  year INT,
  population BIGINT,
  country_id VARCHAR(36) NOT NULL,
  FOREIGN KEY (country_id) REFERENCES countries (id),
  UNIQUE KEY `unique_population_data` (country_id, year)
);