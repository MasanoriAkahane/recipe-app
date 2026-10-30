CREATE TABLE IF NOT EXISTS recipes(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    cookingTime INTEGER NOT NULL,
    favoriteLevel INTEGER NOT NULL,
    ingredients CLOB,
    instructions CLOB,
    image CLOB,
    createdAt TIMESTAMP,
    updatedAt TIMESTAMP,
);
