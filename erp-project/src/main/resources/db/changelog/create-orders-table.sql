create table orders
(
    id         bigint auto_increment,
    order_number       varchar(255),
    order_date date,
    item_description       varchar(255),
    net_unit_price    int,
    quantity int,
    customer_id bigint,
    primary key (id)

);