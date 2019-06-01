-- apply changes
alter table settings drop column default_target_directory;

drop table if exists local_directory;
drop sequence if exists local_directory_seq;
drop table if exists local_meta_data;
drop sequence if exists local_meta_data_seq;
drop table if exists pin_meta_data;
drop sequence if exists pin_meta_data_seq;
drop table if exists pinterest_board;
drop sequence if exists pinterest_board_seq;
drop table if exists web_cookie;
drop sequence if exists web_cookie_seq;
