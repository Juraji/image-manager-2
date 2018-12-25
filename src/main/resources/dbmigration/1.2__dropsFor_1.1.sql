-- apply changes
alter table settings drop column cipher_base;

alter table settings drop column pinterest_username;

alter table settings drop column pinterest_password;

-- noinspection SqlWithoutWhere
delete from settings;
