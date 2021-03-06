
##创建用cboard元数据库

DROP DATABASE IF EXISTS cboard;
CREATE DATABASE cboard CHARACTER SET utf8;



##创建用cboard户:
create user 'cboard'@'%' identified by 'cboard';
grant  all  on  cboard.*  to  'cboard'@'%'  identified  by  'cboard';    ##外网慎重
grant  all  on  cboard.*  to  'cboard'@'localhost'  identified  by  'cboard';
 #-- grant  all  on  cboard.*  to  'cboard'@'主机名'  identified  by  'cboard';
FLUSH  PRIVILEGES;        ##刷新


##########
USE cboard;
##
DROP  TABLE IF EXISTS dashboard_board;
CREATE TABLE  dashboard_board (
  board_id bigint(20) NOT NULL AUTO_INCREMENT,
  user_id varchar(50) NOT NULL,
  category_id bigint(20) DEFAULT NULL,
  board_name varchar(100) NOT NULL,
  layout_json text,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON  UPDATE  CURRENT_TIMESTAMP,
  PRIMARY KEY (board_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP  TABLE IF EXISTS  dashboard_category;
CREATE TABLE dashboard_category (
  category_id bigint(20) NOT NULL AUTO_INCREMENT,
  category_name varchar(100) NOT NULL,
  user_id varchar(100) NOT NULL,
  PRIMARY KEY (category_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP  TABLE IF EXISTS  dashboard_datasource;
CREATE TABLE dashboard_datasource (
  datasource_id bigint(20) NOT NULL AUTO_INCREMENT,
  user_id varchar(50) NOT NULL,
  source_name varchar(100) NOT NULL,
  source_type varchar(100) NOT NULL,
  config text,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP  ON  UPDATE  CURRENT_TIMESTAMP,
  PRIMARY KEY (datasource_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP  TABLE IF EXISTS  dashboard_widget;
CREATE TABLE dashboard_widget (
  widget_id bigint(20) NOT NULL AUTO_INCREMENT,
  user_id varchar(100) NOT NULL,
  category_name varchar(100) DEFAULT NULL,
  widget_name varchar(100) DEFAULT NULL,
  data_json text,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP  ON  UPDATE  CURRENT_TIMESTAMP,
  PRIMARY KEY (widget_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP  TABLE IF EXISTS  dashboard_dataset;
CREATE TABLE dashboard_dataset (
  dataset_id bigint(20) NOT NULL AUTO_INCREMENT,
  user_id varchar(100) NOT NULL,
  category_name varchar(100) DEFAULT NULL,
  dataset_name varchar(100) DEFAULT NULL,
  data_json text,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP  ON  UPDATE  CURRENT_TIMESTAMP,
  PRIMARY KEY (dataset_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP  TABLE IF EXISTS  dashboard_user;
CREATE TABLE dashboard_user (
  user_id varchar(50) NOT NULL,
  login_name varchar(100) DEFAULT NULL,
  user_name varchar(100) DEFAULT NULL,
  user_password varchar(100) DEFAULT NULL,
  user_status varchar(100) DEFAULT NULL,
  PRIMARY KEY (user_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP  TABLE IF EXISTS  dashboard_user_role;
CREATE TABLE dashboard_user_role (
  user_role_id bigint(20) NOT NULL AUTO_INCREMENT,
  user_id varchar(100) DEFAULT NULL,
  role_id varchar(100) DEFAULT NULL,
  PRIMARY KEY (user_role_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP  TABLE IF EXISTS  dashboard_role;
CREATE TABLE dashboard_role (
  role_id varchar(100) NOT NULL,
  role_name varchar(100) DEFAULT NULL,
  user_id varchar(50) DEFAULT NULL,
  PRIMARY KEY (role_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP  TABLE IF EXISTS  dashboard_role_res;
CREATE TABLE dashboard_role_res (
  role_res_id bigint(20) NOT NULL AUTO_INCREMENT,
  role_id varchar(100) DEFAULT NULL,
  res_type varchar(100) DEFAULT NULL,
  res_id bigint(20) DEFAULT NULL,
  permission varchar(20) DEFAULT NULL,
  PRIMARY KEY (role_res_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP  TABLE IF EXISTS  dashboard_job;
CREATE TABLE dashboard_job (
  job_id bigint(20) NOT NULL AUTO_INCREMENT,
  job_name varchar(200) DEFAULT NULL,
  cron_exp varchar(200) DEFAULT NULL,
  start_date timestamp NULL DEFAULT NULL,
  end_date timestamp NULL DEFAULT NULL,
  job_type varchar(200) DEFAULT NULL,
  job_config text,
  user_id varchar(100) DEFAULT NULL,
  last_exec_time timestamp NULL DEFAULT NULL,
  job_status bigint(20),
  exec_log text,
  PRIMARY KEY (job_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP  TABLE IF EXISTS  dashboard_board_param;
CREATE TABLE dashboard_board_param (
  board_param_id bigint(20) NOT NULL AUTO_INCREMENT,
  user_id varchar(50) NOT NULL,
  board_id bigint(20) NOT NULL,
  config text, # dm层sql应该比较简单
  PRIMARY KEY (board_param_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP  TABLE IF EXISTS  dashboard_homepage;
CREATE TABLE dashboard_homepage (
  board_id bigint(20) NOT NULL,
  user_id varchar(50) NOT NULL,
  PRIMARY KEY (board_id, user_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


#######
INSERT INTO dashboard_user (user_id,login_name,user_name,user_password)
VALUES('1', 'admin', 'Administrator', 'ff9830c42660c1dd1942844f8069b74a');

