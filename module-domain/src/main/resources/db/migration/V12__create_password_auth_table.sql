create table password_auth
(
    password_auth_id bigint       not null auto_increment,
    email         varchar(100) not null,
    is_auth       bit          not null,
    primary key (password_auth_id)
) engine=InnoDB