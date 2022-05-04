create table inspiration_tag (
                    inspiration_tag_id bigint not null auto_increment,
                    created_date_time datetime(6),
                    updated_date_time datetime(6),
                    inspiration_id bigint,
                    tag_id bigint,
                    primary key (inspiration_tag_id)
) engine=InnoDB