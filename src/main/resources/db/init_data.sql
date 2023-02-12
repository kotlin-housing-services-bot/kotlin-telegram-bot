--liquibase formatted sql

--changeset TatianaDo:8
insert into management_company(id, company_name, inn, user_id) values (1, 'УК УМНЫЙ ДОМ', '111111111111', 48);

--changeset TatianaDo:9
insert into house(id, management_company_id, address) values (1, 1, 'г. Москва, ул. Левобережная, д. 4, к. 16');
insert into house(id, management_company_id, address) values (2, 1, 'г. Москва, ул. Петрозаводская, д. 15, к. 5');


--changeset TatianaDo:10
insert into public_service(id, house_id, public_service_name, calculation_type) values (1, 1, 'Кап. ремонт', 'BY_FLAT_AREA');
insert into public_service(id, house_id, public_service_name, calculation_type) values (2, 1, 'Электроэнергия', 'BY_METER');
insert into public_service(id, house_id, public_service_name, calculation_type) values (3, 1, 'Отопление', 'BY_FLAT_AREA');
insert into public_service(id, house_id, public_service_name, calculation_type) values (4, 1, 'Холодное водоснабжение и водоотведение', 'BY_METER');
insert into public_service(id, house_id, public_service_name, calculation_type) values (5, 1, 'Горячее водоснабжение, водоотведение и подогрев', 'BY_METER');

insert into public_service(id, house_id, public_service_name, calculation_type) values (6, 2, 'Кап. ремонт', 'BY_FLAT_AREA');
insert into public_service(id, house_id, public_service_name, calculation_type) values (7, 2, 'Электроэнергия', 'BY_METER');
insert into public_service(id, house_id, public_service_name, calculation_type) values (8, 2, 'Отопление', 'BY_FLAT_AREA');
insert into public_service(id, house_id, public_service_name, calculation_type) values (9, 2, 'Холодное водоснабжение и водоотведение', 'BY_METER');
insert into public_service(id, house_id, public_service_name, calculation_type) values (10, 2, 'Горячее водоснабжение, водоотведение и подогрев', 'BY_METER');

--changeset TatianaDo:11
insert into rate(id, public_service_id, rate_sum, date_begin) values (1, 1, 20.99, to_date('01.01.2023', 'dd.mm.yyyy'));
insert into rate(id, public_service_id, rate_sum, date_begin) values (2, 2, 6.41, to_date('01.01.2023', 'dd.mm.yyyy'));
insert into rate(id, public_service_id, rate_sum, date_begin) values (3, 3, 2279.36, to_date('01.01.2023', 'dd.mm.yyyy'));
insert into rate(id, public_service_id, rate_sum, date_begin) values (4, 4, 90.9, to_date('01.01.2023', 'dd.mm.yyyy'));
insert into rate(id, public_service_id, rate_sum, date_begin) values (5, 5, 230.45, to_date('01.01.2023', 'dd.mm.yyyy'));

insert into rate(id, public_service_id, rate_sum, date_begin) values (6, 6, 11.43, to_date('01.01.2023', 'dd.mm.yyyy'));
insert into rate(id, public_service_id, rate_sum, date_begin) values (7, 7, 3.6, to_date('01.01.2023', 'dd.mm.yyyy'));
insert into rate(id, public_service_id, rate_sum, date_begin) values (8, 8, 2055.94, to_date('01.01.2023', 'dd.mm.yyyy'));
insert into rate(id, public_service_id, rate_sum, date_begin) values (9, 9, 46.91, to_date('01.01.2023', 'dd.mm.yyyy'));
insert into rate(id, public_service_id, rate_sum, date_begin) values (10, 10, 150.71, to_date('01.01.2023', 'dd.mm.yyyy'));






