create table customers
(
    id         bigint auto_increment,
    name       varchar(255),
    tax_number varchar(20),
    zip_code    varchar(10),
    city       varchar(255),
    address    varchar(255),
    primary key (id)
);