create table member
(
    member_id    bigint       not null auto_increment,
    created_date_time datetime(6),
    updated_date_time datetime(6),
    email        varchar(100) not null,
    nickname     varchar(30)  not null,
    password     varchar(60)  not null,
    primary key (member_id)
) engine=InnoDB