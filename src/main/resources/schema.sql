CREATE TABLE IF NOT EXISTS recipes(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    reading CLOB,
    category VARCHAR(50) NOT NULL,
    cooking_time INTEGER NOT NULL,
    favorite_level INTEGER NOT NULL,
    servings INTEGER,
    ingredients CLOB,
    ingredients_reading CLOB,
    instructions CLOB,
    image CLOB,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- 古いDBに人数列がまだない場合だけ追加する
ALTER TABLE recipes ADD COLUMN IF NOT EXISTS servings INTEGER;
