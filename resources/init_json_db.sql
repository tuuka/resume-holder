create table if not exists resume_json
(
    uuid varchar(36) not null
        constraint resume_json_pk
            primary key,
    full_name text not null,
    contacts text,
    sections text
);
