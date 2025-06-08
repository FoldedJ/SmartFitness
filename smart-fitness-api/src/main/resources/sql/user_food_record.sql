-- 用户饮食记录表
CREATE TABLE USER_FOOD_RECORD (
    id INTEGER IDENTITY(1,1) PRIMARY KEY,
    user_id INTEGER NOT NULL,
    food_id INTEGER NOT NULL,
    record_date VARCHAR(10) NOT NULL COMMENT '记录日期，格式：yyyy-MM-dd',
    meal_type VARCHAR(20) NOT NULL COMMENT '餐次类型：breakfast/lunch/dinner/snack',
    consume_amount DECIMAL(10,2) NOT NULL COMMENT '消费数量',
    actual_calories DECIMAL(10,2) COMMENT '实际卡路里',
    actual_protein DECIMAL(10,2) COMMENT '实际蛋白质(g)',
    actual_carbohydrate DECIMAL(10,2) COMMENT '实际碳水化合物(g)',
    actual_fat DECIMAL(10,2) COMMENT '实际脂肪(g)',
    actual_fiber DECIMAL(10,2) COMMENT '实际纤维(g)',
    actual_sodium DECIMAL(10,2) COMMENT '实际钠(mg)',
    notes VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT SYSDATE COMMENT '创建时间',
    update_time DATETIME DEFAULT SYSDATE COMMENT '更新时间'
);

-- 创建索引
CREATE INDEX idx_user_food_record_user_id ON USER_FOOD_RECORD(user_id);
CREATE INDEX idx_user_food_record_date ON USER_FOOD_RECORD(record_date);
CREATE INDEX idx_user_food_record_user_date ON USER_FOOD_RECORD(user_id, record_date);

-- 添加外键约束（如果需要）
-- ALTER TABLE USER_FOOD_RECORD ADD CONSTRAINT fk_user_food_record_user_id FOREIGN KEY (user_id) REFERENCES USER(id);
-- ALTER TABLE USER_FOOD_RECORD ADD CONSTRAINT fk_user_food_record_food_id FOREIGN KEY (food_id) REFERENCES FOOD(id);