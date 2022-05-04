alter table inspiration
    add constraint fk_inspiration_member
        foreign key (member_id)
            references member (member_id);

alter table inspiration_tag
    add constraint fk_inspiration_tag_inspiration
        foreign key (inspiration_id)
            references inspiration (inspiration_id);

alter table inspiration_tag
    add constraint fk_inspiration_tag_tag
        foreign key (tag_id)
            references tag (tag_id);

alter table tag
    add constraint tag_member
        foreign key (member_id)
            references member (member_id);
