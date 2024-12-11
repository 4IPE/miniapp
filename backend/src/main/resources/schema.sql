CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    chat_id BIGINT UNIQUE,
    wg_id VARCHAR(255),
    active BOOLEAN DEFAULT FALSE,
    name_tg VARCHAR(255),
    subscription_end_date TIMESTAMP,
    referal_code VARCHAR(255),
    count_referal_users INTEGER,
    demo BOOLEAN DEFAULT FALSE,
    price INTEGER DEFAULT 100,
    info_status BOOLEAN DEFAULT FALSE
);
