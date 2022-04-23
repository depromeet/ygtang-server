create table email_auth
(
    email_auth_id bigint       not null auto_increment,
    email         varchar(255) not null,
    is_auth       bit          not null,
    primary key (email_auth_id)
) engine=InnoDB
