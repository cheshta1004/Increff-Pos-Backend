-- Drop existing table and constraints
DROP TABLE IF EXISTS orderItem;
DROP TABLE IF EXISTS daily_report;

-- Remove preUpdatedAt column from all tables
ALTER TABLE orderItem DROP COLUMN preUpdatedAt;
ALTER TABLE daily_report DROP COLUMN preUpdatedAt;
ALTER TABLE orders DROP COLUMN preUpdatedAt;
ALTER TABLE product DROP COLUMN preUpdatedAt;
ALTER TABLE inventory DROP COLUMN preUpdatedAt;
ALTER TABLE user DROP COLUMN preUpdatedAt;

-- Recreate table with correct constraints
CREATE TABLE orderItem (
    id INT AUTO_INCREMENT PRIMARY KEY,
    orderId INT NOT NULL,
    productId INT NOT NULL,
    quantity INT NOT NULL,
    sellingPrice DOUBLE NOT NULL,
    version INT DEFAULT 0,
    UNIQUE KEY UK_order_product (orderId, productId)
);

CREATE TABLE daily_report (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date DATETIME NOT NULL,
    orderCount BIGINT NOT NULL,
    totalItems BIGINT NOT NULL,
    revenue DOUBLE NOT NULL,
    version INT DEFAULT 0
); 