create schema if not exists config;

create table if not exists config.device_model
(model_id    serial not null primary key
,model_cod   varchar(30)
,name        varchar(50)
,thumbnail   varchar(4000)
);

insert into config.device_model
   (model_cod, name)
values
   ('UNO', 'Arduino UNO');
insert into config.device_model
   (model_cod, name)
values
   ('MEGA', 'Arduino MEGA 2560');
insert into config.device_model
   (model_cod, name)
values
   ('NANO', 'Arduino NANO');

create table if not exists config.device
(device_id   serial not null primary key
,name        varchar(50) unique
,model_id    integer
,runtime_id  varchar(32)
,version_id  varchar(32)
,status_id   integer
);


