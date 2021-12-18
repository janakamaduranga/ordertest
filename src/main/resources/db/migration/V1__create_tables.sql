create sequence hibernate_sequence start with 1 increment by 1;
create table customer_order (id bigint not null, primary key (id));
create table order_item (id bigint not null, string varchar(255), price double, order_id bigint, primary key (id));
alter table order_item add constraint FKgv4bnmo7cbib2nh0b2rw9yvir foreign key (order_id) references customer_order;