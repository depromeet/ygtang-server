create table member
(
    member_id    bigint       not null auto_increment,
    created_date datetime(6),
    updated_date datetime(6),
    email        varchar(255) not null,
    primary key (member_id)
) engine=InnoDB

