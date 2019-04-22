-- apply changes
create table hash_data
(
    id uuid not null,
    bits     varbinary(10000),
    contrast integer,
    version  bigint    not null,
    created  timestamp not null,
    modified timestamp not null,
    constraint ck_hash_data_contrast check ( contrast in (0, 1)),
    constraint pk_hash_data primary key (id)
);

create table local_directory
(
    id uuid not null,
    name             varchar(255)  not null,
    location_on_disk varchar(2048) not null,
    favorite         boolean,
    parent_id uuid,
    version          bigint        not null,
    created          timestamp     not null,
    modified         timestamp     not null,
    constraint pk_local_directory primary key (id)
);

create table local_meta_data
(
    id uuid not null,
    path           varchar(2048),
    quality_rating bigint                not null,
    file_size      bigint                not null,
    width          integer               not null,
    height         integer               not null,
    file_corrected boolean default false not null,
    comments       TEXT,
    hash_id uuid,
    directory_id uuid,
    version        bigint                not null,
    created        timestamp             not null,
    modified       timestamp             not null,
    constraint uq_local_meta_data_hash_id unique (hash_id),
    constraint pk_local_meta_data primary key (id)
);

create table pin_meta_data
(
    id uuid not null,
    path           varchar(2048),
    quality_rating bigint                not null,
    file_size      bigint                not null,
    width          integer               not null,
    height         integer               not null,
    file_corrected boolean default false not null,
    comments       TEXT,
    hash_id uuid,
    pin_id         varchar(255)          not null,
    title          varchar(255),
    pinterest_uri  varchar(2048)         not null,
    download_url   varchar(2048)         not null,
    source_url     varchar(2048),
    board_id uuid,
    version        bigint                not null,
    created        timestamp             not null,
    modified       timestamp             not null,
    constraint uq_pin_meta_data_hash_id unique (hash_id),
    constraint pk_pin_meta_data primary key (id)
);

create table pinterest_board
(
    id uuid not null,
    name             varchar(255)  not null,
    location_on_disk varchar(2048) not null,
    favorite         boolean,
    board_url        varchar(255)  not null,
    slug             varchar(255)  not null,
    board_id         varchar(255)  not null,
    type             integer,
    parent_id uuid,
    version          bigint        not null,
    created          timestamp     not null,
    modified         timestamp     not null,
    constraint ck_pinterest_board_type check ( type in (0, 1)),
    constraint pk_pinterest_board primary key (id)
);

create table settings
(
    id uuid not null,
    cipher_base                      varchar(16),
    pinterest_username               varchar(255),
    pinterest_password               varbinary(255),
    default_target_directory         varchar(2048),
    duplicate_scanner_min_similarity integer   not null,
    version                          bigint    not null,
    created                          timestamp not null,
    modified                         timestamp not null,
    constraint pk_settings primary key (id)
);

create table web_cookie
(
    id uuid not null,
    root_domain varchar(255)          not null,
    name        varchar(255)          not null,
    domain      varchar(2048)         not null,
    expiry      timestamp,
    path        varchar(2048),
    value       varchar(4096)         not null,
    http_only   boolean default false not null,
    secure      boolean default false not null,
    version     bigint                not null,
    created     timestamp             not null,
    modified    timestamp             not null,
    constraint pk_web_cookie primary key (id)
);

create index ix_local_directory_parent_id on local_directory (parent_id);
alter table local_directory
    add constraint fk_local_directory_parent_id foreign key (parent_id) references local_directory (id) on delete restrict on update restrict;

alter table local_meta_data
    add constraint fk_local_meta_data_hash_id foreign key (hash_id) references hash_data (id) on delete restrict on update restrict;

create index ix_local_meta_data_directory_id on local_meta_data (directory_id);
alter table local_meta_data
    add constraint fk_local_meta_data_directory_id foreign key (directory_id) references local_directory (id) on delete restrict on update restrict;

alter table pin_meta_data
    add constraint fk_pin_meta_data_hash_id foreign key (hash_id) references hash_data (id) on delete restrict on update restrict;

create index ix_pin_meta_data_board_id on pin_meta_data (board_id);
alter table pin_meta_data
    add constraint fk_pin_meta_data_board_id foreign key (board_id) references pinterest_board (id) on delete restrict on update restrict;

create index ix_pinterest_board_parent_id on pinterest_board (parent_id);
alter table pinterest_board
    add constraint fk_pinterest_board_parent_id foreign key (parent_id) references pinterest_board (id) on delete restrict on update restrict;

