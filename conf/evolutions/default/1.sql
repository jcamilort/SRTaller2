# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table attribute_db (
  attribute_id              bigint auto_increment not null,
  name                      varchar(255),
  constraint pk_attribute_db primary key (attribute_id))
;

create table business (
  business_id               varchar(255) not null,
  full_address              varchar(255),
  name                      varchar(255),
  city                      varchar(255),
  state                     varchar(255),
  latitude                  double,
  longitude                 double,
  stars                     double,
  review_count              integer,
  categories_db             varchar(255),
  attributes_db             varchar(255),
  open                      tinyint(1) default 0,
  constraint pk_business primary key (business_id))
;

create table category (
  category_id               bigint auto_increment not null,
  name                      varchar(255),
  constraint pk_category primary key (category_id))
;

create table review (
  business_id               varchar(255),
  user_id                   varchar(255),
  stars                     double,
  text                      varchar(255),
  date                      datetime,
  sentiment                 integer)
;

create table tip (
  id                        bigint auto_increment not null,
  user_user_id              varchar(255),
  business_business_id      varchar(255),
  text                      varchar(255),
  date                      datetime,
  likes                     integer,
  sentiment                 integer,
  constraint pk_tip primary key (id))
;

create table user (
  user_id                   varchar(255) not null,
  name                      varchar(255),
  review_count              integer,
  average_stars             double,
  yelping_since             varchar(255),
  fans                      integer,
  constraint pk_user primary key (user_id))
;

alter table tip add constraint fk_tip_user_1 foreign key (user_user_id) references user (user_id) on delete restrict on update restrict;
create index ix_tip_user_1 on tip (user_user_id);
alter table tip add constraint fk_tip_business_2 foreign key (business_business_id) references business (business_id) on delete restrict on update restrict;
create index ix_tip_business_2 on tip (business_business_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table attribute_db;

drop table business;

drop table category;

drop table review;

drop table tip;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

