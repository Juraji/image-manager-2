-- apply changes
create table pinterest_settings
(
    id uuid not null,
    pinterest_username varchar(255),
    password_salt      varbinary(16),
    pinterest_password varbinary(255),
    version            bigint    not null,
    created            timestamp not null,
    modified           timestamp not null,
    constraint pk_pinterest_settings primary key (id)
);

alter table settings
    add column pinterest_settings_id uuid;

alter table settings
    add constraint fk_settings_pinterest_settings_id foreign key (pinterest_settings_id) references pinterest_settings (id) on delete restrict on update restrict;

