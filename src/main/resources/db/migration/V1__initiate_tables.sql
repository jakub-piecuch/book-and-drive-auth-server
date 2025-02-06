-- Create table for tenant
CREATE TABLE tenant (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,

    -- Create unique constraint for name
    CONSTRAINT unique_tenant_name
        UNIQUE (name)
);

-- Create index for frequently queried columns
CREATE INDEX idx_tenant_name ON tenant(name);

-- Create table for _user
CREATE TABLE _user (
    id UUID PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL,
    tenant_id UUID NOT NULL,
    roles VARCHAR(255)[],

    -- Create foreign key constraint for tenant
    CONSTRAINT fk_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenant(id)
        ON DELETE CASCADE,

    -- Create unique constraint for email per tenant
    CONSTRAINT unique_email_per_tenant
        UNIQUE (email, tenant_id)
);

-- Create index for frequently queried columns
CREATE INDEX idx_user_email ON _user(email);
CREATE INDEX idx_user_tenant ON _user(tenant_id);

CREATE INDEX idx_user_email_tenant ON _user(email, tenant_id);

-- Create table for one_time_token
CREATE TABLE one_time_token (
    id UUID PRIMARY KEY,
    token VARCHAR(512) NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    _user_id UUID NOT NULL,

    -- Create foreign key constraint for user
    CONSTRAINT fk_user
        FOREIGN KEY (_user_id)
        REFERENCES _user(id)
        ON DELETE CASCADE,

    -- Ensure one-to-one relationship by making user_id unique
    CONSTRAINT unique_user_token
        UNIQUE (_user_id)
);

-- Create indexes for frequently queried columns
CREATE INDEX idx_one_time_token_user ON one_time_token(_user_id);
CREATE INDEX idx_one_time_token_token ON one_time_token(token);

