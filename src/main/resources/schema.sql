-- Drop existing table and constraints
DROP TABLE IF EXISTS orderItem;
DROP TABLE IF EXISTS daily_report;

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
    version INT DEFAULT 0,
    UNIQUE KEY UK_daily_report_date (date)
); 