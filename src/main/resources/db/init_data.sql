--liquibase formatted sql

--changeset TatianaDo:8
insert into management_company(id, company_name, inn, user_id) values (1, 'УК УМНЫЙ ДОМ', '111111111111', 383036855);

--changeset TatianaDo:9
insert into house(id, management_company_id, address) values (1, 1, 'г. Москва, ул. Левобережная, д. 4');
insert into house(id, management_company_id, address) values (2, 1, 'г. Москва, ул. Петрозаводская, д. 15');


--changeset TatianaDo:10
insert into public_service(id, house_id, public_service_name, calculation_type, unit) values (1, 1, 'Кап. ремонт', 'BY_FLAT_AREA', 'м2');
insert into public_service(id, house_id, public_service_name, calculation_type, unit) values (2, 1, 'Электроэнергия', 'BY_METER', 'кВт.ч');
insert into public_service(id, house_id, public_service_name, calculation_type, unit) values (3, 1, 'Отопление', 'BY_FLAT_AREA', 'Гкал');
insert into public_service(id, house_id, public_service_name, calculation_type, unit) values (4, 1, 'Холодное водоснабжение и водоотведение', 'BY_METER', 'м3');
insert into public_service(id, house_id, public_service_name, calculation_type, unit) values (5, 1, 'Горячее водоснабжение, водоотведение и подогрев', 'BY_METER', 'м3');

insert into public_service(id, house_id, public_service_name, calculation_type, unit) values (6, 2, 'Кап. ремонт', 'BY_FLAT_AREA', 'м2');
insert into public_service(id, house_id, public_service_name, calculation_type, unit) values (7, 2, 'Электроэнергия', 'BY_METER', 'кВт.ч');
insert into public_service(id, house_id, public_service_name, calculation_type, unit) values (8, 2, 'Отопление', 'BY_FLAT_AREA', 'Гкал');
insert into public_service(id, house_id, public_service_name, calculation_type, unit) values (9, 2, 'Холодное водоснабжение и водоотведение', 'BY_METER', 'м3');
insert into public_service(id, house_id, public_service_name, calculation_type, unit) values (10, 2, 'Горячее водоснабжение, водоотведение и подогрев', 'BY_METER', 'м3');

--changeset TatianaDo:11
insert into public_service_rate(id, public_service_id, rate_sum, date_begin) values (1, 1, 20.99, to_date('01.01.2023', 'dd.mm.yyyy'));
insert into public_service_rate(id, public_service_id, rate_sum, date_begin) values (2, 2, 6.41, to_date('01.01.2023', 'dd.mm.yyyy'));
insert into public_service_rate(id, public_service_id, rate_sum, date_begin) values (3, 3, 2279.36, to_date('01.01.2023', 'dd.mm.yyyy'));
insert into public_service_rate(id, public_service_id, rate_sum, date_begin) values (4, 4, 90.9, to_date('01.01.2023', 'dd.mm.yyyy'));
insert into public_service_rate(id, public_service_id, rate_sum, date_begin) values (5, 5, 230.45, to_date('01.01.2023', 'dd.mm.yyyy'));

insert into public_service_rate(id, public_service_id, rate_sum, date_begin) values (6, 6, 11.43, to_date('01.01.2023', 'dd.mm.yyyy'));
insert into public_service_rate(id, public_service_id, rate_sum, date_begin) values (7, 7, 3.6, to_date('01.01.2023', 'dd.mm.yyyy'));
insert into public_service_rate(id, public_service_id, rate_sum, date_begin) values (8, 8, 2055.94, to_date('01.01.2023', 'dd.mm.yyyy'));
insert into public_service_rate(id, public_service_id, rate_sum, date_begin) values (9, 9, 46.91, to_date('01.01.2023', 'dd.mm.yyyy'));
insert into public_service_rate(id, public_service_id, rate_sum, date_begin) values (10, 10, 150.71, to_date('01.01.2023', 'dd.mm.yyyy'));
