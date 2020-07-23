drop table if exists spots;
create table spots(
    vehicle_plate varchar(8) not null,
    driver_type int(2) not null,
    begin_datetime timestamp not null
);