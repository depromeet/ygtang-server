create table email_auth
(
    email_auth_id bigint not null auto_increment,
    auth_token    varchar(255),
    email         varchar(255),
    expire_date   datetime(6),
    expired       bit,
    primary key (email_auth_id)
) engine=InnoDB
