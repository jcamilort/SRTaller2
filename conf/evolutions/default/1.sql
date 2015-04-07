# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

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
  open                      tinyint(1) default 0,
  constraint pk_business primary key (business_id))
;

create table category (
  category_id               integer auto_increment not null,
  name                      varchar(255),
  constraint pk_category primary key (category_id))
;

create table checkin (
  business_id               varchar(255))
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
  user_id                   varchar(255),
  businees_id               varchar(255),
  text                      varchar(255),
  date                      datetime,
  likes                     integer,
  sentiment                 integer)
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




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table business;

drop table category;

drop table checkin;

drop table review;

drop table tip;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

