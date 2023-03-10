--liquibase formatted sql

--changeset TatianaDo:1
create table if not exists management_company
(
    id bigint generated by default as identity(start with 100) not null constraint management_company_pk primary key,
    company_name varchar(50) not null,
    inn varchar(12) not null,
    user_id bigint not null,
    constraint management_company_uk1 unique(inn),
    constraint management_company_uk2 unique(inn, user_id)
);
--rollback drop table management_company;

--changeset TatianaDo:2
create table if not exists house
(
    id bigint generated by default as identity(start with 100) not null constraint house_pk primary key,
    management_company_id bigint not null,
    address varchar(100) not null,
    constraint house_uk unique(address),
    constraint house_fk foreign key(management_company_id) references management_company(id)
    );
--rollback drop table house;

--changeset TatianaDo:3
create table if not exists public_service
(
    id bigint generated by default as identity(start with 100) not null constraint public_service_pk primary key,
    house_id bigint not null,
    public_service_name varchar(50) not null,
    calculation_type varchar(25) not null,
    unit varchar(25) not null,
    constraint public_service_uk unique(house_id, public_service_name),
    constraint public_service_fk foreign key(house_id) references house(id)
    );
--rollback drop service;

--changeset TatianaDo:4
create table if not exists public_service_rate
(
    id bigint generated by default as identity(start with 100) not null constraint public_service_rate_pk primary key,
    public_service_id bigint not null,
    rate_sum numeric not null,
    date_begin date not null,
    constraint public_service_rate_uk unique(public_service_id, rate_sum, date_begin),
    constraint public_service_rate_fk foreign key(public_service_id) references public_service(id)
    );
--rollback drop rate;

--changeset TatianaDo:5
create table if not exists flat
(
    id bigint generated by default as identity(start with 100) not null constraint flat_pk primary key,
    house_id bigint not null,
    flat_number varchar(20) not null,
    area numeric,
    number_of_residents bigint,
    user_id bigint not null,
    chat_id bigint not null,
    constraint flat_uk unique(house_id, flat_number),
    constraint flat_fk foreign key(house_id) references house(id)
    );
--rollback drop flat;


--changeset TatianaDo:6
create table if not exists metric
(
    id bigint generated by default as identity(start with 100) not null constraint metric_pk primary key,
    flat_id bigint not null,
    public_service_id bigint not null,
    metric_value numeric not null,
    metric_date date not null,
    is_init boolean not null,
    constraint metric_uk unique(flat_id, public_service_id, metric_date, is_init),
    constraint metric_fk1 foreign key(flat_id) references flat(id),
    constraint metric_fk2 foreign key(public_service_id) references public_service(id)
    );
--rollback drop meter_reading;

--changeset TatianaDo:7
create table if not exists bill
(
    id bigint generated by default as identity(start with 100) not null constraint bill_pk primary key,
    flat_id bigint not null,
    bill_month integer not null,
    bill_year integer not null,
    bill_data oid not null,
    constraint bill_uk unique(flat_id, bill_month),
    constraint bill_fk foreign key(flat_id) references flat(id)
    );
--rollback drop bill;
