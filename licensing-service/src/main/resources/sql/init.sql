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