create table if not exists resume
(
    uuid varchar(36) not null
        constraint resume_pk
            primary key,
    full_name text not null
);

/* Contact */
create table contact
(
    cont_id serial not null
        constraint contact_pk
            primary key,
    cont_type text not null,
    cont_value text not null,
    resume_uuid varchar(36) not null
        constraint contact_resume_uuid_fk
            references resume
            on delete cascade
);

create unique index resume_uuid_contact_type_index
    on contact (resume_uuid, cont_type);


/* Section */
create table section
(
    sec_id serial not null
        constraint section_pk
            primary key,
    sec_type text not null,
    resume_uuid varchar(36) not null
        constraint section_resume_uuid_fk
            references resume
            on delete cascade
);
create unique index resume_uuid_section_type_index
    on section (resume_uuid, sec_type);

/* texts */
create table texts
(
    texts_id serial not null
        constraint texts_pk
            primary key,
    texts_value text not null,
    section_id int not null
        constraint texts_section_id_fk
            references section
            on delete cascade
);

/* organizations */
create table organization
(
    org_id serial not null
        constraint organization_pk
            primary key,
    org_title text not null,
    org_url text not null,
    section_id int not null
        constraint organization_section_id_fk
            references section
            on delete cascade
);

/* Position */
create table position
(
    pos_id serial not null
        constraint position_pk
            primary key,
    pos_title text not null,
    pos_description text not null,
    start_date text not null,
    end_date text not null,
    organization_id int not null
        constraint position_organization_id_fk
            references organization
            on delete cascade
);
