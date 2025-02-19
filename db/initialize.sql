DROP TABLE IF EXISTS "company_product";
DROP TABLE IF EXISTS "product";
DROP TABLE IF EXISTS "category";
DROP TABLE IF EXISTS "user_company";
DROP TABLE IF EXISTS "company";
DROP TABLE IF EXISTS "address";
DROP TABLE IF EXISTS "user";

CREATE TABLE "user" (
    id LONG GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE "address" (
    id LONG GENERATED ALWAYS AS IDENTITY,
    latitude DECIMAL(19, 16) NOT NULL,
    longitude DECIMAL(19, 16) NOT NULL,
    road VARCHAR(255) NOT NULL,
    house_number VARCHAR(10) NOT NULL,
    city VARCHAR(255),
    town VARCHAR(255),
    village VARCHAR(255),
    country VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE "company" (
    id LONG GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    address_id LONG NOT NULL,
    join_code VARCHAR(10) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (address_id) REFERENCES "address"(id)
);

CREATE TABLE "user_company" (
    id LONG GENERATED ALWAYS AS IDENTITY,
    user_id LONG NOT NULL,
    company_id LONG NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES "user"(id),
    FOREIGN KEY (company_id) REFERENCES "company"(id)
);

CREATE TABLE "category" (
    id LONG GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    parent_category_id LONG,
    PRIMARY KEY (id),
    FOREIGN KEY (parent_category_id) REFERENCES "category"(id)
);

CREATE TABLE "product" (
    id LONG GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    category_id LONG NOT NULL,
    description VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (category_id) REFERENCES "category"(id)
);

CREATE TABLE "company_product" (
    id LONG GENERATED ALWAYS AS IDENTITY,
    company_id LONG NOT NULL,
    product_id LONG NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (company_id) REFERENCES "company"(id),
    FOREIGN KEY (product_id) REFERENCES "product"(id)
);

INSERT INTO "user" (name, surname, email, password, role) VALUES ('admin', '', 'admin', 'admin', 'ADMIN');
INSERT INTO "user" (name, surname, email, password, role) VALUES ('Leo', 'Stričak', 'merchant@merchant.com', 'merchant', 'MERCHANT');
INSERT INTO "user" (name, surname, email, password, role) VALUES ('Julia', 'Janković', 'customer@customer.com', 'customer', 'CUSTOMER');
INSERT INTO "user" (name, surname, email, password, role) VALUES
    ('John', 'Doe', 'john.doe@example.com', 'password', 'CUSTOMER'),
    ('Alice', 'Smith', 'alice.smith@example.com', 'password', 'MERCHANT'),
    ('Bob', 'Johnson', 'bob.johnson@example.com', 'password', 'CUSTOMER'),
    ('Carol', 'Williams', 'carol.williams@example.com', 'password', 'MERCHANT');

INSERT INTO "address" (longitude, latitude, road, house_number, city, country) VALUES (15.976036, 45.8129663, 'Ilica', '1', 'Zagreb', 'Croatia');
INSERT INTO "address" (longitude, latitude, road, house_number, city, country) VALUES (16.0521091, 45.8197321, 'Vukovarska', '1', 'Zagreb', 'Croatia');
INSERT INTO "address" (longitude, latitude, road, house_number, city, country) VALUES (15.976034, 45.8132734, 'Trg bana Jelačića', '1', 'Zagreb', 'Croatia');
INSERT INTO "address" (longitude, latitude, road, house_number, city, country) VALUES
    (16.4402, 43.5081, 'Obala', '12', 'Split', 'Croatia'),
    (15.987654, 45.815678, 'Nova cesta', '45', 'Zagreb', 'Croatia'),
    (14.4422, 45.3271, 'Korzo', '5', 'Rijeka', 'Croatia'),
    (18.4131, 42.6507, 'Sutjeska', '10', 'Mostar', 'Bosnia and Herzegovina');

INSERT INTO "company" (name, address_id, join_code) VALUES ('Google', 1, '1231254512');
INSERT INTO "company" (name, address_id, join_code) VALUES ('Microsoft', 2, '4989469843');
INSERT INTO "company" (name, address_id, join_code) VALUES ('Apple', 3, '8725872512');
INSERT INTO "company" (name, address_id, join_code) VALUES
    ('Amazon', 4, '8271659673'),
    ('Facebook', 5, '9681745927'),
    ('Tesla', 6, '0173559174'),
    ('Samsung', 7, '3125192895');

INSERT INTO "user_company" (user_id, company_id) VALUES (1, 1);
INSERT INTO "user_company" (user_id, company_id) VALUES (1, 2);
INSERT INTO "user_company" (user_id, company_id) VALUES (2, 3);
INSERT INTO "user_company" (user_id, company_id) VALUES
    (4, 4),  -- John Doe joins Amazon
    (5, 5),  -- Alice Smith joins Facebook
    (6, 6),  -- Bob Johnson joins Tesla
    (7, 7),  -- Carol Williams joins Samsung
    (1, 5);

INSERT INTO "category" (name) VALUES ('Computers');
INSERT INTO "category" (name) VALUES ('Components');
INSERT INTO "category" (name) VALUES ('Software');
INSERT INTO "category" (name, parent_category_id) VALUES ('Laptops', 1);
INSERT INTO "category" (name, parent_category_id) VALUES ('Desktops', 1);
INSERT INTO "category" (name, parent_category_id) VALUES ('Monitors', 2);
INSERT INTO "category" (name, parent_category_id) VALUES ('Keyboards', 2);
INSERT INTO "category" (name, parent_category_id) VALUES ('Mice', 2);
INSERT INTO "category" (name, parent_category_id) VALUES ('Windows', 3);
INSERT INTO "category" (name, parent_category_id) VALUES ('Office', 3);
INSERT INTO "category" (name, parent_category_id) VALUES ('Visual Studio', 3);
INSERT INTO "category" (name, parent_category_id) VALUES ('Gaming PCs', 5);
INSERT INTO "category" (name) VALUES
    ('Accessories'),
    ('Smartphones');
INSERT INTO "category" (name, parent_category_id)
VALUES
    ('Headphones', 13),
    ('Chargers', 13),
    ('Android Phones', 14),
    ('iPhones', 14);

INSERT INTO "product" (name, category_id, description) VALUES ('MacBook Pro', 4, 'The ultimate pro notebook.');
INSERT INTO "product" (name, category_id, description) VALUES ('MacBook Air', 4, 'The thinnest and lightest notebook.');
INSERT INTO "product" (name, category_id, description) VALUES ('iMac', 5, 'The all-in-one for all.');
INSERT INTO "product" (name, category_id, description) VALUES ('Mac Mini', 5, 'Mac mini is an affordable powerhouse that packs the entire Mac experience into a 7.7-inch-square frame.');
INSERT INTO "product" (name, category_id) VALUES ('LG 27UL850-W', 6);
INSERT INTO "product" (name, category_id, description) VALUES ('Dell U2718Q', 6, '27" 4K monitor with InfinityEdge and Dell HDR.');
INSERT INTO "product" (name, category_id, description) VALUES
    ('Alienware Aurora', 12, 'High-performance gaming desktop.'),
    ('Sony WH-1000XM4', 15, 'Noise cancelling headphones.'),
    ('Anker PowerPort', 16, 'Fast charging USB hub.'),
    ('Samsung Galaxy S23', 17, 'Latest Android flagship phone.'),
    ('iPhone 14 Pro', 18, 'Apple''s latest smartphone.'),
    ('Dell XPS 13', 4, 'Compact and powerful ultrabook.');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (1, 1, 2000.00, '2021-01-01 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (2, 1, 2100.00, '2021-01-03 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (1, 2, 1000.00, '2021-01-05 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (2, 2, 1100.00, '2021-01-07 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (1, 3, 1500.00, '2021-01-09 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (1, 4, 800.00, '2021-01-11 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (1, 5, 500.00, '2021-01-13 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (1, 6, 600.00, '2021-01-15 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (4, 7, 2500.00, '2021-01-17 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (5, 8, 350.00, '2021-01-19 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (6, 9, 50.00, '2021-01-21 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (7, 10, 900.00, '2021-01-23 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (3, 11, 1100.00, '2021-01-25 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (2, 12, 1200.00, '2021-01-27 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (3, 1, 2050.00, '2021-01-29 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (7, 6, 650.00, '2021-01-31 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (5, 3, 1550.00, '2021-02-02 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (1, 1, 1950.00, '2021-04-01 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (1, 1, 2050.00, '2021-06-01 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (2, 1, 2150.00, '2021-05-01 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (1, 3, 1450.00, '2021-03-15 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (1, 3, 1600.00, '2021-04-15 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (7, 6, 680.00, '2021-03-01 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (2, 12, 1150.00, '2021-03-20 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (1, 4, 750.00, '2021-02-20 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (1, 4, 800.00, '2021-05-20 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (7, 10, 850.00, '2021-04-10 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (7, 10, 920.00, '2021-06-10 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (5, 8, 340.00, '2021-03-05 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (5, 8, 360.00, '2021-05-05 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (4, 7, 2450.00, '2021-04-17 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (4, 7, 2550.00, '2021-07-17 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (3, 11, 1080.00, '2021-02-25 00:00:00');

INSERT INTO "company_product" (company_id, product_id, price, created_at)
VALUES (3, 11, 1120.00, '2021-04-25 00:00:00');