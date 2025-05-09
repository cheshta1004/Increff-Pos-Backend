-- Drop existing table and constraints
DROP TABLE IF EXISTS orderItem;

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

ALTER TABLE orderItem DROP INDEX UKr03826mlwul30ct1d6h1djpw1; 