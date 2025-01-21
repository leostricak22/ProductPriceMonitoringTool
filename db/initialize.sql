DROP TABLE IF EXISTS "user";

CREATE TABLE "user" (
    id LONG GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO "user" (name, email, password, role) VALUES ('admin', 'admin@admin.com', 'admin', 'ADMIN');
INSERT INTO "user" (name, email, password, role) VALUES ('merchant', 'merchant@merchant.com', 'merchant', 'MERCHANT');
INSERT INTO "user" (name, email, password, role) VALUES ('customer', 'customer@customer.com', 'customer', 'CUSTOMER');