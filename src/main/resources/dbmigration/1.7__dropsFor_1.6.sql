-- apply changes
alter table settings drop column pinterest_settings_id;

drop table if exists pinterest_settings;
drop sequence if exists pinterest_settings_seq;
