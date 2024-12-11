CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    chat_id BIGINT UNIQUE,
    wg_id VARCHAR(255),
    active BOOLEAN DEFAULT false,
    name_tg VARCHAR(255) NOT NULL,
    subscription_end_date TIMESTAMP,
    referal_code BIGINT,
    count_referal_users BIGINT DEFAULT 0,
    demo BOOLEAN DEFAULT false,
    price INTEGER DEFAULT 100,
    inform_status BOOLEAN DEFAULT false
);
