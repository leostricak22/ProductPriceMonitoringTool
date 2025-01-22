DROP TABLE IF EXISTS "user";
DROP TABLE IF EXISTS "address";
DROP TABLE IF EXISTS "company";
DROP TABLE IF EXISTS "user_company";

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