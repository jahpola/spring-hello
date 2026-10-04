UPDATE products
SET in_stock = COALESCE(stock_quantity, 0) > 0;

ALTER TABLE products
    ALTER COLUMN in_stock SET NOT NULL,
    ADD CONSTRAINT products_stock_status_consistent
        CHECK (in_stock = (COALESCE(stock_quantity, 0) > 0));
