CREATE TABLE IF NOT EXISTS recipes(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    reading CLOB,
    category VARCHAR(50) NOT NULL,
    cooking_time INTEGER NOT NULL,
    favorite_level INTEGER NOT NULL,
    ingredients CLOB,
    ingredients_reading CLOB,
    instructions CLOB,
    image CLOB,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
