CREATE TABLE IF NOT EXISTS organization
(
    organization_id VARCHAR NOT NULL,
    name            VARCHAR,
    contact_name    VARCHAR,
    contact_email   VARCHAR,
    contact_phone   VARCHAR,
    PRIMARY KEY (organization_id)
    );

CREATE TABLE IF NOT EXISTS license
(
    license_id      VARCHAR NOT NULL,
    organization_id VARCHAR NOT NULL,
    description     VARCHAR,
    product_name    VARCHAR NOT NULL,
    license_type    VARCHAR NOT NULL,
    comment         VARCHAR,
    PRIMARY KEY (license_id),
    FOREIGN KEY (organization_id) references organization (organization_id)
    );

MERGE INTO organization KEY (organization_id)
    VALUES ('e6a625cc-718b-48c2-ac76-1dfdff9a531e', 'Ostock', 'Illary Huaylupo', 'illaryhs@gmail.com', '888888888');
MERGE INTO organization KEY (organization_id)
    VALUES ('d898a142-de44-466c-8c88-9ceb2c2429d3', 'OptimaGrowth', 'Admin', 'illaryhs@gmail.com', '888888888');
MERGE INTO organization KEY (organization_id)
    VALUES ('e839ee96-28de-4f67-bb79-870ca89743a0', 'Ostock', 'Illary Huaylupo', 'illaryhs@gmail.com', '888888888');
MERGE INTO license KEY (license_id)
    VALUES ('f2a9c9d4-d2c0-44fa-97fe-724d77173c62', 'd898a142-de44-466c-8c88-9ceb2c2429d3', 'Software Product', 'Ostock',
    'complete', 'I AM DEV');
MERGE INTO license KEY (license_id)
    VALUES ('279709ff-e6d5-4a54-8b55-a5c37542025b', 'e839ee96-28de-4f67-bb79-870ca89743a0', 'Software Product', 'Ostock',
    'complete', 'I AM DEV');