-- apply changes
create table directory (
  id                            uuid not null,
  name                          varchar(255) not null,
  location_on_disk              varchar(2048) not null,
  favorite                      boolean default false not null,
  parent_id                     uuid,
  version                       bigint not null,
  created                       timestamp not null,
  modified                      timestamp not null,
  constraint pk_directory primary key (id)
);

create table meta_data (
  id                            uuid not null,
  path                          varchar(2048),
  quality_rating                bigint not null,
  file_size                     bigint not null,
  width                         integer not null,
  height                        integer not null,
  file_corrected                boolean default false not null,
  comments                      TEXT,
  hash_id                       uuid,
  directory_id                  uuid,
  version                       bigint not null,
  created                       timestamp not null,
  modified                      timestamp not null,
  constraint uq_meta_data_hash_id unique (hash_id),
  constraint pk_meta_data primary key (id)
);

create index ix_directory_parent_id on directory (parent_id);
alter table directory add constraint fk_directory_parent_id foreign key (parent_id) references directory (id) on delete restrict on update restrict;

alter table meta_data add constraint fk_meta_data_hash_id foreign key (hash_id) references hash_data (id) on delete restrict on update restrict;

create index ix_meta_data_directory_id on meta_data (directory_id);
alter table meta_data add constraint fk_meta_data_directory_id foreign key (directory_id) references directory (id) on delete restrict on update restrict;

