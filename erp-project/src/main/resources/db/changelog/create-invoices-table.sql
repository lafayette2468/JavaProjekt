create table invoices
(
    id                bigint auto_increment,
    invoice_number    varchar(255),
    invoice_date      date,
    fulfilment_date  date,
    due_date          date,
    vat               varchar(10),
    method_of_payment varchar(20),
    order_id bigint,
    primary key (id)
);