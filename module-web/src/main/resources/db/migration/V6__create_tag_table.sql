create table tag (
                     tag_id bigint not null auto_increment,
                     created_date_time datetime(6),
                     updated_date_time datetime(6),
                     content varchar(100),
                     member_id bigint,
                     primary key (tag_id)
) engine=InnoDB