create table inspiration (
                inspiration_id bigint not null auto_increment,
                created_date_time datetime(6),
                updated_date_time datetime(6),
                content longtext,
                del_date_time datetime(6),
                is_deleted bit,
                memo longtext,
                type varchar(255),
                member_id bigint,
                primary key (inspiration_id)
) engine=InnoDB