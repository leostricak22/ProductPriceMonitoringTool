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
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE "address" (
    id LONG GENERATED ALWAYS AS IDENTITY,
    street VARCHAR(255) NOT NULL,
    house_number VARCHAR(10) NOT NULL,
    postal_code VARCHAR(5) NOT NULL,
    city VARCHAR(50) NOT NULL,
    country VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE "company" (
    id LONG GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    address_id LONG NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (address_id) REFERENCES "address"(id)
);

CREATE TABLE "user_company" (
    id LONG GENERATED ALWAYS AS IDENTITY,
    user_id LONG NOT NULL,
    company_id LONG NOT NULL,
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
    PRIMARY KEY (id),
    FOREIGN KEY (category_id) REFERENCES "category"(id)
);

CREATE TABLE "company_product" (
    id LONG GENERATED ALWAYS AS IDENTITY,
    company_id LONG NOT NULL,
    product_id LONG NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (company_id) REFERENCES "company"(id),
    FOREIGN KEY (product_id) REFERENCES "product"(id)
);

INSERT INTO "user" (name, email, password, role) VALUES ('admin', 'admin', 'admin', 'ADMIN');
INSERT INTO "user" (name, email, password, role) VALUES ('merchant', 'merchant@merchant.com', 'merchant', 'MERCHANT');
INSERT INTO "user" (name, email, password, role) VALUES ('customer', 'customer@customer.com', 'customer', 'CUSTOMER');

INSERT INTO "address" (street, house_number, postal_code, city, country) VALUES ('Ilica', '1', '10000', 'Zagreb', 'Croatia');
INSERT INTO "address" (street, house_number, postal_code, city, country) VALUES ('Vukovarska', '1', '10000', 'Zagreb', 'Croatia');
INSERT INTO "address" (street, house_number, postal_code, city, country) VALUES ('Trg bana Jelačića', '1', '10000', 'Zagreb', 'Croatia');

INSERT INTO "company" (name, address_id) VALUES ('Google', 1);
INSERT INTO "company" (name, address_id) VALUES ('Microsoft', 2);
INSERT INTO "company" (name, address_id) VALUES ('Apple', 3);

INSERT INTO "user_company" (user_id, company_id) VALUES (1, 1);
INSERT INTO "user_company" (user_id, company_id) VALUES (1, 2);
INSERT INTO "user_company" (user_id, company_id) VALUES (2, 3);

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