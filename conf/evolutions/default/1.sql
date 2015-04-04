# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table attribute (
  enclosure                 integer(1),
  attribute_id              integer)
;

create table business (
  business_id               varchar(255),
  full_address              varchar(255),
  name                      varchar(255),
  city                      varchar(255),
  state                     varchar(255),
  latitude                  double,
  longitude                 double,
  stars                     double,
  review_count              integer,
  open                      integer(1))
;

create table category (
  category_id               integer,
  name                      varchar(255))
;

create table checkin (
  business_id               varchar(255))
;

create table review (
  business_id               varchar(255),
  user_id                   varchar(255),
  stars                     double,
  text                      varchar(255),
  date                      timestamp,
  sentiment                 integer)
;

create table tip (
  user_id                   varchar(255),
  businees_id               varchar(255),
  text                      varchar(255),
  date                      timestamp,
  likes                     integer,
  sentiment                 integer)
;

create table user (
  user_id                   varchar(255),
  name                      varchar(255),
  review_count              integer,
  average_stars             double,
  yelping_since             varchar(255),
  fans                      integer)
;




# --- !Downs

PRAGMA foreign_keys = OFF;

drop table attribute;

drop table business;

drop table category;

drop table checkin;

drop table review;

drop table tip;

drop table user;

PRAGMA foreign_keys = ON;

