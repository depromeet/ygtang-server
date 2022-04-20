create table email_auth
(
    email_auth_id bigint       not null  auto_increment,
    is_auth          Boolean      not null,
    email         varchar(255) not null,
    primary key (email_auth_id)
) engine=InnoDB