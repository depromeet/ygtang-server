create table user_roles (
    id bigint not null,
    user_roles varchar(255)
) engine=InnoDB

alter table user_roles add constraint FKd5r5xcysqxnk167bgy7fcnd74 foreign key (id) references member (id)