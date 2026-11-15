create table users (
    id uuid primary key default gen_random_uuid(),
    email varchar(254) not null unique,
    password_hash varchar(100) not null,
    display_name varchar(40) not null,
    locale varchar(2) not null check (locale in ('vi', 'en')),
    timezone varchar(64) not null,
    timezone_changed_at timestamptz,
    pending_timezone varchar(64),
    pending_timezone_from timestamptz,
    premium_until timestamptz,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table refresh_tokens (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references users on delete cascade,
    token_hash char(64) not null unique,
    family_id uuid not null,
    expires_at timestamptz not null,
    used_at timestamptz,
    revoked_at timestamptz,
    created_at timestamptz not null
);
create index idx_refresh_tokens_user_id on refresh_tokens (user_id);

create table pause_periods (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references users on delete cascade,
    start_date date not null,
    end_date date not null,
    created_at timestamptz not null,
    check (end_date >= start_date)
);
create index idx_pause_periods_user_start on pause_periods (user_id, start_date);
