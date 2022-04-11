create table member
(
    id           bigint       not null auto_increment,
    created_date datetime(6),
    updated_date datetime(6),
    email        varchar(255) not null,
    primary key (id)
) engine=InnoDB

/*
 추후 사용할 테이블 DDL 추가
 create table Example (

 ... 생략

 ) engine=InnoDB

 추후 사용할 테이블 DDL 추가
 create table Example2 (

 ... 생략

 ) engine=InnoDB
*/